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

    private static final String TAG = StepTestActivity.class.getSimpleName();
    private static final int MESSAGE_UPDATE_STEP_COUNT = 0x1;
    private static final int TARGET_STEPS = 25;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Handler mUpdateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case MESSAGE_UPDATE_STEP_COUNT:
                    mStepCount.setText("Steps: " + mSteps);

                    if(mSteps >= TARGET_STEPS){
                        finishTest();
                    }
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
    private int mSteps;

    private SensorEventListener mStepListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long mCurrLastTime = System.currentTimeMillis();
            mLastStepDeltas.add(mCurrLastTime - mLastStepTime);
            mLastStepTime = mCurrLastTime;

            mSteps += event.values.length;

            Message m = new Message();
            m.what = MESSAGE_UPDATE_STEP_COUNT;

            if(mSteps < TARGET_STEPS) {
                int result = mSoundPool.play(mBeepId, 1, 1, 1, 0, 6);
            }else{
                mEndTime = mCurrLastTime;
                int result = mSoundPool.play(mHornId, 1, 1, 1, 0, 1.5f);
            }
            mUpdateHandler.sendMessage(m);

            Log.i(TAG, "New step detected by STEP_DETECTOR sensor. Total step count: " + mSteps);
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
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

                    mSoundPool.play(mHornId, 1, 1, 1, 0, 1.5f);
                    mStartTime = System.currentTimeMillis();
                    mLastStepTime = mStartTime;
                    mLastStepDeltas = new LinkedList<>();
                    mSensorManager.registerListener(mStepListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST, mHandler);
                }
            }
        });

        mStepCount = (TextView) findViewById(R.id.step_count);
    }

    private void finishTest(){
        Intent i = new Intent(this, StepTestResultActivity.class);
        double length = (mEndTime - mStartTime) / 1000;
        i.putExtra(STEP_TEST_LENGTH, length);
        i.putExtra(STEP_TEST_TARGET, TARGET_STEPS);

        mSensorManager.unregisterListener(mStepListener);
        mHandlerThread.quit();

        startActivity(i);
    }
}
