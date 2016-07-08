package com.alorma.github.sdk.services.repo;

import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.info.RepoInfo;
import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;

public abstract class BranchesSubscriber extends Subscriber<List<Branch>> {

  List<String> names;
  private RepoInfo repoInfo;
  private int selectedIndex = 0;

  public BranchesSubscriber(RepoInfo repoInfo) {
    this.repoInfo = repoInfo;
    names = new ArrayList<>();
  }

  public RepoInfo getRepoInfo() {
    return repoInfo;
  }

  @Override
  public void onCompleted() {
    showBranches(names.toArray(new String[names.size()]), selectedIndex);
  }

  @Override
  public void onError(Throwable e) {

  }

  @Override
  public void onNext(List<Branch> branches) {
    if (branches != null) {

      for (int i = 0; i < branches.size(); i++) {
        String branchName = branches.get(i).name;
        this.names.add(branchName);
        if ((branchName.equalsIgnoreCase(repoInfo.branch))) {
          selectedIndex = i;
        }
      }
    }
  }

  protected abstract void showBranches(String[] branches, int defaultBranchPosition);
}