package core.repositories.markdown;

import core.ApiClient;
import core.datasource.RetrofitWrapper;
import core.repositories.readme.RepositoryReadmeService;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class MarkdownRetrofitWrapper extends RetrofitWrapper {

  public MarkdownRetrofitWrapper(ApiClient apiClient, String token) {
    super(apiClient, token);
  }

  @Override
  protected RepositoryReadmeService get(ApiClient apiClient) {
    Retrofit retrofit = new Retrofit.Builder().baseUrl(apiClient.getApiEndpoint())
        .addConverterFactory(new ToStringConverterFactory())
        .client(getClient())
        .build();

    return get(retrofit);
  }

  public class ToStringConverterFactory extends Converter.Factory {
    private final MediaType MEDIA_TYPE = MediaType.parse("text/plain");

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
      if (String.class.equals(type)) {
        return new Converter<ResponseBody, String>() {
          @Override
          public String convert(ResponseBody value) throws IOException {
            return value.string();
          }
        };
      }
      return null;
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations,
        Retrofit retrofit) {

      if (String.class.equals(type)) {
        return new Converter<String, RequestBody>() {
          @Override
          public RequestBody convert(String value) throws IOException {
            return RequestBody.create(MEDIA_TYPE, value);
          }
        };
      }
      return null;
    }
  }

  @Override
  protected RepositoryReadmeService get(Retrofit retrofit) {
    return retrofit.create(RepositoryReadmeService.class);
  }
}
