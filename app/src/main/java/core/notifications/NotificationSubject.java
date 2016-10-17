package core.notifications;

import core.ShaUrl;

public class NotificationSubject extends ShaUrl {

  public String title;
  public String latest_comment_url;
  public String type;

  public NotificationSubject() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getLatest_comment_url() {
    return latest_comment_url;
  }

  public void setLatest_comment_url(String latest_comment_url) {
    this.latest_comment_url = latest_comment_url;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
