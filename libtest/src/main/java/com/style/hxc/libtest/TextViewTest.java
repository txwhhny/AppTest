package com.style.hxc.libtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/10/28.
 */
public class TextViewTest extends TextView implements GestureDetector.OnGestureListener {

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public interface OnTextViewTestClickedListener
    {
        public void onTextViewTestClicked();
    }
    private OnTextViewTestClickedListener ml;

    private GestureDetector mDetector;
    private boolean mbShowText;
    private int miLabelPosition;

    public TextViewTest(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDetector = new GestureDetector(this);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextViewTest, 0, 0);
        try {
            mbShowText = ta.getBoolean(R.styleable.TextViewTest_showText, false);
            miLabelPosition = ta.getInteger(R.styleable.TextViewTest_labelPosition, 0);
        }  finally {
            ta.recycle();
        }
    }

    public boolean isShowText()
    {
        return mbShowText;
    }

    public void setMbShowText(boolean showText)
    {
        mbShowText = showText;
        invalidate();
        requestLayout();
    }

    public void setOnTextViewTestClickedListener(OnTextViewTestClickedListener l)
    {
        ml = l;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (ml != null)
        {
            ml.onTextViewTestClicked();
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
