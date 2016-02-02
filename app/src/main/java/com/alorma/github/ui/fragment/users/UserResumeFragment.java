package com.alorma.github.ui.fragment.users;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.bean.ProfileItem;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.activity.OrganizationsActivity;
import com.alorma.github.ui.activity.gists.GistsMainActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.TimeUtils;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bernat.borras on 13/12/15.
 */
public class UserResumeFragment extends BaseFragment implements TitleProvider {

    ViewGroup cardAbout;
    ViewGroup cardGithub;
    private List<ProfileItem> profileItems = new ArrayList<>();
    private int color = Color.BLACK;
    private UserResumeCallback userResumeCallback;
    private User user;
    private boolean cardsFilled = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_resume_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardAbout = (ViewGroup) view.findViewById(R.id.cardAbout);
        cardGithub = (ViewGroup) view.findViewById(R.id.cardGithub);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (user != null && !cardsFilled) {
            fill(user);
        }
    }

    public void fill(User user) {
        this.user = user;
        if (getActivity() != null && isAdded()) {
            fillCardBio(user);
            fillCardGithubData(user);
            cardsFilled = true;
        }
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        for (ProfileItem profileItem : profileItems) {
            profileItem.updateColor(color);
        }
    }

    private void fillCardBio(User user) {
        if (user.created_at != null) {
            ProfileItem profileUserCreated = new ProfileItem(Octicons.Icon.oct_clock,
                    TimeUtils.getDateToText(getActivity(), user.created_at, R.string.joined_at), null);
            addItem(profileUserCreated, cardAbout);
        }
        if (!TextUtils.isEmpty(user.company)) {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.putExtra(SearchManager.QUERY, user.company);
            ProfileItem profileUserOrganization =
                    new ProfileItem(Octicons.Icon.oct_organization, user.company, intent);
            addItem(profileUserOrganization, cardAbout);
        }
        if (!TextUtils.isEmpty(user.location)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri geo = Uri.parse("geo:0,0?q=" + user.location);
            intent.setData(geo);
            ProfileItem profileUserLocation =
                    new ProfileItem(Octicons.Icon.oct_location, user.location, intent);
            addItem(profileUserLocation, cardAbout);
        }
        if (!TextUtils.isEmpty(user.email)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{user.email});
            ProfileItem profileUserEmail = new ProfileItem(Octicons.Icon.oct_mail, user.email, intent);
            addItem(profileUserEmail, cardAbout);
        }
        if (!TextUtils.isEmpty(user.blog)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(user.blog));
            ProfileItem profileUserBlog = new ProfileItem(Octicons.Icon.oct_link, user.blog, intent);
            addItem(profileUserBlog, cardAbout);
        }
    }

    private void addItem(ProfileItem profileItem, ViewGroup parent) {
        if (parent != null && profileItem != null) {
            profileItem.color = color;
            View view = profileItem.getView(getActivity(), parent);
            profileItems.add(profileItem);
            if (view != null) {
                view.setTag("item");
                parent.addView(view);
            }
        }
    }

    @Override
    public int getTitle() {
        return R.string.info;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_info;
    }

    private void fillCardGithubData(User user) {
        if (user.organizations > 0) {
            Intent intent = OrganizationsActivity.launchIntent(getActivity(), user.login);
            final ProfileItem profileItemOrgs =
                    new ProfileItem(Octicons.Icon.oct_organization, getString(R.string.orgs_num_empty), intent);
            addItem(profileItemOrgs, cardGithub);
        }

        if (user.public_repos > 0) {
            String text = getString(R.string.repos_num, user.public_repos);
            ProfileItem profileItemRepos = new ProfileItem(Octicons.Icon.oct_repo, text, null);
            profileItemRepos.setCallback(new ProfileItem.Callback() {
                @Override
                public void onSelected(int id) {
                    notifyOpenRepos();
                }
            });
            addItem(profileItemRepos, cardGithub);
        }

        if (user.public_gists > 0) {
            String text = getString(R.string.gists_num, user.public_gists);
            Intent intent = GistsMainActivity.createLauncherIntent(getActivity(), user.login);
            ProfileItem profileItemGists = new ProfileItem(Octicons.Icon.oct_gist, text, intent);
            addItem(profileItemGists, cardGithub);
        }

    }

    private void notifyOpenRepos() {
        if (userResumeCallback != null) {
            userResumeCallback.openRepos(user.login);
        }
    }

    public void setUserResumeCallback(UserResumeCallback userResumeCallback) {
        this.userResumeCallback = userResumeCallback;
    }

    public interface UserResumeCallback {
        void openRepos(String login);
    }
}
