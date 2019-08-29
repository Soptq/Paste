package soptqs.paste.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichType;
import com.zzhoujay.richtext.callback.Callback;

import soptqs.paste.R;

public class ViewerActivity extends AppCompatActivity {

    private TextView viewer;
    private ProgressBar progressBar;
    private Intent intent;
    private String content;
    private Toolbar toolbar;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.invalidate();
        content = intent.getStringExtra("content");
        RichText.clear(ViewerActivity.this);
        RichText.from(content)
                .type(RichType.markdown)
                .autoFix(true)
                .bind(ViewerActivity.this)
                .done(new Callback() {
                    @Override
                    public void done(boolean imageLoadDone) {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .into(viewer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceStage) {
        super.onCreate(savedInstanceStage);
        Log.e("tag", "onCreate: ");
        setContentView(R.layout.activity_viewer);
        RichText.initCacheDir(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewer = findViewById(R.id.viewer);
        progressBar = findViewById(R.id.loader);

        intent = getIntent();
        content = intent.getStringExtra("content");
        RichText.from(content)
                .type(RichType.markdown)
                .autoFix(true)
                .bind(ViewerActivity.this)
                .done(new Callback() {
                    @Override
                    public void done(boolean imageLoadDone) {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .into(viewer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RichText.clear(ViewerActivity.this);
        RichText.recycle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.reader_size:
                new MaterialDialog.Builder(ViewerActivity.this)
                        .title(R.string.text_size_title)
                        .inputType(InputType.TYPE_CLASS_NUMBER)
                        .inputRange(1,3)
                        .input(String.valueOf(viewer.getTextSize()), null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                if (!"".equals(input)){
                                    viewer.setTextSize(TypedValue.COMPLEX_UNIT_PX, Integer.parseInt(input.toString()));
                                }
                            }
                        })
                        .show();
            default:
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        return true;
    }
}
