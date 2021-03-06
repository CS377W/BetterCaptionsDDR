package com.example.bettercaptionddr;

import java.util.Date;

import com.google.android.glass.sample.compass.OrientationManager;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MainActivity extends BaseActivity implements SensorEventListener {
    private UpcomingMoveIndicator left, center, right;
    private FeedbackView feedbackView;
    private CompassIndicator compassIndicator;
    private OrientationManager mOrientationManager;

    private Handler handler = new Handler();
    private static int BEAT_INTERVAL;

    private long timeOfBarHit = -1;

    private static final int INCREMENT_BY = 20;
    private class UpcomingMoveRunnable implements Runnable {
	public UpcomingMoveIndicator mBar;
	private Handler upcomingMoveHandler = new Handler();

	@Override
	public void run() {
	    if (mBar == null) return;

	    // At this point, the bar has reached the maximum point
	    if (!mBar.increment(INCREMENT_BY)) {
		timeOfBarHit = (new Date()).getTime();

		if (mBar.getRequiresMove()) {
		    handleOnDirAndMove(mBar);
		} else {
		    handleOnDir(mBar);
		}

		mBar.reset();
		return;
	    }

	    upcomingMoveHandler.postDelayed(this, BEAT_INTERVAL/(100/INCREMENT_BY));
	}
    }
    
    @Override
    protected void handleOnScroll(int direction) {
	if (direction < 0) {
	    BEAT_INTERVAL /= 1.5;
	    MOVE_TOLERANCE = BEAT_INTERVAL/3;
	} else {
	    BEAT_INTERVAL *= 1.5;
	    MOVE_TOLERANCE = BEAT_INTERVAL/3;
	}
    }

    private Handler moveHandler = new Handler();
    private static int MOVE_TOLERANCE;

    private void handleOnDirAndMove(final UpcomingMoveIndicator mBar) {
	if (timeOfBarHit == -1) return;

	moveHandler.postDelayed(new Runnable() {
	    @Override
	    public void run() {
		// Clear previous
		if (!feedbackView.isStateNeutral()) {
		    feedbackView.setStateNeutral();
		}
		
		int dirBucket = compassIndicator.getBucket();
		boolean isBucketMatch = (dirBucket == 1 && mBar == left)
			|| (dirBucket == 2 && mBar == center)
			|| (dirBucket == 3 && mBar == right);
		
		Log.d("CS377W", "isBucketMatch:" + isBucketMatch + " " + Math.abs(timeOfAccelerometerMove - timeOfBarHit) + "");
		if (isBucketMatch
			&& timeOfAccelerometerMove != -1
			&& Math.abs(timeOfAccelerometerMove - timeOfBarHit) < MOVE_TOLERANCE) {
		    // We got a hit!
		    feedbackView.setStateCorrect();
		} else {
		    // We missed it.
		    feedbackView.setStateIncorrect();
		}
	    }
	}, 0);
    }

    private void handleOnDir(final UpcomingMoveIndicator mBar) {
	if (timeOfBarHit == -1) return;

	moveHandler.postDelayed(new Runnable() {
	    @Override
	    public void run() {
		// Clear previous
		if (!feedbackView.isStateNeutral()) {
		    feedbackView.setStateNeutral();
		}
		
		int dirBucket = compassIndicator.getBucket();
		boolean isBucketMatch = (dirBucket == 1 && mBar == left)
			|| (dirBucket == 2 && mBar == center)
			|| (dirBucket == 3 && mBar == right);
		
		Log.d("CS377W", "isBucketMatch:" + isBucketMatch);
		if (isBucketMatch) {
		    // We got a hit!
		    feedbackView.setStateCorrect();
		} else {
		    // We missed it.
		    feedbackView.setStateIncorrect();
		}
	    }
	}, 0);
    }
    
    private boolean beatIsRunning = true;

    private Runnable beatRunnable = new Runnable() {
	public void run() {
	    if (!beatIsRunning) return;
	    
	    int which = (int) (Math.random() * 4);
	    UpcomingMoveIndicator currentBar = which == 1 ? left : which == 2 ? center : which == 3 ? right : null;
	    boolean requiresMove = Math.random() >= 0.5;
	    if (currentBar != null) {
		currentBar.setRequiresMove(requiresMove);
		UpcomingMoveRunnable upcomingMoveRunnable = new UpcomingMoveRunnable();
		upcomingMoveRunnable.mBar = currentBar;
		upcomingMoveRunnable.run();
	    }

	    handler.postDelayed(this, BEAT_INTERVAL);
	}
    };

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.ddr_layout);

	feedbackView = (FeedbackView) findViewById(R.id.feedbackView);
	compassIndicator = (CompassIndicator) findViewById(R.id.compassIndicator);
	left = (UpcomingMoveIndicator) findViewById(R.id.progressBar1);
	center = (UpcomingMoveIndicator) findViewById(R.id.progressBar2);
	right = (UpcomingMoveIndicator) findViewById(R.id.progressBar3);

	setUpSensors();
	startMediaPlayer();
	
	BEAT_INTERVAL = (int) (60 / 128.0f * 1000) * 3;
	MOVE_TOLERANCE = BEAT_INTERVAL/3;
	
	beatIsRunning = true;
	beatRunnable.run();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	mediaPlayer.stop();
	mOrientationManager.removeOnChangedListener(mCompassListener);
        mOrientationManager.stop();
        beatIsRunning = false;
    }

    @Override
    protected void onPause() {
	super.onPause();
	mediaPlayer.stop();
	beatIsRunning = false;
    }

    // @Override
    // protected void onResume() {
    // super.onResume();
    // startMediaPlayer();
    // beatIsRunning = true;
    // beatRunnable.run();
    // }

    private void startMediaPlayer() {
	if (mediaPlayer != null) {
	    mediaPlayer.stop();
	    mediaPlayer = null;
	}

	mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bad);
	mediaPlayer.setLooping(true);
	mediaPlayer.start();
    }

    // === SENSORS ===

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private void setUpSensors() {
	sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

	LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mOrientationManager = new OrientationManager(sensorManager, locationManager);
        mOrientationManager.addOnChangedListener(mCompassListener);
        mOrientationManager.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	// can be safely ignored for this demo
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    handleAccelerometerEvent(event);
	}
    }

    private boolean hasInitAccel = false;
    private float lastX, lastY, lastZ;
    private final float NOISE = (float) 2.0;
    private static final int MOVE_TRESHOLD = 3;
    private long timeOfAccelerometerMove = -1;

    private void handleAccelerometerEvent(SensorEvent event) {
	float[] gravity = event.values.clone();

	if (gravity != null) {
	    float x = gravity[0];
	    float y = gravity[1];
	    float z = gravity[2];

	    if (!hasInitAccel) {
		lastX = x;
		lastY = y;
		lastZ = z;
		hasInitAccel = true;
	    } else {
		float deltaX = Math.abs(lastX - x);
		float deltaY = Math.abs(lastY - y);
		float deltaZ = Math.abs(lastZ - z);

		if (deltaX < NOISE) deltaX = 0.0f;
		if (deltaY < NOISE) deltaY = 0.0f;
		if (deltaZ < NOISE) deltaZ = 0.0f;

		lastX = x;
		lastY = y;
		lastZ = z;

		//Log.d("CS377W", "X:"+deltaX+" Y:"+deltaY+" Z:"+deltaZ);
		if (deltaX >= MOVE_TRESHOLD || deltaY >= MOVE_TRESHOLD || deltaZ >= MOVE_TRESHOLD) {
		    timeOfAccelerometerMove = (new Date()).getTime();
		}
	    }
	}
    }
    
    private final OrientationManager.OnChangedListener mCompassListener = new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
            compassIndicator.setDegree(orientationManager.getHeading());
        }

        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
        }

        @Override
        public void onAccuracyChanged(OrientationManager orientationManager) {
        }
    };
}
