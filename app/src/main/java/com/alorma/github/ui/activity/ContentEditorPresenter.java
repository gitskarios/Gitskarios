package com.alorma.github.ui.activity;

import com.alorma.github.ui.utils.ContentEditorText;
import imgurapiexample.imgurmodel.ImageResponse;
import imgurapiexample.imgurmodel.Upload;
import imgurapiexample.services.ImgurUpload;
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
    if (file != null) {
      Upload upload = new Upload();
      upload.image = file;

      imgurUpload.uploadImage(upload, clientId)
          .subscribeOn(scheduleOn)
          .observeOn(observeOn)
          .doOnSubscribe(() -> callback.showImageLoading(upload.image.getName()))
          .filter(ImageResponse::isSucces)
          .subscribe(o -> {
            String name = null;
            name = file.getName();
            setImageUrl(name, o.data.link);
            callback.onImageUploaded(upload.image.getName(), o.data.link);
          }, throwable -> {
            callback.showImageUploadError(upload.image.getName());
          });
    } else {
      callback.showImageUploadError(null);
    }
  }

  public void setImageUrl(String name, String link) {
    String textForImage = new ContentEditorText().getTextForImage(name, link);
    callback.appendText("\n" + textForImage + "\n");
  }

  public void setCallback(Callback callback) {
    if (callback == null) {
      callback = new Callback.Null();
    }
    this.callback = callback;
  }

  public interface Callback {
    void showImageLoading(String name);

    void appendText(String text);

    void showImageUploadError(String name);

    void onImageUploaded(String name, String link);

    class Null implements Callback {

      @Override
      public void showImageLoading(String name) {

      }

      @Override
      public void appendText(String text) {

      }

      @Override
      public void showImageUploadError(String name) {

      }

      @Override
      public void onImageUploaded(String name, String link) {

      }
    }
  }
}
