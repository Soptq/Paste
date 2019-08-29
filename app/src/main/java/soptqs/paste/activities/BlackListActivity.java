package soptqs.paste.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.itemanimators.AlphaInAnimator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soptqs.paste.R;
import soptqs.paste.adapters.BlackListAdapter;
import soptqs.paste.database.AppItem;
import soptqs.paste.utils.AppUtils;

/**
 * Created by S0ptq on 2018/2/26.
 */

public class BlackListActivity extends AppCompatActivity {

    Toolbar toolbar;
    private List<AppItem> appItemList = new ArrayList<>();
    private ProgressBar progressBar;
    private MaterialSearchView searchView;
    private BlackListAdapter adapter;
    private List<AppItem> appItemListBack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceStage) {
        super.onCreate(savedInstanceStage);
        setContentView(R.layout.activity_blacklist);
        final SwipeMenuRecyclerView recyclerView = findViewById(R.id.bl_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new AlphaInAnimator());

        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.bl_progress);
        searchView = findViewById(R.id.search_view);
        toolbar.setTitle(R.string.title_bl);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Handler handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                super.run();
                final List<AppItem> appInfoList = AppUtils.scanLocalInstallAppList(BlackListActivity.this.getPackageManager());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        appItemList.addAll(appInfoList);
                        Log.e("bl", "onSearchViewShown: show" + appItemList.size());
                        adapter = new BlackListAdapter(appItemList);
                        recyclerView.setAdapter(adapter);
                        appItemListBack.addAll(appItemList);
                    }
                });
            }
        }.start();

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("bl", "onSearchViewShown: submit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("bl", "onSearchViewShown: change");
                List<AppItem> searchList = search(newText, appItemListBack);
                appItemList.clear();
                appItemList.addAll(searchList);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Log.e("bl", "onSearchViewShown: show");
                appItemList.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onSearchViewClosed() {
                Log.e("bl", "onSearchViewShown: hide");
                Log.e("bl", "onSearchViewShown: hide " + appItemListBack.size());
                appItemList.clear();
                appItemList.addAll(appItemListBack);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blacklist, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    public List search(String name, List list) {
        List results = new ArrayList();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < list.size(); i++) {
            Matcher nMatcher = pattern.matcher(((AppItem) list.get(i)).getName());
            if (nMatcher.find()) {
                results.add(list.get(i));
            } else {
                Matcher pnMatcher = pattern.matcher(((AppItem) list.get(i)).getPakageName());
                if (pnMatcher.find()) {
                    results.add(list.get(i));
                }
            }
        }
        return results;
    }

}
