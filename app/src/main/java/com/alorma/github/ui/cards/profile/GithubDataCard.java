package com.alorma.github.ui.cards.profile;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ListOrganizations;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.gitskarios.basesdk.client.BaseClient;
import com.alorma.github.sdk.services.orgs.GetOrgsClient;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 23/11/2014.
 */
public class GithubDataCard implements View.OnClickListener {

    private GithubDataCardListener githubDataCardListener;

    private User user;
    private int avatarColor;
    private TextView textOrgs;

    public GithubDataCard(User user, View view, int avatarColor) {
        this.user = user;
        this.avatarColor = avatarColor;
        setupInnerViewElements(view);
    }

    public void setupInnerViewElements(View view) {
        setUpRepos(view);
        setUpGists(view);
        setUpOrgs(view);
    }

    private void setUpRepos(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.iconRepositories);

        IconicsDrawable githubIconDrawable = drawable(view.getContext(), Octicons.Icon.oct_repo);

        icon.setImageDrawable(githubIconDrawable);

        TextView text = (TextView) view.findViewById(R.id.textRepositories);

        text.setText(view.getContext().getString(R.string.repos_num, user.public_repos));

        view.findViewById(R.id.repositories).setOnClickListener(this);
    }

    private void setUpGists(View view) {
        ImageView icon = (ImageView) view.findViewById(R.id.iconGists);

        IconicsDrawable githubIconDrawable = drawable(view.getContext(), Octicons.Icon.oct_gist);

        icon.setImageDrawable(githubIconDrawable);

        TextView text = (TextView) view.findViewById(R.id.textGists);

        text.setText(view.getContext().getString(R.string.gists_num, user.public_gists));

        view.findViewById(R.id.gists).setOnClickListener(this);
    }

    private void setUpOrgs(final View view) {
        final ImageView icon = (ImageView) view.findViewById(R.id.iconOrgs);

        IconicsDrawable githubIconDrawable = drawable(view.getContext(), Octicons.Icon.oct_organization);

        icon.setImageDrawable(githubIconDrawable);

        textOrgs = (TextView) view.findViewById(R.id.textOrgs);

        view.findViewById(R.id.orgs).setOnClickListener(this);

        GetOrgsClient orgsClient = new GetOrgsClient(view.getContext(), user.login);
        orgsClient.setOnResultCallback(new BaseClient.OnResultCallback<ListOrganizations>() {
            @Override
            public void onResponseOk(ListOrganizations organizations, Response r) {
                if (organizations != null && organizations.size() > 0) {
                    textOrgs.setText(view.getContext().getString(R.string.orgs_num, organizations.size()));
                } else {
                    view.findViewById(R.id.orgs).setVisibility(View.GONE);
                    view.findViewById(R.id.dividerGists).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFail(RetrofitError error) {

            }
        });
        orgsClient.execute();
    }

    private IconicsDrawable drawable(Context context, Octicons.Icon icon) {
        IconicsDrawable githubIconDrawable = new IconicsDrawable(context, icon);

        githubIconDrawable.sizeDp(30);
        githubIconDrawable.color(avatarColor);

        return githubIconDrawable;
    }

    @Override
    public void onClick(View v) {
        if (githubDataCardListener != null) {
            switch (v.getId()) {
                case R.id.repositories:
                    githubDataCardListener.onRepositoriesRequest(user.login);
                    break;
                case R.id.orgs:
                    githubDataCardListener.onOrganizationsRequest(user.login);
                    break;
                case R.id.gists:
                    githubDataCardListener.onGistsRequest(user.login);
                    break;
            }
        }
    }

    public void setGithubDataCardListener(GithubDataCardListener githubDataCardListener) {
        this.githubDataCardListener = githubDataCardListener;
    }

    public interface GithubDataCardListener {
        void onRepositoriesRequest(String username);

        void onOrganizationsRequest(String username);

        void onGistsRequest(String username);
    }
}
