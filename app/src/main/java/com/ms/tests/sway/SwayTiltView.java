package com.ms.tests.sway;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ms.tests.R;

/**
 * Created by radhika on 4/6/17.
 */

public class SwayTiltView extends View {

    private static final double BALL_SENSITIVITY = 0.005;
    private static final double BALL_RADIUS = 50;

    private boolean mActive;

    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private Paint circlePaint;
    private Path circlePath;

    private Bitmap mBitmap;
    private Bitmap mBall;
    private Bitmap mPaddle;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);;

    private Point mPaddlePoint;
    private Point mBubblePoint;
    private Point mVelocity;

    private long mLastUpdateTime;
    private long mCurrentTime;

    private boolean mStartedTest;

    float score;
    int bestScore;

    public SwayTiltView(Context context) {
        super(context);

        init();
    }

    public SwayTiltView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SwayTiltView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init(){
        setDrawingCacheEnabled(true);

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStrokeWidth(2f);

        mPaddlePoint = new Point();
        mBubblePoint = new Point();
        mVelocity = new Point(0, 0);

        mBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
    }

    public void startTest(){
        mStartedTest = true;
        mActive = true;

        postInvalidate();
    }

    public void endTest(){
        mActive = false;
    }

    private void createScaledPaddle(Canvas c){
        Bitmap unscaledPaddle = BitmapFactory.decodeResource(getResources(), R.drawable.bullseye);
        float ratio = unscaledPaddle.getHeight() / unscaledPaddle.getWidth();
        int targetWidth = c.getWidth();
        int targetHeight = (int)(ratio * targetWidth);

        mPaddle = Bitmap.createScaledBitmap(unscaledPaddle, targetWidth, targetHeight, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!mStartedTest && mActive)
            return;

        if(mPaddle == null)
            createScaledPaddle(canvas);

        canvas.drawBitmap(mPaddle, null, getPaddleRect(canvas), mBitmapPaint);
        canvas.drawBitmap(mBall, null, getBallRect(), mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
    }

    private Rect getPaddleRect(Canvas c){
        return new Rect(0, 0, c.getWidth(), c.getHeight());
    }

    private Rect getBallRect(){
        return new Rect((int)(mBubblePoint.x - BALL_RADIUS),
                (int)(mBubblePoint.y - BALL_RADIUS),
                (int)(mBubblePoint.x + BALL_RADIUS),
                (int)(mBubblePoint.y + BALL_RADIUS));
    }

    private void update(long delta){
        if(mStartedTest) {
            float oldX = mBubblePoint.x;
            float oldY = mBubblePoint.y;
            mBubblePoint.x += mVelocity.x * delta * BALL_SENSITIVITY;
            mBubblePoint.y += mVelocity.y * delta * BALL_SENSITIVITY;

            bestScore += 1;

            float dist = (float)Math.sqrt(Math.pow(mBubblePoint.x-mCanvas.getWidth()/2, 2) + Math.pow(mBubblePoint.y-mCanvas.getHeight()/2, 2));
            score += 1 - Math.min(dist / mCanvas.getWidth(), 1);


            if (Math.abs(oldX - mBubblePoint.x) < 5 || Math.abs(oldY - mBubblePoint.y) < 5) {
                mPath.quadTo(oldX, oldY,mBubblePoint.x, mBubblePoint.y);
                circlePath.reset();
                circlePath.addCircle(mBubblePoint.x, mBubblePoint.y, 30, Path.Direction.CW);
            }
            Log.v("TEST", "Inside updating position");
        }

        Log.v("TEST", "Inside update");

        postInvalidate();
    }

    public Bitmap getBitmapForSaving(){
        return mBitmap;
    }

    public void onTiltChanged(int tiltX, int tiltY){
        mCurrentTime = System.currentTimeMillis();

        mVelocity.x = tiltX;
        mVelocity.y = tiltY;

        if(mLastUpdateTime == 0) {
            mLastUpdateTime = mCurrentTime;
            mPath.reset();
            return;
        }else
            update(mCurrentTime - mLastUpdateTime);

        mLastUpdateTime = mCurrentTime;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        mBubblePoint.x = w/2;
        mBubblePoint.y = h/2;

        mPaddlePoint.x = 0;
        mPaddlePoint.y = 0;
    }


    public int getScore() {
        return (int)(score * 100 / bestScore);
    }
}