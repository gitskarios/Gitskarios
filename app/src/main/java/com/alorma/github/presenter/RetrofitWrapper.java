package com.alorma.github.presenter;

import com.alorma.github.sdk.core.ApiClient;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class RetrofitWrapper extends RestWrapper {

  private final String token;
  public RetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient);
    this.token = token;
  }

  @Override
  protected <T> T get(ApiClient apiClient) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(apiClient.getApiEndpoint())
        .addConverterFactory(GsonConverterFactory.create())
        .client(getClient())
        .build();

    return get(retrofit);
  }

  protected abstract <T> T get(Retrofit retrofit);

  private OkHttpClient getClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    builder.addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
            .header("Authorization", "token " + token)
            .method(original.method(), original.body());

        return chain.proceed(builder.build());
      }
    });

    return builder.build();
  }
}
