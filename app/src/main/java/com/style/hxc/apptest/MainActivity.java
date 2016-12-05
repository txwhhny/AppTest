package com.style.hxc.apptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.style.hxc.libnavcontroller.NavController;
import com.style.hxc.libnavcontroller.NavControllerEx;
import com.style.hxc.libtest.TextViewTest;

public class MainActivity extends AppCompatActivity implements TextViewTest.OnTextViewTestClickedListener, NavController.OnNavMovingListener {
    private NavControllerEx ctrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextViewTest)(findViewById(R.id.tvTest))).setOnTextViewTestClickedListener(this);

        ctrl = ((NavControllerEx) findViewById(R.id.btnNav));
        ctrl.setOnNavMovingListener(this);
        NavController nav = (NavController)findViewById(R.id.btnNavCtrl);
        nav.setOnNavMovingListener(this);
    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        ctrl.onTouchEvent(event);
        return super.onTouchEvent(event);

    }

    @Override
    public void onTextViewTestClicked() {
        Toast.makeText(MainActivity.this, "这是测试文本", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNavMoving(int radian, int length) {
        ((TextViewTest)findViewById(R.id.tvTest)).setText("radian:" + radian + " length:" + length);
    }
}
