///////////////////////////////////////////////////////////////////////////////
//
// Project:          myVision
// FileName:         CameraOverlay.java
//
// Author:           Divya Amirtharaj

// To identify the right location for the captured OCR Text

package edu.harvard.cs50.divya.amirtharaj.ocr.ui.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import edu.harvard.cs50.divya.amirtharaj.ocr.OCRGraphic;
import edu.harvard.cs50.divya.amirtharaj.ocr.OCRText;

public class CameraOverlay<T extends CameraOverlay.Graphic> extends View {
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private Set<T> mGraphics = new HashSet<>();

    static int mLocation = 0;

    public static abstract class Graphic {
        private CameraOverlay mOverlay;

        public Graphic(CameraOverlay overlay) {
            mOverlay = overlay;
        }

        public abstract void draw(Canvas canvas);


        float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

        protected float translateX(float x) {
            if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        protected float translateY(float y) {
            return scaleY(y);
        }

        protected void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    public CameraOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();

    }

    public void add(T graphic) {
        synchronized (mLock) {
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }
        }
    }

    public String getAllText() {
        TextBlock text = null;
        OCRGraphic ocrGraphic;
        String ocrText = "";

        ArrayList<OCRText> OCRTextArray = new ArrayList<>();

        for (T graphic : mGraphics) {
            ocrGraphic = (OCRGraphic) graphic;
            text = ocrGraphic.getTextBlock();
            RectF rect = new RectF(text.getBoundingBox());
            OCRTextArray.add(new OCRText(rect.top, rect.left, text.getValue()));
        }

        Collections.sort( OCRTextArray, new Comparator<OCRText>() {
            @Override
            public int compare(OCRText lhs, OCRText rhs) {
                int result = Float.valueOf(lhs.getY()).compareTo(Float.valueOf(rhs.getY()));
                if (result != 0) {
                    return result;
                }
                else {
                    return Float.valueOf(lhs.getX()).compareTo(Float.valueOf(rhs.getX()));
                }
            }} );

        for (OCRText mOCRText : OCRTextArray) {
            String sText = mOCRText.getOcrText();
            ocrText = ocrText.concat(sText);
            Log.i("DATA_READ",sText);
        }
        return ocrText;
    }
}
