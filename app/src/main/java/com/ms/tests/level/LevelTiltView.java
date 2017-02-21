package com.ms.tests.level;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ms.tests.R;

/**
 * Created by Carlos on 2/21/2017.
 */

public class LevelTiltView extends View {
    private static final double BALL_SENSITIVITY = 0.026;
    private static final double BALL_RADIUS = 50;

    private boolean mActive;

    private Canvas mCanvas;
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

    public LevelTiltView(Context context) {
        super(context);

        init();
    }

    public LevelTiltView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LevelTiltView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init(){
        setDrawingCacheEnabled(true);
        mPaddlePoint = new Point();
        mBubblePoint = new Point();
        mVelocity = new Point(0, 0);

        mBall = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mPaddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
    }

    public void startTest(){
        mStartedTest = true;
        mActive = true;

        postInvalidate();
    }

    public void endTest(){
        mActive = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!mStartedTest && mActive)
            return;

        canvas.drawBitmap(mPaddle, null, getPaddleRect(canvas), mBitmapPaint);
        canvas.drawBitmap(mBall, null, getBallRect(), mBitmapPaint);
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
            mBubblePoint.x += mVelocity.x * delta * BALL_SENSITIVITY;
            mBubblePoint.y += mVelocity.y * delta * BALL_SENSITIVITY;
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
}
