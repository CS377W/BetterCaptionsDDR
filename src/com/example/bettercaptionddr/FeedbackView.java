package com.example.bettercaptionddr;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FeedbackView extends View {
    private int mState;
    private Paint mPaint;

    public FeedbackView(Context context) {
	super(context);
	init();
    }

    public FeedbackView(Context context, AttributeSet attrs) {
	super(context, attrs);

	TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
		R.styleable.FeedbackView, 0, 0);

	try {
	    mState = a.getInteger(R.styleable.FeedbackView_state, 0);
	    init();
	} finally {
	    a.recycle();
	}
    }

    public FeedbackView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }
    
    private void init() {
	mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }
    
    private void setUpColors() {
	if (mState == 1) // incorrect
	    mPaint.setColor(0xffee2222);
	else if (mState == 2) // correct
	    mPaint.setColor(0xff22cc22);
	else
	    mPaint.setColor(0x44ffffff);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	setUpColors();
	canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

    public void setStateCorrect() {
	mState = 2;
	invalidate();
	requestLayout();
    }

    public void setStateIncorrect() {
	mState = 1;
	invalidate();
	requestLayout();
    }

    public void setStateNeutral() {
	mState = 0;
	invalidate();
	requestLayout();
    }

}
