///////////////////////////////////////////////////////////////////////////////
//
// Project:          OCR
// FileName:         MainActivity.java
//
// Author:           Divya Amirtharaj
//
// Description

package edu.harvard.cs50.divya.amirtharaj.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;


public class MainActivity extends Activity implements View.OnClickListener {

    private CompoundButton isTranslate;
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private Spinner fps, fontSize, orientation, lang;
    private TextView statusMessage;

    private static final int RC_OCR_CAPTURE = 9003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        statusMessage = findViewById(R.id.status_message);
        isTranslate = findViewById(R.id.is_translate);
        autoFocus = findViewById(R.id.auto_focus);
        useFlash = findViewById(R.id.use_flash);

        fps = findViewById(R.id.fps);
        fontSize = findViewById(R.id.font_size);
        orientation = findViewById(R.id.orientation);
        lang = findViewById(R.id.lang);

        findViewById(R.id.read_text).setOnClickListener(this);
        Intent intent = new Intent(this, OCRActivity.class);
        setSettings(intent);
    }

    private void setSettings(Intent intent) {
        intent.putExtra( OCRActivity.IsTranslate, isTranslate.isChecked());
        intent.putExtra( OCRActivity.AutoFocus, autoFocus.isChecked());
        intent.putExtra( OCRActivity.UseFlash, useFlash.isChecked());
        intent.putExtra( OCRActivity.FPS, String.valueOf(fps.getSelectedItem()));
        intent.putExtra( OCRActivity.FontSize, String.valueOf(fontSize.getSelectedItem()));
        intent.putExtra( OCRActivity.Orientation, String.valueOf(orientation.getSelectedItem()));
        intent.putExtra( OCRActivity.Lang, String.valueOf(lang.getSelectedItem()));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            Intent intent = new Intent(this, OCRActivity.class);
            setSettings(intent);
            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra( OCRActivity.TextBlockObject);
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
