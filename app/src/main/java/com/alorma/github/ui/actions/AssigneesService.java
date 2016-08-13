package com.alorma.github.ui.actions;

import com.alorma.github.sdk.bean.dto.request.EditIssueAssigneesRequestDTO;
import com.alorma.github.sdk.bean.dto.response.Issue;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AssigneesService {

  @POST("/repos/{owner}/{name}/issues/{num}/assignees")
  Call<Issue> changeAssignees(@Path("owner") String owner, @Path("name") String repo, @Path("num") int num,
      @Body EditIssueAssigneesRequestDTO assigneesRequestDTO);

  @HTTP(method = "DELETE", path = "/repos/{owner}/{name}/issues/{num}/assignees", hasBody = true)
  Call<Issue> removeAssignees(@Path("owner") String owner, @Path("name") String repo, @Path("num") int num,
      @Body EditIssueAssigneesRequestDTO assigneesRequestDTO);
}
