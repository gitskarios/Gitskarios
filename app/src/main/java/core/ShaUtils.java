package core;

public class ShaUtils {

  private static final int MAX_SHA_LENGHT = 7;

  public static String shortSha(String sha) {
    if (sha == null) {
      throw new IllegalArgumentException("SHA cannot be empty");
    }

    if (sha.isEmpty()) {
      throw new IllegalArgumentException("SHA cannot be empty");
    }

    if (sha.length() < MAX_SHA_LENGHT) {
      throw new IllegalArgumentException("SHA cannot be empty");
    }

    return sha.substring(0, MAX_SHA_LENGHT);
  }
}
