package core.search;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IssuesSearchRequestTest {

  private IssuesSearchRequest builder;

  @Before
  public void setup() {
    builder = new IssuesSearchRequest();
  }

  @Test
  public void shouldContainIsOpen_whenOnlyStateAndIsOpen() {
    builder.setIsOpen(true);

    String s = builder.build();

    assertThat(s).contains("is:open");
  }

  @Test
  public void shouldContainIsClosed_whenOnlyStateAndIsClosed() {
    builder.setIsOpen(false);

    String s = builder.build();

    assertThat(s).contains("is:closed");
  }

  @Test
  public void shouldContainIsIssue_whenPullRequestFalse() {
    String s = builder.setIsPullRequest(false).build();

    assertThat(s).contains("type:issue");
  }

  @Test
  public void shouldContainIsPr_whenPullRequestTrue() {
    String s = builder.setIsPullRequest(true).build();

    assertThat(s).contains("type:pr");
  }

  @Test
  public void shouldContainIsPublic_whenIsPublicTrue() {
    String s = builder.setIsPublic(true).build();

    assertThat(s).contains("is:public");
  }

  @Test
  public void shouldContainIsPrivate_whenIsPublicFalse() {
    String s = builder.setIsPublic(false).build();

    assertThat(s).contains("is:private");
  }

  @Test
  public void shouldContainsActionAndAuthor_whenSet() {
    String s = builder.setActionAndAuthor("assigned", "alorma").build();

    assertThat(s).contains("assigned:alorma");
  }

  @Test
  public void shouldContainUser_whenUserIsSet() {
    String s = builder.setUser("alorma").build();

    assertThat(s).contains("user:alorma");
  }

  @Test
  public void shouldContainsUserRepo_whenRepoAndUserAreSet() {
    String s = builder.setRepo("gitskarios", "alorma").build();

    assertThat(s).contains("repo:alorma/gitskarios");
  }

  @Test
  public void shouldContainIsPublicAndUser() {
    String s = builder.setUser("alorma").setIsPublic(true).build();

    assertThat(s).contains("is:public");
    assertThat(s).contains("user:alorma");
  }
}