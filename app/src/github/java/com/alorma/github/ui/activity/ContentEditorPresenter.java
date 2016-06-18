package com.alorma.github.ui.activity;

import akiniyalocts.imgurapiexample.imgurmodel.ImageResponse;
import akiniyalocts.imgurapiexample.imgurmodel.Upload;
import akiniyalocts.imgurapiexample.services.ImgurUpload;
import com.alorma.github.ui.utils.ContentEditorText;
import java.io.File;
import rx.Scheduler;

public class ContentEditorPresenter {

  private String clientId;
  private Callback callback;
  private ImgurUpload imgurUpload;
  private rx.Scheduler observeOn;
  private Scheduler scheduleOn;

  public ContentEditorPresenter(String clientId, ImgurUpload imgurUpload, Scheduler observeOn, Scheduler scheduleOn) {
    this.clientId = clientId;
    this.imgurUpload = imgurUpload;
    this.observeOn = observeOn;
    this.scheduleOn = scheduleOn;
  }

  public void uploadImageWithImgurAPI(File file) {
    Upload upload = new Upload();
    upload.image = file;

    imgurUpload.uploadImage(upload, clientId)
        .subscribeOn(scheduleOn)
        .observeOn(observeOn)
        .doOnSubscribe(() -> callback.showImageLoading())
        .doOnCompleted(() -> callback.hideImageLoading())
        .filter(ImageResponse::isSucces)
        .subscribe(o -> {
          String name = null;
          if (file != null) {
            name = file.getName();
          }
          setImageUrl(name, o.data.link);
        }, this::onErrorPublishingImage);
  }

  private void onErrorPublishingImage(Throwable throwable) {
    callback.showImageUploadError();
  }

  public void setImageUrl(String name, String link) {
    String textForImage = new ContentEditorText().getTextForImage(name, link);
    callback.appendText(textForImage);
  }

  public void setCallback(Callback callback) {
    if (callback == null) {
      callback = new Callback.Null();
    }
    this.callback = callback;
  }

  public interface Callback {
    void showImageLoading();

    void appendText(String text);

    void showImageUploadError();

    void hideImageLoading();

    class Null implements Callback {

      @Override
      public void showImageLoading() {

      }

      @Override
      public void appendText(String text) {

      }

      @Override
      public void showImageUploadError() {

      }

      @Override
      public void hideImageLoading() {

      }
    }
  }
}
