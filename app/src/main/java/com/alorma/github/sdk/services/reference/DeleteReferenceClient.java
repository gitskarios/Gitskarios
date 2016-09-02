package com.alorma.github.sdk.services.reference;

import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.reference.GitReferenceService;

import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

public class DeleteReferenceClient extends GithubClient<Boolean> {
    private final RepoInfo info;
    private String ref;

    public DeleteReferenceClient(RepoInfo repoInfo, String ref) {
        super();
        this.info = repoInfo;
        this.ref = "heads/" + ref;
    }

    @Override
    protected Observable<Boolean> getApiObservable(RestAdapter restAdapter) {
        return restAdapter.create(GitReferenceService.class)
                .deleteReference(info.owner, info.name, ref)
                .map(new Func1<Response, Boolean>() {
                    @Override
                    public Boolean call(Response r) {
                        return r != null && r.getStatus() == 204;
                    }
                });
    }
}
