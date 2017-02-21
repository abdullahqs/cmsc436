package com.ms.tests.level;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ms.tests.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

public class LevelTestActivity extends AppCompatActivity implements SensorEventListener {
    public static final String RESULT_IMAGE_URI = "result_image_uri";
    private static final int REQUEST_EXTERNAL_STORAGE = 0x2;
    private static final double MIN_THRESHOLD = 1.5;
    private final static String TAG = "LevelTestActivity";

    private float[] mGravity;
    private float[] mGeomagnetic;

    private Button mStart;
    private LevelTiltView mTiltView;
    private TextView mDegreeView;

    private SensorManager mSensorManager;
    private Sensor mAccel;
    private Sensor mMagnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_test);
        mDegreeView = (TextView) findViewById(R.id.degree_view);
        mTiltView = (LevelTiltView) findViewById(R.id.tilt_view);
        mStart = (Button) findViewById(R.id.start_level_test);

        mStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mTiltView.startTest();
                mStart.setText("Stop Test");
                mStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTiltView.endTest();
                        onSaveDrawing(null);
                    }
                });
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccel == null)
            Log.d(TAG, "accelerometer is null");

        mMagnet = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnet == null)
            Log.d(TAG, "magnetometer is null");
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

        if (mGravity == null || mGeomagnetic == null) {
            Log.w(TAG, "mGravity or mGeomagnetic is null");
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

        mTiltView.onTiltChanged(rollDeg, pitchDeg);

        Log.v(TAG, String.format("%d, %d", rollDeg, pitchDeg));
        mDegreeView.setText(String.format("%d, %d", pitchDeg, rollDeg));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private int scaleAngle(double degrees){
        degrees = Math.toDegrees(degrees);
        return (int) (Math.abs(degrees) < MIN_THRESHOLD ? 0 : degrees);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE){
            for(int i = 0; i < permissions.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    return;
                Log.d(getClass().getSimpleName(), "Req'd " + permissions[i] + " with result: " + grantResults[i]);
            }

            saveBitmap();
        }
    }

    public void onSaveDrawing(View v){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }else {
            saveBitmap();
        }
    }

    public void saveBitmap() {

        File file=new File(Environment.getExternalStorageDirectory()+"/levelTest");
        if(!file.isDirectory()){
            file.mkdir();
        }

        file = new File(Environment.getExternalStorageDirectory()+"/levelTest",System.currentTimeMillis()+".jpg");
        Uri fileUri = null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            mTiltView.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Level Test");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Level Test for MS");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            fileUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(LevelTestActivity.this, LevelTestResultsActivity.class);

        if(fileUri != null)
            i.putExtra(RESULT_IMAGE_URI, fileUri.toString());

        startActivity(i);
    }
}
