///////////////////////////////////////////////////////////////////////////////
//
// Project:          OCR
// FileName:         OCRText.java
//
// Author:           Divya Amirtharaj
//
//To Preview the Camera Image and captured OCR Text
//
//////////////////////////// 80 columns wide //////////////////////////////////

package edu.harvard.cs50.divya.amirtharaj.ocr;

public class OCRText implements Comparable<OCRText> {
    private float x, y;
    private String ocrText;

    public OCRText(float top, float left, String text){
        y = top;
        x = left;
        ocrText = text;
    }

    public float getX(){
        return x;
    }

    public float getY() {
        return y;
    }

    public String getOcrText(){
        return ocrText;
    }

    @Override
    public int compareTo(OCRText another) {
        int result = Float.valueOf(getY()).compareTo(Float.valueOf(another.getY()));
        if (result != 0) {
            return result;
        }
        else {
            return Float.valueOf(getX()).compareTo(Float.valueOf(another.getX()));
        }
    }
}
