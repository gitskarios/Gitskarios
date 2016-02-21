package com.alorma.github;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.RepoInfo;
import io.mola.galimatias.GalimatiasParseException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GitskariosUriManagerTest {

  private GitskariosUriManager urisManager;

  @Before
  public void before() {
    urisManager = new GitskariosUriManager();
  }

  @Test
  public void shouldGiveGitskariosRepositoryValues_whenParsingGitskariosUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/gitskarios/Gitskarios";

    // when
    RepoInfo repoInfo = urisManager.getRepoInfo(uri);

    //then
    assertEquals(repoInfo.owner, "gitskarios");
    assertEquals(repoInfo.name, "Gitskarios");
  }

  @Test
  public void shouldGiveGitskariosUser_whenParsingGitskariosUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/alorma";

    // when
    User user = urisManager.getUser(uri);

    //then
    assertEquals(user.login, "alorma");
  }
  @Test
  public void shouldGiveRepositoryNull_whenParsingGitskariosNotificationsUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/site";

    // when
    RepoInfo repoInfo = urisManager.getRepoInfo(uri);

    //then
    assertNull(repoInfo);
  }

  @Test
  public void shouldGiveUserNull_whenParsingGitskariosUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/notifications";

    // when
    User user = urisManager.getUser(uri);

    //then
    assertNull(user);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubNotifiactionsUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/notifications";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubSettingsUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/settings";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubBlogUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/blog";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubExploreUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/explore";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubRepositoriesUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/dashboard";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubEmptyUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubSiteUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/site";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubSecurityUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/security";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubContactUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/contact";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }

  @Test
  public void shouldGiveTrue_whenParsingGithubAboutUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/about";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }
  @Test
  public void shouldGiveTrue_whenParsingGithubOrgsUrl() throws GalimatiasParseException {
    // given
    String uri = "https://github.com/orgs";

    // when
    boolean isProtected = urisManager.isReserved(uri);

    //then
    assertTrue(isProtected);
  }
}
