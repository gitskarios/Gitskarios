package core.issue;

import core.issues.IssueSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IssuesSearchService {

  @GET("/search/issues")
  Call<IssueSearchResponse> issues(@Query("q") String filter);

  @GET("/search/issues")
  Call<IssueSearchResponse> issues(@Query("q") String filter, @Query("page") int page);

  @GET("/search/issues")
  Call<IssueSearchResponse> issues(@Query("q") String filter, @Query("sort") String sort);

  @GET("/search/issues")
  Call<IssueSearchResponse> issues(@Query("q") String filter, @Query("sort") String sort, @Query("page") int page);
}
