package com.alorma.gitskarios.core.client;

/**
 * Created by bernat.borras on 8/1/16.
 */
public class UrlProvider {

  private static UrlProviderInterface urlProviderInterface;

  public static void setUrlProviderInstance(UrlProviderInterface urlProviderInterface) {
    UrlProvider.urlProviderInterface = urlProviderInterface;
  }

  public static UrlProviderInterface getInstance() {
    return urlProviderInterface;
  }
}
