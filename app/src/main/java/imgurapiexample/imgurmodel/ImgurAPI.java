package imgurapiexample.imgurmodel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by AKiniyalocts on 2/23/15.
 * <p/>
 * This is our imgur API. It generates a rest API via Retrofit from Square inc.
 * <p/>
 * more here: http://square.github.io/retrofit/
 */
public interface ImgurAPI {
  String server = "https://api.imgur.com/3/";

  /****************************************
   * Upload
   * Image upload API
   */

  /**
   * @param auth #Type of authorization for upload
   * @param file image
   */
  @POST("image")
  Call<ImageResponse> postImage(@Header("Authorization") String auth, @Body RequestBody file);
}
