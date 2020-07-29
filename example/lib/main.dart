import 'dart:convert';
import 'dart:io' as io;
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'dart:async';
import 'package:flutter/services.dart' show MethodCall, MethodChannel, PlatformException, rootBundle;
import 'package:flutter_document_reader_api/flutter_document_reader_api.dart';
import 'package:image_picker/image_picker.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Future<String> getImage() async {
    setStatus("Processing image...");
    return base64Encode(io.File((await ImagePicker().getImage(source: ImageSource.gallery)).path).readAsBytesSync());
  }

  Object setStatus(String s) => {setState(() => _status = s)};
  String _status = "Loading...";
  var _portrait = Image.asset('assets/images/portrait.png');
  var _docImage = Image.asset('assets/images/id.png');
  List<List<String>> _scenarios = [];
  String _selectedScenario = "Mrz";
  bool _canRfid = false;
  bool _doRfid = false;
  var printError = (Object error) => print((error as PlatformException).message);

  @override
  void initState() {
    super.initState();
    initPlatformState();
    MethodChannel('flutter_document_reader_api').setMethodCallHandler((MethodCall call) => setStatus(call.arguments));
  }

  Future<void> initPlatformState() async {
    print(await FlutterDocumentReaderApi.prepareDatabase("Full"));
    setStatus("Initializing...");
    ByteData byteData = await rootBundle.load("assets/regula.license");
    print(await FlutterDocumentReaderApi.initializeReader(base64.encode(byteData.buffer.asUint8List(byteData.offsetInBytes, byteData.lengthInBytes))));
    setStatus("Ready");
    bool canRfid = await FlutterDocumentReaderApi.isRFIDAvailableForUse();
    setState(() => _canRfid = canRfid);
    List<List<String>> scenarios = [];
    var scenariosTemp = json.decode(await FlutterDocumentReaderApi.getAvailableScenarios());
    for (var i = 0; i < scenariosTemp.length; i++) {
      Scenario scenario = Scenario.fromJson(scenariosTemp[i] is String ? json.decode(scenariosTemp[i]) : scenariosTemp[i]);
      scenarios.add([scenario.name, scenario.caption]);
    }
    setState(() => _scenarios = scenarios);
    FlutterDocumentReaderApi.setConfig(jsonEncode({
      "functionality": {"videoCaptureMotionControl": true, "showCaptureButton": true},
      "customization": {"showResultStatusMessages": true, "showStatusMessages": true},
      "processParams": {"scenario": _selectedScenario}
    }));
  }

  displayResults(DocumentReaderResults results) {
    setState(() {
      _status = results.getTextFieldValueByType(eVisualFieldType.FT_SURNAME_AND_GIVEN_NAMES) ?? "";
      _docImage = Image.asset('assets/images/id.png');
      _portrait = Image.asset('assets/images/portrait.png');
      if (results.getGraphicFieldImageByType(207) != null) _docImage = Image.memory(Uri.parse("data:image/png;base64," + results.getGraphicFieldImageByType(eGraphicFieldType.GF_DOCUMENT_IMAGE).replaceAll('\n', '')).data.contentAsBytes());
      if (results.getGraphicFieldImageByType(201) != null) _portrait = Image.memory(Uri.parse("data:image/png;base64," + results.getGraphicFieldImageByType(eGraphicFieldType.GF_PORTRAIT).replaceAll('\n', '')).data.contentAsBytes());
    });
  }

  void handleResults(String jString) {
    var results = DocumentReaderResults.fromJson(json.decode(jString));
    if (_doRfid && results != null && results.chipPage != 0) {
      String accessKey = results.getTextFieldValueByType(eVisualFieldType.FT_MRZ_STRINGS);
      if (accessKey != null && accessKey != "")
        FlutterDocumentReaderApi.setRfidScenario(jsonEncode({"mrz": accessKey.replaceAll('^', '').replaceAll('\n', ''), "pacePasswordType": eRFID_Password_Type.PPT_MRZ}));
      else if (results.getTextFieldValueByType(159) != null && results.getTextFieldValueByType(159) != "") FlutterDocumentReaderApi.setRfidScenario(jsonEncode({"password": results.getTextFieldValueByType(159), "pacePasswordType": eRFID_Password_Type.PPT_CAN}));
      FlutterDocumentReaderApi.startRFIDReader().then((dynamic S) => this.displayResults(DocumentReaderResults.fromJson(json.decode(S)))).catchError(printError);
    } else
      displayResults(results);
  }

  void onChangeRfid(bool value) {
    setState(() => _doRfid = value && _canRfid);
    FlutterDocumentReaderApi.setConfig(jsonEncode({
      "processParams": {"doRfid": _doRfid}
    }));
  }

  Widget createImage(String title, double height, double width, ImageProvider image) {
    return Column(mainAxisAlignment: MainAxisAlignment.center, crossAxisAlignment: CrossAxisAlignment.center, children: <Widget>[Text(title), Image(height: height, width: width, image: image)]);
  }

  Widget createButton(String text, VoidCallback onPress) {
    return Container(
      padding: EdgeInsets.fromLTRB(5, 0, 5, 0),
      transform: Matrix4.translationValues(0, -7.5, 0),
      child: FlatButton(color: Color.fromARGB(50, 10, 10, 10), onPressed: onPress, child: Text(text)),
      width: 150,
    );
  }

  Widget _buildRow(int index) {
    Radio radio = new Radio(
        value: _scenarios[index][0],
        groupValue: _selectedScenario,
        onChanged: (value) => setState(() {
              _selectedScenario = value;
              FlutterDocumentReaderApi.setConfig(jsonEncode({
                "processParams": {"scenario": _selectedScenario}
              }));
            }));
    return Container(child: ListTile(title: GestureDetector(onTap: () => radio.onChanged(_scenarios[index][0]), child: Text(_scenarios[index][1])), leading: radio), padding: EdgeInsets.only(left: 40));
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Center(child: Text(_status))),
        body: Column(mainAxisAlignment: MainAxisAlignment.center, children: <Widget>[
          Row(mainAxisAlignment: MainAxisAlignment.center, crossAxisAlignment: CrossAxisAlignment.center, children: <Widget>[
            createImage("Portrait", 150, 150, _portrait.image),
            createImage("Document image", 150, 200, _docImage.image),
          ]),
          Expanded(child: Container(color: Color.fromARGB(5, 10, 10, 10), child: ListView.builder(itemCount: _scenarios.length, itemBuilder: (BuildContext context, int index) => _buildRow(index)))),
          CheckboxListTile(value: _doRfid, onChanged: onChangeRfid, title: Text("Process rfid reading ${_canRfid ? "" : "(unavailable)"}")),
          Row(mainAxisAlignment: MainAxisAlignment.center, children: <Widget>[
            createButton("Scan document", () async => this.handleResults(await FlutterDocumentReaderApi.showScanner())),
            createButton("Scan image", () async => this.handleResults(await FlutterDocumentReaderApi.recognizeImage(await getImage()))),
          ])
        ]),
      ),
    );
  }
}