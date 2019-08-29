package soptqs.paste;

import android.content.Context;
import android.content.Intent;
import com.blankj.utilcode.util.Utils;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import james.colorpickerdialog.ColorPicker;
import org.litepal.LitePal;
import soptqs.paste.services.ClipBoardListener;

import java.util.ArrayList;
import java.util.List;

public class Paste extends ColorPicker {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        LitePal.initialize(this);
        LitePal.getDatabase();
        List<OnActivityResultListener> onActivityResultListeners = new ArrayList<>();
        context = getApplicationContext();
        Intent startIntent = new Intent(this, ClipBoardListener.class);
        startService(startIntent);
        Utils.init(context);
    }
}
