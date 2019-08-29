package soptqs.paste.dialog;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import com.blankj.utilcode.util.ToastUtils;
import org.apache.commons.lang3.StringUtils;
import ren.qinc.edit.PerformEdit;
import soptqs.paste.R;
import soptqs.paste.database.DataProcess;
import soptqs.paste.utils.AppUtils;
import soptqs.paste.utils.PreferenceUtils;
import soptqs.paste.views.CustomEditView;

/**
 * Created by S0ptq on 2018/2/17.
 */

public class CardEditDialog extends Activity {

    private ImageView cardCheck;
    private ImageView cardUndo;
    private ImageView cardRedo;
    private CustomEditView editText;
    private ImageView insertHttp;
    private ImageView insertMagnet;

    private int category;
    private String content;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setFinishOnTouchOutside(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.dialog_cardedit);

        ImageView cardBack = findViewById(R.id.card_edit_back);
        cardCheck = findViewById(R.id.card_edit_check);
        editText = findViewById(R.id.card_edit_edit);
        cardUndo = findViewById(R.id.card_edit_undo);
        cardRedo = findViewById(R.id.card_edit_redo);
        insertHttp = findViewById(R.id.dialog_edit_insert_http);
        insertMagnet = findViewById(R.id.dialog_edit_insert_magnet);

        editText.setScrollView((ScrollView) findViewById(R.id.edit_scollView));

        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        insertHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertText(editText, "http://");
            }
        });

        insertMagnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertText(editText, "magnet:?xt=urn:btih:");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        category = intent.getIntExtra("category", 0);
        if (category == 0) {
            content = "\n";
        } else {
            content = intent.getStringExtra("content");
            if (content == null) content = "\n";
        }
        final String time = intent.getStringExtra("time");
        editText.setText(content);
        final PerformEdit performEdit = new PerformEdit(editText);

        cardCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.getInt(PreferenceUtils.PREF_WORK_MODE, 1) == PreferenceUtils.PREF_MODE_ACCESSIBILITY &&
                        prefs.getBoolean(PreferenceUtils.PREF_TIPS_ENABLE, true) &&
                        !AppUtils.isAccessibilitySettingsOn(CardEditDialog.this)) {
                    ToastUtils.setBgColor(ContextCompat.getColor(CardEditDialog.this, R.color.colorRed));
                    ToastUtils.setMsgColor(Color.WHITE);
                    ToastUtils.showLong(R.string.edit_tips);
                } else {
                    if (category != 0) {
                        DataProcess.deleteFromBoard(time, CardEditDialog.this);
                    }
                }

                ClipData clipData = ClipData.newPlainText(null,
                        editText.getText());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clipData);
                }

                finish();
            }
        });

        cardUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performEdit.undo();
            }
        });

        cardRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performEdit.redo();
            }
        });
    }

    /**
     * 获取EditText光标所在的位置
     */
    private int getEditTextCursorIndex(EditText mEditText) {
        return mEditText.getSelectionStart();
    }

    /**
     * 向EditText指定光标位置插入字符串
     */
    private void insertText(EditText mEditText, String mText) {
        mEditText.getText().insert(getEditTextCursorIndex(mEditText), mText);
    }

    /**
     * 向EditText指定光标位置删除字符串
     */
    private void deleteText(EditText mEditText) {
        if (!StringUtils.isEmpty(mEditText.getText().toString())) {
            mEditText.getText().delete(getEditTextCursorIndex(mEditText) - 1, getEditTextCursorIndex(mEditText));
        }
    }
}
