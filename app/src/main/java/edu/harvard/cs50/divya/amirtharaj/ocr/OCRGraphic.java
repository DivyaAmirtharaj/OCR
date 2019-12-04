///////////////////////////////////////////////////////////////////////////////
//
// Project:          OCR
// FileName:         OCRGraphic.java
//
// Author:           Divya Amirtharaj
//
//To Preview the Camera Image and captured OCR Text
//


package edu.harvard.cs50.divya.amirtharaj.ocr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;

import java.util.List;

import edu.harvard.cs50.divya.amirtharaj.ocr.ui.camera.CameraOverlay;

public class OCRGraphic extends CameraOverlay.Graphic {

    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final TextBlock mText;

    OCRGraphic(CameraOverlay overlay, TextBlock text) {
        super(overlay);

        mText = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize( OCRSettings.getFontSize());
        }

//        getTranslateService();
        postInvalidate();
    }


    public TextBlock getTextBlock() {
        return mText;
    }


    @Override
    public void draw(Canvas canvas) {
        TextBlock text = mText;
        if (text == null) {
            return;
        }

        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        float fontSize;
        fontSize =  OCRSettings.getFontSize();

        if (mText.getValue().length() > 0 && rect.height() >0 && fontSize == 0) {
            fontSize = (rect.height()/38) * rect.width() / mText.getValue().length();
            if (fontSize < 24) {
                fontSize = 24;
            }
            else if (fontSize > 76) {
                fontSize = 76;
            }
        }
        sTextPaint.setTextSize(fontSize);
        sTextPaint.setColor(Color.YELLOW);

        List<? extends Text> textComponents = text.getComponents();
        for(Text currentText : textComponents) {
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            String lang = OCRSettings.getLang();
            String translatedText;
            if (OCRSettings.isTRANSLATE() && OCRSettings.isInternet()) {
                translatedText = translate(currentText.getValue());
            }
            else {
                translatedText = currentText.getValue();
            }
            canvas.drawText(translatedText, left, bottom, sTextPaint);
///           canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
        }
    }
    public String translate(String inputText) {
        //Get input text to be translated:
        if(OCRSettings.isTRANSLATE() && OCRSettings.isInternet()){
            Translate translate=OCRSettings.getTranslate();
            Translation translation = translate.translate(inputText, Translate.TranslateOption.targetLanguage(OCRSettings.getLang()), Translate.TranslateOption.model("base"));
            String outputText = translation.getTranslatedText();
            return outputText;
        }
        else {
            return inputText;
        }
    }

}
