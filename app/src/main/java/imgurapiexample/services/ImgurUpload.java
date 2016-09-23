package imgurapiexample.services;

import imgurapiexample.Constants;
import imgurapiexample.imgurmodel.ImageResponse;
import imgurapiexample.imgurmodel.ImgurAPI;
import imgurapiexample.imgurmodel.Upload;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by AKiniyalocts on 1/12/15.
 * <p/>
 * Our upload service. This creates our Retrofit, uploads our image, and notifies us of the response.
 */
public class ImgurUpload {

  public Observable<ImageResponse> uploadImage(final Upload upload, final String clientId) {
    Func0<Observable<ImageResponse>> defer = new Func0<Observable<ImageResponse>>() {
      @Override
      public Observable<ImageResponse> call() {
        try {
          return getCall(upload, clientId);
        } catch (IOException e) {
          return Observable.error(e);
        }
      }
    };
    return Observable.defer(defer);
  }

  private Observable<ImageResponse> getCall(Upload upload, String clientId) throws IOException {
    Retrofit restAdapter = buildRestAdapter();

    // create RequestBody instance from file
    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), upload.image);
    ImgurAPI imgurAPI = restAdapter.create(ImgurAPI.class);
    String clientAuth = Constants.getClientAuth(clientId);
    Call<ImageResponse> call = imgurAPI.postImage(clientAuth, requestFile);

    Response<ImageResponse> response = call.execute();

    if (response.isSuccessful()) {
      return Observable.just(response.body());
    } else {
      return Observable.error(new Exception(response.errorBody().string()));
    }
  }

  protected Retrofit buildRestAdapter() {
    return new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(ImgurAPI.server).build();
  }
}
