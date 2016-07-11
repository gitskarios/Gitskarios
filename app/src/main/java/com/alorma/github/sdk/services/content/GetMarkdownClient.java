package com.alorma.github.sdk.services.content;

import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.services.client.GithubClient;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Scanner;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;
import rx.Observable;

/**
 * Created by Bernat on 22/07/2014.
 */
public class GetMarkdownClient extends GithubClient<String> {

  private RequestMarkdownDTO readme;

  public GetMarkdownClient(RequestMarkdownDTO readme) {
    super();
    this.readme = readme;
  }

  @Override
  protected Observable<String> getApiObservable(RestAdapter restAdapter) {
    return restAdapter.create(ContentService.class).markdown(readme.text);
  }

  @Override
  public void intercept(RequestFacade request) {
    super.intercept(request);
    request.addHeader("Content-type", "text/plain");
  }

  @Override
  protected Converter customConverter() {
    return new Converter() {
      @Override
      public Object fromBody(TypedInput body, Type type) throws ConversionException {
        try {
          return new Scanner(body.in(), "UTF-8").useDelimiter("\\A").next();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      public TypedOutput toBody(Object object) {
        return new TypedString((String) object);
      }
    };
  }
}
