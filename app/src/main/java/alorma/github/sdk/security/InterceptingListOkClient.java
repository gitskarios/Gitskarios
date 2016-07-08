package com.alorma.github.sdk.security;

import com.alorma.gitskarios.core.client.BaseListClient;
import com.alorma.gitskarios.core.client.PaginationLink;
import com.alorma.gitskarios.core.client.RelType;
import com.squareup.okhttp.OkHttpClient;
import java.io.IOException;
import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class InterceptingListOkClient extends OkClient {

  private BaseListClient baseClient;

  public InterceptingListOkClient(OkHttpClient client, BaseListClient baseClient) {
    super(client);
    this.baseClient = baseClient;
  }

  @Override
  public Response execute(Request request) throws IOException {

    Response response = super.execute(request);
    try {
      for (Header header : response.getHeaders()) {
        if (header.getName().equals("Link")) {
          String[] parts = header.getValue().split(",");
          for (String part : parts) {
            PaginationLink bottomPaginationLink = new PaginationLink(part);
            if (bottomPaginationLink.rel == RelType.last) {
              baseClient.last = bottomPaginationLink.uri;
              baseClient.lastPage = bottomPaginationLink.page;
            } else if (bottomPaginationLink.rel == RelType.next) {
              baseClient.next = bottomPaginationLink.uri;
              baseClient.nextPage = bottomPaginationLink.page;
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return response;
  }
}
