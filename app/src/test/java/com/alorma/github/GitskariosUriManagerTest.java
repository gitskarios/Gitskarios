package com.alorma.github;

import android.net.Uri;
import com.alorma.github.GitskariosUriManager;
import com.alorma.github.sdk.bean.info.RepoInfo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GitskariosUriManagerTest {

  private GitskariosUriManager urisManager;

  @Before
  public void before() {
    urisManager = new GitskariosUriManager();
  }

  @Test
  public void extractRepoInfoTest() {
    // given
    String uri = "https://github.com/gitskarios/Gitskarios";

    // when
    RepoInfo repoInfo = urisManager.getRepoInfo(Uri.parse(uri));

    //then
    assertEquals(repoInfo.owner, "gitskarios");
    assertEquals(repoInfo.name, "Gitskarios");
  }
}
