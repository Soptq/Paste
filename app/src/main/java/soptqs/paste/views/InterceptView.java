package soptqs.paste.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by S0ptq on 2018/2/11.
 */

public class InterceptView extends RelativeLayout {

    public InterceptView(Context context) {
        super(context);
    }

    public InterceptView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public InterceptView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
