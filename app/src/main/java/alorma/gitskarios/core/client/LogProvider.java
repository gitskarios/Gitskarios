package com.alorma.gitskarios.core.client;

/**
 * Created by bernat.borras on 8/1/16.
 */
public class LogProvider {

  private static LogProviderInterface logProviderInterface;

  public static void setTokenProviderInstance(LogProviderInterface logProviderInterface) {
    LogProvider.logProviderInterface = logProviderInterface;
  }

  public static LogProviderInterface getInstance() {
    return logProviderInterface;
  }
}
