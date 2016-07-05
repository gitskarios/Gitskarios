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
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.content.NewFileClient;
import com.alorma.github.ui.activity.base.RepositoryThemeActivity;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditContentActivity extends RepositoryThemeActivity {

  public static final String FILE_INFO = "FILE_INFO";
  private static final int NEW_ISSUE_REQUEST = 575;

  private boolean creatingContent = false;
  private FileInfo fileInfo;

  private TextView pathTextView;
  private EditText editTitle;
  private TextView editBody;

  public static Intent createLauncherIntent(Context context, FileInfo info) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(FILE_INFO, info);
    Intent intent = new Intent(context, EditContentActivity.class);
    intent.putExtras(bundle);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_content);

    if (getIntent().getExtras() != null) {
      fileInfo = getIntent().getExtras().getParcelable(FILE_INFO);
      if (fileInfo != null) {
        findViews();
        setTitle(getString(R.string.new_content_title, fileInfo.name));
        pathTextView.setText(fileInfo.path);
        editBody.setText(fileInfo.content);
      }
    } else {
      finish();
    }
  }

  private void findViews() {
    editTitle = (EditText) findViewById(R.id.editTitle);
    editBody = (TextView) findViewById(R.id.editBody);
    pathTextView = (TextView) findViewById(R.id.currentPath);

    if (editBody != null) {
      editBody.setOnClickListener(v -> {
        Intent intent = ContentEditorActivity.createLauncherIntent(v.getContext(), fileInfo.repoInfo, 0, fileInfo.content,
            editBody.getText().toString(), false, false);

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
    request.sha = fileInfo.head;
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
        showProgressDialog(R.string.edit_content);
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
    NewFileClient client = new NewFileClient(newContentRequest, fileInfo.repoInfo, fileInfo.path);
    client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(newContentResponse -> {
      Toast.makeText(EditContentActivity.this, "File updated", Toast.LENGTH_SHORT).show();
      hideProgressDialog();
      setResult(RESULT_OK);
      finish();
    }, throwable -> {
      Toast.makeText(EditContentActivity.this, "Error updating file", Toast.LENGTH_SHORT).show();
      hideProgressDialog();
    });
  }
}