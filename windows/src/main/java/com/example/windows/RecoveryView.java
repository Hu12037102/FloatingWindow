package com.example.windows;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RecoveryView extends View {

    private RectF mArcRectF;
    private Paint mArcPaint;
    private Paint mTextPaint;
    private static final String DRAW_TEXT = "取消悬浮";
    private Rect mTextRect;
    private static final int TEXT_MARGIN = 80;
    private Bitmap mBitmap;

    public RecoveryView(Context context) {
        this(context,null);
    }

    public RecoveryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initArcPaint();
        initTextPaint();
    }

    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setTextSize(36);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWhlite));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
    }

    private void initArcPaint() {
        mArcPaint = new Paint();
        mArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorFFF3776E));
        mArcPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasureSize(DispalyUtils.viewSize(getContext(), 200), widthMeasureSpec);
        int height = getMeasureSize(DispalyUtils.viewSize(getContext(), 200), heightMeasureSpec);
        if (width > height) {
            setMeasuredDimension(height, height);
        } else {
            setMeasuredDimension(width, width);
        }
    }


    private int getMeasureSize(int defaultSize, int measureSpec) {
        int size;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        switch (measureMode) {
            case MeasureSpec.AT_MOST:
                size = measureSize;
                break;
            case MeasureSpec.EXACTLY:
                size = measureSize;
                break;
            default:
                size = defaultSize;
                break;
        }
        return size;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcRectF = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mTextRect = new Rect();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.image_cancel);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
         // super.onDraw(canvas);

        canvas.drawArc(mArcRectF, 180, 90, true, mArcPaint);
        canvas.drawText(RecoveryView.DRAW_TEXT, getMeasuredWidth() / 2 - mTextPaint.measureText(RecoveryView.DRAW_TEXT) - DispalyUtils.viewSize(getContext(),TEXT_MARGIN), getMeasuredHeight() / 2 - TEXT_MARGIN, mTextPaint);
        mTextPaint.getTextBounds(RecoveryView.DRAW_TEXT, 0, RecoveryView.DRAW_TEXT.length() - 1, mTextRect);
        canvas.drawBitmap(mBitmap, getMeasuredWidth() / 2 - mTextPaint.measureText(RecoveryView.DRAW_TEXT) - DispalyUtils.viewSize(getContext(),TEXT_MARGIN),
                getMeasuredHeight() / 2-mTextRect.height()-mBitmap.getHeight()-DispalyUtils.viewSize(getContext(),100), mArcPaint);
        mTextRect.height();
        Log.w("onDraw--", mTextPaint.measureText("取消悬浮") + "--");
    }
}
