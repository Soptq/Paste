package soptqs.paste.services.assistant;

import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

/**
 * Created by S0ptq on 2018/2/11.
 */

public class AssistantSessionService extends VoiceInteractionSessionService {

    @Override
    public VoiceInteractionSession onNewSession(Bundle bundle) {
        return new AssistantSession(this);
    }
}
