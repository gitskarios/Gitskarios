package core.notifications;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NotificationsService {

  @GET("/notifications")
  Call<List<Notification>> getNotifications(@Query("all") boolean all, @Query("participating") boolean participating);
}
