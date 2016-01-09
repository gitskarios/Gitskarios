package com.alorma.github.ui.adapter.orgs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;

/**
 * Created by Bernat on 04/09/2014.
 */
public class OrganizationsAdapter extends RecyclerArrayAdapter<Organization, OrganizationsAdapter.ViewHolder> {

    public OrganizationsAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    public void setRepoOwner(String owner) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_user_square, parent, false));
    }

    @Override
    protected void onBindViewHolder(final ViewHolder holder, Organization organization) {
        UniversalImageLoaderUtils.loadUserAvatarSquare(holder.avatar, organization);
        holder.text.setText(organization.login);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthorImage);
            text = (TextView) itemView.findViewById(R.id.textAuthorLogin);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Organization organization = getItem(getAdapterPosition());
                    v.getContext().startActivity(OrganizationActivity.launchIntent(v.getContext(), organization.login));
                }
            });
        }
    }
}
