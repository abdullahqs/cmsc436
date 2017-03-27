package com.ms.tests.rotation;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;

import java.util.ArrayList;

public class RotateTestActivity extends AppCompatActivity implements SensorEventListener{

    private Button mStart;
    private SensorManager mSensorManager;
    private final static String TAG = "CalibrationActivity";
    private static final double MIN_THRESHOLD = 0.5;

    boolean listened;
    int counter;


    private Sensor mAccel;
    private Sensor mMagnet;
    String start;
    String end;

    private float[] mGravity;
    private float[] mGeomagnetic;


    String startend;
    TextView display;
    int[] startcoord;
    int[] endcoord;
    int[] repTime;

    Button btn1;
    Button btn2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_test);



        Intent i = getIntent();
        startend = i.getStringExtra(RotateTestResults.KEY);
        display = (TextView) findViewById(R.id.startandend);
        display.setText(startend);

//        String[] out = startend.split(",");
//        startcoord[0] = Integer.parseInt(out[0]);
//        startcoord[1] = Integer.parseInt(out[1]);
//        endcoord[0] = Integer.parseInt(out[2]);
//        endcoord[1] = Integer.parseInt(out[3]);
//        repTime = new int[10];

        listened = false;
        counter = 0;

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccel == null) {
            Log.d(TAG, "accelerometer is null");
        }

        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnet == null) {
            Log.d(TAG, "magnetometer is null");
        }

        mStart = (Button)  findViewById(R.id.gobutton);

        mStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(RotateTestActivity.this, RotateResultActivity.class);
                String temp = ""+counter;
                i.putExtra(RotateTestResults.KEY, temp);
                startActivity(i);

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
        int rollDeg = scaleAngle(roll);
        int pitchDeg = -1* scaleAngle(pitch);

        Log.v(TAG, String.format("%d, %d", rollDeg, pitchDeg));

        if(!listened && rollDeg <= -170 && pitchDeg <= -10){
            listened = true;
        } else if(listened && rollDeg >= -10 && pitchDeg >= -10){
            listened = false;
            counter += 1;
        }



    }


}
