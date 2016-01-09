package com.alorma.github.ui.adapter.commit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubStatus;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by a557114 on 06/09/2015.
 */
public class GithubStatusAdapter extends RecyclerArrayAdapter<GithubStatus, GithubStatusAdapter.ViewHolder> {

    public GithubStatusAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.github_status, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, GithubStatus githubStatus) {
        IIcon icon = Octicons.Icon.oct_check;
        int background = R.drawable.github_status_circle_green;
        if (githubStatus.state.equals("pending")) {
            icon = Octicons.Icon.oct_clock;
            background = R.drawable.github_status_circle_yellow;
        } else if (githubStatus.state.equals("failure")) {
            icon = Octicons.Icon.oct_x;
            background = R.drawable.github_status_circle_red;
        }

        IconicsDrawable drawable = new IconicsDrawable(holder.icon.getContext(), icon).colorRes(R.color.white)
                .sizeRes(R.dimen.material_drawer_item_primary)
                .paddingRes(R.dimen.gapMedium);
        holder.icon.setImageDrawable(drawable);

        holder.icon.setBackgroundResource(background);

        holder.name.setText(githubStatus.description);

        holder.description.setText(TimeUtils.getTimeAgoString(githubStatus.updated_at));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView name;
        private final TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getItem(getAdapterPosition()).target_url));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
