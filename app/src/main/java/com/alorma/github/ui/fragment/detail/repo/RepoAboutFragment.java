package com.alorma.github.ui.fragment.detail.repo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.cache.QnCacheProvider;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetReadmeContentsClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import com.alorma.github.ui.activity.ForksActivity;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.activity.RepoDetailActivity;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.TimeUtils;
import com.alorma.gitskarios.core.client.BaseClient;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Bernat on 01/01/2015.
 */
public class RepoAboutFragment extends Fragment implements TitleProvider, BranchManager, BackManager, BaseClient.OnResultCallback<String> {

    private static final String REPO_INFO = "REPO_INFO";

    private RepoInfo repoInfo;
    private Repo currentRepo;
    private TextView htmlContentView;
    private ImageView profileIcon;

    private TextView starredPlaceholder;
    private TextView watchedPlaceholder;
    private TextView forkPlaceHolder;

    private TextView authorName;
    private View fork;
    private TextView forkOfTextView;
    private TextView createdAtTextView;
    private Boolean repoStarred = null;
    private Boolean repoWatched = null;
    private View author;

    public static RepoAboutFragment newInstance(RepoInfo repoInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(REPO_INFO, repoInfo);

        RepoAboutFragment f = new RepoAboutFragment();
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.repo_overview_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        author = view.findViewById(R.id.author);
        profileIcon = (ImageView) author.findViewById(R.id.profileIcon);
        authorName = (TextView) author.findViewById(R.id.authorName);

        htmlContentView = (TextView) view.findViewById(R.id.htmlContentView);

        fork = view.findViewById(R.id.fork);
        forkOfTextView = (TextView) fork.findViewById(R.id.forkOf);

        createdAtTextView = (TextView) view.findViewById(R.id.createdAt);

        starredPlaceholder = (TextView) view.findViewById(R.id.starredPlaceholder);
        watchedPlaceholder = (TextView) view.findViewById(R.id.watchedPlaceHolder);
        forkPlaceHolder = (TextView) view.findViewById(R.id.forkPlaceHolder);

        starredPlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repoStarred != null) {
                    changeStarStatus();
                }
            }
        });

        watchedPlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repoWatched != null) {
                    changeWatchedStatus();
                }
            }
        });

        forkPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repoInfo != null) {
                    Intent intent = ForksActivity.launchIntent(v.getContext(), repoInfo);
                    startActivity(intent);
                }
            }
        });

        fork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRepo != null && currentRepo.parent != null) {
                    RepoInfo repoInfo = new RepoInfo();
                    repoInfo.owner = currentRepo.parent.owner.login;
                    repoInfo.name = currentRepo.parent.name;
                    if (!TextUtils.isEmpty(currentRepo.default_branch)) {
                        repoInfo.branch = currentRepo.default_branch;
                    }

                    Intent intent = RepoDetailActivity.createLauncherIntent(getActivity(), repoInfo);
                    startActivity(intent);
                }
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRepo != null && currentRepo.owner != null) {
                    if (currentRepo.owner.type == UserType.User) {
                        Intent intent =
                            ProfileActivity.createLauncherIntent(getActivity(), currentRepo.owner);
                        startActivity(intent);
                    } else if (currentRepo.owner.type == UserType.Organization) {
                        Intent intent = OrganizationActivity.launchIntent(getActivity(),
                            currentRepo.owner.login);
                        startActivity(intent);
                    }
                }
            }
        });

        getContent();
    }

    @Override
    public int getTitle() {
        return R.string.overview_fragment_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_info;
    }

    protected void loadArguments() {
        if (getArguments() != null) {
            repoInfo = getArguments().getParcelable(REPO_INFO);
        }
    }

    private void getContent() {
        if (repoInfo == null) {
            loadArguments();
        }

        GetRepoClient repoClient = new GetRepoClient(getActivity(), repoInfo);
        repoClient.observable().observeOn(AndroidSchedulers.mainThread()).subscribe(
            new Subscriber<Pair<Repo, Response>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Pair<Repo, Response> repoResponsePair) {
                    currentRepo = repoResponsePair.first;
                    getReadme();
                    getStarWatchData();
                    setData(currentRepo);
                }
            });

        boolean contains = QnCacheProvider.getInstance(QnCacheProvider.TYPE.REPO).contains(repoInfo.toString() + "_README");
        if (contains) {
            onResponseOk(QnCacheProvider.getInstance(QnCacheProvider.TYPE.REPO).<String>get(repoInfo.toString() + "_README"), null);
            getReadme();
        } else {
            getReadme();
        }

    }

    private void getReadme() {
        GetReadmeContentsClient repoMarkdownClient = new GetReadmeContentsClient(getActivity(), repoInfo);
        repoMarkdownClient.setCallback(this);
        repoMarkdownClient.execute();
    }

    @Override
    public void onResponseOk(final String htmlContent, Response r) {
        if (htmlContent != null && htmlContentView != null) {
            String htmlCode = HtmlUtils.format(htmlContent).toString();
            HttpImageGetter imageGetter = new HttpImageGetter(getActivity());

            imageGetter.repoInfo(repoInfo);
            imageGetter.bind(htmlContentView, htmlCode, repoInfo.hashCode());

            htmlContentView.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
            QnCacheProvider.getInstance(QnCacheProvider.TYPE.REPO).set(repoInfo.toString() + "_README", htmlCode);
        }
    }

    private void setData(Repo currentRepo) {
        if (this.currentRepo != null) {
            User owner = this.currentRepo.owner;
            ImageLoader.getInstance().displayImage(owner.avatar_url, profileIcon);
            authorName.setText(owner.login);

            if (this.currentRepo.parent != null) {
                fork.setVisibility(View.VISIBLE);
                forkOfTextView.setCompoundDrawables(getIcon(Octicons.Icon.oct_repo_forked, 24), null, null, null);
                forkOfTextView.setText(this.currentRepo.parent.owner.login + "/" + this.currentRepo.parent.name);
            }

            createdAtTextView.setCompoundDrawables(getIcon(Octicons.Icon.oct_clock, 24), null, null, null);
            createdAtTextView.setText(TimeUtils.getDateToText(getActivity(), this.currentRepo.created_at, R.string.created_at));

            starredPlaceholder.setText(String.valueOf(this.currentRepo.stargazers_count));
            watchedPlaceholder.setText(String.valueOf(this.currentRepo.subscribers_count));
            forkPlaceHolder.setText(String.valueOf(this.currentRepo.forks_count));

            forkPlaceHolder.setCompoundDrawables(getIcon(Octicons.Icon.oct_repo_forked, 24), null, null, null);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        // TODO HTML readme cannot be shown
    }

    private IconicsDrawable getIcon(IIcon icon, int sizeDp) {
        return new IconicsDrawable(getActivity(), icon).colorRes(R.color.primary).sizeDp(sizeDp);
    }

    @Override
    public void setCurrentBranch(String branch) {
        if (getActivity() != null) {
            getReadme();
        }
    }


    @Override
    public boolean onBackPressed() {
        return true;
    }

    protected void getStarWatchData() {
        CheckRepoStarredClient repoStarredClient = new CheckRepoStarredClient(getActivity(), currentRepo.owner.login, currentRepo.name);
        repoStarredClient.setOnResultCallback(new StarredResult());
        repoStarredClient.execute();

        CheckRepoWatchedClient repoWatchedClient = new CheckRepoWatchedClient(getActivity(), currentRepo.owner.login, currentRepo.name);
        repoWatchedClient.setOnResultCallback(new WatchedResult());
        repoWatchedClient.execute();
    }

    private void changeStarStatus() {
        if (repoStarred) {
            UnstarRepoClient unstarRepoClient = new UnstarRepoClient(getActivity(), currentRepo.owner.login, currentRepo.name);
            unstarRepoClient.setOnResultCallback(new UnstarActionResult());
            unstarRepoClient.execute();
        } else {
            StarRepoClient starRepoClient = new StarRepoClient(getActivity(), currentRepo.owner.login, currentRepo.name);
            starRepoClient.setOnResultCallback(new StarActionResult());
            starRepoClient.execute();
        }
    }

    private void changeWatchedStatus() {
        if (repoWatched) {
            UnwatchRepoClient unwatchRepoClient = new UnwatchRepoClient(getActivity(), currentRepo.owner.login, currentRepo.name);
            unwatchRepoClient.setOnResultCallback(new UnwatchActionResult());
            unwatchRepoClient.execute();
        } else {
            WatchRepoClient watchRepoClient = new WatchRepoClient(getActivity(), currentRepo.owner.login, currentRepo.name);
            watchRepoClient.setOnResultCallback(new WatchActionResult());
            watchRepoClient.execute();
        }
    }

    /**
     * Results for STAR
     */
    private class StarredResult implements BaseClient.OnResultCallback<Response> {

        @Override
        public void onResponseOk(Response o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                changeStarView();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error != null) {
                if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                    repoStarred = false;
                    changeStarView();
                }
            }
        }
    }

    private class UnstarActionResult implements BaseClient.OnResultCallback<Response> {

        @Override
        public void onResponseOk(Response o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = false;
                changeStarView();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            changeStarView();
        }
    }

    private class StarActionResult implements BaseClient.OnResultCallback<Response> {

        @Override
        public void onResponseOk(Response o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoStarred = true;
                changeStarView();
            }

        }

        @Override
        public void onFail(RetrofitError error) {
            changeStarView();
        }
    }

    private void changeStarView() {
        if (getActivity() != null) {
            IconicsDrawable drawable = new IconicsDrawable(getActivity(), Octicons.Icon.oct_star).sizeDp(24);
            if (repoStarred != null && repoStarred) {
                drawable.colorRes(R.color.primary);
            } else {
                drawable.colorRes(R.color.icons);
            }
            starredPlaceholder.setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * RESULTS FOR WATCH
     */

    private class WatchedResult implements BaseClient.OnResultCallback<Response> {

        @Override
        public void onResponseOk(Response o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoWatched = true;
                changeWatchView();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            if (error != null) {
                if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
                    repoWatched = false;
                    changeWatchView();
                }
            }
        }
    }

    private class UnwatchActionResult implements BaseClient.OnResultCallback<Response> {

        @Override
        public void onResponseOk(Response o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoWatched = false;
                changeWatchView();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            changeWatchView();
        }
    }

    private class WatchActionResult implements BaseClient.OnResultCallback<Object> {

        @Override
        public void onResponseOk(Object o, Response r) {
            if (r != null && r.getStatus() == 204) {
                repoWatched = true;
                changeWatchView();
            }
        }

        @Override
        public void onFail(RetrofitError error) {
            changeWatchView();
        }
    }

    private void changeWatchView() {
        if (getActivity() != null) {
            IconicsDrawable drawable = new IconicsDrawable(getActivity(), Octicons.Icon.oct_eye).sizeDp(24);
            if (repoWatched != null && repoWatched) {
                drawable.colorRes(R.color.primary);
            } else {
                drawable.colorRes(R.color.icons);
            }
            watchedPlaceholder.setCompoundDrawables(drawable, null, null, null);
        }
    }

}
