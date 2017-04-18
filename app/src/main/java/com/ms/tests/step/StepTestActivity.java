package com.ms.tests.step;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ms.tests.R;

import java.util.LinkedList;

public class StepTestActivity extends AppCompatActivity {
    public static final String STEP_TEST_LENGTH = "step_test_length";
    public static final String STEP_TEST_TARGET = "step_test_target_steps";
    public static final String STEP_TEST_MOVING_VELOCITY = "step_test_moving_veloc";
    public static final String STEP_TEST_ACCUMULATED_VELOCITY = "step_test_accum_veloc";

    private static final String TAG = StepTestActivity.class.getSimpleName();
    private static final int MESSAGE_TEST_FINISHED = 0x1;
    private static final long TARGET_STEPS_TIME = 10000000000l;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mUpdateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case MESSAGE_TEST_FINISHED:
                    finishTest();
                    break;
            }
        }
    };

    private TextView mStepCount;
    private SoundPool mSoundPool;

    private LinkedList<Long> mLastStepDeltas;

    private long mStartTime;
    private long mLastStepTime = 0;
    private long mEndTime;

    private boolean mBeepLoaded;
    private int mBeepId;
    private int mHornId;
    private double velocity;
    private long mLastTimestamp = -1;

    private double prevAccelX = 0, prevAccelY = 0;

    private Vector mAverageVelocity = new Vector(-1, -1);
    private class Vector {
        double x;
        double y;

        public Vector(double x, double y){
            this.x = x;
            this.y = y;
        }

        double magnitude(){
            return Math.sqrt(x*x + y*y);
        }
    }

    private Vector mPreviousAccel = new Vector(-1, -1);

    private SensorEventListener mStepListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(mLastTimestamp == -1) {
                mLastTimestamp = event.timestamp;
                mAverageVelocity.x = 0;
                mAverageVelocity.y = 0;

                mPreviousAccel.x = 0;
                mPreviousAccel.y = 0;
                return;
            }

            double accelX, accelY, distX = 0, distY = 0, velX = mAverageVelocity.x, velY = mAverageVelocity.y;
            double accelKFactor = 0.1;

            accelX = event.values[0];
            accelY = event.values[1];

            accelX = (accelX * accelKFactor) + prevAccelX * (1 - accelKFactor);
            accelY = (accelY * accelKFactor) + prevAccelY * (1 - accelKFactor);

            double interval = (event.timestamp - mLastTimestamp) * (1.0f / 1000000000.0f);

            mAverageVelocity.x += accelX * interval;
            mAverageVelocity.y += accelY * interval;

            distX += mAverageVelocity.x*interval + velX;
            distY += mAverageVelocity.y*interval + velY;

            if(event.timestamp - mLastTimestamp < TARGET_STEPS_TIME) {
                //int result = mSoundPool.play(mBeepId, 1, 1, 1, 0, 6);
            }else{
                mEndTime = event.timestamp;
                int result = mSoundPool.play(mHornId, 1, 1, 1, 0, 1.5f);
                Message m = Message.obtain(mUpdateHandler, MESSAGE_TEST_FINISHED);
                mUpdateHandler.sendMessage(m);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d(TAG, "Accuracy changed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        mHandlerThread = new HandlerThread("step_detector");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSoundPool = new SoundPool.Builder().build();//.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build()).setMaxStreams(1).build();
        mBeepId = mSoundPool.load(this, R.raw.beep, 1);
        mHornId = mSoundPool.load(this, R.raw.horn, 1);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(sampleId == mBeepId){
                    mBeepLoaded = true;
                }else if(mBeepLoaded && sampleId == mHornId){
                    Log.d(TAG, "LOADED HORN SOUND");
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

                    mSoundPool.play(mHornId, 1, 1, 1, 0, 1.5f);
                    mSensorManager.registerListener(mStepListener, mSensor, SensorManager.SENSOR_DELAY_GAME, mHandler);
                }
            }
        });

        mStepCount = (TextView) findViewById(R.id.step_count);
    }

    private void finishTest(){
        mSensorManager.unregisterListener(mStepListener);
        mHandlerThread.quit();

        Intent i = new Intent(this, StepTestResultActivity.class);
        i.putExtra(STEP_TEST_ACCUMULATED_VELOCITY, mAverageVelocity.magnitude()/100);


        startActivity(i);
    }
}