package soptqs.paste.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.util.Log;
import com.yhao.floatwindow.FloatWindow;

/**
 * Created by S0ptq on 2018/5/27.
 * Handle Float Button's position when device is orientated
 */

public class FloatButtonOrientationService extends Service {

    int x;
    int y;

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //切换为竖屏

        if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            Log.e("orientation", "onConfigurationChanged: por");
            x = FloatWindow.get().getX();
            y = FloatWindow.get().getY();
            FloatWindow.get().updateX(y);
            FloatWindow.get().updateX(x);
        }
        //切换为横屏
        else if (newConfig.orientation == this.getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            Log.e("orientation", "onConfigurationChanged: lan");
            x = FloatWindow.get().getX();
            y = FloatWindow.get().getY();
            FloatWindow.get().updateX(y);
            FloatWindow.get().updateX(x);
        }
    }
}
