package soptqs.paste.services;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import com.blankj.utilcode.util.ToastUtils;
import org.litepal.crud.DataSupport;
import soptqs.paste.R;
import soptqs.paste.constants.RegexConstants;
import soptqs.paste.database.ClipSaves;
import soptqs.paste.database.DataProcess;
import soptqs.paste.floatwindow.FloatWindow;
import soptqs.paste.utils.*;

import java.util.List;

import static soptqs.paste.database.DataProcess.addToBoard;

/**
 * Created by S0ptq on 2018/2/7.
 * Clipboard Listener Service and General in-app boardcast receiver center
 */

public class ClipBoardListener extends Service {
    public static final String TAG = "ClipListenerService";

    private final static int MODE_COPY = 2;
    private final static int MODE_LOCK = 3;
    private final static int MODE_APPEND = 4;

    CharSequence addedText;
    String clipCreateTime;
    LockReceiver lockReceiver;
    Boolean isAppending = false;
    Boolean isLocking = false;
    private SharedPreferences prefs;
    private ClipboardManager manager;
    private NotificationManager notificationManager;
    private String packagename;
    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            switch (prefs.getInt(PreferenceUtils.PREF_SERVICE_MODE, 2)) {
                case MODE_COPY:
                    isAppending = false;
                    switch (prefs.getInt(PreferenceUtils.PREF_WORK_MODE, 1)) {
                        case PreferenceUtils.PREF_MODE_ROOT:
                            if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                                addedText = manager.getPrimaryClip().getItemAt(0).getText();
                                if (!RegexConstants.isBlocking(addedText.toString())) {
                                    clipCreateTime = String.valueOf(System.currentTimeMillis());
                                    String request = SU.getSU().runCommand("dumpsys activity | grep \"mFocusedActivity\"");
                                    if (!TextUtils.isEmpty(request)) {
                                        String requests[] = request.split(" ")[3].split("/");
                                        packagename = requests[0];
                                    }
                                    if (packagename == null) packagename = "UNKNOWN";
                                    if (!DataProcess.checkBL(packagename)) {
                                        try {
                                            addToBoard(addedText.toString(), clipCreateTime, packagename, false, false);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
                                            updateNotification();
                                        }
                                    }
                                } else {
                                    isAppending = false;
                                    isLocking = !isLocking;
                                    if (isLocking) {
                                        String lockContent = prefs.getString(PreferenceUtils.PREF_LOCK_WHAT, null);
                                        if (lockContent != null) {
                                            restoreClipBoard(ClipBoardListener.this, lockContent);
                                        } else {
                                            lockContent = DataSupport.findLast(ClipSaves.class).getContent();
                                            restoreClipBoard(ClipBoardListener.this, lockContent);
                                        }
                                    }
                                }
                            }
                            break;
                        case PreferenceUtils.PREF_MODE_ACCESSIBILITY:
                            if (prefs.getBoolean(PreferenceUtils.PREF_TIPS_ENABLE, true)
                                    && !AppUtils.isAccessibilitySettingsOn(ClipBoardListener.this)
                                    && prefs.getInt(PreferenceUtils.PREF_CARD_PREFER, 0) == 0) {
                                FloatWindow.hidePopupWindow();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(ClipBoardListener.this)) {
                                    ToastUtils.setBgColor(ContextCompat.getColor(ClipBoardListener.this, R.color.colorRed));
                                    ToastUtils.setMsgColor(Color.WHITE);
                                    ToastUtils.showLong(R.string.draw_overlay);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ClipBoardListener.this);
                                    builder.setTitle(R.string.permission_title1);
                                    builder.setMessage(R.string.permission_content1);
                                    builder.setCancelable(true);
                                    builder.setPositiveButton(R.string.turnon_access, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });
                                    builder.setNegativeButton(R.string.acknowledge, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                                                addedText = manager.getPrimaryClip().getItemAt(0).getText();
                                                if (!RegexConstants.isBlocking(addedText.toString())) {
                                                    clipCreateTime = String.valueOf(System.currentTimeMillis());
                                                    try {
                                                        packagename = RsenAccessibilityService.foregroundPackageName();
                                                    } catch (Exception e) {
                                                        packagename = "UNKNOWN";
                                                    }
                                                    if (!DataProcess.checkBL(packagename)) {
                                                        try {
                                                            addToBoard(addedText.toString(), clipCreateTime, packagename, false, false);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        if (prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
                                                            updateNotification();
                                                        }
                                                    }
                                                } else {
                                                    isAppending = false;
                                                    isLocking = !isLocking;
                                                    if (isLocking) {
                                                        String lockContent = prefs.getString(PreferenceUtils.PREF_LOCK_WHAT, null);
                                                        if (lockContent != null) {
                                                            restoreClipBoard(ClipBoardListener.this, lockContent);
                                                        } else {
                                                            lockContent = DataSupport.findLast(ClipSaves.class).getContent();
                                                            restoreClipBoard(ClipBoardListener.this, lockContent);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    final AlertDialog dialog = builder.create();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                                    } else
                                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                                    dialog.show();
                                }
                            } else {
                                if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                                    addedText = manager.getPrimaryClip().getItemAt(0).getText();
                                    if (!RegexConstants.isBlocking(addedText.toString())) {
                                        clipCreateTime = String.valueOf(System.currentTimeMillis());
                                        try {
                                            packagename = RsenAccessibilityService.foregroundPackageName();
                                        } catch (Exception e) {
                                            packagename = "UNKNOWN";
                                        }
                                        if (!DataProcess.checkBL(packagename)) {
                                            try {
                                                addToBoard(addedText.toString(), clipCreateTime, packagename, false, false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if (prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
                                                updateNotification();
                                            }
                                        }
                                    } else {
                                        isAppending = false;
                                        isLocking = !isLocking;
                                        if (isLocking) {
                                            String lockContent = prefs.getString(PreferenceUtils.PREF_LOCK_WHAT, null);
                                            if (lockContent != null) {
                                                restoreClipBoard(ClipBoardListener.this, lockContent);
                                            } else {
                                                lockContent = DataSupport.findLast(ClipSaves.class).getContent();
                                                restoreClipBoard(ClipBoardListener.this, lockContent);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case MODE_LOCK:
                    isAppending = false;
                    isLocking = !isLocking;
                    if (isLocking) {
                        String lockContent = prefs.getString(PreferenceUtils.PREF_LOCK_WHAT, null);
                        if (lockContent != null) {
                            restoreClipBoard(ClipBoardListener.this, lockContent);
                        } else {
                            lockContent = DataSupport.findLast(ClipSaves.class).getContent();
                            restoreClipBoard(ClipBoardListener.this, lockContent);
                        }
                    }
                    break;
                case MODE_APPEND:
                    if (!isAppending) {
                        prefs.edit().putString(PreferenceUtils.PREF_TEMP_APPEND, "").apply();
                        isAppending = true;
                    }
                    if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {
                        addedText = manager.getPrimaryClip().getItemAt(0).getText();
                        String string = prefs.getString(PreferenceUtils.PREF_TEMP_APPEND, "");
                        string = string + addedText;
                        prefs.edit().putString(PreferenceUtils.PREF_TEMP_APPEND, string).apply();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private static void restoreClipBoard(Context context, String string) {
        Log.e(TAG, "restoreClipBoard: " + string);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, string);
        if (clipData != null) {
            clipboard.setPrimaryClip(clipData);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Boolean isLive = true;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        lockReceiver = new LockReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("soptq.intent.notification.UPDATE");
        intentFilter.addAction("soptq.intent.SHOW_POPUPWINDOW");
        intentFilter.addAction("soptq.intent.COPY_1");
        intentFilter.addAction("soptq.intent.COPY_2");
        intentFilter.addAction("soptq.intent.COPY_3");
        intentFilter.addAction("soptq.intent.COPY_4");
        registerReceiver(lockReceiver, intentFilter);
        onCreateNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        manager.addPrimaryClipChangedListener(mPrimaryClipChangedListener);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.removePrimaryClipChangedListener(mPrimaryClipChangedListener);
        FloatWindow.hidePopupWindow();
        finishNotification();
        unregisterReceiver(lockReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isAlive() {
        return isAlive();
    }

    public void onCreateNotification(){
        if (!prefs.getBoolean(PreferenceUtils.PREF_NOTI_SHOW, true)) {
            return;
        }
        PendingIntent pendingIntent = null;
        if (prefs.getBoolean(PreferenceUtils.PREF_NOTIENTRY, true)) {
            Intent intent = new Intent("soptq.intent.SHOW_POPUPWINDOW");
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, null)
                .setSmallIcon(R.mipmap.ic_smallicon)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setColor(getResources().getColor(R.color.colorAPPDark))
                .setOngoing(true);
        if (prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
            builder.setCustomContentView(getContentView(true))
                    .setCustomBigContentView(getContentView(false));
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
            builder.setPriority(NotificationCompat.PRIORITY_MIN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
            notificationManager.createNotificationChannel(new NotificationChannel(this.getResources().getString(R.string.channel_service),
                    this.getResources().getString(R.string.channel_service), NotificationManager.IMPORTANCE_HIGH));
            builder.setChannelId(this.getResources().getString(R.string.channel_service));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
            notificationManager.createNotificationChannel(new NotificationChannel(this.getResources().getString(R.string.channel_service),
                    this.getResources().getString(R.string.channel_service), NotificationManager.IMPORTANCE_MIN));
            builder.setChannelId(this.getResources().getString(R.string.channel_service));
        }
        startForeground(1004, builder.build());
//        notificationManager.notify(1004, builder.build());
    }

    private RemoteViews getContentView(boolean isCollapsed) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), isCollapsed ? R.layout.custom_notification : R.layout.custom_notification_expand);
        return dataProcess(remoteViews);
    }

    private RemoteViews dataProcess(RemoteViews remoteViews) {
        int count = DataSupport.count(ClipSaves.class);
        List<ClipSaves> clipSavesList = DataSupport.order("id desc").limit(4).find(ClipSaves.class);
        SimpleDateFormatThreadSafe sdf = new SimpleDateFormatThreadSafe("yyyy-MM-dd");
        Intent intent1 = new Intent("soptq.intent.COPY_1");
        PendingIntent pending1 = PendingIntent.getBroadcast(this, 1, intent1, 0);
        Intent intent2 = new Intent("soptq.intent.COPY_2");
        PendingIntent pending2 = PendingIntent.getBroadcast(this, 2, intent2, 0);
        Intent intent3 = new Intent("soptq.intent.COPY_3");
        PendingIntent pending3 = PendingIntent.getBroadcast(this, 3, intent3, 0);
        Intent intent4 = new Intent("soptq.intent.COPY_4");
        PendingIntent pending4 = PendingIntent.getBroadcast(this, 4, intent4, 0);
        switch (count) {
            case 0:
                remoteViews.setViewVisibility(R.id.custom_noti__coll_1, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_1, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_2, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_3, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_4, View.GONE);
                break;
            case 1:
                String name11 = "unknown";
                try {
                    name11 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(0).getPakageName(), 0)).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                remoteViews.setTextViewText(R.id.custom_noti_coll_appName1, name11);
                remoteViews.setTextViewText(R.id.custom_noti_coll_appTime1, sdf.format(Long.parseLong(clipSavesList.get(0).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_coll_content1, clipSavesList.get(0).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_coll_clip1, pending1);

                remoteViews.setTextViewText(R.id.custom_noti_appName1, name11);
                remoteViews.setTextViewText(R.id.custom_noti_appTime1, sdf.format(Long.parseLong(clipSavesList.get(0).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content1, clipSavesList.get(0).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip1, pending1);

                remoteViews.setViewVisibility(R.id.custom_noti_2, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_3, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_4, View.GONE);
                break;
            case 2:
                String name21 = "unknown";
                String name22 = "unknown";
                try {
                    name21 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(0).getPakageName(), 0)).toString();
                    name22 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(1).getPakageName(), 0)).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                remoteViews.setTextViewText(R.id.custom_noti_coll_appName1, name22);
                remoteViews.setTextViewText(R.id.custom_noti_coll_appTime1, sdf.format(Long.parseLong(clipSavesList.get(1).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_coll_content1, clipSavesList.get(1).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_coll_clip1, pending2);

                remoteViews.setTextViewText(R.id.custom_noti_appName1, name21);
                remoteViews.setTextViewText(R.id.custom_noti_appTime1, sdf.format(Long.parseLong(clipSavesList.get(0).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content1, clipSavesList.get(0).getContent());
                remoteViews.setTextViewText(R.id.custom_noti_appName2, name22);

                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip1, pending1);
                remoteViews.setTextViewText(R.id.custom_noti_appTime2, sdf.format(Long.parseLong(clipSavesList.get(1).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content2, clipSavesList.get(1).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip2, pending2);

                remoteViews.setViewVisibility(R.id.custom_noti_3, View.GONE);
                remoteViews.setViewVisibility(R.id.custom_noti_4, View.GONE);
                break;
            case 3:
                String name31 = "unknown";
                String name32 = "unknown";
                String name33 = "unknown";
                try {
                    name31 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(0).getPakageName(), 0)).toString();
                    name32 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(1).getPakageName(), 0)).toString();
                    name33 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(2).getPakageName(), 0)).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                remoteViews.setTextViewText(R.id.custom_noti_coll_appName1, name32);
                remoteViews.setTextViewText(R.id.custom_noti_coll_appTime1, sdf.format(Long.parseLong(clipSavesList.get(1).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_coll_content1, clipSavesList.get(1).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_coll_clip1, pending2);

                remoteViews.setTextViewText(R.id.custom_noti_appName1, name31);
                remoteViews.setTextViewText(R.id.custom_noti_appTime1, sdf.format(Long.parseLong(clipSavesList.get(0).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content1, clipSavesList.get(0).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip1, pending1);

                remoteViews.setTextViewText(R.id.custom_noti_appName2, name32);
                remoteViews.setTextViewText(R.id.custom_noti_appTime2, sdf.format(Long.parseLong(clipSavesList.get(1).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content2, clipSavesList.get(1).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip2, pending2);

                remoteViews.setTextViewText(R.id.custom_noti_appName3, name33);
                remoteViews.setTextViewText(R.id.custom_noti_appTime3, sdf.format(Long.parseLong(clipSavesList.get(2).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content3, clipSavesList.get(2).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip3, pending3);

                remoteViews.setViewVisibility(R.id.custom_noti_4, View.GONE);
                break;
            default:
                String name41 = "unknown";
                String name42 = "unknown";
                String name43 = "unknown";
                String name44 = "unknown";
                try {
                    name41 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(0).getPakageName(), 0)).toString();
                    name42 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(1).getPakageName(), 0)).toString();
                    name43 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(2).getPakageName(), 0)).toString();
                    name44 = this.getPackageManager().getApplicationLabel(this.getPackageManager().getApplicationInfo(clipSavesList.get(3).getPakageName(), 0)).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                remoteViews.setTextViewText(R.id.custom_noti_coll_appName1, name42);
                remoteViews.setTextViewText(R.id.custom_noti_coll_appTime1, sdf.format(Long.parseLong(clipSavesList.get(1).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_coll_content1, clipSavesList.get(1).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_coll_clip1, pending2);
                remoteViews.setTextViewText(R.id.custom_noti_appName1, name41);
                remoteViews.setTextViewText(R.id.custom_noti_appTime1, sdf.format(Long.parseLong(clipSavesList.get(0).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content1, clipSavesList.get(0).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip1, pending1);
                remoteViews.setTextViewText(R.id.custom_noti_appName2, name42);
                remoteViews.setTextViewText(R.id.custom_noti_appTime2, sdf.format(Long.parseLong(clipSavesList.get(1).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content2, clipSavesList.get(1).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip2, pending2);
                remoteViews.setTextViewText(R.id.custom_noti_appName3, name43);
                remoteViews.setTextViewText(R.id.custom_noti_appTime3, sdf.format(Long.parseLong(clipSavesList.get(2).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content3, clipSavesList.get(2).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip3, pending3);
                remoteViews.setTextViewText(R.id.custom_noti_appName4, name44);
                remoteViews.setTextViewText(R.id.custom_noti_appTime4, sdf.format(Long.parseLong(clipSavesList.get(3).getTime())));
                remoteViews.setTextViewText(R.id.custom_noti_content4, clipSavesList.get(3).getContent());
                remoteViews.setOnClickPendingIntent(R.id.custom_noti_clip4, pending4);
                break;
        }
        return remoteViews;
    }

    private void updateNotification() {
        if (prefs.getBoolean(PreferenceUtils.PREF_NOTICLIP, false)) {
            stopForeground(true);
//        notificationManager.cancel(1004);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCreateNotification();
                }
            }, 100);
        }
    }

    public void finishNotification(){
        notificationManager.cancel(1004);
    }

    public class LockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (FloatWindow.isShown) FloatWindow.hidePopupWindow();
            }
            if ("soptq.intent.notification.UPDATE".equals(intent.getAction())) {
                updateNotification();
            }
            if ("soptq.intent.SHOW_POPUPWINDOW".equals(intent.getAction())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(ClipBoardListener.this)) {
                    ToastUtils.setBgColor(ContextCompat.getColor(ClipBoardListener.this, R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.draw_overlay);
                } else FloatWindow.showPopupWindow(ClipBoardListener.this);
            }
            if ("soptq.intent.COPY_1".equals(intent.getAction())) {
                List<ClipSaves> clipSavesList = DataSupport.order("id desc").limit(4).find(ClipSaves.class);
                ClipData clipData1 = ClipData.newPlainText(null, clipSavesList.get(0).getContent());
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData1);
                PasteToastUtils.getPasteToastUtils().ToastShow(context, null);
            }
            if ("soptq.intent.COPY_2".equals(intent.getAction())) {
                List<ClipSaves> clipSavesList = DataSupport.order("id desc").limit(4).find(ClipSaves.class);
                ClipData clipData2 = ClipData.newPlainText(null, clipSavesList.get(1).getContent());
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData2);
                PasteToastUtils.getPasteToastUtils().ToastShow(context, null);
            }
            if ("soptq.intent.COPY_3".equals(intent.getAction())) {
                List<ClipSaves> clipSavesList = DataSupport.order("id desc").limit(4).find(ClipSaves.class);
                ClipData clipData3 = ClipData.newPlainText(null, clipSavesList.get(2).getContent());
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData3);
                PasteToastUtils.getPasteToastUtils().ToastShow(context, null);
            }
            if ("soptq.intent.COPY_4".equals(intent.getAction())) {
                List<ClipSaves> clipSavesList = DataSupport.order("id desc").limit(4).find(ClipSaves.class);
                ClipData clipData4 = ClipData.newPlainText(null, clipSavesList.get(3).getContent());
                assert clipboard != null;
                clipboard.setPrimaryClip(clipData4);
                PasteToastUtils.getPasteToastUtils().ToastShow(context, null);
            }
        }
    }
}
