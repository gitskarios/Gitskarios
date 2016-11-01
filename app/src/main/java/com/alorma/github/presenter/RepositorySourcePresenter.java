package com.alorma.github.presenter;

import com.alorma.github.injector.named.ComputationScheduler;
import com.alorma.github.injector.named.MainScheduler;
import com.alorma.gitskarios.core.Pair;
import core.datasource.SdkItem;
import core.repositories.Branch;
import core.repositories.Repo;
import java.util.List;
import rx.Observable;
import rx.Scheduler;

public class RepositorySourcePresenter
    extends BaseRxPresenter<Pair<Repo, Branch>, Pair<Branch, List<Branch>>, View<Pair<Branch, List<Branch>>>> {

  public RepositorySourcePresenter(@MainScheduler Scheduler mainScheduler, @ComputationScheduler Scheduler ioScheduler) {
    super(mainScheduler, ioScheduler, null);
  }

  @Override
  public void execute(Pair<Repo, Branch> data) {
    Observable<Pair<Branch, List<Branch>>> observable = Observable.fromCallable(() -> {

      List<Branch> branches = data.first.branches;

      int branchIndex = -1;
      for (int i = 0; i < branches.size(); i++) {
        if (!data.second.name.equals(branches.get(0).name)) {
          branchIndex = i;
        }
      }

      if (branchIndex != -1) {
        branches.remove(branchIndex);
        branches.add(0, data.second);
      }

      return branches;
    }).map(branches -> new Pair<>(branches.get(0), branches));

    subscribe(observable.map(SdkItem::new), false);
  }
}
