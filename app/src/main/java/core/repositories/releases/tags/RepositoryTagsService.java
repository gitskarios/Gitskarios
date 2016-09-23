package core.repositories.releases.tags;

import core.repositories.Commit;
import core.repositories.releases.Release;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API service for repository tags
 */
public interface RepositoryTagsService {
    @GET("/repos/{owner}/{name}/tags?per_page=15")
    Call<List<Tag>> tags(@Path("owner") String owner, @Path("name") String repo, @Query("sort") String sort);

    @GET("/repos/{owner}/{name}/tags?per_page=15")
    Call<List<Tag>> tags(@Path("owner") String owner, @Path("name") String repo, @Query("page") int page, @Query("sort") String sort);

    @GET("/repos/{owner}/{name}/commits/{sha}")
    Call<Commit> commit(@Path("owner") String owner, @Path("name") String repo, @Path("sha") String sha);

    @GET("/repos/{owner}/{name}/releases/tags/{tag}")
    Call<Release> release(@Path("owner") String owner, @Path("name") String repo, @Path("tag") String tag);
}
