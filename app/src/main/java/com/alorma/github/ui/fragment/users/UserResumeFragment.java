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
import com.alorma.github.ui.activity.OrganizationsActivity;
import com.alorma.github.ui.activity.gists.GistsMainActivity;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.utils.TimeUtils;
import com.github.javierugarte.GitHubContributionsView;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import core.User;
import java.util.ArrayList;
import java.util.List;

public class UserResumeFragment extends BaseFragment implements TitleProvider {

  ViewGroup cardAbout;
  ViewGroup cardContributions;
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
    cardContributions = (ViewGroup) view.findViewById(R.id.cardContributions);
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
      fillCardContributions(user);
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
    if (user.getCreatedAt() != null) {
      ProfileItem profileUserCreated =
          new ProfileItem(Octicons.Icon.oct_clock, TimeUtils.getDateToText(getActivity(), user.getCreatedAt(), R.string.joined_at), null);
      addItem(profileUserCreated, cardAbout);
    }
    if (!TextUtils.isEmpty(user.getCompany())) {
      Intent intent = new Intent(Intent.ACTION_SEARCH);
      intent.putExtra(SearchManager.QUERY, user.getCompany());
      ProfileItem profileUserOrganization = new ProfileItem(Octicons.Icon.oct_organization, user.getCompany(), intent);
      addItem(profileUserOrganization, cardAbout);
    }
    if (!TextUtils.isEmpty(user.getLocation())) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      Uri geo = Uri.parse("geo:0,0?q=" + user.getLocation());
      intent.setData(geo);
      ProfileItem profileUserLocation = new ProfileItem(Octicons.Icon.oct_location, user.getLocation(), intent);
      addItem(profileUserLocation, cardAbout);
    }
    if (!TextUtils.isEmpty(user.getEmail())) {
      Intent intent = new Intent(Intent.ACTION_SENDTO);
      intent.setData(Uri.parse("mailto:"));
      intent.putExtra(Intent.EXTRA_EMAIL, new String[] { user.getEmail() });
      ProfileItem profileUserEmail = new ProfileItem(Octicons.Icon.oct_mail, user.getEmail(), intent);
      addItem(profileUserEmail, cardAbout);
    }
    if (!TextUtils.isEmpty(user.getBlog())) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      if (!user.getBlog().startsWith("http://") && !user.getBlog().startsWith("https://")) {
        user.setBlog("http://" + user.getBlog());
      }
      intent.setData(Uri.parse(user.getBlog()));
      ProfileItem profileUserBlog = new ProfileItem(Octicons.Icon.oct_link, user.getBlog(), intent);
      addItem(profileUserBlog, cardAbout);
    }
  }

  private void fillCardContributions(User user) {
    if (user != null && user.getLogin() != null && getView() != null) {
      GitHubContributionsView contributionsView = (GitHubContributionsView) getView().findViewById(R.id.github_contributions_view);
      contributionsView.loadUserName(user.getLogin());
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
    if (user.getOrganizationsCount() > 0) {
      Intent intent = OrganizationsActivity.launchIntent(getActivity(), user.getLogin());
      final ProfileItem profileItemOrgs = new ProfileItem(Octicons.Icon.oct_organization, getString(R.string.orgs_num_empty), intent);
      addItem(profileItemOrgs, cardGithub);
    }

    if (user.getPublicRepos() > 0) {
      String text = getString(R.string.repos_num, user.getPublicRepos());
      ProfileItem profileItemRepos = new ProfileItem(Octicons.Icon.oct_repo, text, null);
      profileItemRepos.setCallback(id -> notifyOpenRepos());
      addItem(profileItemRepos, cardGithub);
    }

    if (user.getPublicGists() > 0) {
      String text = getString(R.string.gists_num, user.getPublicGists());
      Intent intent = GistsMainActivity.createLauncherIntent(getActivity(), user.getLogin());
      ProfileItem profileItemGists = new ProfileItem(Octicons.Icon.oct_gist, text, intent);
      addItem(profileItemGists, cardGithub);
    }
  }

  private void notifyOpenRepos() {
    if (userResumeCallback != null) {
      userResumeCallback.openRepos(user.getLogin());
    }
  }

  public void setUserResumeCallback(UserResumeCallback userResumeCallback) {
    this.userResumeCallback = userResumeCallback;
  }

  @Override
  protected int getLightTheme() {
    return R.style.AppTheme_Profile;
  }

  @Override
  protected int getDarkTheme() {
    return R.style.AppTheme_Dark_Profile;
  }

  public interface UserResumeCallback {
    void openRepos(String login);
  }
}
