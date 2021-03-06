package com.ms.tests.rotation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ms.tests.R;


public class RotateTestCalibrationActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private final static String TAG = "CalibrationActivity";
    private static final double MIN_THRESHOLD = 0.5;


    int[] pitchrolls;
    public int pitchDeg;
    public int rollDeg;

    private Sensor mAccel;
    private Sensor mMagnet;
    String start;
    String end;



    private float[] mGravity;
    private float[] mGeomagnetic;
    Button mStart;
    private TextView mDegreeView;
    private TextView mTitle;

    TextView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_calibration);

        mStart = (Button) findViewById(R.id.start_rotate_test);
        mTitle = (TextView) findViewById(R.id.startend);
        mDegreeView = (TextView) findViewById(R.id.level);
        pitchrolls = new int[4];
        pitchDeg = 0;
        rollDeg = 0;


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccel == null)
            Log.d(TAG, "accelerometer is null");

        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnet == null)
            Log.d(TAG, "magnetometer is null");



        mStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pitchrolls[0] = pitchDeg;
                pitchrolls[1] = rollDeg;
                mStart.setText("Calibrate the End Point");
                mStart.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){


                        RadioButton armButton, legButton;

                        legButton = (RadioButton) findViewById(R.id.leg_mode);
                        armButton = (RadioButton) findViewById(R.id.arm_mode);

                        boolean isArm = true;
                        if(legButton.isChecked()){
                            isArm = false;
                        }

                        pitchrolls[2] = pitchDeg;
                        pitchrolls[3] = rollDeg;
                        Intent i = new Intent(RotateTestCalibrationActivity.this, RotateTestActivity.class);
                        i.putExtra(RotateTestResults.MODE_KEY,isArm);
                        i.putExtra(RotateTestResults.KEY, pitchrolls);
                        startActivity(i);
                    }
                });
            }
        });


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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private int scaleAngle(double degrees){
        degrees = Math.toDegrees(degrees);
        return (int) (Math.abs(degrees) < MIN_THRESHOLD ? 0 : degrees);
    }


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

        // Orientation contains: azimuth, pitch and roll - we'll use roll
        float pitch = orientation[1];
        float roll = orientation[2];
        rollDeg = scaleAngle(roll);
        pitchDeg = -1* scaleAngle(pitch);

        Log.v(TAG, String.format("%d, %d", pitchDeg,rollDeg));
        mDegreeView.setText(String.format("%d, %d", pitchDeg, rollDeg));
    }

}
