package core.notifications;

public class NotificationsRequest {
  private String token;
  private boolean allNotifications;
  private boolean participatingNotifications;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isAllNotifications() {
    return allNotifications;
  }

  public void setAllNotifications(boolean allNotifications) {
    this.allNotifications = allNotifications;
  }

  public boolean isParticipatingNotifications() {
    return participatingNotifications;
  }

  public void setParticipatingNotifications(boolean participatingNotifications) {
    this.participatingNotifications = participatingNotifications;
  }
}
