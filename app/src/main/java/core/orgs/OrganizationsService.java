package core.orgs;

import core.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrganizationsService {
  @GET("/user/orgs")
  Call<List<User>> listOrgs();

  @GET("/user/orgs")
  Call<List<User>> listOrgs(@Query("page") int page);

  @GET("/users/{username}/orgs")
  Call<List<User>> listOrgs(@Path("username") String username);

  @GET("/users/{username}/orgs")
  Call<List<User>> listOrgs(@Path("username") String username, @Query("page") int page);
}
