package com.alorma.github.sdk.services.search;

import com.alorma.github.sdk.bean.dto.response.search.IssuesSearch;
import com.alorma.github.sdk.bean.dto.response.search.ReposSearch;
import com.alorma.github.sdk.bean.dto.response.search.UsersSearch;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SearchClient {

  //Async
  @GET("/search/repositories")
  void repos(@Query(value = "q", encodeValue = false) String query, Callback<ReposSearch> callback);

  @GET("/search/repositories")
  void repos(@Query(value = "q", encodeValue = false) String query, @Query("page") int page, Callback<ReposSearch> callback);

  @GET("/search/issues")
  void issues(@Query(value = "q", encodeValue = false) String query, Callback<IssuesSearch> callback);

  @GET("/search/issues")
  void issues(@Query(value = "q", encodeValue = false) String query, @Query("page") int page, Callback<IssuesSearch> callback);

  @GET("/search/users")
  void users(@Query(value = "q", encodeValue = false) String query, Callback<UsersSearch> callback);

  @GET("/search/users")
  void users(@Query(value = "q", encodeValue = false) String query, @Query("page") int page, Callback<UsersSearch> callback);
}
