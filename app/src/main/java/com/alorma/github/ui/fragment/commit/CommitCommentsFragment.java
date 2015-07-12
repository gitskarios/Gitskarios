package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.commit.GetCommitCommentsClient;
import com.alorma.github.ui.adapter.commit.CommitCommentAdapter;
import com.alorma.github.ui.fragment.base.PaginatedListFragment;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

/**
 * Created by Bernat on 23/06/2015.
 */
public class CommitCommentsFragment extends PaginatedListFragment<List<CommitComment>, CommitCommentAdapter> {

    private static final String INFO = "INFO";
    private CommitInfo info;

    public static CommitCommentsFragment newInstance(CommitInfo info) {
        CommitCommentsFragment f = new CommitCommentsFragment();
        Bundle b = new Bundle();
        b.putParcelable(INFO, info);
        f.setArguments(b);
        return f;
    }

    @Override
    protected void executeRequest() {
        super.executeRequest();
        if (info != null) {
            GetCommitCommentsClient commitCommentsClient = new GetCommitCommentsClient(getActivity(), info);
            commitCommentsClient.setOnResultCallback(this);
            commitCommentsClient.execute();
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        if (info != null) {
            GetCommitCommentsClient commitCommentsClient = new GetCommitCommentsClient(getActivity(), info, page);
            commitCommentsClient.setOnResultCallback(this);
            commitCommentsClient.execute();
        }
    }

    @Override
    protected void onResponse(List<CommitComment> commitComments, boolean refreshing) {
        if (commitComments != null && commitComments.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(commitComments, paging);
            } else {
                CommitCommentAdapter commentsAdapter = new CommitCommentAdapter(LayoutInflater.from(getActivity()), info.repoInfo);
                setAdapter(commentsAdapter);
            }
        } else if (getAdapter() == null || getAdapter().getItemCount() == 0) {
            setEmpty();
        }
    }

    @Override
    protected void loadArguments() {
        info = getArguments().getParcelable(INFO);
    }

    @Override
    protected Octicons.Icon getNoDataIcon() {
        return Octicons.Icon.oct_comment_discussion;
    }

    @Override
    protected int getNoDataText() {
        return R.string.no_commit_comments;
    }
}
