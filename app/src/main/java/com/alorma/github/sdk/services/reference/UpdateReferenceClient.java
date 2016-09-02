package com.alorma.github.sdk.services.reference;

import com.alorma.github.sdk.bean.dto.request.UpdateReferenceRequest;
import com.alorma.github.sdk.bean.dto.response.GitReference;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.client.GithubClient;

import retrofit.RestAdapter;
import rx.Observable;

public class UpdateReferenceClient extends GithubClient<GitReference> {
    private final RepoInfo info;
    private String ref;
    private UpdateReferenceRequest request;

    public UpdateReferenceClient(RepoInfo repoInfo, String ref, UpdateReferenceRequest request) {
        super();
        this.info = repoInfo;
        this.ref = "heads/" + ref;
        this.request = request;
    }

    @Override
    protected Observable<GitReference> getApiObservable(RestAdapter restAdapter) {
        return restAdapter.create(GitReferenceService.class)
                .updateReference(info.owner, info.name, ref, request);
    }
}
