package com.example.bettercaptionddr;

import java.util.Queue;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.*;

public class MainActivity extends Activity {
    private ProgressBar left, center, right;
    private FeedbackView feedbackView;
    private CompassIndicator compassIndicator;
    private int numBeatsSoFar = 0;
    private ProgressBar currentBar;

    private Handler handler = new Handler();
    private static final int BEAT_INTERVAL = (int) ((float) 60 / 60 * 1000);
    
    public Handler mHandler = new Handler() {
	public void handleMessage(Message msg) {
	    if (currentBar == null) return;
	    currentBar.setProgress(currentBar.getProgress() + 5);
	}
    };
    
    public Handler mHandler2 = new Handler() {
	public void handleMessage(Message msg) {
	    if (currentBar == null) return;
	    currentBar.setProgress(0);
	}
    };

    private Runnable beatRunnable = new Runnable() {
	public void run() {
	    int which = (int) (Math.random() * 4);

	    which = 2;
	    mHandler2.obtainMessage().sendToTarget();
	    
	    switch (which) {
	    case 1:
		currentBar = left;
		break;
	    case 2:
		currentBar = center;
		break;
	    case 3:
		currentBar = right;
	    default:
		currentBar = null;
		break;
	    }
	    
	    mHandler2.obtainMessage().sendToTarget();

	    for (int i = 0; i < 20; i++) {
		mHandler.obtainMessage().sendToTarget();
		
		try {
		    Thread.sleep(10);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.ddr_layout);

	feedbackView = (FeedbackView) findViewById(R.id.feedbackView);
	compassIndicator = (CompassIndicator) findViewById(R.id.compassIndicator);
	left = (ProgressBar) findViewById(R.id.progressBar1);
	center = (ProgressBar) findViewById(R.id.progressBar2);
	right = (ProgressBar) findViewById(R.id.progressBar3);

	(new Thread(beatRunnable)).start();

	// left.setProgress(10);
	// center.setProgress(80);
	//
	// right.getProgressDrawable().setColorFilter(Color.YELLOW,
	// Mode.MULTIPLY);
	// right.setProgress(100);

	// compassIndicator.setDegree(-45.5f);
	// feedbackView.setStateCorrect();
    }
}
