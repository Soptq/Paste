package soptqs.paste.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomEditView extends AppCompatEditText {

    /*
     * 画线的画笔
     */
    private Paint linePaint;
    /*
     * 每一行的高度
     */
    private int lineHeight;
    /*
     * 行数
     */
    private int count;

    private ScrollView mScrollView;

    public CustomEditView(Context context) {
        super(context);
    }

    public CustomEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        linePaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取行的高度
        lineHeight = getLineHeight();
        count = getHeight() / lineHeight;
        //记录画到哪一行
        int nextLine = 0;
        //因为这个组件可能给它设置了边距，所以要先画第一条线
        nextLine = getCompoundPaddingTop();
        //给画笔设置颜色
        linePaint.setColor(Color.GRAY);
        canvas.drawLine(0.0F, nextLine, getRight(), nextLine, linePaint);
        //画线
        for (int i = 0; i < getLineCount(); i++) {
            nextLine += lineHeight;
            canvas.drawLine(getLeft(), nextLine, getRight(), nextLine, linePaint);
            canvas.save();
        }
    }

    // 添加弹性效果
    public void setScrollView(ScrollView scrollView) {
        mScrollView = scrollView;
    }
}
