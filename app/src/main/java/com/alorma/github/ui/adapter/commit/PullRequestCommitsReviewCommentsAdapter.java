package com.alorma.github.ui.adapter.commit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComment;
import com.alorma.github.sdk.bean.issue.PullRequestStoryCommit;
import com.alorma.github.ui.activity.CommitDetailActivity;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.ui.view.issue.ReviewCommentView;
import com.alorma.github.utils.TextUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.IOException;

/**
 * Created by Bernat on 07/09/2014.
 */
public class PullRequestCommitsReviewCommentsAdapter
        extends RecyclerArrayAdapter<IssueStoryDetail, PullRequestCommitsReviewCommentsAdapter.Holder> {

    private static final int VIEW_INVALID = -1;
    private static final int VIEW_COMMIT = 0;
    private static final int VIEW_REVIEW = 1;

    private boolean shortMessage;
    private RepoInfo repoInfo;

    public PullRequestCommitsReviewCommentsAdapter(LayoutInflater inflater, boolean shortMessage, RepoInfo repoInfo) {
        super(inflater);
        this.shortMessage = shortMessage;
        this.repoInfo = repoInfo;
    }

    @Override
    public PullRequestCommitsReviewCommentsAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_COMMIT) {
            return new CommitViewHolder(getInflater().inflate(R.layout.row_commit, parent, false));
        } else if (viewType == VIEW_REVIEW) {
            return new ReviewCommentHolder(getInflater().inflate(R.layout.timeline_review_comment, parent, false));
        } else {
            return new Holder(getInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(PullRequestCommitsReviewCommentsAdapter.Holder holder, IssueStoryDetail detail) {

        if (detail instanceof PullRequestStoryCommit) {
            Commit commit = ((PullRequestStoryCommit) detail).commit;
            handleCommit((CommitViewHolder) holder, commit);
        } else if (detail instanceof IssueStoryReviewComment) {
            handleReviewComment((ReviewCommentHolder) holder, (IssueStoryReviewComment) detail);
        }
    }

    private void handleCommit(CommitViewHolder holder, Commit commit) {
        User author = commit.author;

        if (author == null) {
            author = commit.commit.author;
        }

        if (author == null) {
            author = commit.commit.committer;
        }

        if (author != null) {

            holder.avatar.setUser(author);

            if (author.login != null) {
                holder.user.setText(author.login);
            } else if (author.name != null) {
                holder.user.setText(author.name);
            } else if (author.email != null) {
                holder.user.setText(author.email);
            }
        }

        String message = commit.shortMessage();
        if (commit.commit != null && commit.commit.shortMessage() != null) {
            message = commit.commit.shortMessage();
        }

        if (shortMessage) {
            try {
                holder.title.setText(TextUtils.splitLines(message, 2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.title.setText(message);
        }

        if (commit.sha != null) {
            holder.sha.setText(commit.shortSha());
        }

        holder.textNums.setText("");

        if (commit.stats != null) {
            String textCommitsStr = null;
            if (commit.stats.additions > 0 && commit.stats.deletions > 0) {
                textCommitsStr =
                        holder.itemView.getContext().getString(R.string.commit_file_add_del, commit.stats.additions, commit.stats.deletions);
                holder.textNums.setVisibility(View.VISIBLE);
            } else if (commit.stats.additions > 0) {
                textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_add, commit.stats.additions);
                holder.textNums.setVisibility(View.VISIBLE);
            } else if (commit.stats.deletions > 0) {
                textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_del, commit.stats.deletions);
                holder.textNums.setVisibility(View.VISIBLE);
            } else {
                holder.textNums.setVisibility(View.GONE);
            }

            if (textCommitsStr != null) {
                holder.textNums.setText(Html.fromHtml(textCommitsStr));
            }
        } else {
            holder.textNums.setVisibility(View.GONE);
        }

        if (commit.files != null && commit.files.size() > 0) {
            holder.numFiles.setVisibility(View.VISIBLE);
            holder.numFiles.setText(holder.itemView.getContext().getString(R.string.num_of_files, commit.files.size()));
        } else {
            holder.numFiles.setVisibility(View.GONE);
        }

        holder.comments_count.setText(String.valueOf(commit.comment_count));
        applyIcon(holder.comments_count, Octicons.Icon.oct_comment_discussion);
    }

    private void applyIcon(TextView textView, Octicons.Icon value) {
        IconicsDrawable drawableForks = new IconicsDrawable(textView.getContext(), value);
        drawableForks.sizeRes(R.dimen.textSizeSmall);
        drawableForks.colorRes(R.color.icons);
        textView.setCompoundDrawables(null, null, drawableForks, null);
        int offset = textView.getResources().getDimensionPixelOffset(R.dimen.textSizeSmall);
        textView.setCompoundDrawablePadding(offset);
    }

    private void handleReviewComment(ReviewCommentHolder holder, IssueStoryReviewComment comment) {
        holder.reviewCommentView.setReviewCommit(comment, repoInfo);
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        if (getItem(position) instanceof PullRequestStoryCommit) {
            return VIEW_COMMIT;
        } else if (getItem(position) instanceof IssueStoryReviewComment) {
            return VIEW_REVIEW;
        }

        return VIEW_INVALID;
    }

    public class CommitViewHolder extends Holder {

        private final TextView title;
        private final TextView user;
        private final TextView sha;
        private final TextView textNums;
        private final TextView numFiles;
        private final UserAvatarView avatar;
        private final TextView comments_count;

        public CommitViewHolder(final View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            user = (TextView) itemView.findViewById(R.id.user);
            sha = (TextView) itemView.findViewById(R.id.sha);
            textNums = (TextView) itemView.findViewById(R.id.textNums);
            numFiles = (TextView) itemView.findViewById(R.id.numFiles);
            comments_count = (TextView) itemView.findViewById(R.id.comments_count);
            avatar = (UserAvatarView) itemView.findViewById(R.id.avatarAuthor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItem(getAdapterPosition()) instanceof PullRequestStoryCommit) {
                        Commit commit = ((PullRequestStoryCommit) getItem(getAdapterPosition())).commit;
                        CommitInfo info = new CommitInfo();
                        info.repoInfo = repoInfo;
                        info.sha = commit.sha;

                        Intent intent = CommitDetailActivity.launchIntent(v.getContext(), info);
                        v.getContext().startActivity(intent);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (getItem(getAdapterPosition()) instanceof PullRequestStoryCommit) {
                        Commit item = ((PullRequestStoryCommit) getItem(getAdapterPosition())).commit;
                        copy(item.shortSha());
                        Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.sha_copied, item.shortSha()), Toast.LENGTH_SHORT)
                                .show();
                    }
                    return true;
                }
            });
        }

        public void copy(String text) {
            ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Gitskarios", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    private class ReviewCommentHolder extends Holder {
        private ReviewCommentView reviewCommentView;

        public ReviewCommentHolder(View itemView) {
            super(itemView);
            reviewCommentView = (ReviewCommentView) itemView.findViewById(R.id.review);
        }
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }
}
