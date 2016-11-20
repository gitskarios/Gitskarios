package core.issue;

import core.issues.Issue;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface IssuesService {

  @GET("/repos/{owner}/{name}/issues?sort=updated")
  Call<List<Issue>> issues(@Path("owner") String owner, @Path("name") String repo, @QueryMap Map<String, String> filter);

  @GET("/repos/{owner}/{name}/issues?sort=updated")
  Call<List<Issue>> issues(@Path("owner") String owner, @Path("name") String repo, @QueryMap Map<String, String> filter,
      @retrofit.http.Query("page") int page);
}
