package com.clean.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

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

        GetRepoClient repoClient = new GetRepoClient(context, repoInfo);

        /*
        repoClient.setSaveCache(new Action1<Repo>() {
            @Override
            public void call(Repo repo) {
                QnCacheProvider.getInstance(QnCacheProvider.TYPE.REPO)
                    .set(repoInfo.toString(), repo, TimeUnit.MINUTES.toMillis(10));
            }
        });
        */

        repoClient.observable()
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
