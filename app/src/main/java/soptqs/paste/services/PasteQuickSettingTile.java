package soptqs.paste.services;

import android.annotation.TargetApi;
import android.os.Handler;
import android.service.quicksettings.TileService;

import soptqs.paste.floatwindow.FloatWindow;
import soptqs.paste.utils.AppUtils;

/**
 * Created by S0ptq on 2018/3/10.
 * A QuickSetting Tile to show popup Window
 * API >= 24 Nought
 */

@TargetApi(24)
public class PasteQuickSettingTile extends TileService {
    @Override
    public void onTileAdded() {

    }

    @Override
    public void onTileRemoved() {

    }

    @Override
    public void onClick() {
        AppUtils.collapseStatusBar(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FloatWindow.showPopupWindow(PasteQuickSettingTile.this);
            }
        }, 300);
    }

    @Override
    public void onStartListening() {

    }

    @Override
    public void onStopListening() {

    }
}
