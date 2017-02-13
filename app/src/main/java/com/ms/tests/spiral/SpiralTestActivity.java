package com.ms.tests.spiral;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ms.tests.R;

public class SpiralTestActivity extends AppCompatActivity {

    DrawingView dv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        dv = new DrawingView(this);
        setContentView(dv);*/

        setContentView(R.layout.activity_spiral_test);
    }


}
