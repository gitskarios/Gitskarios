package com.alorma.github.ui.renderers.user;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.musenkishi.atelier.Atelier;
import com.musenkishi.atelier.ColorType;
import com.musenkishi.atelier.swatch.DarkVibrantSwatch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pedrogomez.renderers.Renderer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by a557114 on 30/07/2015.
 */
public class UserRender extends Renderer<User> {

    private Context context;

    public UserRender(Context context) {
        this.context = context;
    }

    @Override
    protected void setUpView(View view) {

    }

    @Override
    protected void hookListeners(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Bind(R.id.avatarAuthorImage) ImageView avatarAuthorImage;
    @Bind(R.id.textRootView) TextView textRootView;
    @Bind(R.id.textAuthorLogin) TextView textAuthorLogin;

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View inflatedView = layoutInflater.inflate(R.layout.row_user_square, viewGroup, false);
        ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void render() {
        User user = getContent();
        ImageLoader.getInstance().displayImage(user.avatar_url, avatarAuthorImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                //Set color to a View's imageUri
                Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
                        .into(textRootView);

                //Set color to text in a TextView
                Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.TEXT_TITLE))
                        .into(textAuthorLogin);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        textAuthorLogin.setText(user.login);
    }
}
