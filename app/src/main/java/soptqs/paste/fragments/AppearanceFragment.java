package soptqs.paste.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import soptqs.paste.R;
import soptqs.paste.database.CardItem;

import java.util.ArrayList;
import java.util.List;

public class AppearanceFragment extends BaseFragment {

    private RecyclerView colorRecyclerView;
    private TextView cardWidth;
    private AppCompatSeekBar cardWidthSeekBar;
    private TextView windowHeight;
    private AppCompatSeekBar windowHeightSeekBar;

    private List<CardItem> cardItemList = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appearance, container, false);
        colorRecyclerView = view.findViewById(R.id.appearance_color_recyclerView);
        cardWidth = view.findViewById(R.id.appearance_card_width);
        cardWidthSeekBar = view.findViewById(R.id.appearance_card_width_seekbar);
        windowHeight = view.findViewById(R.id.appearance_window_height);
        windowHeightSeekBar = view.findViewById(R.id.appearance_window_height_seekbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        initView();
        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.fragment_4);
    }

    private void initView() {

    }
}
