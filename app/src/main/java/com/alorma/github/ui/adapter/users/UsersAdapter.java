package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.CircularImageView;
import com.musenkishi.atelier.Atelier;
import com.musenkishi.atelier.ColorType;
import com.musenkishi.atelier.swatch.DarkVibrantSwatch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapter extends RecyclerArrayAdapter<User, UsersAdapter.ViewHolder> {

    public UsersAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    public void setRepoOwner(String owner) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_user_square, parent, false));
    }

    @Override
    protected void onBindViewHolder(final ViewHolder holder, User user) {
        ImageLoader.getInstance().displayImage(user.avatar_url, holder.avatar, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Context context = holder.itemView.getContext();

                //Set color to a View's imageUri
                Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.BACKGROUND))
                        .into(holder.textRootView);

                //Set color to text in a TextView
                Atelier.with(context, imageUri)
                        .load(loadedImage)
                        .swatch(new DarkVibrantSwatch(ColorType.TEXT_TITLE))
                        .into(holder.text);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        holder.text.setText(user.login);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView text;
        private final View textRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthorImage);
            text = (TextView) itemView.findViewById(R.id.textAuthorLogin);
            textRootView = itemView.findViewById(R.id.textRootView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = getItem(getAdapterPosition());
                    if (user.type == UserType.Organization) {
                        v.getContext().startActivity(OrganizationActivity.launchIntent(v.getContext(), user.login));
                    } else {
                        v.getContext().startActivity(ProfileActivity.createLauncherIntent(v.getContext(), user));
                    }
                }
            });
        }
    }
}
