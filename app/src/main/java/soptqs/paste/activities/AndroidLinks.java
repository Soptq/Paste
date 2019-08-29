package soptqs.paste.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.OnRecommendedClickedListener;
import me.drakeet.support.about.Recommended;
import me.drakeet.support.about.extension.RecommendedLoaderDelegate;
import me.drakeet.support.about.provided.PicassoImageLoader;
import soptqs.paste.R;

/**
 * Created by S0ptq on 2018/2/24.
 */

@SuppressLint("SetTextI18n")
@SuppressWarnings("SpellCheckingInspection")
public class AndroidLinks extends AbsAboutActivity
        implements OnRecommendedClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImageLoader(new PicassoImageLoader());
        setOnRecommendedClickedListener(this);
    }


    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setImageResource(R.drawable.ic_group_black_24dp);
        slogan.setText("Links");
        version.setText("Share Your Loves");
    }


    @Override
    protected void onItemsCreated(@NonNull Items items) {
        // Load more Recommended items from remote server asynchronously
        RecommendedLoaderDelegate.attach(this, items.size());
        // or
        // RecommendedLoader.getInstance().loadInto(this, items.size());
    }


    @Override
    public boolean onRecommendedClicked(@NonNull View itemView, @NonNull Recommended recommended) {
        return false;
    }
}
