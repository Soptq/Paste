package soptqs.paste.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import soptqs.paste.R;

/**
 * Created by S0ptq on 2018/2/5.
 */

public class HelpFragment extends BaseFragment {

    private View help1View;
    private ExpandableRelativeLayout help1ContentView;
    private View help2View;
    private ExpandableRelativeLayout help2ContentView;
    private View help3View;
    private ExpandableRelativeLayout help3ContentView;
    private View help4View;
    private ExpandableRelativeLayout help4ContentView;
    private View help5View;
    private ExpandableRelativeLayout help5ContentView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        help1View = view.findViewById(R.id.help_1_title_view);
        help1ContentView = view.findViewById(R.id.help_1_content);
        help2View = view.findViewById(R.id.help_2_title_view);
        help2ContentView = view.findViewById(R.id.help_2_content);
        help3View = view.findViewById(R.id.help_3_title_view);
        help3ContentView = view.findViewById(R.id.help_3_content);
        help4View = view.findViewById(R.id.help_4_title_view);
        help4ContentView = view.findViewById(R.id.help_4_content);
        help5View = view.findViewById(R.id.help_5_title_view);
        help5ContentView = view.findViewById(R.id.help_5_content);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                help2ContentView.collapse();
                help3ContentView.collapse();
                help4ContentView.collapse();
                help5ContentView.collapse();
            }
        }, 100);

        help1View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help1ContentView.toggle();
            }
        });

        help2View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help2ContentView.toggle();
            }
        });

        help3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help3ContentView.toggle();
            }
        });

        help4View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help4ContentView.toggle();
            }
        });

        help5View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help5ContentView.toggle();
            }
        });

        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.fragment_3);
    }
}
