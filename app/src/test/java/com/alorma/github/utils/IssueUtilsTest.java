package com.alorma.github.utils;

import com.alorma.github.sdk.bean.dto.response.Issue;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueUtilsTest {

  private Issue issue;
  private IssueUtils iu;
  private Permissions permissions;

  @Before
  public void setUp() throws Exception {
    iu = new IssueUtils();
    issue = new Issue();
    Repo repository = new Repo();
    issue.repository = repository;
    permissions = new Permissions();
    repository.permissions = permissions;
  }

  @Test
  public void shouldReturnFalse_whenIssueIsLocked() {
    issue.locked = true;

    assertThat(iu.canComment(issue)).isFalse();
  }

  @Test
  public void shouldReturnTrue_whenIssueIsUnlocked() {
    issue.locked = false;

    assertThat(iu.canComment(issue)).isTrue();
  }

  @Test
  public void shouldReturnTrue_whenPushAccessAndLocked() {
    permissions.push = true;
    issue.locked = true;

    assertThat(iu.canComment(issue)).isTrue();
  }

  @Test
  public void shouldReturnTrue_whenAdminAccessAndLocked() {
    permissions.admin = true;
    issue.locked = true;

    assertThat(iu.canComment(issue)).isTrue();
  }

  @Test
  public void shouldReturnFalse_whenPullAccessAndLocked() {
    permissions.pull = true;
    issue.locked = true;

    assertThat(iu.canComment(issue)).isFalse();
  }

  @Test
  public void shouldReturnFalse_whenNullIssue() {
    assertThat(iu.canComment(null)).isFalse();
  }

  @Test
  public void shouldReturnFalse_whenNullRepositoryAndLocked() {
    issue.locked = true;
    issue.repository = null;
    assertThat(iu.canComment(issue)).isFalse();
  }

  @Test
  public void shouldReturnTrue_whenLockedWithPushAccess() {
    issue.locked = true;
    permissions.push = true;
    assertThat(iu.canComment(issue)).isTrue();
  }

  @Test
  public void shouldReturnTrue_whenLockedWithAdminAccess() {
    issue.locked = true;
    permissions.admin = true;
    assertThat(iu.canComment(issue)).isTrue();
  }

  @Test
  public void shouldReturnTrue_whenNullRepositoryAndUnlocked() {
    issue.locked = false;
    issue.repository = null;
    assertThat(iu.canComment(issue)).isTrue();
  }


}