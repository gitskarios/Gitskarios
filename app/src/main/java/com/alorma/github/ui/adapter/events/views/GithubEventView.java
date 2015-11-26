package com.alorma.github.ui.adapter.events.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.alorma.github.sdk.bean.dto.response.GithubEvent;
import com.alorma.github.sdk.bean.dto.response.events.payload.GithubEventPayload;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Bernat on 04/10/2014.
 */
public abstract class GithubEventView<K extends GithubEventPayload> extends FrameLayout {

  protected GithubEvent event;
  protected K eventPayload;

  public GithubEventView(Context context) {
    super(context);
    init();
  }

  public GithubEventView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public GithubEventView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  protected void init() {
    isInEditMode();
    inflate();
  }

  protected abstract void inflate();

  public void setEvent(GithubEvent event) {
    this.event = event;
    setEventPayload(event.payload);
    populateView(event);
  }

  protected abstract void populateView(GithubEvent event);

  public void setEventPayload(Object eventPayload) {
    Gson gson = new Gson();
    this.eventPayload = convert(gson, gson.toJson(eventPayload));
  }

  protected abstract K convert(Gson gson, String s);

  public void handleImage(ImageView imageView, GithubEvent event) {
    ImageLoader.getInstance().cancelDisplayTask(imageView);
    DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder().cacheOnDisk(true)
        .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
        .bitmapConfig(Bitmap.Config.ALPHA_8)
        .build();
    ImageLoader.getInstance().displayImage(event.actor.avatar_url, imageView, displayImageOptions);
  }
}
