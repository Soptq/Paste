package soptqs.paste.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import soptqs.paste.Paste;

/**
 * Created by S0ptq on 2018/2/5.
 */

public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Paste paste = (Paste) getContext().getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public abstract String getTitle(Context context);

    public void onSelect() {

    }
}
