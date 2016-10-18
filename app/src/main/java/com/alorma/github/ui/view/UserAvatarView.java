package com.alorma.github.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.alorma.timeline.RoundTimelineView;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import core.User;
import io.fabric.sdk.android.Fabric;

public class UserAvatarView extends RoundTimelineView implements View.OnClickListener {
  private User user;

  public UserAvatarView(Context context) {
    this(context, null);
  }

  public UserAvatarView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public UserAvatarView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public UserAvatarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyle) {
    if (!isInEditMode()) {
      final TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.UserAvatarView, defStyle, 0);

      boolean showLine = typedArray.getBoolean(R.styleable.UserAvatarView_uav_show_timeline, false);

      if (showLine) {
        setTimelineType(TYPE_MIDDLE);
        setLineColor(Color.GRAY);
      } else {
        setTimelineType(TYPE_HIDDEN);
      }
      setIndicatorSize(getResources().getDimensionPixelOffset(R.dimen.user_avatar));
      setIndicatorColor(Color.TRANSPARENT);
      setLineWidth(1.5f);
      setTimelineAlignment(ALIGNMENT_START);
    }
  }

  public void setUser(User user) {
    this.user = user;
    UniversalImageLoaderUtils.loadUserAvatar(this, user);

    setOnClickListener(this);
  }

  @Override
  public void onClick(final View v) {

    if (Fabric.isInitialized()) {
      Answers.getInstance().logContentView(new ContentViewEvent().putContentName("UserAvatarViewClick"));
    }

    if (UserType.Organization.name().equals(user.getType())) {
      v.getContext().startActivity(OrganizationActivity.launchIntent(v.getContext(), user.getLogin()));
    } else {
      final Intent intent = ProfileActivity.createLauncherIntent(v.getContext(), user);
      if (getTag() != null) {
        int color = (int) getTag();
        intent.putExtra(ProfileActivity.EXTRA_COLOR, color);
      }
      v.getContext().startActivity(intent);
    }
  }
}
