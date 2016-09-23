package core.repositories;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReposService {

  @GET("/user/repos?type=owner")
  Call<List<Repo>> userReposList(@Query("sort") String sort);

  @GET("/user/repos?type=owner")
  Call<List<Repo>> userReposList(@Query("page") int page, @Query("sort") String sort);

  @GET("/users/{username}/repos?type=owner")
  Call<List<Repo>> userReposList(@Path("username") String username, @Query("sort") String sort);

  @GET("/users/{username}/repos?type=owner")
  Call<List<Repo>> userReposList(@Path("username") String username, @Query("page") int page, @Query("sort") String sort);

  // Starred repos
  @GET("/user/starred?sort=updated")
  Call<List<Repo>> userStarredReposList(@Query("sort") String sort);

  @GET("/user/starred?sort=updated")
  Call<List<Repo>> userStarredReposList(@Query("page") int page, @Query("sort") String sort);

  @GET("/users/{username}/starred?sort=updated")
  Call<List<Repo>> userStarredReposList(@Path("username") String username, @Query("sort") String sort);

  @GET("/users/{username}/starred?sort=updated")
  Call<List<Repo>> userStarredReposList(@Path("username") String username, @Query("page") int page, @Query("sort") String sort);

  // Watched repos
  @GET("/user/subscriptions")
  Call<List<Repo>> userSubscribedReposList(@Query("sort") String sort);

  @GET("/user/subscriptions")
  Call<List<Repo>> userSubscribedReposList(@Query("page") int page, @Query("sort") String sort);

  @GET("/users/{username}/subscriptions")
  Call<List<Repo>> userSubscribedReposList(@Path("username") String username, @Query("sort") String sort);

  @GET("/users/{username}/subscriptions")
  Call<List<Repo>> userSubscribedReposList(@Path("username") String username, @Query("page") int page, @Query("sort") String sort);

  // Member
  @GET("/user/repos?affiliation=collaborator,organization_member")
  Call<List<Repo>> userMemberRepos(@Query("sort") String sort);

  // Member
  @GET("/user/repos?affiliation=collaborator,organization_member")
  Call<List<Repo>> userMemberRepos(@Query("page") int page, @Query("sort") String sort);

  @GET("/user/repos?affiliation=organization_member")
  Call<List<Repo>> userReposListFromOrgs(@Query("sort") String sort);

  @GET("/user/repos?affiliation=organization_member")
  Call<List<Repo>> userReposListFromOrgs(@Query("page") int page, @Query("sort") String sort);

  // Orgs
  @GET("/orgs/{org}/repos?type=all")
  Call<List<Repo>> orgsReposList(@Path("org") String org, @Query("sort") String sort);

  @GET("/orgs/{org}/repos?type=all")
  Call<List<Repo>> orgsReposList(@Path("org") String org, @Query("page") int page, @Query("sort") String sort);

}
