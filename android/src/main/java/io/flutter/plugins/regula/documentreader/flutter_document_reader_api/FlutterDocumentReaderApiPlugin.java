package io.flutter.plugins.regula.documentreader.flutter_document_reader_api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.regula.documentreader.api.completions.IDocumentReaderCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderInitCompletion;
import com.regula.documentreader.api.completions.IDocumentReaderPrepareCompletion;
import com.regula.documentreader.api.enums.DocReaderAction;
import com.regula.documentreader.api.params.ImageInputParam;
import com.regula.documentreader.api.params.rfid.PKDCertificate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import static com.regula.documentreader.api.DocumentReader.Instance;

@SuppressWarnings({"unchecked", "unused", "NullableProblems", "ConstantConditions", "RedundantSuppression"})
public class FlutterDocumentReaderApiPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private Context context;
    private MethodChannel channel;
    private Object args;
    private static int databaseDownloadProgress = 0;
    private FlutterDocumentReaderApiPlugin newInstance;

    private FlutterDocumentReaderApiPlugin(MethodChannel channel, Context context) {
        this.channel = channel;
        this.context = context;
    }

    public FlutterDocumentReaderApiPlugin() {
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        newInstance.context = binding.getActivity();
        Log.d("TAG", "AttachedToActivity");
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        newInstance.context = binding.getActivity();
        Log.d("TAG", "ReattachedToActivityForConfigChanges");
    }

    @Override
    public void onDetachedFromActivity() {
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_document_reader_api");
        methodChannel.setMethodCallHandler(newInstance = new FlutterDocumentReaderApiPlugin(methodChannel, flutterPluginBinding.getApplicationContext()));
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
    }

    private Context getContext() {
        return context;
    }

    private interface Callback {
        void success(Object o);

        void error(String s);

        default void success() {
            success("");
        }
    }

    private <T> T args(int index) {
        try {
            return (T) new JSONObject((String) ((List<T>) args).get(index));
        } catch (JSONException ignored) {
            return ((List<T>) args).get(index);
        }
    }

    private void sendProgress(int progress) {
        channel.invokeMethod("updatePercentage", "Downloading database: " + progress + "%");
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        String action = call.method;
        args = call.arguments;
        Callback callback = new Callback() {
            @Override
            public void success(Object o) {
                result.success(o);
            }

            @Override
            public void error(String s) {
                result.error("", s, null);
            }
        };
        try {
            switch (action) {
                case "getAPIVersion":
                    getAPIVersion(callback);
                    break;
                case "getAvailableScenarios":
                    getAvailableScenarios(callback);
                    break;
                case "isRFIDAvailableForUse":
                    isRFIDAvailableForUse(callback);
                    break;
                case "getCoreMode":
                    getCoreMode(callback);
                    break;
                case "getCoreVersion":
                    getCoreVersion(callback);
                    break;
                case "getDatabaseDate":
                    getDatabaseDate(callback);
                    break;
                case "getDatabaseID":
                    getDatabaseID(callback);
                    break;
                case "getDatabaseVersion":
                    getDatabaseVersion(callback);
                    break;
                case "getDocumentReaderIsReady":
                    getDocumentReaderIsReady(callback);
                    break;
                case "getDocumentReaderStatus":
                    getDocumentReaderStatus(callback);
                    break;
                case "getDatabaseCountriesNumber":
                    getDatabaseCountriesNumber(callback);
                    break;
                case "getDatabaseDocumentsNumber":
                    getDatabaseDocumentsNumber(callback);
                    break;
                case "selectedScenario":
                    selectedScenario(callback);
                    break;
                case "getSessionLogFolder":
                    getSessionLogFolder(callback);
                    break;
                case "getDatabaseDescription":
                    getDatabaseDescription(callback);
                    break;
                case "showScanner":
                    showScanner(callback);
                    break;
                case "startNewPage":
                    startNewPage(callback);
                    break;
                case "startNewSession":
                    startNewSession(callback);
                    break;
                case "startRFIDReader":
                    startRFIDReader(callback);
                    break;
                case "stopRFIDReader":
                    stopRFIDReader(callback);
                    break;
                case "stopScanner":
                    stopScanner(callback);
                    break;
                case "deinitializeReader":
                    deinitializeReader(callback);
                    break;
                case "isAuthenticatorAvailableForUse":
                    isAuthenticatorAvailableForUse(callback);
                    break;
                case "getConfig":
                    getConfig(callback);
                    break;
                case "getRfidScenario":
                    getRfidScenario(callback);
                    break;
                case "getLicenseExpiryDate":
                    getLicenseExpiryDate(callback);
                    break;
                case "getLicenseCountryFilter":
                    getLicenseCountryFilter(callback);
                    break;
                case "licenseIsRfidAvailable":
                    licenseIsRfidAvailable(callback);
                    break;
                case "getCameraSessionIsPaused":
                    getCameraSessionIsPaused(callback);
                    break;
                case "removeDatabase":
                    removeDatabase(callback);
                    break;
                case "cancelDBUpdate":
                    cancelDBUpdate(callback);
                    break;
                case "resetConfiguration":
                    resetConfiguration(callback);
                    break;
                case "clearPKDCertificates":
                    clearPKDCertificates(callback);
                    break;
                case "readRFID":
                    readRFID(callback);
                    break;
                case "setEnableCoreLogs":
                    setEnableCoreLogs(callback, args(0));
                    break;
                case "addPKDCertificates":
                    addPKDCertificates(callback, args(0));
                    break;
                case "setCameraSessionIsPaused":
                    setCameraSessionIsPaused(callback, args(0));
                    break;
                case "getScenario":
                    getScenario(callback, args(0));
                    break;
                case "recognizeImages":
                    recognizeImages(callback, args(0));
                    break;
                case "showScannerWithCameraID":
                    showScannerWithCameraID(callback, args(0));
                    break;
                case "runAutoUpdate":
                    runAutoUpdate(callback, args(0));
                    break;
                case "setConfig":
                    setConfig(callback, args(0));
                    break;
                case "setRfidScenario":
                    setRfidScenario(callback, args(0));
                    break;
                case "initializeReader":
                    initializeReader(callback, args(0));
                    break;
                case "initializeReaderWithDatabasePath":
                    initializeReaderWithDatabasePath(callback, args(0), args(1));
                    break;
                case "prepareDatabase":
                    prepareDatabase(callback, args(0));
                    break;
                case "recognizeImage":
                    recognizeImage(callback, args(0));
                    break;
                case "recognizeImageFrame":
                    recognizeImageFrame(callback, args(0), args(1));
                    break;
                case "recognizeImageWithOpts":
                    recognizeImageWithOpts(callback, args(0), args(1));
                    break;
                case "recognizeVideoFrame":
                    recognizeVideoFrame(callback, args(0), args(1));
                    break;
                case "showScannerWithCameraIDAndOpts":
                    showScannerWithCameraIDAndOpts(callback, args(0), args(1));
                    break;
                case "recognizeImageWithImageInputParams":
                    recognizeImageWithImageInputParams(callback, args(0), args(1));
                    break;
                case "recognizeImageWithCameraMode":
                    recognizeImageWithCameraMode(callback, args(0), args(1));
                    break;
            }
        } catch (Exception ignored) {
        }
    }

    private void getAvailableScenarios(Callback callback) throws JSONException {
        callback.success(JSONConstructor.generateList(Instance().availableScenarios, JSONConstructor::generateDocumentReaderScenario, getContext()).toString());
    }

    private void getAPIVersion(Callback callback) {
        callback.success(Instance().version.api);
    }

    private void getCoreVersion(Callback callback) {
        callback.success(Instance().version.core);
    }

    private void getCoreMode(Callback callback) {
        callback.success(Instance().version.coreMode);
    }

    private void getDatabaseID(Callback callback) {
        callback.success(Instance().version.database.databaseID);
    }

    private void getDatabaseVersion(Callback callback) {
        callback.success(Instance().version.database.version);
    }

    private void getDatabaseDate(Callback callback) {
        callback.success(Instance().version.database.date);
    }

    private void getDatabaseDescription(Callback callback) {
        callback.success(Instance().version.database.databaseDescription);
    }

    private void getDatabaseCountriesNumber(Callback callback) {
        callback.success(Instance().version.database.countriesNumber);
    }

    private void getDatabaseDocumentsNumber(Callback callback) {
        callback.success(Instance().version.database.documentsNumber);
    }

    private void deinitializeReader(Callback callback) {
        Instance().deinitializeReader();
        callback.success();
    }

    private void isAuthenticatorAvailableForUse(Callback callback) {
        callback.success(Instance().isAuthenticatorAvailableForUse());
    }

    private void getConfig(Callback callback) throws JSONException {
        callback.success(RegulaConfig.getConfig(Instance(), getContext()).toString());
    }

    private void getRfidScenario(Callback callback) {
        callback.success(Instance().rfidScenario().toJson());
    }

    private void selectedScenario(Callback callback) throws JSONException {
        callback.success(JSONConstructor.generateDocumentReaderScenario(Instance().getScenario(Instance().processParams().getScenario())).toString());
    }

    private void getScenario(Callback callback, String scenario) throws JSONException {
        callback.success(JSONConstructor.generateDocumentReaderScenario(Instance().getScenario(scenario)).toString());
    }

    private void getLicenseExpiryDate(Callback callback) {
        if (Instance().license().getExpiryDate() == null)
            callback.error("null");
        else
            callback.success(Instance().license().getExpiryDate().toString());
    }

    private void getLicenseCountryFilter(Callback callback) {
        if (Instance().license().getCountryFilter() == null)
            callback.error("null");
        else
            callback.success(JSONConstructor.generateList(Instance().license().getCountryFilter()).toString());
    }

    private void licenseIsRfidAvailable(Callback callback) {
        callback.success(Instance().license().isRfidAvailable());
    }

    private void getDocumentReaderIsReady(Callback callback) {
        callback.success(Instance().getDocumentReaderIsReady());
    }

    private void getDocumentReaderStatus(Callback callback) {
        callback.success(Instance().getDocumentReaderStatus());
    }

    private void isRFIDAvailableForUse(Callback callback) {
        callback.success(Instance().isRFIDAvailableForUse());
    }

    private void initializeReader(Callback callback, Object license) {
        if (!Instance().getDocumentReaderIsReady())
            Instance().initializeReader(getContext(), Base64.decode(license.toString(), Base64.DEFAULT), getInitCompletion(callback));
        else
            callback.success("already initialized");
    }

    private void startNewSession(Callback callback) {
        Instance().startNewSession();
        callback.success();
    }

    private void startNewPage(Callback callback) {
        Instance().startNewPage();
        callback.success();
    }

    private void recognizeImageWithImageInputParams(Callback callback, String base64Image, final JSONObject params) throws JSONException {
        if (Instance().getDocumentReaderIsReady())
            Instance().recognizeImage(JSONConstructor.bitmapFromBase64(base64Image), new ImageInputParam(params.getInt("width"), params.getInt("height"), params.getInt("type")), getCompletion(callback));
        else
            callback.error("document reader not ready");
    }

    private void recognizeImageWithOpts(Callback callback, final JSONObject opts, String base64Image) throws JSONException {
        RegulaConfig.setConfig(Instance(), opts, getContext());
        recognizeImage(callback, base64Image);
    }

    private void recognizeImage(Callback callback, String base64Image) {
        if (Instance().getDocumentReaderIsReady())
            Instance().recognizeImage(JSONConstructor.bitmapFromBase64(base64Image), getCompletion(callback));
        else
            callback.error("document reader not ready");
    }

    private void recognizeImages(Callback callback, JSONArray base64Images) throws JSONException {
        if (Instance().getDocumentReaderIsReady()) {
            Bitmap[] images = new Bitmap[base64Images.length()];
            for (int i = 0; i < images.length; i++)
                images[i] = JSONConstructor.bitmapFromBase64(base64Images.getString(i));
            Instance().recognizeImages(images, getCompletion(callback));
        } else
            callback.error("document reader not ready");
    }

    private void removeDatabase(Callback callback) {
        callback.success(Instance().removeDatabase(getContext()));
    }

    private void cancelDBUpdate(Callback callback) {
        callback.success(Instance().cancelDBUpdate());
    }

    private void resetConfiguration(Callback callback) {
        Instance().resetConfiguration();
        callback.success();
    }

    private void setEnableCoreLogs(Callback callback, boolean enableLogs) {
        Instance().setEnableCoreLogs(enableLogs);
        callback.success();
    }

    private void addPKDCertificates(Callback callback, JSONArray certificatesJSON) throws JSONException {
        List<PKDCertificate> certificates = new ArrayList<>();
        for (int i = 0; i < certificatesJSON.length(); i++) {
            JSONObject certificate = certificatesJSON.getJSONObject(i);
            certificates.add(new PKDCertificate(JSONConstructor.byteArrayFromJson(certificate.getJSONArray("binaryData")), certificate.getInt("resourceType"), certificate.has("certificate") ? JSONConstructor.byteArrayFromJson(certificate.getJSONArray("privateKey")) : null));
        }
        Instance().addPKDCertificates(certificates);
        callback.success();
    }

    private void clearPKDCertificates(Callback callback) {
        Instance().clearPKDCertificates();
        callback.success();
    }

    private void recognizeImageFrame(Callback callback, String base64Image, final JSONObject opts) throws JSONException {
        if (Instance().getDocumentReaderIsReady())
            Instance().recognizeImageFrame(JSONConstructor.bitmapFromBase64(base64Image), new ImageInputParam(opts.getInt("width"), opts.getInt("height"), opts.getInt("type")), getCompletion(callback));
        else
            callback.error("document reader not ready");
    }

    private void recognizeVideoFrame(Callback callback, String byteString, final JSONObject opts) throws JSONException {
        if (Instance().getDocumentReaderIsReady())
            Instance().recognizeVideoFrame(byteString.getBytes(), new ImageInputParam(opts.getInt("width"), opts.getInt("height"), opts.getInt("type")), getCompletion(callback));
        else
            callback.error("document reader not ready");
    }

    private void showScannerWithCameraID(Callback callback, int cameraID) {
        if (Instance().getDocumentReaderIsReady())
            Instance().showScanner(getContext(), cameraID, getCompletion(callback));
        else
            callback.error("document reader not ready");
    }

    private void showScanner(Callback callback) {
        showScannerWithCameraID(callback, -1);
    }

    private void showScannerWithCameraIDAndOpts(Callback callback, int cameraID, final JSONObject opts) throws JSONException {
        if (Instance().getDocumentReaderIsReady()) {
            RegulaConfig.setConfig(Instance(), opts, getContext());
            Instance().showScanner(getContext(), cameraID, getCompletion(callback));
        } else
            callback.error("document reader not ready");
    }

    private void stopScanner(Callback callback) {
        Instance().stopScanner(getContext());
        callback.success();
    }

    private void startRFIDReader(Callback callback) {
        Instance().startRFIDReader(getContext(), getCompletion(callback));
    }

    private void stopRFIDReader(Callback callback) {
        Instance().stopRFIDReader(getContext());
        callback.success();
    }

    private void prepareDatabase(Callback callback, String dbID) {
        Instance().prepareDatabase(getContext(), dbID, getPrepareCompletion(callback));
    }

    private void runAutoUpdate(Callback callback, String dbID) {
        Instance().runAutoUpdate(getContext(), dbID, getPrepareCompletion(callback));
    }

    private void setRfidScenario(Callback callback, final JSONObject opts) throws JSONException {
        RegulaConfig.setRfidScenario(opts);
        callback.success();
    }

    private void getSessionLogFolder(Callback callback) {
        callback.success(Instance().processParams().sessionLogFolder);
    }

    private void setConfig(Callback callback, final JSONObject opts) throws JSONException {
        RegulaConfig.setConfig(Instance(), opts, getContext());
        callback.success();
    }

    private void setCameraSessionIsPaused(Callback callback, @SuppressWarnings("unused") boolean ignored) {
        callback.error("setCameraSessionIsPaused() is an ios-only method");
    }

    private void readRFID(Callback callback) {
        callback.error("readRFID() is an ios-only method");
    }

    private void getCameraSessionIsPaused(Callback callback) {
        callback.error("getCameraSessionIsPaused() is an ios-only method");
    }

    private void recognizeImageWithCameraMode(Callback callback, String base64, boolean mode) {
        callback.error("recognizeImageWithCameraMode() is an ios-only method");
    }

    private void initializeReaderWithDatabasePath(Callback callback, Object license, String path) {
        callback.error("initializeReaderWithDatabasePath() is an ios-only method");
    }

    private IDocumentReaderCompletion getCompletion(Callback callback) {
        return (action, results, error) -> {
            switch (action) {
                case DocReaderAction.COMPLETE:
                    callback.success(JSONConstructor.resultsToJsonObject(results, getContext()).toString());
                    break;
                case DocReaderAction.CANCEL:
                    callback.error("Canceled by user");
                    break;
                case DocReaderAction.ERROR:
                    callback.error("Error: " + error);
                    break;
            }
        };
    }

    private IDocumentReaderPrepareCompletion getPrepareCompletion(Callback callback) {
        return new IDocumentReaderPrepareCompletion() {
            @Override
            public void onPrepareProgressChanged(int progress) {
                if (progress != databaseDownloadProgress) {
                    sendProgress(progress);
                    databaseDownloadProgress = progress;
                }
            }

            @Override
            public void onPrepareCompleted(boolean status, Throwable error) {
                if (status)
                    callback.success("database prepared");
                else
                    callback.error("database preparation failed: " + error.toString());
            }
        };
    }

    private IDocumentReaderInitCompletion getInitCompletion(Callback callback) {
        return (success, error) -> {
            if (success)
                callback.success("init completed");
            else
                callback.error("Init failed:" + error);
        };
    }
}