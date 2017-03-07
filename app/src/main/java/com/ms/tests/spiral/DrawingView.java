package com.ms.tests.spiral;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
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

    private ArrayList<PointF> userPoints;
    private Bitmap spiral;
    private int spiralY;
    private Point[] spiralPoints;

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

        userPoints = new ArrayList<PointF>();
        spiralPoints = new Point[32];
        spiralPoints[ 0] = new Point(158, 22);
        spiralPoints[ 1] = new Point(182, 41);
        spiralPoints[ 2] = new Point(196, 63);
        spiralPoints[ 3] = new Point(206, 87);
        spiralPoints[ 4] = new Point(210, 114);
        spiralPoints[ 5] = new Point(207, 142);
        spiralPoints[ 6] = new Point(197, 169);
        spiralPoints[ 7] = new Point(182, 188);
        spiralPoints[ 8] = new Point(162, 203);
        spiralPoints[ 9] = new Point(140, 213);
        spiralPoints[10] = new Point(113, 217);
        spiralPoints[11] = new Point(87, 213);
        spiralPoints[12] = new Point(64, 203);
        spiralPoints[13] = new Point(46, 187);
        spiralPoints[14] = new Point(35, 167);
        spiralPoints[15] = new Point(28, 146);
        spiralPoints[16] = new Point(28, 122);
        spiralPoints[17] = new Point(35, 100);
        spiralPoints[18] = new Point(48, 82);
        spiralPoints[19] = new Point(66, 69);
        spiralPoints[20] = new Point(94, 62);
        spiralPoints[21] = new Point(118, 66);
        spiralPoints[22] = new Point(136, 77);
        spiralPoints[23] = new Point(147, 94);
        spiralPoints[24] = new Point(153, 114);
        spiralPoints[25] = new Point(148, 138);
        spiralPoints[26] = new Point(133, 153);
        spiralPoints[27] = new Point(113, 160);
        spiralPoints[28] = new Point(95, 153);
        spiralPoints[29] = new Point(85, 136);
        spiralPoints[30] = new Point(92, 118);
        spiralPoints[31] = new Point(105, 125);
    }


    private void createScaledSpiral(Canvas c){
        Bitmap unscaledSpiral = BitmapFactory.decodeResource(getResources(), R.drawable.spiral);
        float ratio = unscaledSpiral.getHeight() / unscaledSpiral.getWidth();
        int targetWidth = c.getWidth();
        int targetHeight = (int)(ratio * targetWidth);

        spiral = Bitmap.createScaledBitmap(unscaledSpiral, targetWidth, targetHeight, false);
        spiralY = c.getHeight()/2 - spiral.getHeight()/2;

        int trueWidth = 240;
        float scaledBy = (float)targetWidth / trueWidth;

        for (int i = 0; i < spiralPoints.length; i++) {
            spiralPoints[i].x *= scaledBy;
            spiralPoints[i].y *= scaledBy;
            spiralPoints[i].y += spiralY;
        }
    }

    public ArrayList<Pair> getUserPoints(){
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for(PointF point :this.userPoints){
            Pair pair = new Pair(point.x,point.y);
            pairs.add(pair);
        }
        return pairs;
    }

    public ArrayList<Pair> getFixedPoints(){
        ArrayList<Pair> pairs = new ArrayList<Pair>();
        for(Point point :this.spiralPoints){
            Pair pair = new Pair(point.x,point.y);
            pairs.add(pair);
        }
        return pairs;
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

        // Draws debug points
        for (int i = 0; i < spiralPoints.length; i++) {
            canvas.drawPoint(spiralPoints[i].x, spiralPoints[i].y, mPaint);
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        userPoints.add(new PointF(x, y));
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

    // DON'T USE
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

        double score = agg/inputPoints.size();
        score = (500 - score)/5;
        return score;

        // Standarizing based on number of points inputted.
        //return agg/inputPoints.size();

    }
}