package com.example.bettercaptionddr;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CompassIndicator extends View {
    private float mHeading = 0;
    private float mInitialHeading = -1;
    private Paint mBGPaint, mIndicatorPaint;
    private static final float INDICATOR_WIDTH = 20.0f;
    private static final float MAX_DEGREES = 50.0f;

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
	return (mHeading * getWidth()) / MAX_DEGREES;
    }
    
    public void setDegree(float heading) {
	if (heading < 0 || heading > MAX_DEGREES) return;

	// TODO: have alex explain how this works
	// float upperBound = 180 + MAX_DEGREES / 2;
	// float lowerBound = 180 - MAX_DEGREES / 2;
	// if (mDegree > upperBound) mDegree = upperBound;
	// if (mDegree < lowerBound) mDegree = lowerBound;
	//
	// if (mInitialHeading == -1) mInitialHeading = heading;
	// float difference = heading - mInitialHeading;
	// float absDiff = Math.abs(difference);
	// if (absDiff > 180) absDiff = 360 - absDiff;
	// int sign = (difference < 0) ? -1 : 1;
	// mHeading = 180 - sign * absDiff;
	
	mHeading = heading;

	invalidate();
	requestLayout();
    }
    
    public int getBucket() {
	// ranges [0, MAX_DEGREES/3), [MAX_DEGREES/3, MAX_DEGREES*2/3), [MAX_DEGREES(2/3,MAX_DEGREES]
	if (mHeading < MAX_DEGREES/3) {
	    return 1;
	} else if (mHeading < 2*MAX_DEGREES/3) {
	    return 2;
	} else {
	    return 3;
	}
    }

}
