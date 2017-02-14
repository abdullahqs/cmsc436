package com.ms.tests.spiral;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ms.tests.R;
import com.ms.tests.TestSelectionActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class SpiralTestActivity extends AppCompatActivity {

    DrawingView dv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        dv = new DrawingView(this);
        setContentView(dv);*/

        setContentView(R.layout.activity_spiral_test);
        dv = (DrawingView) findViewById(R.id.spiralTest);
    }

    public void onClick(View v) {

        File file=new File(Environment.getExternalStorageDirectory()+"/ms/spiral");
        if(!file.isDirectory()){
            file.mkdir();
        }

        file=new File(Environment.getExternalStorageDirectory()+"/ms/spiral",System.currentTimeMillis()+".jpg");

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
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(SpiralTestActivity.this, TestSelectionActivity.class);
        startActivity(i);
    }
}
