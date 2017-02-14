package com.ms.tests.spiral;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.ms.tests.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

public class SpiralTestActivity extends AppCompatActivity {
    public static final String RESULT_IMAGE_URI = "result_image_uri";
    private static final int REQUEST_EXTERNAL_STORAGE = 0x1;

    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spiral_test);
        dv = (DrawingView) findViewById(R.id.spiralTest);


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

        File file=new File(Environment.getExternalStorageDirectory()+"/spiralTest");
        if(!file.isDirectory()){
            file.mkdir();
        }

        file = new File(Environment.getExternalStorageDirectory()+"/spiralTest",System.currentTimeMillis()+".jpg");
        Uri fileUri = null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            dv.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Spiral Test");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Spiral Test for MS");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            fileUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(SpiralTestActivity.this, SpiralTestResultActivity.class);

        if(fileUri != null)
            i.putExtra(RESULT_IMAGE_URI, fileUri);

        startActivity(i);
    }
}
