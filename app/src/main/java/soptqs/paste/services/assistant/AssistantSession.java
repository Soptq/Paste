package soptqs.paste.services.assistant;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;

/**
 * Created by S0ptq on 2018/2/11.
 */

public class AssistantSession extends VoiceInteractionSession {

    AssistantSession(Context context) {
        super(context);
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
        super.onHandleAssist(data, structure, content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            onBackPressed();
            Intent intent = new Intent("soptq.intent.SHOW_POPUPWINDOW");
            getContext().sendBroadcast(intent);
        }
    }
}
