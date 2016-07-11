package com.alorma.gitskarios.core.client;

/**
 * Created by bernat.borras on 8/1/16.
 */
public class UsernameProvider {

  private static UsernameProviderInterface usernameProviderInterface;

  public static void setUsernameProviderInterface(
      UsernameProviderInterface usernameProviderInterface) {
    UsernameProvider.usernameProviderInterface = usernameProviderInterface;
  }

  public static UsernameProviderInterface getInstance() {
    return usernameProviderInterface;
  }
}
