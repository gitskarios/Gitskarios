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
public class UserResumeFragment extends BaseFragment implements TitleProvider{

    private List<ProfileItem> profileItems = new ArrayList<>();
    ViewGroup cardAbout;
    private int color = Color.BLACK;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_resume_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardAbout = (ViewGroup) view.findViewById(R.id.cardAbout);

    }

    public void fill(User user) {
        if (getActivity() != null && isAdded()) {
            fillCardBio(user);
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

    /*
    private void fillCardGithubData(User user) {
        if (user.public_repos > 0) {
            String text = getString(R.string.repos_num, user.public_repos);
            Intent intent = ReposActivity.launchIntent(getActivity(), user.login, user.type);
            ProfileItem profileItemRepos = new ProfileItem(Octicons.Icon.oct_repo, text, intent);
            profileItemsAdapter.add(profileItemRepos);
        }
        if (user.public_gists > 0) {
            String text = getString(R.string.gists_num, user.public_gists);
            Intent intent = GistsMainActivity.createLauncherIntent(getActivity(), user.login);
            ProfileItem profileItemGists = new ProfileItem(Octicons.Icon.oct_gist, text, intent);
            profileItemsAdapter.add(profileItemGists);
        }

        Intent intent = OrganizationsActivity.launchIntent(getActivity(), user.login);
        final ProfileItem profileItemOrgs =
                new ProfileItem(Octicons.Icon.oct_organization, getString(R.string.orgs_num_empty), intent);
        profileItemsAdapter.add(profileItemOrgs);

        GetOrgsClient orgsClient = new GetOrgsClient(this, user.login);
        orgsClient.observable()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Pair<List<Organization>, Integer>, List<Organization>>() {
                    @Override
                    public List<Organization> call(Pair<List<Organization>, Integer> listIntegerPair) {
                        return listIntegerPair.first;
                    }
                })
                .subscribe(new Subscriber<List<Organization>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Organization> organizations) {
                        if (organizations != null && organizations.size() > 0) {
                            profileItemOrgs.value = getString(R.string.orgs_num, organizations.size());
                            profileItemsAdapter.notifyDataSetChanged();
                        } else {
                            profileItemsAdapter.remove(profileItemOrgs);
                        }
                    }
                });
    }
    */

}
