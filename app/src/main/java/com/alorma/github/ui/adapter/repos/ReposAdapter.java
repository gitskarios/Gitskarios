package com.alorma.github.ui.adapter.repos;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.UrlsManager;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.text.DecimalFormat;

public class ReposAdapter extends RecyclerArrayAdapter<Repo, ReposAdapter.ViewHolder> {

    private boolean showOwnerName;
    private boolean showOwnerNameExtra = true;

    public ReposAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_repo, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, Repo repo) {
        holder.textTitle.setText(showOwnerName ? repo.owner.login : repo.name);

        if (showOwnerNameExtra) {
            holder.textOwnerName.setText(repo.owner.login);
        } else {
            holder.textOwnerName.setText("");
        }

        applyIcon(holder.textStarts, Octicons.Icon.oct_star);
        holder.textStarts.setText(new DecimalFormat().format(repo.stargazers_count));

        applyIcon(holder.textForks, Octicons.Icon.oct_repo_forked);
        holder.textForks.setText(new DecimalFormat().format(repo.forks_count));

        if (repo.description != null) {
            holder.textDescription.setVisibility(View.VISIBLE);
            holder.textDescription.setText(repo.description);
        } else {
            holder.textDescription.setVisibility(View.GONE);
        }

        if (repo.isPrivate) {
            holder.repoPrivate.setVisibility(View.VISIBLE);
        } else {
            holder.repoPrivate.setVisibility(View.GONE);
        }
    }

    public void showOwnerName(boolean showOwnerName) {
        this.showOwnerName = showOwnerName;
        notifyDataSetChanged();
    }

    public void showOwnerNameExtra(boolean showOwnerNameExtra) {
        this.showOwnerNameExtra = showOwnerNameExtra;
        notifyDataSetChanged();
    }

    private void applyIcon(TextView textView, Octicons.Icon value) {
        IconicsDrawable drawableForks = new IconicsDrawable(textView.getContext(), value);
        drawableForks.sizeRes(R.dimen.textSizeSmall);
        drawableForks.colorRes(R.color.icons);
        textView.setCompoundDrawables(null, null, drawableForks, null);
        int offset = textView.getResources().getDimensionPixelOffset(R.dimen.textSizeSmall);
        textView.setCompoundDrawablePadding(offset);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle;
        public TextView repoPrivate;
        public TextView textOwnerName;
        public TextView textDescription;
        public TextView textForks;
        public TextView textStarts;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.repoName);
            repoPrivate = (TextView) itemView.findViewById(R.id.repoPrivate);
            textOwnerName = (TextView) itemView.findViewById(R.id.textOwnerName);
            textDescription = (TextView) itemView.findViewById(R.id.descriptionText);
            textStarts = (TextView) itemView.findViewById(R.id.textStarts);
            textForks = (TextView) itemView.findViewById(R.id.textForks);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Repo item = getItem(getAdapterPosition());
                    if (item != null) {
                        v.getContext().startActivity(new UrlsManager(v.getContext()).manageRepos(Uri.parse(item.html_url)));
                    }
                }
            });
        }
    }
}