package com.style.hxc.libnavcontroller;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/1.
 */
public class NavControllerEx extends LinearLayout implements NavController.OnNavMovingListener {

    private NavController.OnNavMovingListener mbtnNavControllerListener;
    private NavController mbtnNavController;
    private TextView mtvInfo;

    public NavControllerEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navcontrollerex_layout, this);
        mbtnNavController=(NavController) findViewById(R.id.btnNavController);
        mtvInfo=(TextView)findViewById(R.id.tvInfo);
        mbtnNavController.setOnNavMovingListener(this);
    }

    public NavControllerEx(Context context) {
        super(context);

    }

    @Override
    public void onNavMoving(int radian, int length) {
        mtvInfo.setText("radian:" + radian + " power:" + length);
        if (mbtnNavControllerListener != null)
        {
            mbtnNavControllerListener.onNavMoving(radian, length);
        }
    }

    public void setOnNavMovingListener(NavController.OnNavMovingListener l)
    {
        mbtnNavControllerListener = l;
    }
}
