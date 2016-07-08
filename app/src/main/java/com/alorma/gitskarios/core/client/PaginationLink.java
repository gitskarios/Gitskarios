package com.alorma.gitskarios.core.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class PaginationLink {

  public URI uri;
  public RelType rel;
  public int page;

  public PaginationLink(String link) {
    link = link.trim().replace("<", "").replace(">", "").replace("\"", "");
    String url = link.split(";")[0];
    String rel = link.split("rel=")[1];

    this.uri = URI.create(url);

    try {
      String page = splitQuery(this.uri).get("page");
      if (page != null) {
        this.page = Integer.valueOf(page);
      }

      if (RelType.next.toString().equals(rel)) {
        this.rel = RelType.next;
      } else if (RelType.last.toString().equals(rel)) {
        this.rel = RelType.last;
      } else if (RelType.first.toString().equals(rel)) {
        this.rel = RelType.first;
      } else if (RelType.prev.toString().equals(rel)) {
        this.rel = RelType.prev;
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public static Map<String, String> splitQuery(URI url) throws UnsupportedEncodingException {
    Map<String, String> query_pairs = new LinkedHashMap<>();
    String query = url.getQuery();
    String[] pairs = query.split("&");
    for (String pair : pairs) {
      int idx = pair.indexOf("=");
      query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
          URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
    }
    return query_pairs;
  }
}
