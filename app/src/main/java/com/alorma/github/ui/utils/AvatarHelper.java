package com.alorma.github.ui.utils;

import core.User;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AvatarHelper {

  private static final String GITHUB_AVATAR =
      "https://camo.githubusercontent.com/a8dd47617ff250cc8de355d383227032e8a9cf4d/68747470733a2f2f302e67726176617461722e636f6d2f6176617461722f37376366623362363061306336663039623233373964396265363736396239353f643d68747470732533412532462532466173736574732d63646e2e6769746875622e636f6d253246696d6167657325324667726176617461727325324667726176617461722d757365722d3432302e706e6726723d7826733d313430";

  public static String getAvatar(User user) {
    if (user == null) {
      return null;
    } else if (user.getAvatar() != null) {
      return user.getAvatar();
    } else if (user.getEmail() != null) {
      try {
        return "https://www.gravatar.com/avatar/" + md5(user.getEmail()) + "?d=" + urlEncode(GITHUB_AVATAR);
      } catch (UnsupportedEncodingException e) {
        return GITHUB_AVATAR;
      }
    } else {
      return null;
    }
  }

  private static String md5(final String s) {
    final String MD5 = "MD5";
    try {
      // Create MD5 Hash
      MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
      digest.update(s.getBytes());
      byte messageDigest[] = digest.digest();

      // Create Hex String
      StringBuilder hexString = new StringBuilder();
      for (byte aMessageDigest : messageDigest) {
        String h = Integer.toHexString(0xFF & aMessageDigest);
        while (h.length() < 2) h = "0" + h;
        hexString.append(h);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return GITHUB_AVATAR;
  }

  private static String urlEncode(String s) throws UnsupportedEncodingException {
    return URLEncoder.encode(s, "UTF-8");
  }
}
