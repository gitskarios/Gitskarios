package com.alorma.github.ui.activity;

import akiniyalocts.imgurapiexample.services.ImgurUpload;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
import com.alorma.github.ui.utils.IntentHelper;
import com.alorma.github.ui.utils.uris.UriUtils;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.gitskarios.core.Pair;
import com.github.mobile.util.HtmlUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.EmptyPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ContentEditorActivity extends BackActivity
    implements Toolbar.OnMenuItemClickListener, QueryTokenReceiver, ContentEditorPresenter.Callback {

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
  private ContentEditorPresenter contentEditorPresenter;

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

      Scheduler observeOn = AndroidSchedulers.mainThread();
      Scheduler subscribeOn = Schedulers.io();
      contentEditorPresenter = new ContentEditorPresenter(getString(R.string.imgur_client_id), new ImgurUpload(), observeOn, subscribeOn);

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
          editText.setText(Html.fromHtml(HtmlUtils.format(content).toString()));
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      PermissionListener permissionListener = new EmptyPermissionListener() {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
          IntentHelper.chooseFileIntent(ContentEditorActivity.this);
        }
      };
      Dexter.checkPermission(permissionListener, Manifest.permission.READ_EXTERNAL_STORAGE);
    } else {
      IntentHelper.chooseFileIntent(this);
    }
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
        case IntentHelper.FILE_PICK:
          String path = UriUtils.getPath(this, data.getData());
          if (path != null) {
            File file = new File(path);
            contentEditorPresenter.setCallback(this);
            contentEditorPresenter.uploadImageWithImgurAPI(file);
          }
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

    contentEditorPresenter.setCallback(this);

    if (issueInfo != null) {
      String issueComment = CacheWrapper.getIssueComment(issueInfo.toString());
      if (issueComment != null) {
        editText.setText(Html.fromHtml(issueComment));
      }
    }
  }

  @Override
  public void onStop() {
    saveCache();
    contentEditorPresenter.setCallback(null);
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
          .filter(s -> queryToken.getTokenString().startsWith("@") && queryToken.getTokenString().length() > 2)
          .map(s -> s.replaceFirst("@", ""))
          .flatMap(s -> {
            Observable<List<User>> search = new UsersSearchClient(s).observable()
                .delay(200, TimeUnit.MILLISECONDS)
                .map(new Func1<Pair<List<User>, Integer>, List<User>>() {
                  @Override
                  public List<User> call(Pair<List<User>, Integer> listIntegerPair) {
                    return listIntegerPair.first;
                  }
                })
                .delay(2, TimeUnit.SECONDS);

            Observable<List<User>> contributors = new GetRepoContributorsClient(issueInfo.repoInfo).observable().map(contributors1 -> {
              List<User> users = new ArrayList<>();
              for (Contributor contributor : contributors1) {
                if (contributor.author.login.contains(s)) {
                  users.add(contributor.author);
                }
              }
              return users;
            });

            return Observable.concat(contributors, search);
          })
          .map(users -> {
            List<SearchableUser> searchableUsers = new ArrayList<>();
            for (User user : users) {
              SearchableUser searchableUser = new SearchableUser();
              searchableUser.id = user.id;
              searchableUser.login = user.login;
              searchableUsers.add(searchableUser);
            }
            return searchableUsers;
          })
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(users -> editText.onReceiveSuggestionsResult(new SuggestionsResult(queryToken, users), "github"), throwable -> {

          }, () -> {

          });

      return buckets;
    }
    return Collections.emptyList();
  }

  @Override
  protected void configureTheme(boolean dark) {
    if (dark) {
      setTheme(R.style.AppTheme_Dark_Repository);
    } else {
      setTheme(R.style.AppTheme_Repository);
    }
  }

  @Override
  public void showImageLoading(String imageName) {
    NotificationCompat.Builder builder = getImageNotificationBuilder(imageName);
    builder.setProgress(100, 50, true);
    builder.setOngoing(true);

    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    manager.notify(imageName.hashCode(), builder.build());
  }

  @NonNull
  private NotificationCompat.Builder getImageNotificationBuilder(String imageName) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    builder.setColor(AttributesUtils.getPrimaryColor(this));
    builder.setSmallIcon(R.drawable.ic_stat_name);
    builder.setContentTitle(getString(R.string.imgur_uploading_image_title));
    builder.setContentText(getString(R.string.imgur_uploading_image_text, imageName));
    return builder;
  }

  @Override
  public void appendText(String text) {
    editText.getText().append("\n");
    editText.getText().append(text);
    editText.getText().append("\n");
  }

  @Override
  public void showImageUploadError(String imageName) {
    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    manager.cancel(imageName.hashCode());
  }

  @Override
  public void hideImageLoading(String imageName, String link) {
    NotificationCompat.Builder builder = getImageNotificationBuilder(imageName);
    Intent openPhotoIntent = new Intent(Intent.ACTION_VIEW);
    openPhotoIntent.setData(Uri.parse(link));
    PendingIntent openIntent = PendingIntent.getActivity(this, 1234, openPhotoIntent, 0);
    builder.addAction(0, getString(R.string.imgur_uploading_image_action_open), openIntent);

    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    manager.notify(imageName.hashCode(), builder.build());
  }
}
