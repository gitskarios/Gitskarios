package com.alorma.github.ui.fragment.releases;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.bean.dto.response.UserType;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.ui.activity.OrganizationActivity;
import com.alorma.github.ui.activity.ProfileActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.TimeUtils;
import com.alorma.gitskarios.core.client.BaseClient;
import com.gh4a.utils.UiUtils;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseAboutFragment extends BaseFragment implements TitleProvider {

    private static final String RELEASE = "RELEASE";
    private static final String REPO_INFO = "REPO_INFO";
    private View author;
    private ImageView profileIcon;
    private TextView authorName;
    private TextView htmlContentView;
    private TextView createdAtTextView;
    private ImageView createdIcon;
    private View progressBar;

    public static ReleaseAboutFragment newInstance(Release release, RepoInfo repoInfo) {
        ReleaseAboutFragment releaseAboutFragment = new ReleaseAboutFragment();

        Bundle args = new Bundle();
        args.putParcelable(RELEASE, release);
        args.putParcelable(REPO_INFO, repoInfo);

        releaseAboutFragment.setArguments(args);

        return releaseAboutFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.release_detail_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        author = view.findViewById(R.id.author);
        profileIcon = (ImageView) author.findViewById(R.id.profileIcon);
        authorName = (TextView) author.findViewById(R.id.authorName);

        createdAtTextView = (TextView) view.findViewById(R.id.createdAt);
        createdIcon = (ImageView) view.findViewById(R.id.createdIcon);

        progressBar = view.findViewById(R.id.progressBar);
        htmlContentView = (TextView) view.findViewById(R.id.htmlContentView);

        final Release release = getArguments().getParcelable(RELEASE);

        if (release != null) {
            User owner = release.author;
            ImageLoader.getInstance().displayImage(owner.avatar_url, profileIcon);
            authorName.setText(owner.login);

            createdIcon.setImageDrawable(new IconicsDrawable(getActivity(), Octicons.Icon.oct_clock).colorRes(R.color.primary).actionBar());
            createdAtTextView.setText(TimeUtils.getDateToText(getActivity(), release.created_at, R.string.created_at));

            final RepoInfo repoInfo = getArguments().getParcelable(REPO_INFO);

            if (repoInfo != null && release.body != null && htmlContentView != null) {

                RequestMarkdownDTO requestMarkdownDTO = new RequestMarkdownDTO();
                requestMarkdownDTO.text = release.body;
                GetMarkdownClient markdownClient = new GetMarkdownClient(getActivity(), requestMarkdownDTO);
                markdownClient.setOnResultCallback(new BaseClient.OnResultCallback<String>() {
                    @Override
                    public void onResponseOk(String s, Response r) {
                        String htmlCode = HtmlUtils.format(s).toString();
                        HttpImageGetter imageGetter = new HttpImageGetter(getActivity());

                        imageGetter.repoInfo(repoInfo);
                        imageGetter.bind(htmlContentView, htmlCode, repoInfo.hashCode());

                        htmlContentView.setMovementMethod(UiUtils.CHECKING_LINK_METHOD);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFail(RetrofitError error) {

                    }
                });
                markdownClient.execute();
            }

            author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (release.author != null) {
                        if (release.author.type == UserType.User) {
                            Intent intent = ProfileActivity.createLauncherIntent(getActivity(), release.author);
                            startActivity(intent);
                        } else if (release.author.type == UserType.Organization) {
                            Intent intent = OrganizationActivity.launchIntent(getActivity(), release.author.login);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getTitle() {
        return R.string.repo_release_fragment_detail_title;
    }
}
