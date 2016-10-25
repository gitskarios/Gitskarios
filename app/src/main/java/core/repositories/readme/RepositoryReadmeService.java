package core.repositories.readme;

import com.alorma.github.sdk.bean.dto.response.Content;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RepositoryReadmeService {

  @GET("/repos/{owner}/{name}/readme")
  Call<Content> readme(@Path("owner") String owner, @Path("name") String repo);

  @GET("/repos/{owner}/{name}/readme")
  Call<Content> readme(@Path("owner") String owner, @Path("name") String repo, @Query("ref") String ref);

  @POST("/markdown/raw")
  Call<ResponseBody> markdown(@Body String readme);
}
