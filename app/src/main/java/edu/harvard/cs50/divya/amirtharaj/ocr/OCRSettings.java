///////////////////////////////////////////////////////////////////////////////
//
// Project:          OCR
// FileName:         OCRSettings.java
//
// Author:           Divya Amirtharaj
//
// History
// Create Date:      9/1/2016
//To Preview the Camera Image and captured OCR Text
//
//////////////////////////// 80 columns wide //////////////////////////////////

package edu.harvard.cs50.divya.amirtharaj.ocr;


import com.google.cloud.translate.Translate;

public class OCRSettings {
    private static String mLang;
    private static String mCountry;
    private static Translate mTranslate;
    private static boolean mINTERNET = false;
    private static boolean mTRANSLATE = false;

    public static String getLang() {
        return mLang;
    }
    public static void setLang(String lang) {
        mLang = lang;
    }

    public static void setCountry(String country)
    {
        mCountry = country;
    }
    public static String getCountry()
    {
        return mCountry;
    }

    public static void setTranslate(Translate translate)
    {
        mTranslate = translate;
    }
    public static Translate getTranslate()
    {
        return mTranslate;
    }

    public static float getFontSize() {
        return mfontSize;
    }
    public static void setFontSize(float fontSize) {
        mfontSize = fontSize;
    }

    public static void startTranslate(){ mTRANSLATE=true;}
    public static void stopTranslate(){ mTRANSLATE=false;}
    public static boolean isTRANSLATE() { return mTRANSLATE;}

    public static void setInternet(boolean internet){ mINTERNET=internet;}
    public static boolean isInternet(){ return mINTERNET;}

    public static float mfontSize;

}
