package com.alorma.github.ui.adapter.users;

import android.content.Intent;
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
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;

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

        UniversalImageLoaderUtils.loadUserAvatarSquare(holder.avatar, user);

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
                public void onClick(final View v) {
                    User user = getItem(getAdapterPosition());
                    if (user.type == UserType.Organization) {
                        v.getContext().startActivity(OrganizationActivity.launchIntent(v.getContext(), user.login));
                    } else {
                        final Intent intent = ProfileActivity.createLauncherIntent(v.getContext(), user);
                        if (textRootView.getTag() != null) {
                            int color = (int) textRootView.getTag();
                            intent.putExtra(ProfileActivity.EXTRA_COLOR, color);
                        }
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}
