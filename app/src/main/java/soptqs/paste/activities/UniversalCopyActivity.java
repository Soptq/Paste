package soptqs.paste.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import soptqs.paste.R;
import soptqs.paste.nodes.CopyNode;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.views.CopyOverlayView;

/**
 * Created by S0ptq on 2018/3/26.
 * Universal Copy Activity
 * To handle user interface
 */

public class UniversalCopyActivity extends AppCompatActivity implements ActionMode.Callback {
    private List<CopyNode> copyNodes;
    private List<CopyOverlayView> copyOverlayViews;
    private RelativeLayout relativeLayout;
    private int statusBarHeight;
    private CopyListener copyListener;

    private Toolbar toolbar;
    private FloatingActionButton exitFullScreen;

    private ActionMode actionMode;
    private boolean m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        exitFullScreen = findViewById(R.id.fab_exitfullscreen);
        exitFullScreen.hide(false);
        exitFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setVisibility(View.VISIBLE);
                exitFullScreen.hide(true);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(toolbar, "alpha", 0f, 1f)
                );
                animatorSet.setDuration(100);
                animatorSet.start();
            }
        });

        copyListener = new CopyImpl(this);
        copyOverlayViews = new ArrayList<>();
        relativeLayout = findViewById(R.id.relativelayout);
        copyNodes = new ArrayList<>();
        ArrayList copyNodes = getIntent().getParcelableArrayListExtra("copy_nodes");
        statusBarHeight = AppUtils.getStatusBarHeight(this);
        if (copyNodes != null && copyNodes.size() > 0) {
            CopyNode[] nodes = (CopyNode[]) copyNodes.toArray(new CopyNode[0]);
            Arrays.sort(nodes, new CopyNodeComparator());
            for (int i = 0; i < nodes.length; ++i) {
                addNode(nodes[i], statusBarHeight);
            }
        }

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (copyOverlayViews.size() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < copyOverlayViews.size(); i++) {
                        stringBuilder.append(copyOverlayViews.get(i).getText());
                        if (i + 1 < copyOverlayViews.size()) {
                            stringBuilder.append("\n");
                        }
                    }
                    AppUtils.copy(UniversalCopyActivity.this, stringBuilder.toString());
                    ToastUtils.setBgColor(getResources().getColor(R.color.colorgreen));
                    ToastUtils.showLong(R.string.universe_copy_success);
                    onBackPressed();
                } else {
                    ToastUtils.setBgColor(getResources().getColor(R.color.colorRed));
                    ToastUtils.showLong(R.string.universe_copy_no_content);
                }
            }
        });

    }

    private void addNode(CopyNode copyNode, int statusBarHeight) {
        new CopyOverlayView(this, copyNode, copyListener).addView(relativeLayout, statusBarHeight);
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_universal_copy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            case R.id.universal_fullscreen:
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(toolbar, "alpha", 1f, 0f)
                );
                animatorSet.setDuration(100);
                animatorSet.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setVisibility(View.INVISIBLE);
                        exitFullScreen.show(true);
                        exitFullScreen.bringToFront();
                    }
                }, 100);
                break;
            default:
        }
        return true;
    }

    public interface CopyListener {
        void listen(CopyOverlayView copyOverlayView);
    }

    class CopyImpl implements CopyListener {
        UniversalCopyActivity UniversalCopyActivity;

        public CopyImpl(UniversalCopyActivity UniversalCopyActivity) {
            this.UniversalCopyActivity = UniversalCopyActivity;
        }

        @Override
        public void listen(CopyOverlayView copyOverlayView) {
            if (copyOverlayView.isActive()) {
                UniversalCopyActivity.copyOverlayViews.add(copyOverlayView);
                if (UniversalCopyActivity.actionMode == null) {
                    UniversalCopyActivity.actionMode = UniversalCopyActivity.startActionMode(UniversalCopyActivity);
                    return;
                }
                return;
            }
            UniversalCopyActivity.copyOverlayViews.remove(copyOverlayView);
            if (UniversalCopyActivity.copyOverlayViews.size() < 0 && UniversalCopyActivity.actionMode != null) {
                UniversalCopyActivity.m = true;
                UniversalCopyActivity.actionMode.finish();
            }
        }
    }

    public class CopyNodeComparator implements Comparator<CopyNode> {
        //按面积从大到小排序
        public int compare(CopyNode o1, CopyNode o2) {
            long o1Size = o1.caculateSize();
            long o2Size = o2.caculateSize();
            return Long.compare(o2Size, o1Size);
        }
    }

}
