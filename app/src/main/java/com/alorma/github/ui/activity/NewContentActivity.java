package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alorma.github.Base64;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.NewContentRequest;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.NewFileClient;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewContentActivity extends RepositoryThemeActivity {

  public static final String REPO_INFO = "REPO_INFO";
  public static final String PATH = "PATH";
  private static final int EMOJI_CODE = 1554;
  private static final int NEW_ISSUE_REQUEST = 575;

  private boolean creatingContent = false;
  private RepoInfo repoInfo;

  private TextView pathTextView;
  private EditText editPath;
  private EditText editTitle;
  private TextView editBody;

  private String currentPath;

  public static Intent createLauncherIntent(Context context, RepoInfo info, String currentPath) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(REPO_INFO, info);
    bundle.putString(PATH, currentPath);
    Intent intent = new Intent(context, NewContentActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  public int getToolbarId() {
    return super.getToolbarId();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_content);

    if (getIntent().getExtras() != null) {
      repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);
      currentPath = getIntent().getExtras().getString(PATH);
      findViews();

      pathTextView.setText(currentPath);

      setTitle(getString(R.string.new_content_title, repoInfo.name));
    } else {
      finish();
    }
  }

  private void findViews() {
    editTitle = (EditText) findViewById(R.id.editTitle);
    editBody = (TextView) findViewById(R.id.editBody);
    pathTextView = (TextView) findViewById(R.id.currentPath);
    editPath = (EditText) findViewById(R.id.editPath);

    if (editBody != null) {
      editBody.setOnClickListener(v -> {
        String hint = getString(R.string.add_issue_body);
        Intent intent =
            ContentEditorActivity.createLauncherIntent(v.getContext(), repoInfo, 0, hint, editBody.getText().toString(), false, false);

        startActivityForResult(intent, NEW_ISSUE_REQUEST);
      });
    }
    if (getToolbar() != null) {
      ViewCompat.setElevation(getToolbar(), 8);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.new_issue, menu);

    MenuItem item = menu.findItem(R.id.action_send);
    if (item != null) {
      IconicsDrawable githubIconDrawable = new IconicsDrawable(this, Octicons.Icon.oct_plus);
      githubIconDrawable.actionBar();
      githubIconDrawable.colorRes(R.color.white);
      item.setIcon(githubIconDrawable);
    }
    return true;
  }

  private NewContentRequest checkDataAndCreateFile() {
    if (editTitle.length() <= 0) {
      editTitle.setError(getString(R.string.content_message_mandatory));
      return null;
    }
    if (editBody.length() <= 0) {
      editBody.setError(getString(R.string.content_body_mandatory));
      return null;
    }
    creatingContent = true;

    String encoded = Base64.encode(editBody.getText().toString().getBytes());

    NewContentRequest request = new NewContentRequest();
    request.content = encoded;
    request.message = editTitle.getText().toString();
    return request;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    MenuItem item = menu.findItem(R.id.action_send);
    if (item != null) {
      item.setEnabled(!creatingContent);
    }

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    switch (item.getItemId()) {
      case R.id.action_send:
        NewContentRequest contentRequest = checkDataAndCreateFile();
        invalidateOptionsMenu();
        createContent(contentRequest);
        showProgressDialog(R.string.creating_content);
        break;
    }
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK && data != null) {
      String content = data.getStringExtra(ContentEditorActivity.CONTENT);
      editBody.setText(content);
    }
  }

  private void createContent(NewContentRequest newContentRequest) {
    String path = currentPath + editPath.getText().toString();
    if (currentPath.equals("/")) {
      path = editPath.getText().toString();
    }
    NewFileClient client = new NewFileClient(newContentRequest, repoInfo, path);
    client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(newContentResponse -> {
      Toast.makeText(NewContentActivity.this, "File created", Toast.LENGTH_SHORT).show();
      hideProgressDialog();
      setResult(RESULT_OK);
      finish();
    }, throwable -> {
      Toast.makeText(NewContentActivity.this, "Error creating file", Toast.LENGTH_SHORT).show();
      hideProgressDialog();
    });
  }
}