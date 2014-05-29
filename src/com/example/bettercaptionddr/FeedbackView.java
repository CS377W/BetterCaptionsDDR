package com.example.bettercaptionddr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FeedbackView extends View {
    private enum State { NEUTRAL, CORRECT, INCORRECT };
    private State mState = State.INCORRECT;
    private int pointModifier = 1;
    private int mScore = 0;
    private Paint mPaint;
    private Paint mScorePaint;

    public FeedbackView(Context context) {
	super(context);
	init();
    }

    public FeedbackView(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public FeedbackView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    private void init() {
	mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mScorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mScorePaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    private void setUpColors() {
	if (mState == State.INCORRECT) {
	    mPaint.setColor(0xaaee2222);
	} else if (mState == State.CORRECT) {
	    mPaint.setColor(0xaa22cc22);
	} else {
	    mPaint.setColor(0x44ffffff);
	}
	
	mScorePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	setUpColors();
	canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
	if (getHeight() != mScorePaint.getTextSize()) {
	    Log.d("CS377W", " mScorePaint.setTextSize");
	    mScorePaint.setTextSize(getHeight());
	}
	canvas.drawText(mScore + "", 0,  getHeight(), mScorePaint);
    }

    public void setStateCorrect() {
	if(pointModifier <= 1) pointModifier = 1;
	mState = State.CORRECT;
	mScore += pointModifier;
	pointModifier++; //COMBOOOOOOOO
	invalidate();
	requestLayout();
    }

    public boolean isStateCorrect() {
	return mState == State.CORRECT;
    }

    public void setStateIncorrect() {
	if(pointModifier >= 1) pointModifier = -1;
	mState = State.INCORRECT;
	mScore += pointModifier;
	pointModifier--; //NEGATIVE COMBOOOOO
	invalidate();
	requestLayout();
    }

    public boolean isStateIncorrect() {
	return mState == State.INCORRECT;
    }

    public void setStateNeutral() {
	mState = State.NEUTRAL;
	invalidate();
	requestLayout();
    }

    public boolean isStateNeutral() {
	return mState == State.NEUTRAL;
    }
}
