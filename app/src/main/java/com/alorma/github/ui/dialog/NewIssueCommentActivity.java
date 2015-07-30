package com.alorma.github.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alorma.github.R;
import com.alorma.github.emoji.Emoji;
import com.alorma.github.emoji.EmojiBitmapLoader;
import com.alorma.github.emoji.EmojisActivity;
import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.issues.NewIssueCommentClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.basesdk.client.BaseClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 06/09/2014.
 */
public class NewIssueCommentActivity extends BackActivity implements BaseClient.OnResultCallback<GithubComment> {

    private static final String ISSUE_INFO = "ISSUE_INFO";
    private static final int EMOJI_CODE = 4524;
    private EditText edit;
    private IssueInfo issueInfo;
    private EmojiBitmapLoader emojiBitmapLoader;
    private TextWatcher bodyTextWatcher;

    public static Intent launchIntent(Context context, IssueInfo issueInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ISSUE_INFO, issueInfo);

        Intent intent = new Intent(context, NewIssueCommentActivity.class);

        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_issue_comment);

        emojiBitmapLoader = new EmojiBitmapLoader();

        if (getIntent().getExtras() != null) {
            issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);

            if (issueInfo != null) {
                edit = (EditText) findViewById(R.id.edit);


                bodyTextWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().contains(":")) {
                            emojiBitmapLoader.parseTextView(edit);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                };

                edit.addTextChangedListener(bodyTextWatcher);
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_issue_comment, menu);

        MenuItem itemSend = menu.findItem(R.id.action_send);
        if (itemSend != null) {
            IconicsDrawable iconDrawable = new IconicsDrawable(this, Octicons.Icon.oct_bug);
            iconDrawable.color(Color.WHITE);
            iconDrawable.actionBarSize();
            itemSend.setIcon(iconDrawable);
        }

        MenuItem emojiMenu = menu.findItem(R.id.action_add_emoji);
        emojiMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        Drawable emojiIcon = new IconicsDrawable(this, Octicons.Icon.oct_octoface).actionBar().color(Color.WHITE);
        emojiMenu.setIcon(emojiIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_send) {
            String body = edit.getText().toString();
            showProgressDialog(R.style.SpotDialog_CommentIssue);
            NewIssueCommentClient client = new NewIssueCommentClient(this, issueInfo, body);
            client.setOnResultCallback(this);
            client.execute();
        } else if (item.getItemId() == R.id.action_add_emoji) {
            Intent intent = new Intent(this, EmojisActivity.class);
            startActivityForResult(intent, EMOJI_CODE);
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EMOJI_CODE && resultCode == RESULT_OK && data != null) {
            Emoji emoji = data.getParcelableExtra(EmojisActivity.EMOJI);
            if (emoji != null) {
                edit.removeTextChangedListener(bodyTextWatcher);
                edit.setText(edit.getText() + " :" + emoji.getKey() + ": ");
                emojiBitmapLoader.parseTextView(edit);
                edit.setSelection(edit.getText().length());
            }
        }
    }

    @Override
    public void onResponseOk(GithubComment githubComment, Response r) {
        hideProgressDialog();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onFail(RetrofitError error) {
        hideProgressDialog();
        ErrorHandler.onError(this, "NewCommentDialog", error);
    }
}
