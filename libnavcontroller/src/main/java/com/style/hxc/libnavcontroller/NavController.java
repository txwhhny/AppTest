package com.style.hxc.libnavcontroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/10/31.
 */
public class NavController extends View {

    private final static int INNER_COLOR_DEFAULT = 0x40F0E68C;
    private final static int OUTER_COLOR_DEFAULT = 0x40C0C0C0;
    private final static int BG_COLOR_DEFAULT = 0x0800FF00;

    private int OUTER_WIDTH_SIZE;
    private int OUTER_HEIGHT_SIZE;
    private boolean mIsLocked;
    private int miInnerColor;
    private int miOuterColor;
    private int miBackgroundColor;
    private int miRealWidth;//绘图使用的宽
    private int miRealHeight;//绘图使用的高
    private float mfInnerCenterX;
    private float mfInnerCenterY;
    private float mfInnerRadius;
    private float mfOuterCenterX;
    private float mfOuterCenterY;
    private float mfOuterRadius;
    private Paint mtInnerPaint;
    private Paint mtOuterPaint;

    private Paint mtCenterLocationPaint;
    private Path mtPathH;
    private Path mtPathV;

    private OnNavMovingListener mCallBack = null;
    public interface OnNavMovingListener{
        public void onNavMoving(int iRadian, int iPower);
    }

    public NavController(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.NavController);
        miInnerColor = ta.getColor(R.styleable.NavController_InnerColor, INNER_COLOR_DEFAULT);
        miOuterColor = ta.getColor(R.styleable.NavController_OuterColor, OUTER_COLOR_DEFAULT);
        miBackgroundColor = ta.getColor(R.styleable.NavController_BackgroundColor, BG_COLOR_DEFAULT);
        mIsLocked = ta.getBoolean(R.styleable.NavController_IsLocked, false);
        ta.recycle();

        OUTER_WIDTH_SIZE = dip2px(context, 125.0f);
        OUTER_HEIGHT_SIZE = dip2px(context, 125.0f);

        mtInnerPaint = new Paint();
        mtOuterPaint = new Paint();
        mtCenterLocationPaint = new Paint();

        mtInnerPaint.setColor(miInnerColor);
        mtInnerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mtOuterPaint.setColor(miOuterColor);
        mtOuterPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setBackgroundColor(miBackgroundColor);


        mtPathH = new Path();
        mtPathV = new Path();
        DashPathEffect pathEffect = new DashPathEffect(new float[] { 5,10 }, 0);
        mtCenterLocationPaint.reset();
        mtCenterLocationPaint.setStyle(Paint.Style.STROKE);
        mtCenterLocationPaint.setStrokeWidth(5);
        mtCenterLocationPaint.setColor(0x80C0C0C0);
        mtCenterLocationPaint.setAntiAlias(true);
        mtCenterLocationPaint.setPathEffect(pathEffect);
    }

    private int measureWidth(int widthMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        //处理三种模式
        if(widthMode==MeasureSpec.EXACTLY){
            return widthVal+getPaddingLeft()+getPaddingRight();
        }else if(widthMode==MeasureSpec.UNSPECIFIED){
            return OUTER_WIDTH_SIZE;
        }else{
            return Math.min(OUTER_WIDTH_SIZE,widthVal);
        }
    }

    private int measureHeight(int heightMeasureSpec)
    {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightValue = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY)
        {
            return heightValue + getPaddingTop() + getPaddingBottom();
        }
        else if (heightMode == MeasureSpec.UNSPECIFIED)
        {
            return OUTER_HEIGHT_SIZE;
        }
        else
        {
            return Math.min(OUTER_HEIGHT_SIZE, heightValue);
        }
    }

    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    private void resetInnerCirclePosition()
    {
        mfInnerCenterX = miRealWidth / 2 + getPaddingLeft();
        mfInnerCenterY = miRealHeight / 2 + getPaddingTop();
        invalidate();
        if (mCallBack != null)
        {
            mCallBack.onNavMoving(0, 0);
        }
    }

    private void resetCoordinate()
    {
        mtPathH.reset();
        mtPathH.moveTo(mfOuterCenterX - mfOuterRadius, mfOuterCenterY);
        mtPathH.lineTo(mfOuterCenterX + mfOuterRadius, mfOuterCenterY);
        mtPathV.reset();
        mtPathV.moveTo(mfOuterCenterX, mfOuterCenterY - mfOuterRadius);
        mtPathV.lineTo(mfOuterCenterX, mfOuterCenterY + mfOuterRadius);
    }

    private void changeInnerCirclePosition(MotionEvent e) {
        //圆的方程：（x-miRealWidth/2）^2 +（y - miRealHeight/2）^2 <= outRadius^2
        //第一步，确定有效的触摸点集
        float X = e.getX();
        float Y = e.getY();
//        boolean isPointInOutCircle = Math.pow(X-miRealWidth/2,2) +Math.pow(Y-miRealHeight/2,2) <= Math.pow(mfOuterRadius,2);
//        if(isPointInOutCircle)
//        {
//            Log.i("TAG","inCircle");
            //两种情况：小圆半径
            float fLimitRadius = mfOuterRadius - (mfInnerRadius / 2);
            boolean isPointInFree = Math.pow(X- mfOuterCenterX,2) +Math.pow(Y- mfOuterCenterY,2)<=Math.pow(fLimitRadius,2);
            if(isPointInFree)
            {
                mfInnerCenterX = X;
                mfInnerCenterY = Y;
            }
            else
            {
                //处理限制区域，这部分使用触摸点与中心点与外圆方程交点作为内圆的中心点
                //使用近似三角形来确定这个点
                //求出触摸点，触摸点垂足和中心点构成的直角三角形（pointTri）的直角边长
                float pointTriX = mfOuterCenterX - X;//横边
                float pointTriY = mfOuterCenterY - Y;//竖边
                float pointTriZ = (float) Math.sqrt((Math.pow(pointTriX,2)+Math.pow(pointTriY,2)));
                float TriSin = pointTriY/pointTriZ;
                float TriCos = pointTriX/pointTriZ;
                //求出在圆环上的三角形的两个直角边的长度
                float limitCircleTriY = fLimitRadius*TriSin;
                float limitCircleTriX = fLimitRadius*TriCos;
                mfInnerCenterX = mfOuterCenterX - limitCircleTriX;
                mfInnerCenterY = mfOuterCenterY - limitCircleTriY;
//                Log.i("TAG","inLimit");
            }
            invalidate();
//        }
//        else
//        {
//            Log.i("TAG","notInCircle");
//        }

        if(mCallBack!=null)
        {
            float fPower = (float)Math.sqrt(Math.pow(mfInnerCenterX - mfOuterCenterX,2) + Math.pow(mfInnerCenterY - mfOuterCenterY, 2));
            fPower = fPower * 100 / (fLimitRadius);

            float fRadian = (float)Math.atan2(mfOuterCenterX - mfInnerCenterX, mfOuterCenterY - mfInnerCenterY);
            fRadian = (float)(fRadian * 180 / Math.PI);
            if (fRadian < 0)     fRadian += 180;
            if (mfInnerCenterX > mfOuterCenterX)    fRadian += 180;
            mCallBack.onNavMoving((int)fRadian, (int)fPower);
        }
    }

    public void setBackGroundColor(int iColor)
    {
        miBackgroundColor = iColor;
        setBackgroundColor(iColor);
    }

    public int getBackgroundColor()
    {
        return miBackgroundColor;
    }

    public void setInnerColor(int iColor)
    {
        miInnerColor = iColor;
        mtInnerPaint.setColor(miInnerColor);
    }

    public int getInnerColor()
    {
        return miInnerColor;
    }

    public void setOuterColor(int iColor)
    {
        miOuterColor = iColor;
        mtOuterPaint.setColor(miOuterColor);
    }

    public int getOuterColor()
    {
        return miOuterColor;
    }

    public void setIsLocked(boolean bIsLocked)
    {
        mIsLocked = bIsLocked;
        mfOuterRadius = Math.min(miRealWidth, miRealHeight) * (mIsLocked ? 8 : 7) / 20;
        resetInnerCirclePosition();
        resetCoordinate();
    }

    public boolean getIsLocked()
    {
        return mIsLocked;
    }

    public void setOnNavMovingListener(OnNavMovingListener listener){
        mCallBack = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        miRealWidth = w - getPaddingLeft() - getPaddingRight();
        miRealHeight = h - getPaddingTop() - getPaddingBottom();

        mfInnerCenterX = miRealWidth / 2 + getPaddingLeft();
        mfInnerCenterY = miRealHeight / 2 + getPaddingTop();
        mfOuterCenterX = mfInnerCenterX;
        mfOuterCenterY = mfInnerCenterY;

        mfOuterRadius = Math.min(miRealWidth, miRealHeight) * 2 / 5 ;
        mfInnerRadius = mfOuterRadius / 3;

        resetCoordinate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
         //画外部圆
        canvas.drawCircle(mfOuterCenterX, mfOuterCenterY, mfOuterRadius, mtOuterPaint);
        //内部圆
        canvas.drawCircle(mfInnerCenterX, mfInnerCenterY, mfInnerRadius, mtInnerPaint);

        canvas.drawPath(mtPathH, mtCenterLocationPaint);
        canvas.drawPath(mtPathV, mtCenterLocationPaint);
    }

    /**
     * Implement this method to handle touch screen motion events.
     * <p/>
     * If this method is used to detect click actions, it is recommended that
     * the actions be performed by implementing and calling
     * {@link #performClick()}. This will ensure consistent system behavior,
     * including:
     * <ul>
     * <li>obeying click sound preferences
     * <li>dispatching OnClickListener calls
     * <li>handling {@link AccessibilityNodeInfo#ACTION_CLICK ACTION_CLICK} when
     * accessibility features are enabled
     * </ul>
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mfInnerRadius = mfOuterRadius  * 2 / 5;
        mfOuterRadius = Math.min(miRealWidth, miRealHeight) * 8 / 20 ;
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            changeInnerCirclePosition(event);

        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE){
            changeInnerCirclePosition(event);
            Log.i("TAG","MOVED");
        }
        else if(event.getAction()==MotionEvent.ACTION_UP)
        {
            if (mIsLocked)
            {
                changeInnerCirclePosition(event);
            }
            else
            {
                mfOuterRadius = Math.min(miRealWidth, miRealHeight) * 7 / 20;
                resetInnerCirclePosition();
            }
        }
        resetCoordinate();

        return true;
//        return super.onTouchEvent(event);
    }
}
