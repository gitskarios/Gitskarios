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

public class ReposAdapter extends RecyclerArrayAdapter<Repo, ReposAdapter.ViewHolder> {

    public ReposAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.row_repo, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Repo repo = getItem(position);

        holder.textTitle.setText(repo.name);

        String starText = holder.itemView.getResources().getString(R.string.star_icon_text, repo.stargazers_count);
        applyIcon(holder.textStarts, Octicons.Icon.oct_star);
        holder.textStarts.setText(starText);

        String forkText = holder.itemView.getResources().getString(R.string.fork_icon_text, repo.forks_count);
        applyIcon(holder.textForks, Octicons.Icon.oct_repo_forked);
        holder.textForks.setText(forkText);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle;
        public TextView repoPrivate;
        public TextView textDescription;
        public TextView textForks;
        public TextView textStarts;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.repoName);
            repoPrivate = (TextView) itemView.findViewById(R.id.repoPrivate);
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

    private void applyIcon(TextView textView, Octicons.Icon value) {
        IconicsDrawable drawableForks = new IconicsDrawable(textView.getContext(), value);
        drawableForks.sizeRes(R.dimen.textSizeSmall);
        drawableForks.colorRes(R.color.icons);
        textView.setCompoundDrawables(null, null, drawableForks, null);
        int offset = textView.getResources().getDimensionPixelOffset(R.dimen.textSizeSmall);
        textView.setCompoundDrawablePadding(offset);
    }

}