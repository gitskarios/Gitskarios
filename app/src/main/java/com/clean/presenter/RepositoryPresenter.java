package com.clean.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.alorma.github.cache.CacheWrapper;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by bernat.borras on 12/11/15.
 */
public class RepositoryPresenter extends Presenter<RepoInfo, Repo> {

    private Context context;

    public RepositoryPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void load(@NonNull final RepoInfo repoInfo, @NonNull final Callback<Repo> repoCallback) {

        GetRepoClient repoClient = new GetRepoClient(repoInfo);

        Observable<Repo> memory = Observable.create(new Observable.OnSubscribe<Repo>() {
            @Override
            public void call(Subscriber<? super Repo> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(CacheWrapper.getRepository(repoInfo));
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        GetRepoBranchesClient branchesClient = new GetRepoBranchesClient(repoInfo);

        Observable<Repo> combinedWithBranches = Observable.combineLatest(repoClient.observable().subscribeOn(Schedulers.io()), branchesClient.observable().subscribeOn(Schedulers.io())
                , new Func2<Repo, List<Branch>, Repo>() {
                    @Override
                    public Repo call(Repo repo, List<Branch> branches) {
                        repo.branches = branches;
                        if (branches.size() == 1) {
                            repo.default_branch = branches.get(0).name;
                        }
                        return repo;
                    }
                });

        Observable<Repo> repoObservable = combinedWithBranches.doOnNext(new Action1<Repo>() {
            @Override
            public void call(Repo repo) {
                CacheWrapper.setRepository(repo);
            }
        });

        Observable
                .concat(memory, repoObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Repo>() {

                    @Override
                    public void onNext(Repo repo) {
                        repoCallback.onResponse(repo);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {

                    }
                });
    }
}
