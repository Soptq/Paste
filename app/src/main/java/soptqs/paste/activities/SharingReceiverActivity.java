package soptqs.paste.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.blankj.utilcode.util.ToastUtils;
import org.apache.commons.lang3.StringUtils;
import soptqs.paste.R;
import soptqs.paste.database.DataProcess;
import soptqs.paste.utils.ImageUtils;
import soptqs.paste.utils.PreferenceUtils;
import soptqs.paste.utils.SaveImageToRaw;

import java.util.ArrayList;

/**
 * Created by S0ptq on 2018/3/10.
 */

public class SharingReceiverActivity extends Activity {

    private SharedPreferences prefs;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (!prefs.getBoolean(PreferenceUtils.PREF_ENABLE_SHARE, true)) {
            ToastUtils.setBgColor(ContextCompat.getColor(SharingReceiverActivity.this, R.color.colorRed));
            ToastUtils.setMsgColor(Color.WHITE);
            ToastUtils.showLong(R.string.share_disabled_toast);
        } else {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            Log.e("type", "onCreate: " + type);
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    dealTextMessage(intent);
                } else if (type.startsWith("image/")) {
                    dealPicStream(intent);
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {
                    dealMultiplePicStream(intent);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    void dealTextMessage(Intent intent) {
        String share = intent.getStringExtra(Intent.EXTRA_TEXT);
        String title = intent.getStringExtra(Intent.EXTRA_TITLE);
        Log.e(" ", "dealTextMessage: " + share);
        DataProcess.addToBoard(share,
                String.valueOf(System.currentTimeMillis()),
                null,
                true,
                false);
    }

    void dealPicStream(Intent intent) {
        float ps = (float) prefs.getInt(PreferenceUtils.PREF_COMPRESSOR_INT, 100) / 100;
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.e("originurl", "dealPicStream: " + uri);
        String time = String.valueOf(System.currentTimeMillis());
        new SaveImageToRaw(this).savaImage(notificationManager,
                time + ".png",
                ImageUtils.small(ImageUtils.getBitmapFromUri(uri, this), ps));
        DataProcess.addToBoard(time,
                String.valueOf(System.currentTimeMillis()),
                null,
                true,
                true);
    }

    void dealMultiplePicStream(Intent intent) {
        float ps = (float) prefs.getInt(PreferenceUtils.PREF_COMPRESSOR_INT, 100) / 100;
        ArrayList<Uri> arrayList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        ArrayList list = new ArrayList();
        for (Uri str : arrayList) {
            String time = String.valueOf(System.currentTimeMillis());
            new SaveImageToRaw(this).savaImage(notificationManager,
                    time + ".png",
                    ImageUtils.small(ImageUtils.getBitmapFromUri(str, this), ps));
            list.add(time);
        }
        String list_str = StringUtils.join(list, ",");
        DataProcess.addToBoard(list_str,
                String.valueOf(System.currentTimeMillis()),
                null,
                true,
                true);
    }
}
