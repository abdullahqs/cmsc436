package com.ms.tests.sway;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ms.tests.R;
import com.ms.tests.level.LevelTestActivity;
import com.ms.tests.level.LevelTestResultsActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SwayTestCalibration extends AppCompatActivity implements SensorEventListener{

    public static final String RESULT_IMAGE_URI = "result_image_uri";
    public static final String RESULT_SCORE = "result_score";
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
    public int pitchDeg;
    public int azimutDeg;
    public int rollDeg;

    boolean inited;
    int initPitch;
    int initAzimut;
    int initRoll;
    int lastAzimut;
    int positive;

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
                saveBitmap();
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

        float R[] = new float[9];
        float I[] = new float[9];
        if (!SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)) {
            Log.w(TAG, "getRotationMatrix() failed");
            return;
        }

        float orientation[] = new float[3];
        SensorManager.getOrientation(R, orientation);

        float pitch = orientation[1];
        float azimut= orientation[0];
        float roll = orientation[2];
        azimutDeg = scaleAngle(azimut);
        pitchDeg = scaleAngle(pitch);
        rollDeg = scaleAngle(roll);

        if (azimutDeg < 0) azimutDeg += 360;
        azimutDeg += 180;

        if (!inited) {
            initRoll = rollDeg;
            initAzimut = azimutDeg;
            initPitch = pitchDeg;
            inited = true;
        }


        mSwayView.onTiltChanged(azimutDeg - initAzimut, rollDeg - initRoll);

        Log.v(TAG, String.format("%d, %d", pitchDeg,azimutDeg));
        mDegreeView.setText(String.format("%d, %d, %d", azimutDeg - initAzimut, scaleAngle(orientation[1]) - initPitch, scaleAngle(orientation[2]) - initRoll));



    }
    private int scaleAngle(double degrees){
        degrees = Math.toDegrees(degrees);
        return (int) (Math.abs(degrees) < MIN_THRESHOLD ? 0 : degrees);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveBitmap();
                } else {
                    Intent i = new Intent(SwayTestCalibration.this, SwayTestResults.class);
                    startActivity(i);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void saveBitmap() {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            return;
        }

        File file=new File(Environment.getExternalStorageDirectory()+"/swayTest");
        if(!file.isDirectory()){
            file.mkdir();
        }

        file = new File(Environment.getExternalStorageDirectory()+"/swayTest",System.currentTimeMillis()+".jpg");
        Uri fileUri = null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            mSwayView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Sway Test");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Sway Test for MS");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            fileUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(SwayTestCalibration.this, SwayTestResults.class);

        if(fileUri != null)
            i.putExtra(RESULT_IMAGE_URI, fileUri.toString());
        i.putExtra(RESULT_SCORE, mSwayView.getScore());

        startActivity(i);
    }
}