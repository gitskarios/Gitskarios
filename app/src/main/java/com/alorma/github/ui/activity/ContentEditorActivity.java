package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alorma.github.R;
import com.alorma.github.bean.SearchableUser;
import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.emoji.Emoji;
import com.alorma.github.emoji.EmojisActivity;
import com.alorma.github.sdk.bean.dto.response.Contributor;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoContributorsClient;
import com.alorma.github.sdk.services.search.UsersSearchClient;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.gitskarios.core.Pair;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsResultListener;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 11/07/2015.
 */
public class ContentEditorActivity extends BackActivity implements Toolbar.OnMenuItemClickListener, QueryTokenReceiver {

    public static final String CONTENT = "CONTENT";
    private static final String HINT = "HINT";
    private static final String PREFILL = "PREFILL";
    private static final String REPO_INFO = "REPO_INFO";
    private static final String ISSUE_NUM = "ISSUE_NUM";
    private static final String ALLOW_EMPTY = "ALLOW_EMPTY";
    private static final String BACK_IS_OK = "BACK_IS_OK";
    private static final int EMOJI_REQUEST = 1515;

    private RichEditorView editText;
    private Toolbar toolbarExtra;

    private boolean allowEmpty;
    private boolean backIsOk;
    private IssueInfo issueInfo;
    private boolean applied = false;

    public static Intent createLauncherIntent(Context context, String hint, String prefill, boolean allowEmpty, boolean backIsOk) {
        Intent intent = new Intent(context, ContentEditorActivity.class);

        if (hint != null) {
            intent.putExtra(HINT, hint);
        }
        if (prefill != null) {
            intent.putExtra(PREFILL, prefill);
        }

        intent.putExtra(ALLOW_EMPTY, allowEmpty);
        intent.putExtra(BACK_IS_OK, backIsOk);

        return intent;
    }

    public static Intent createLauncherIntent(Context context, RepoInfo repoInfo, int issueNum, String hint, String prefill,
                                              boolean allowEmpty, boolean backIsOk) {
        Intent intent = new Intent(context, ContentEditorActivity.class);

        if (hint != null) {
            intent.putExtra(HINT, hint);
        }
        if (prefill != null) {
            intent.putExtra(PREFILL, prefill);
        }
        if (repoInfo != null) {
            intent.putExtra(REPO_INFO, repoInfo);
        }
        intent.putExtra(ISSUE_NUM, issueNum);
        intent.putExtra(ALLOW_EMPTY, allowEmpty);
        intent.putExtra(BACK_IS_OK, backIsOk);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_editor);

        setTitle("");

        if (getIntent().getExtras() != null) {

            findViews();

            toolbarExtra.inflateMenu(R.menu.content_editor_extra);
            toolbarExtra.setOnMenuItemClickListener(this);

            final String hint = getIntent().getExtras().getString(HINT);

            if (!TextUtils.isEmpty(hint)) {
                editText.setHint(hint);
            }

            editText.setHint(getString(R.string.edit_hint));
            editText.setQueryTokenReceiver(this);

            String content = getIntent().getExtras().getString(PREFILL);
            if (getIntent().getExtras().containsKey(REPO_INFO) && getIntent().getExtras().containsKey(ISSUE_NUM)) {

                RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
                int issueNumber = getIntent().getExtras().getInt(ISSUE_NUM);

                issueInfo = new IssueInfo();
                issueInfo.repoInfo = repoInfo;
                issueInfo.num = issueNumber;

                if (!TextUtils.isEmpty(content)) {
                    String htmlCode = HtmlUtils.format(content).toString();
                    editText.setText(htmlCode);
                }
            }

            allowEmpty = getIntent().getExtras().getBoolean(ALLOW_EMPTY, false);

            if (!allowEmpty) {
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        invalidateOptionsMenu();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (hint != null) {
                        if (editText.getText().length() > 0) {
                            setTitle(hint);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            backIsOk = getIntent().getExtras().getBoolean(BACK_IS_OK, false);
        } else {
            finish();
        }
    }

    private void findViews() {
        editText = (RichEditorView) findViewById(R.id.edit);
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
        MenuItem trashItem = menu.findItem(R.id.action_trash);

        if (okItem != null) {
            IconicsDrawable iconicsDrawable = new IconicsDrawable(this, Octicons.Icon.oct_trashcan).actionBar().color(Color.WHITE);
            trashItem.setIcon(iconicsDrawable);
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

                if (issueInfo != null) {
                    applied = true;
                    CacheWrapper.clearIssueComment(issueInfo.toString());
                }

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.action_trash:
                editText.setText("");
                CacheWrapper.clearIssueComment(issueInfo.toString());
                break;
            case R.id.add_content_editor_emojis:
                Intent intentEmojis = new Intent(this, EmojisActivity.class);
                startActivityForResult(intentEmojis, EMOJI_REQUEST);
                break;
            case R.id.add_content_editor_source:
                editText.getText().append(" ```");
                editText.getText().append("\n");
                editText.getText().append("\n");
                editText.getText().append(" ```");

                editText.setSelection(editText.getText().length() - 5);
                break;
            case R.id.add_content_editor_picture:
                showAddPicture();
                break;
        }
        return true;
    }

    private void showAddPicture() {
        new MaterialDialog.Builder(this).title(R.string.addPicture)
                .content(R.string.addPictureContent)
                .input(R.string.addPictureHint, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
                        editText.getText().append("\n");
                        editText.getText().append("\n");
                        editText.getText().append("![]");
                        editText.getText().append("(" + charSequence.toString() + ")");
                        editText.getText().append("\n");
                        editText.getText().append("\n");
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
                    editText.getText().append(" :" + emoji.getKey() + ": ");
                    break;
            }
        }
    }

    @Override
    protected void close(boolean navigateUp) {
        saveCache();
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

    @Override
    public void onStart() {
        super.onStart();

        if (issueInfo != null) {
            String issueComment = CacheWrapper.getIssueComment(issueInfo.toString());
            if (issueComment != null) {
                editText.setText(issueComment);
            }
        }
    }

    @Override
    public void onStop() {
        saveCache();
        super.onStop();
    }

    private void saveCache() {
        if (!applied && issueInfo != null) {
            try {
                CacheWrapper.setNewIssueComment(issueInfo.toString(), editText.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<String> onQueryReceived(@NonNull final QueryToken queryToken) {
        if (!queryToken.getKeywords().startsWith("@")) {
            final List<String> buckets = new ArrayList<>();
            buckets.add("github");

            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(queryToken.getKeywords());
                    }
                }
            })
                    .filter(new Func1<String, Boolean>() {
                        @Override
                        public Boolean call(String s) {
                            return queryToken.getTokenString().startsWith("@") && queryToken.getTokenString().length() > 2;
                        }
                    })
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            return s.replaceFirst("@", "");
                        }
                    })
                    .flatMap(new Func1<String, Observable<List<User>>>() {
                        @Override
                        public Observable<List<User>> call(final String s) {
                            Observable<List<User>> search = new UsersSearchClient(s).observable().delay(200, TimeUnit.MILLISECONDS)
                                    .map(new Func1<Pair<List<User>, Integer>, List<User>>() {
                                        @Override
                                        public List<User> call(Pair<List<User>, Integer> listIntegerPair) {
                                            return listIntegerPair.first;
                                        }
                                    })
                                    .delay(2, TimeUnit.SECONDS);

                            Observable<List<User>> contributors = new GetRepoContributorsClient(issueInfo.repoInfo)
                                    .observable()
                                    .map(new Func1<List<Contributor>, List<User>>() {
                                        @Override
                                        public List<User> call(List<Contributor> contributors) {
                                            List<User> users = new ArrayList<>();
                                            for (Contributor contributor : contributors) {
                                                if (contributor.author.login.contains(s)) {
                                                    users.add(contributor.author);
                                                }
                                            }
                                            return users;
                                        }
                                    });

                            return Observable.concat(contributors, search);
                        }
                    }).map(new Func1<List<User>, List<SearchableUser>>() {
                @Override
                public List<SearchableUser> call(List<User> users) {
                    List<SearchableUser> searchableUsers = new ArrayList<>();
                    for (User user : users) {
                        SearchableUser searchableUser = new SearchableUser();
                        searchableUser.id = user.id;
                        searchableUser.login = user.login;
                        searchableUsers.add(searchableUser);
                    }
                    return searchableUsers;
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<SearchableUser>>() {
                        @Override
                        public void call(List<SearchableUser> users) {
                            editText.onReceiveSuggestionsResult(new SuggestionsResult(queryToken, users), "github");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    }, new Action0() {
                        @Override
                        public void call() {

                        }
                    });


            return buckets;
        }
        return Collections.emptyList();
    }


}
