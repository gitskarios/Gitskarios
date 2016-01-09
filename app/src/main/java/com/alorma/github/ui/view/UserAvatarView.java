package com.alorma.github.ui.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;

import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import io.fabric.sdk.android.Fabric;

/**
 * Created by bernat.borras on 9/1/16.
 */
public class UserAvatarView extends CircularImageView implements View.OnClickListener {
    private User user;

    public UserAvatarView(Context context) {
        super(context);
        init();
    }

    public UserAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserAvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(getWidth())
                    .height(getHeight())
                    .endConfig()
                    .buildRound("A", ColorGenerator.MATERIAL.getColor("A"));

            setImageDrawable(drawable);
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

        if (user.type == UserType.Organization) {
            v.getContext().startActivity(OrganizationActivity.launchIntent(v.getContext(), user.login));
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
