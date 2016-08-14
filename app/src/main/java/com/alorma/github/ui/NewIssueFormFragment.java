package com.alorma.github.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.IssueRequest;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.issues.PostNewIssueClient;
import com.alorma.github.ui.activity.IssueDetailActivity;
import com.alorma.yaml.formbuilder.YamlFormFragment;
import java.io.InputStream;
import java.util.Map;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewIssueFormFragment extends YamlFormFragment {

  private RepoInfo repoInfo;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      repoInfo = getArguments().getParcelable(GitskariosIssueActivity.REPO_INFO);
    }
  }

  @Override
  protected void formSubmitted(Map<String, Object> items) {
    String title = String.valueOf(items.get("title"));
    String content = String.valueOf(items.get("content"));
    String device = String.valueOf(items.get("device"));
    String os = String.valueOf(items.get("os_version"));
    String type = String.valueOf(items.get("issue_type"));

    IssueRequest issueRequest = new IssueRequest();
    issueRequest.title = "[" + type + "] " + title;
    issueRequest.body = "Os version: " + os + "\n" + "Device: " + device + "\n" + content;

    createIssue(issueRequest);
  }

  private void createIssue(IssueRequest issue) {
    PostNewIssueClient postNewIssueClient = new PostNewIssueClient(repoInfo, issue);
    postNewIssueClient.observable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Issue>() {
          @Override
          public void onCompleted() {

          }

          @Override
          public void onError(Throwable e) {
            Toast.makeText(getActivity(), R.string.create_issue_error, Toast.LENGTH_SHORT).show();
          }

          @Override
          public void onNext(Issue issue) {
            if (issue != null) {
              IssueInfo issueInfo = new IssueInfo();
              issueInfo.repoInfo = repoInfo;
              issueInfo.num = issue.number;
              Intent launcherIntent = IssueDetailActivity.createLauncherIntent(getActivity(), issueInfo);
              startActivity(launcherIntent);
              getActivity().setResult(Activity.RESULT_OK);
              getActivity().finish();
            }
          }
        });
  }

  @Override
  protected InputStream getFormInputStream() {
    return getResources().openRawResource(R.raw.form);
  }
}
