package com.alorma.github.sdk.services.issues;

import com.alorma.github.sdk.bean.dto.request.EditIssueRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.GithubClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.RestAdapter;
import retrofit.converter.Converter;
import retrofit.converter.GsonConverter;
import rx.Observable;

public class EditIssueClient extends GithubClient<Issue> {
  private IssueInfo info;
  private EditIssueRequestDTO editIssueRequestDTO;

  public EditIssueClient(IssueInfo info, EditIssueRequestDTO editIssueRequestDTO) {
    super();
    this.info = info;
    this.editIssueRequestDTO = editIssueRequestDTO;
  }

  @Override
  protected Observable<Issue> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(IssuesService.class)
        .editIssue(info.repoInfo.owner, info.repoInfo.name, info.num, editIssueRequestDTO);
  }

  @Override
  protected Converter customConverter() {
    Gson gson = new GsonBuilder().serializeNulls().create();
    return new GsonConverter(gson);
  }
}
