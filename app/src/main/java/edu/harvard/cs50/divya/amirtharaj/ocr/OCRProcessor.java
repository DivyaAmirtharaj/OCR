///////////////////////////////////////////////////////////////////////////////
//
// Project:          OCR
// FileName:         OCRProcessor.java
//
// Author:           Divya Amirtharaj
//
//To Preview the Camera Image and captured OCR Text
//
package edu.harvard.cs50.divya.amirtharaj.ocr;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import edu.harvard.cs50.divya.amirtharaj.ocr.ui.camera.CameraOverlay;


public class OCRProcessor implements Detector.Processor<TextBlock> {

    private CameraOverlay<OCRGraphic> mGraphicOverlay;

    OCRProcessor(CameraOverlay<OCRGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            OCRGraphic graphic = new OCRGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
