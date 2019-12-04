///////////////////////////////////////////////////////////////////////////////
//
// Project:          OCR
// FileName:         OCRActivity.java
//
// Author:           Divya Amirtharaj
//
// History
//To Preview the Camera Image and captured OCR Text
//


package edu.harvard.cs50.divya.amirtharaj.ocr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import edu.harvard.cs50.divya.amirtharaj.ocr.ui.camera.CameraOverlay;
import edu.harvard.cs50.divya.amirtharaj.ocr.ui.camera.CameraSource;
import edu.harvard.cs50.divya.amirtharaj.ocr.ui.camera.CameraView;

public final class OCRActivity extends AppCompatActivity {

    private static final String TAG = "OCRActivity";

    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    public static final String IsTranslate = "IsTranslate";
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String FPS = "1";
    public static final String FontSize = "40";
    public static final String TextBlockObject = "String";
    public static final String Orientation = "Auto";
    public static final String Lang = "US English";

    private CameraSource mCameraSource;
    private CameraView mPreview;
    private CameraOverlay<OCRGraphic> mGraphicOverlay;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    private TextToSpeech tts;

    private boolean isTranslate;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        setContentView(R.layout.ocr_capture);

        mPreview = (CameraView) findViewById(R.id.preview);
        mGraphicOverlay = (CameraOverlay<OCRGraphic>) findViewById(R.id.graphicOverlay);

        isTranslate = getIntent().getBooleanExtra(IsTranslate, false);
        boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, true);
        boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
        String fps = getIntent().getStringExtra(FPS);
        String fontSize = getIntent().getStringExtra(FontSize);
        String orientation = getIntent().getStringExtra(Orientation);
        String lang = getIntent().getStringExtra(Lang);

        if (orientation.equals("Landscape")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation.equals("Portrait")) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
          else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        OCRSettings.stopTranslate();  //Initially don't translate, let it capture the text first
        OCRSettings.setLang(lang);
        OCRSettings.setFontSize(Float.parseFloat(fontSize));

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash, fps);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        if (isTranslate && checkInternetConnection()) {
            //If there is internet connection, get translate service and start translation:
            getTranslateService();
        }

        Snackbar.make(mGraphicOverlay, R.string.info_msg,
                Snackbar.LENGTH_LONG)
                .show();

        setLang(lang);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLang(String lang) {
        if (lang.equals("Spanish")) {
            OCRSettings.setLang( "es" );
            OCRSettings.setCountry( "MX" );
        }
        else if (lang.equals("French")) {
            OCRSettings.setLang( "fr" );
            OCRSettings.setCountry( "FR" );
        }
        else if (lang.equals("Japanese")) {
            OCRSettings.setLang( "ja" );
            OCRSettings.setCountry( "JP" );
        }
        else if (lang.equals("Chinese")) {
            OCRSettings.setLang( "zh" );
            OCRSettings.setCountry( "CN" );
        }
        else if (lang.equals("German")) {
            OCRSettings.setLang( "de" );
            OCRSettings.setCountry( "DE" );
        }
        else if (lang.equals("Italian")) {
            OCRSettings.setLang( "it" );
            OCRSettings.setCountry( "IT" );
        }
        else if (lang.equals("Korean")) {
            OCRSettings.setLang( "ko" );
            OCRSettings.setCountry( "KR" );
        }
        else if (lang.equals("Hindi")) {
            OCRSettings.setLang( "hi" );
            OCRSettings.setCountry( "IN" );
        }
        else if (lang.equals("Russian")) {
            OCRSettings.setLang( "ru" );
            OCRSettings.setCountry( "RU" );
        }
        else if (lang.equals("Arabic")) {
            OCRSettings.setLang( "ar" );
            OCRSettings.setCountry( "AE" );
        }
        else if (lang.equals("Greek")) {
            OCRSettings.setLang( "el" );
            OCRSettings.setCountry( "GR" );
        }
        else if (lang.equals("Hebrew")) {
            OCRSettings.setLang( "he" );
            OCRSettings.setCountry( "IL" );
        }
        else if (lang.equals("Vietnamese")) {
            OCRSettings.setLang( "vi" );
            OCRSettings.setCountry( "VN" );
        }
        else if (lang.equals("Tamil")) {
            OCRSettings.setLang( "ta" );
            OCRSettings.setCountry( "IN" );
        }
        else {
            OCRSettings.setLang( "en" );
            OCRSettings.setCountry( "US" );
        }
        final Locale locale = new Locale( OCRSettings.getLang(), OCRSettings.getCountry());
        TextToSpeech.OnInitListener listener =  new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(final int status) {  tts.setLanguage(locale); } };

        tts = new TextToSpeech(this.getApplicationContext(), listener);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        this.getApplicationContext().getResources().updateConfiguration(config, null);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            super.onBackPressed();
        }

        if (id == R.id.info) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage(R.string.info_msg);
            dlgAlert.setTitle("info");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash, String fps) {
        Context context = getApplicationContext();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OCRProcessor(mGraphicOverlay));

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
            }
        }

        mCameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(Float.parseFloat(fps))
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tts.stop();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);

            String fps = getIntent().getStringExtra(FPS);
            String fontSize = getIntent().getStringExtra(FontSize);

            OCRSettings.setFontSize(Float.parseFloat(fontSize));

            createCameraSource(autoFocus, useFlash, fps);
            return;
        }
        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OCR")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private boolean onTap(float rawX, float rawY) {
        String ocrText, speechText;

        tts.stop();
        ocrText = mGraphicOverlay.getAllText();

        if (ocrText != null && ocrText.length() > 0 ) {
            if(isTranslate && OCRSettings.isInternet()) {
                getTranslateService();
                OCRSettings.startTranslate();
                speechText = translate(ocrText);

            }
            else {
                speechText = ocrText;
            }
            tts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "DEFAULT");
        }
        else {
           if (tts.isSpeaking()) {
                tts.stop();
                OCRSettings.stopTranslate();
            }
           else {
                tts.speak("Sorry, no text found", TextToSpeech.QUEUE_FLUSH, null, "DEFAULT");
            }
        }
        return ocrText != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    public boolean checkInternetConnection() {

        boolean connected;
        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        OCRSettings.setInternet( connected );
        return connected;
    }

    public String translate(String inputText) {
        Translate translate=OCRSettings.getTranslate();
        Translation translation = translate.translate(inputText, Translate.TranslateOption.targetLanguage(OCRSettings.getLang()), Translate.TranslateOption.model("base"));
        return translation.getTranslatedText();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.cred)) {
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            OCRSettings.setTranslate( translateOptions.getService());
        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

}
