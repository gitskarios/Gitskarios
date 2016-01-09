package com.alorma.github.ui.adapter.commit;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.UserAvatarView;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;

/**
 * Created by Bernat on 23/06/2015.
 */
public class CommitCommentAdapter extends RecyclerArrayAdapter<CommitComment, CommitCommentAdapter.ViewHolder> {

    private RepoInfo repoInfo;

    public CommitCommentAdapter(LayoutInflater inflater, RepoInfo repoInfo) {
        super(inflater);
        this.repoInfo = repoInfo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.commit_comment_row, parent, false));
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, CommitComment commitComment) {
        if (commitComment.user != null) {

            holder.textAuthor.setText(commitComment.user.login);

            holder.imageAuthor.setUser(commitComment.user);
        }

        if (commitComment.body_html != null) {
            String htmlCode = HtmlUtils.format(commitComment.body_html).toString();
            HttpImageGetter imageGetter = new HttpImageGetter(holder.itemView.getContext());
            imageGetter.repoInfo(repoInfo);
            imageGetter.bind(holder.textContent, htmlCode, commitComment.hashCode());
            holder.textContent.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
        }

        holder.toolbar.setVisibility(View.INVISIBLE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textContent;
        private final TextView textAuthor;
        private final UserAvatarView imageAuthor;
        private final Toolbar toolbar;

        public ViewHolder(View itemView) {
            super(itemView);
            textContent = (TextView) itemView.findViewById(R.id.textContent);
            textAuthor = (TextView) itemView.findViewById(R.id.textAuthor);
            imageAuthor = (UserAvatarView) itemView.findViewById(R.id.avatarAuthor);
            toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
        }
    }
}
