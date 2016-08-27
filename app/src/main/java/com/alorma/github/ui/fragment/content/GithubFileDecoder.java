package com.alorma.github.ui.fragment.content;

import com.alorma.github.Base64;
import java.io.UnsupportedEncodingException;
import rx.Observable;

public class GithubFileDecoder {
  private String encoded;

  public GithubFileDecoder(String encoded) {
    this.encoded = encoded;
  }

  public Observable<String> decode() {
    return Observable.defer(() -> {
      try {
        return Observable.just(Base64.decode(encoded));
      } catch (Exception e) {
        return Observable.error(e);
      }
    }).flatMap(bytes -> {
      try {
        return Observable.just(new String(bytes, "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        return Observable.error(e);
      }
    });
  }
}
