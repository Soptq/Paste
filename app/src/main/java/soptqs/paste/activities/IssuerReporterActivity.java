package soptqs.paste.activities;


import android.os.Bundle;
import com.heinrichreimersoftware.androidissuereporter.model.github.ExtraInfo;
import com.heinrichreimersoftware.androidissuereporter.model.github.GithubTarget;
import soptqs.paste.activities.BaseActivity.IssueBaseActivity;

/**
 * Created by S0ptq on 2018/2/21.
 */

public class IssuerReporterActivity extends IssueBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMinimumDescriptionLength(10);
    }


    @Override
    public GithubTarget getTarget() {
        return new GithubTarget("Soptq", "Paste");
    }

    @Override
    public void onSaveExtraInfo(ExtraInfo extraInfo) {
        extraInfo.put("Info 1", "logcat");
        extraInfo.put("Info 2", true);
    }

}
