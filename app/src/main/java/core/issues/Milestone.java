package core.issues;

import com.google.gson.annotations.SerializedName;
import core.ShaUrl;
import core.User;
import java.util.Date;

public class Milestone extends ShaUrl implements Comparable<Milestone> {

  public String title;
  public int number;
  public MilestoneState state;
  public String description;
  public User creator;
  @SerializedName("open_issues") public int openIssues;
  @SerializedName("closed_issues") public int closedIssues;
  @SerializedName("created_at") public Date createdAt;
  @SerializedName("updated_at") public Date updatedAt;
  @SerializedName("due_on") public String dueOn;

  public Milestone() {
  }

  @Override
  public int compareTo(Milestone another) {
    return title.toLowerCase().compareTo(another.title.toLowerCase());
  }

  public String getTitle() {
    return title;
  }

  public int getNumber() {
    return number;
  }

  public MilestoneState getState() {
    return state;
  }

  public String getDescription() {
    return description;
  }

  public User getCreator() {
    return creator;
  }

  public int getOpenIssues() {
    return openIssues;
  }

  public int getClosedIssues() {
    return closedIssues;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public String getDueOn() {
    return dueOn;
  }
}
