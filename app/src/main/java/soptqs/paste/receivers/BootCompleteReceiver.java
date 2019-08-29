package soptqs.paste.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import soptqs.paste.Paste;
import soptqs.paste.services.ClipBoardListener;
import soptqs.paste.utils.PreferenceUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(Paste.getContext());
        if (prefs.getBoolean(PreferenceUtils.PREF_AUTOBOOT, false)) {
            Intent intent1 = new Intent(context, ClipBoardListener.class);
            context.startService(intent1);
        }
    }
}
