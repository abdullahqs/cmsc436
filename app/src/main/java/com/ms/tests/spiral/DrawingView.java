package com.ms.tests.spiral;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.ms.tests.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ramse on 2/13/2017.
 */

public class DrawingView extends ImageView {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;

    private Bitmap spiral;
    private int spiralX, spiralY;

    class Pair {
        double x;
        double y;

        public Pair(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    public DrawingView(Context context) {
        super(context);

        Init(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Init(context);
    }

    void Init(Context c) {
        setDrawingCacheEnabled(true);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        context=c;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStrokeWidth(2f);
    }

    private void createScaledSpiral(Canvas c){
        Bitmap unscaledSpiral = BitmapFactory.decodeResource(getResources(), R.drawable.spiral);
        float ratio = unscaledSpiral.getHeight() / unscaledSpiral.getWidth();
        int targetWidth = c.getWidth();
        int targetHeight = (int)(ratio * targetWidth);

        spiral = Bitmap.createScaledBitmap(unscaledSpiral, targetWidth, targetHeight, false);
        spiralY = c.getHeight()/2 - spiral.getHeight()/2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(spiral == null)
            createScaledSpiral(canvas);

        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(spiral, 0, spiralY, mBitmapPaint);
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        mCanvas.drawPath(mPath,  mPaint);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    public boolean SaveImage(String fileName) {

        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("tests/spiral", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            getDrawingCache().compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public double calculateScore(ArrayList<Pair> inputPoints, ArrayList<Pair> fixedPoints){
        double agg = 0;
        double currentDeviation = 999999;

        for (Pair p: inputPoints){
            for (Pair fp: fixedPoints){
                currentDeviation = Math.min(currentDeviation, Math.sqrt(Math.pow((p.x - fp.x),2) + Math.pow((p.y - fp.y),2)));
            }
            agg += currentDeviation;
            currentDeviation = 999999;
        }

        // Standarizing based on number of points inputted. 
        return agg/inputPoints.size();

    }
}