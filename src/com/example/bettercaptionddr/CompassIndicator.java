package com.example.bettercaptionddr;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CompassIndicator extends View {
    private float mDegree = 0;
    private Paint mBGPaint, mIndicatorPaint;
    private static final float INDICATOR_WIDTH = 20.0f;

    public CompassIndicator(Context context) {
	super(context);
	init();
    }

    public CompassIndicator(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public CompassIndicator(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }
    
    private void init() {
	mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mBGPaint.setColor(Color.BLACK);
	
	mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mIndicatorPaint.setColor(0xaaffffff);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBGPaint);
        
        float xLoc = degreeToXLoc();
        float left = xLoc - INDICATOR_WIDTH/2;
        canvas.drawRect(left, 0, left + INDICATOR_WIDTH, getHeight(), mIndicatorPaint);
    }
    
    private float degreeToXLoc() {
	float transDegree = mDegree + 180;
	return (transDegree * getWidth()) / 360.0f;
    }
    
    public void setDegree(float degree) {
	if (degree < -180 || degree > 180) return;
	mDegree = degree;
	invalidate();
	requestLayout();
    }
}
