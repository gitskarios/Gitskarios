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
import com.musenkishi.atelier.Atelier;
import com.musenkishi.atelier.ColorType;
import com.musenkishi.atelier.swatch.DarkVibrantSwatch;
import com.musenkishi.atelier.swatch.VibrantSwatch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Bernat on 09/07/2015.
 */
public class UsersAdapterSquare extends RecyclerView.Adapter<UsersAdapterSquare.Holder> {

    private LayoutInflater inflater;
    private List<User> users;

    public UsersAdapterSquare(LayoutInflater inflater) {
        this.inflater = inflater;
        users = new ArrayList<>();
    }

    @Override
    public UsersAdapterSquare.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.row_user_square, parent, false));
    }

    @Override
    public void onBindViewHolder(final UsersAdapterSquare.Holder holder, int position) {
        User user = users.get(position);

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

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView text;
        private final View textRootView;

        public Holder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthorImage);
            text = (TextView) itemView.findViewById(R.id.textAuthorLogin);
            textRootView = itemView.findViewById(R.id.textRootView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = users.get(getAdapterPosition());
                    if (user.type == UserType.Organization) {
                        v.getContext().startActivity(OrganizationActivity.launchIntent(v.getContext(), user.login));
                    } else {
                        v.getContext().startActivity(ProfileActivity.createLauncherIntent(v.getContext(), user));
                    }
                }
            });
        }
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection<User> users) {
        this.users.addAll(users);
        notifyDataSetChanged();
    }
}
