package core.issue;

import java.security.InvalidParameterException;
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
  public void shouldContainIsIssue_whenBuildEmpty() {
    String s = builder.build();

    assertThat(s).contains("is:issue");
  }

  @Test
  public void shouldContainIsIssue_whenPullRequestFalse() {
    String s = builder.setIsPullRequest(false).build();

    assertThat(s).contains("is:issue");
  }

  @Test
  public void shouldContainIsPr_whenPullRequestTrue() {
    String s = builder.setIsPullRequest(true).build();

    assertThat(s).contains("is:pr");
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
    String s = builder.setAuthor("alorma").setAction("assigned").build();

    assertThat(s).contains("assigned:alorma");
  }

  @Test
  public void shouldNotContainsActionAndAuthor_whenAuthorIsNotSet() {
    String s = builder.setAction("assigned").build();

    assertThat(s).doesNotContain("assigned");
  }

  @Test
  public void shouldNotContainsActionAndAuthor_whenActionIsNotSet() {
    String s = builder.setAuthor("alorma").build();

    assertThat(s).doesNotContain("alorma");
  }

  @Test
  public void shouldContainUser_whenUserIsSet() {
    String s = builder.setUser("alorma").build();

    assertThat(s).contains("user:alorma");
  }

  @Test(expected = InvalidParameterException.class)
  public void shouldThrowException_whenRepoIsSetAndUserIsNotSet() {
    builder.setRepo("gitskarios").build();
  }

  @Test
  public void shouldContainsUserRepo_whenRepoAndUserAreSet() {
    String s = builder.setRepo("gitskarios").setUser("alorma").build();

    assertThat(s).contains("repo:alorma/gitskarios");
  }
}