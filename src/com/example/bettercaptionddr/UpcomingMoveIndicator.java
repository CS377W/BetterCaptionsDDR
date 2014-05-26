package com.example.bettercaptionddr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.Shader;

public class UpcomingMoveIndicator extends View {
    private int progress = 0;
    private Paint mBGPaint, mProgressPaint;
    private int mHeight = 0;

    public UpcomingMoveIndicator(Context context) {
	super(context);
	init();
    }

    public UpcomingMoveIndicator(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public UpcomingMoveIndicator(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    private void init() {
	mBGPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mBGPaint.setColor(Color.BLACK);
	mBGPaint.setStyle(Paint.Style.FILL);

	mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private LinearGradient gradient;
    private boolean requiresMove;

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);

	if (mHeight != getHeight()) {
	    mHeight = getHeight();
	    updateGradient();
	}

	canvas.drawRect(0, 0, getWidth(), getHeight(), mBGPaint);

	float height = getHeight() * progress/100.0f;
	canvas.drawRect(0, 0, getWidth(), height, mProgressPaint);
    }

    private void updateGradient() {
	gradient = new LinearGradient(0, 0, 0, mHeight, Color.BLACK, (requiresMove ? Color.YELLOW : Color.WHITE), Shader.TileMode.MIRROR);
	mProgressPaint.setShader(gradient);
    }

    public void setRequiresMove(boolean requiresMove) {
	this.requiresMove = requiresMove;
	updateGradient();
	invalidate();
	requestLayout();
    }
    
    public boolean getRequiresMove() {
	return requiresMove;
    }

    public void setProgress(int progress) {
	this.progress = progress;
	invalidate();
	requestLayout();
    }

    public int getProgress() {
	return progress;
    }

    public boolean increment(int by) {
	if (progress == 100) return false;
	setProgress(Math.min(progress + by, 100));
	return true;
    }

    public void reset() {
	setProgress(0);
    }
}
