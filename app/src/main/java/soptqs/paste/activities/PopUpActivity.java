package soptqs.paste.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import soptqs.paste.floatwindow.FloatWindow;

/**
 * Created by S0ptq on 2018/2/7.
 */

public class PopUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanced){
        super.onCreate(savedInstanced);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                FloatWindow.showPopupWindow(getApplicationContext());
            } else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else FloatWindow.showPopupWindow(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

}
