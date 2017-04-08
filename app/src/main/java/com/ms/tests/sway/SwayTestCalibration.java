package com.ms.tests.sway;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ms.tests.R;

import java.util.Timer;
import java.util.TimerTask;

public class SwayTestCalibration extends AppCompatActivity implements SensorEventListener{

    public static final String RESULT_IMAGE_URI = "result_image_uri";
    private static final int REQUEST_EXTERNAL_STORAGE = 0x2;
    private static final double MIN_THRESHOLD = 0.5;
    private final static String TAG = "SwayTestActivity";

    private float[] mGravity;
    private float[] mGeomagnetic;

    private SwayTiltView mSwayView;
    private TextView mDegreeView;

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private Sensor mMagnet;

    int[] pitchrolls;
    public int yawDeg;
    public int pitchDeg;
    public int rollDeg;

    int initYaw, initRoll;
    boolean inited;

    private MediaPlayer beepSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sway_test_calibration);
        mDegreeView = (TextView) findViewById(R.id.degree_view);
        mSwayView = (SwayTiltView) findViewById(R.id.tilt_view);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccel == null)
            Log.d(TAG, "accelerometer is null");

        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnet == null)
            Log.d(TAG, "magnetometer is null");


        mSwayView.startTest();
        beepSound = MediaPlayer.create(this, R.raw.beep);

       // end test after 10 seconds
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mSwayView.endTest();
                beepSound.start();
                Intent i = new Intent(SwayTestCalibration.this, SwayTestResults.class);
                startActivity(i);
            }
        }, 10000);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnet, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }




    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values == null) {
            return;
        }

        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values;
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values;
                break;
            default:
                Log.w(TAG, "Unknown sensor type " + sensorType);
                return;
        }

        if (mGravity == null) {
            Log.w(TAG, "mGravity is null");
            return;
        }
        if (mGeomagnetic == null) {
            Log.w(TAG, "mGeomagnetic is null");
            return;
        }

        float rot[] = new float[9];
        if (!SensorManager.getRotationMatrix(rot, null, mGravity, mGeomagnetic)) {
            Log.w(TAG, "getRotationMatrix() failed");
            return;
        }

        float orientation[] = new float[9];
        SensorManager.getOrientation(rot, orientation);

        float yaw = orientation[0];
        float pitch = orientation[1];
        float roll = orientation[2];
        yawDeg = scaleAngle(yaw);
        rollDeg = scaleAngle(roll);
        pitchDeg = -1* scaleAngle(pitch);

        if (!inited) {
            inited = true;
            initYaw = yawDeg;
            initRoll =rollDeg;
        }

        mSwayView.onTiltChanged(pitchDeg - initRoll,yawDeg - initYaw);

        Log.v(TAG, String.format("%d, %d", pitchDeg,rollDeg));
        mDegreeView.setText(String.format("%d, %d, %d", yawDeg, pitchDeg, rollDeg));

    }
    private int scaleAngle(double degrees){
        degrees = Math.toDegrees(degrees);
        return (int) (Math.abs(degrees) < MIN_THRESHOLD ? 0 : degrees);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
