package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.emoji.Emoji;
import com.alorma.github.emoji.EmojisActivity;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by Bernat on 11/07/2015.
 */
public class ContentEditorActivity extends BackActivity implements Toolbar.OnMenuItemClickListener {

    private static final String HINT = "HINT";
    private static final String PREFILL = "PREFILL";
    private static final String REPO_INFO = "REPO_INFO";
    private static final String ISSUE_NUM = "ISSUE_NUM";
    private static final String ALLOW_EMPTY = "ALLOW_EMPTY";
    private static final String BACK_IS_OK = "BACK_IS_OK";

    public static final String CONTENT = "CONTENT";
    private static final int EMOJI_REQUEST = 1515;

    private EditText editText;
    private Toolbar toolbarExtra;

    private boolean allowEmpty;
    private boolean backIsOk;

    public static Intent createLauncherIntent(Context context, RepoInfo repoInfo, int issueNum, String hint, String prefill, boolean allowEmpty, boolean backIsOk) {
        Intent intent = new Intent(context, ContentEditorActivity.class);

        intent.putExtra(HINT, hint);
        intent.putExtra(PREFILL, prefill);
        intent.putExtra(REPO_INFO, repoInfo);
        intent.putExtra(ISSUE_NUM, issueNum);
        intent.putExtra(ALLOW_EMPTY, allowEmpty);
        intent.putExtra(BACK_IS_OK, backIsOk);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_editor);

        if (getIntent().getExtras() != null) {

            findViews();

            toolbarExtra.inflateMenu(R.menu.content_editor_extra);
            toolbarExtra.setOnMenuItemClickListener(this);

            String hint = getIntent().getExtras().getString(HINT);

            if (!TextUtils.isEmpty(hint)) {
                editText.setHint(hint);
            }

            String content = getIntent().getExtras().getString(PREFILL);

            if (!TextUtils.isEmpty(content)) {
                RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
                int issueNumber = getIntent().getExtras().getInt(ISSUE_NUM);

                String htmlCode = HtmlUtils.format(content).toString();
                HttpImageGetter imageGetter = new HttpImageGetter(this);

                imageGetter.repoInfo(repoInfo);
                imageGetter.bind(editText, htmlCode, issueNumber);

                editText.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
                editText.setSelection(editText.getText().length());

            }

            allowEmpty = getIntent().getExtras().getBoolean(ALLOW_EMPTY, false);

            if (!allowEmpty) {
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (TextUtils.isEmpty(editText.getText())) {
                            invalidateOptionsMenu();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            backIsOk = getIntent().getExtras().getBoolean(BACK_IS_OK, false);

        } else {
            finish();
        }
    }

    private void findViews() {
        editText = (EditText) findViewById(R.id.edit);
        toolbarExtra = (Toolbar) findViewById(R.id.toolbarExtra);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.content_editor, menu);

        MenuItem okItem = menu.findItem(R.id.action_ok);

        if (okItem != null) {
            IconicsDrawable iconicsDrawable = new IconicsDrawable(this, Octicons.Icon.oct_check).actionBar().color(Color.WHITE);
            okItem.setIcon(iconicsDrawable);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem okItem = menu.findItem(R.id.action_ok);

        okItem.setEnabled(allowEmpty || !TextUtils.isEmpty(editText.getText()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_ok:
                Intent intent = new Intent();
                intent.putExtra(CONTENT, editText.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.add_content_editor_emojis:
                Intent intentEmojis = new Intent(this, EmojisActivity.class);
                startActivityForResult(intentEmojis, EMOJI_REQUEST);
                break;
            case R.id.add_content_editor_source:
                editText.append(" ```");
                editText.append("\n");
                editText.append("\n");
                editText.append(" ```");

                editText.setSelection(editText.length() - 4);
                break;
            case R.id.add_content_editor_picture:
                showAddPicture();
                break;
        }
        return true;
    }

    private void showAddPicture() {
        new MaterialDialog.Builder(this)
                .title(R.string.addPicture)
                .content(R.string.addPictureContent)
                .input(R.string.addPictureHint, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        editText.append("\n");
                        editText.append("\n");
                        editText.append("![]");
                        editText.append("(" + charSequence.toString() + ")");
                        editText.append("\n");
                        editText.append("\n");
                    }
                })
                .neutralText(R.string.cancel)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case EMOJI_REQUEST:
                    Emoji emoji = data.getParcelableExtra(EmojisActivity.EMOJI);
                    editText.append(" :" + emoji.getKey() + ": ");
                    break;
            }
        }
    }

    @Override
    protected void close() {
        int result = RESULT_CANCELED;
        if (!allowEmpty) {
            finish();
            return;
        } else {
            if (TextUtils.isEmpty(editText.getText())) {
                result = RESULT_OK;
            }
            if (backIsOk) {
                result = RESULT_OK;
            }

            Intent intent = new Intent();
            intent.putExtra(CONTENT, editText.getText().toString());

            setResult(result, intent);
            finish();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        onOptionsItemSelected(item);
        return true;
    }
}
