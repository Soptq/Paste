package soptqs.paste.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import soptqs.paste.R;

/**
 * Created by S0ptq on 2018/2/6.
 */

public class PasteToastUtils {
    public static PasteToastUtils pasteToastUtils;
    private Toast toast;

    private PasteToastUtils() {
    }

    public static PasteToastUtils getPasteToastUtils() {
        if (pasteToastUtils == null) {
            pasteToastUtils = new PasteToastUtils();
        }
        return pasteToastUtils;
    }

    public void ToastShow(Context context, ViewGroup root) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_view, root);
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 100);
        toast.setView(view);
        toast.show();
    }

    public void ToastCancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
