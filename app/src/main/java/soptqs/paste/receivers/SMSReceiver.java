package soptqs.paste.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsMessage;
import com.blankj.utilcode.util.ToastUtils;
import soptqs.paste.Paste;
import soptqs.paste.R;
import soptqs.paste.database.DataProcess;
import soptqs.paste.utils.PreferenceUtils;
import soptqs.paste.utils.SMSCode;

public class SMSReceiver extends BroadcastReceiver {

    String clipCreateTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs;
        prefs = PreferenceManager.getDefaultSharedPreferences(Paste.getContext());

        if (!prefs.getBoolean(PreferenceUtils.PREF_SMS_ENABLE, false)) return;

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");

        if (pdus == null) return;

        for (Object p : pdus) {
            byte[] sms = (byte[]) p;

            SmsMessage message = SmsMessage.createFromPdu(sms);

            String content = message.getMessageBody();
            String number = message.getDisplayOriginatingAddress();
            SMSCode.SMSInfo smsInfo = SMSCode.findSMSCode(content);

            if (smsInfo != null) {
                String code = "<@PasteResource@>"
                        + smsInfo.code + "<@PasteResourceDivide@>" + number
                        + "<@PasteResource@>";
                ToastUtils.setBgColor(ContextCompat.getColor(context, R.color.colorgreen));
                ToastUtils.setMsgColor(Color.WHITE);
                ToastUtils.showLong(R.string.sms_copied);
                clipCreateTime = String.valueOf(System.currentTimeMillis());
                try {
                    DataProcess.addToBoard(code, clipCreateTime, "soptqs.paste", false, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
