package com.alorma.github.sdk.services.reference;

import com.alorma.github.sdk.bean.dto.request.UpdateReferenceRequest;
import com.alorma.github.sdk.bean.dto.response.GitReference;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.Path;
import rx.Observable;

public interface GitReferenceService {

    @GET("/repos/{owner}/{repo}/git/refs/{ref}")
    Observable<GitReference> getReference(@Path("owner") String owner, @Path("repo") String repoName,
                                          @Path("ref") String ref);

    @PATCH("/repos/{owner}/{repo}/git/refs/{ref}")
    Observable<GitReference> updateReference(@Path("owner") String owner, @Path("repo") String repoName,
                                             @Path("ref") String ref, @Body UpdateReferenceRequest body);

    @DELETE("/repos/{owner}/{repo}/git/refs/{ref}")
    Observable<Response> deleteReference(@Path("owner") String owner, @Path("repo") String repoName,
                                         @Path("ref") String ref);

}
