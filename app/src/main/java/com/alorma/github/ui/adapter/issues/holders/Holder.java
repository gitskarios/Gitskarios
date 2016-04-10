package com.alorma.github.ui.adapter.issues.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;

public abstract class Holder extends RecyclerView.ViewHolder {

  public Holder(View itemView) {
    super(itemView);
  }

  public abstract void setIssue(RepoInfo repoInfo, final Issue issue);

  public abstract void setDetail(IssueStoryDetail detail);

}