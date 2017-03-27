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
    int process;
    int temptime;

    private Sensor mAccel;
    private Sensor mMagnet;
    String start;
    String end;

    private float[] mGravity;
    private float[] mGeomagnetic;
    private boolean isArm;

    int[] startend;
    TextView display;
    int[] repTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_test);



        Intent i = getIntent();
        startend = i.getIntArrayExtra(RotateTestResults.KEY);
        isArm = i.getBooleanExtra(RotateTestResults.MODE_KEY,isArm);

        display = (TextView) findViewById(R.id.startandend);

        listened = false;
        counter = 0;
        process = 0;
        temptime = 0;
        String out = ""+counter+" reps completed";
        display.setText(out);


        repTime = new int[10];

        listened = false;
        counter = 0;
        process = 0;
        temptime = 0;

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
                if(counter == 10) {
                    Intent i = new Intent(RotateTestActivity.this, RotateResultActivity.class);
                    String temp = "" + counter;
                    i.putExtra(RotateTestResults.RESULT_KEY, repTime);
                    i.putExtra(RotateTestResults.KEY, temp);
                    startActivity(i);
                }

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

    private boolean confirmStart(int pitchDeg, int rollDeg){
        if(isArm) {
            return pitchDeg <= startend[0] + 30;
        } else {
            return pitchDeg <= startend[0] + 20;
        }
    }

    private boolean confirmEnd(int pitchDeg, int rollDeg){
        if(isArm) {
            return pitchDeg <= startend[2] + 30;
        } else {
            return pitchDeg >= startend[2] - 20;
        }
    }

    private boolean confirmMiddle(int pitchDeg, int rollDeg){
        if(isArm) {
            return pitchDeg >= 75;
        } else {
            return pitchDeg >= -40;
        }
    }



    public void onSensorChanged(SensorEvent event) {
        if (event.values == null) {
            return;
        }

            if (counter <= 9) {
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
                int pitchDeg = -1 * scaleAngle(pitch);

                Log.v(TAG, String.format("%d, %d", pitchDeg, rollDeg));

                if (!listened && confirmStart(pitchDeg, rollDeg) && process == 0) {
                    listened = true;
                    process += 1;
                    double temp = System.currentTimeMillis() / 1000;
                    temptime = (int) Math.floor(temp);
                } else if (listened && confirmMiddle(pitchDeg, rollDeg) && process == 1) {
                    process += 1;
                } else if (listened && confirmEnd(pitchDeg, rollDeg) && process == 2) {
                    listened = false;
                    process += 1;
                } else if (!listened && confirmMiddle(pitchDeg, rollDeg) && process == 3) {
                    process += 1;
                } else if (!listened && confirmStart(pitchDeg, rollDeg) && process == 4) {
                    double end = System.currentTimeMillis() / 1000;
                    int endsec = (int) Math.floor(end);
                    temptime = endsec - temptime;
                    repTime[counter] = temptime;
                    counter += 1;
                    process = 0;
                    temptime = 0;
                    String out = "" + counter +" reps completed";
                    display.setText(out);
                }
            } else {
                mStart.setText("See Results");
                return;
            }

    }

}
