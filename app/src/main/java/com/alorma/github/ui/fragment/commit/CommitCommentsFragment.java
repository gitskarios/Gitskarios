package com.alorma.github.ui.fragment.commit;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitComment;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.sdk.services.client.GithubListClient;
import com.alorma.github.sdk.services.commit.GetCommitCommentsClient;
import com.alorma.github.ui.adapter.commit.CommitCommentAdapter;
import com.alorma.github.ui.fragment.base.LoadingListFragment;
import com.alorma.gitskarios.core.Pair;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Bernat on 23/06/2015.
 */
public class CommitCommentsFragment extends LoadingListFragment<CommitCommentAdapter> implements Observer<List<CommitComment>> {

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
            setAction(new GetCommitCommentsClient(info));
        }
    }

    @Override
    protected void executePaginatedRequest(int page) {
        super.executePaginatedRequest(page);
        if (info != null) {
            setAction(new GetCommitCommentsClient(info, page));
        }
    }

    private void setAction(GithubListClient<List<CommitComment>> client) {
        client.observable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Pair<List<CommitComment>, Integer>, List<CommitComment>>() {
            @Override
            public List<CommitComment> call(Pair<List<CommitComment>, Integer> listIntegerPair) {
                setPage(listIntegerPair.second);
                return listIntegerPair.first;
            }
        }).subscribe(this);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(List<CommitComment> commitComments) {
        if (commitComments != null && commitComments.size() > 0) {
            hideEmpty();
            if (getAdapter() != null) {
                getAdapter().addAll(commitComments);
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
        info = (CommitInfo) getArguments().getParcelable(INFO);
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
