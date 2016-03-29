package com.alorma.github.presenter.notifications;

import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.core.datasource.CloudDataSource;
import com.alorma.github.sdk.core.datasource.RestWrapper;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;

public class CloudNotificationsDataSource
    extends CloudDataSource<NotificationsRequest, List<Notification>> {

  public CloudNotificationsDataSource(RestWrapper restWrapper) {
    super(restWrapper);
  }

  @Override
  protected List<Notification> execute(NotificationsRequest request, RestWrapper service)
      throws IOException {
    Call<List<Notification>> notifications =
        service.<NotificationsService>get().getNotifications(request.isAllNotifications(),
            request.isParticipatingNotifications());

    return notifications.execute().body();
  }
}
