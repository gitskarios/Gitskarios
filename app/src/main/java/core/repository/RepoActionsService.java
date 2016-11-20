package core.repository;

import com.alorma.github.sdk.bean.dto.request.WatchBodyRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RepoActionsService {

  @Headers("Content-Length: 0")
  @PUT("/user/starred/{owner}/{repo}")
  Call<ResponseBody> starRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body String empty);

  @PUT("/repos/{owner}/{repo}/subscription")
  Call<ResponseBody> watchRepo(@Path("owner") String owner, @Path("repo") String repo,
      @Body WatchBodyRequest bodyRequest);

  @GET("/user/starred/{owner}/{repo}")
  Call<ResponseBody> checkIfRepoIsStarred(@Path("owner") String owner, @Path("repo") String repo);

  @DELETE("/user/starred/{owner}/{repo}")
  Call<ResponseBody> unstarRepo(@Path("owner") String owner, @Path("repo") String repo);

  @GET("/repos/{owner}/{repo}/subscription")
  Call<ResponseBody> checkIfRepoIsWatched(@Path("owner") String owner, @Path("repo") String repo);

  @DELETE(("/repos/{owner}/{repo}/subscription"))
  Call<ResponseBody> unwatchRepo(@Path("owner") String owner, @Path("repo") String repo);
}
