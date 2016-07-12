package com.alorma.gitskarios.core.client;

/**
 * Created by bernat.borras on 8/1/16.
 */
public class TokenProvider {

  private static TokenProviderInterface tokenProviderInterface;

  public static void setTokenProviderInstance(TokenProviderInterface tokenProviderInterface) {
    TokenProvider.tokenProviderInterface = tokenProviderInterface;
  }

  public static TokenProviderInterface getInstance() {
    return tokenProviderInterface;
  }
}
