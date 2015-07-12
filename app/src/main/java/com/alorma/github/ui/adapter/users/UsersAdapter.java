package com.alorma.github.ui.adapter.users;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Bernat on 14/07/2014.
 */
public class UsersAdapter extends RecyclerArrayAdapter<User, UsersAdapter.ViewHolder> {


    private String owner;

    public UsersAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    public void setRepoOwner(String owner) {
        this.owner = owner;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_user, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = getItem(position);

        ImageLoader.getInstance().displayImage(user.avatar_url, holder.imageView);


        holder.textView.setText(user.login);

        if (owner != null && owner.equalsIgnoreCase(user.login)) {
            holder.userRepoOwner.setVisibility(View.VISIBLE);
        } else {
            holder.userRepoOwner.setVisibility(View.INVISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CircularImageView imageView;
        private final TextView textView;
        private final TextView userRepoOwner;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (CircularImageView) itemView.findViewById(R.id.avatarAuthorImage);
            textView = (TextView) itemView.findViewById(R.id.textAuthorLogin);
            userRepoOwner = (TextView) itemView.findViewById(R.id.userRepoOwner);
        }
    }
}
