package soptqs.paste.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import soptqs.paste.R;
import soptqs.paste.activities.UniversalCopyActivity;
import soptqs.paste.kotlin.MaterialPopMenuProvider;
import soptqs.paste.nodes.CopyNode;

/**
 * Created by S0ptq on 2018/3/26.
 */

public class CopyOverlayView extends View {

    private Rect rect;
    private String string;
    private Paint paint;
    private boolean active;


    public CopyOverlayView(final Context context, CopyNode copyNode, UniversalCopyActivity.CopyListener copyListener) {
        super(context);
        this.active = false;
        this.rect = copyNode.getRect();
        this.string = copyNode.getString();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5.0f);
        setOnClickListener(new OverLayClick(copyListener, this));
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MaterialPopMenuProvider.universalCopyLongClickPopUpMenu(CopyOverlayView.this, context, getText());
                return true;
            }
        });
    }

    public String getText() {
        return string;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (active) {
            paint.reset();
            paint.setColor(getContext().getResources().getColor(R.color.transparentactive));
        } else {
            paint.reset();
            paint.setColor(getContext().getResources().getColor(R.color.copyPrimaryDark));
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(8.0f);
            paint.setAntiAlias(true);

            RectF oval = new RectF(0, 0, getWidth(), getHeight());
            PathEffect effects = new DashPathEffect(new float[]{25, 5}, 0);
            paint.setPathEffect(effects);
            canvas.drawRoundRect(oval, 0, 0, paint);
        }
        canvas.drawRect(0.0f, 0.0f, (float) rect.width(), (float) rect.height(), paint);
    }

    public void addView(RelativeLayout relativeLayout, int marginHeight) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(this.rect.width(), this.rect.height());
        layoutParams.leftMargin = this.rect.left;
        layoutParams.topMargin = Math.max(0, this.rect.top - marginHeight);
        relativeLayout.addView(this, layoutParams);

    }

    class OverLayClick implements OnClickListener {
        UniversalCopyActivity.CopyListener copyListener = null;
        CopyOverlayView copyOverlayView = null;

        public OverLayClick(UniversalCopyActivity.CopyListener copyListener, CopyOverlayView copyOverlayView) {
            this.copyListener = copyListener;
            this.copyOverlayView = copyOverlayView;
        }

        @Override
        public void onClick(View v) {
            copyOverlayView.setActive(!copyOverlayView.active);
            copyListener.listen(copyOverlayView);
        }
    }

    class OverLayLongClick implements OnLongClickListener {
        UniversalCopyActivity.CopyListener copyListener = null;
        CopyOverlayView copyOverlayView = null;

        public OverLayLongClick(UniversalCopyActivity.CopyListener copyListener, CopyOverlayView copyOverlayView) {
            this.copyListener = copyListener;
            this.copyOverlayView = copyOverlayView;
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(getContext(), getText(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
