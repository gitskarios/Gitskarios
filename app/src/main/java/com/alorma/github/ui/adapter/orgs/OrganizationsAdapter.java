package com.alorma.github.ui.adapter.orgs;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Organization;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.view.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Bernat on 04/09/2014.
 */
public class OrganizationsAdapter extends LazyAdapter<Organization> {

    public OrganizationsAdapter(Context context, List<Organization> organizations) {
        super(context, 0, organizations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View v = inflate(R.layout.row_user, viewGroup, false);
        Organization organization = getItem(position);

        CircularImageView imageView = (CircularImageView) v.findViewById(R.id.avatarAuthorImage);

        ImageLoader.getInstance().displayImage(organization.avatar_url, imageView);

        TextView textView = (TextView) v.findViewById(R.id.textAuthorLogin);

        textView.setText(organization.login);

        View divider = v.findViewById(R.id.divider);

        if (position == getCount()) {
            divider.setVisibility(View.GONE);
        }

        return v;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
