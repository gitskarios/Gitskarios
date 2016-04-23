package com.alorma.github.ui.fragment.repos;

import com.alorma.github.R;
import com.alorma.github.sdk.core.repositories.Repo;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.util.List;

public class MembershipReposFragment extends ReposFragment {

  public static MembershipReposFragment newInstance() {
    return new MembershipReposFragment();
  }

  @Override
  public int getTitle() {
    return R.string.navigation_orgs_members;
  }

  @Override
  public IIcon getTitleIcon() {
    return Octicons.Icon.oct_repo;
  }

  @Override
  public void onResponse(List<Repo> repos) {

  }

  @Override
  public void loadMoreItems() {

  }
}
