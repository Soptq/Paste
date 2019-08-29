package soptqs.paste.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.util.Objects;

import soptqs.paste.R;

/**
 * Created by S0ptq on 2018/2/18.
 */

public class ShortCut extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent addShortcut;
        if (Objects.equals(getIntent().getAction(), Intent.ACTION_CREATE_SHORTCUT)) {
            /*初始化添加快捷图标的Intent*/
            addShortcut = new Intent();
            addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, "PopUp Please");
            Parcelable icon = Intent.ShortcutIconResource.fromContext(this, R.mipmap.float_button);
            addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            Intent popUp = new Intent(this, PopUpActivity.class);
            popUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, popUp);
            /*设置Result*/
            //因为Action是由Launcher通过startActivityForResult这个方法发出的。
            setResult(RESULT_OK, addShortcut);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
