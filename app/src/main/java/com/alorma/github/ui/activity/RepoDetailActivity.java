package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.inapp.IabConstants;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoStarredClient;
import com.alorma.github.sdk.services.repo.actions.CheckRepoWatchedClient;
import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.repo.FilesTreeFragment;
import com.alorma.github.ui.fragment.detail.repo.MarkdownFragment;
import com.alorma.github.ui.fragment.issues.IssuesFragment;
import com.alorma.github.ui.listeners.RefreshListener;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailActivity extends BackActivity implements RefreshListener, BaseClient.OnResultCallback<Repo>, AdapterView.OnItemSelectedListener {

	public static final String OWNER = "OWNER";
	public static final String REPO = "REPO";
	public static final String FROM_INTENT_FILTER = "FROM_INTENT_FILTER";
	public static final String DESCRIPTION = "DESCRIPTION";

	private Uri shareUri;
	private String description;
	private RepoInfo repoInfo;
	private boolean fromIntentFilter;
	private boolean repoStarred;
	private boolean repoWatched;
	private SmoothProgressBar smoothBar;
	private Repo currentRepo;
	private MarkdownFragment markDownFragment;
	private FilesTreeFragment filesTreeFragment;
	private IssuesFragment issuesFragment;
	private ArrayAdapter<Branch> branchesAdapter;
	private Spinner repoDetailBranches;
	private Branch currentBranch;
	private Spinner spinnerNavigation;

	public static Intent createLauncherActivity(Context context, String owner, String repo, String description) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);
		bundle.putString(DESCRIPTION, description);
		bundle.putBoolean(FROM_INTENT_FILTER, false);

		Intent intent = new Intent(context, RepoDetailActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	public static Intent createIntentFilterLauncherActivity(Context context, String owner, String repo, String description) {
		Bundle bundle = new Bundle();
		bundle.putString(OWNER, owner);
		bundle.putString(REPO, repo);
		bundle.putString(DESCRIPTION, description);
		bundle.putBoolean(FROM_INTENT_FILTER, true);

		Intent intent = new Intent(context, RepoDetailActivity.class);
		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repo_detail_activity);

		if (getIntent().getExtras() != null) {
			repoInfo = new RepoInfo();
			repoInfo.owner = getIntent().getExtras().getString(OWNER);
			repoInfo.repo = getIntent().getExtras().getString(REPO);

			setUpShare(repoInfo);

			description = getIntent().getExtras().getString(DESCRIPTION);
			fromIntentFilter = getIntent().getExtras().getBoolean(FROM_INTENT_FILTER);

			spinnerNavigation = (Spinner) findViewById(R.id.repoDetailNavigation);
			repoDetailBranches = (Spinner) findViewById(R.id.repoDetailBranches);

			spinnerNavigation.setOnItemSelectedListener(this);
			repoDetailBranches.setOnItemSelectedListener(this);

			spinnerNavigation.setVisibility(View.INVISIBLE);
			repoDetailBranches.setVisibility(View.INVISIBLE);

			load();
		} else {
			finish();
		}
	}

	private void load() {
		GetRepoClient repoClient = new GetRepoClient(this, repoInfo.owner, repoInfo.repo);
		repoClient.setOnResultCallback(this);
		repoClient.execute();

		CheckRepoStarredClient starredClient = new CheckRepoStarredClient(this, repoInfo.owner, repoInfo.repo);
		starredClient.setOnResultCallback(new StarredResult());
		starredClient.execute();

		CheckRepoWatchedClient watcheClien = new CheckRepoWatchedClient(this, repoInfo.owner, repoInfo.repo);
		watcheClien.setOnResultCallback(new WatchedResult());
		watcheClien.execute();

		smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);

	}

	private void setUpShare(RepoInfo info) {
		shareUri = Uri.parse(ApiConstants.WEB_URL);
		shareUri = shareUri.buildUpon().appendPath(info.owner).appendPath(info.repo).build();

		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.repo_detail_activity, menu);

		if(menu != null) {
			MenuItem item = menu.findItem(R.id.share_repo);
			if (item != null) {
				IconDrawable iconDrawable = new IconDrawable(this, Iconify.IconValue.fa_share_alt);
				iconDrawable.color(Color.WHITE);
				iconDrawable.actionBarSize();
				item.setIcon(iconDrawable);
			}
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (menu != null) {
			MenuItem item = menu.findItem(R.id.share_repo);
			if (item != null) {
				item.setVisible(shareUri != null);
			}

			final View actionView = menu.findItem(R.id.action_menu).getActionView().findViewById(R.id.menuPlaceHolder);

			menu.findItem(R.id.action_menu).getActionView().findViewById(R.id.menuItem)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							PopupMenu menu = new MainMenu(RepoDetailActivity.this, actionView, R.menu.repo_detail_content);
							if (currentRepo != null && currentRepo.parent != null) {
								menu = new MainMenu(RepoDetailActivity.this, actionView, R.menu.repo_detail_content_parent);
							}
							menu.show();
						}
					});
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.share_repo) {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setData(shareUri);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, repoInfo.owner + "/" + repoInfo.repo);
			intent.putExtra(Intent.EXTRA_TEXT, description + "\n\n" + shareUri);

			startActivity(Intent.createChooser(intent, "Share repository!"));
		}

		if (item.getItemId() == R.id.action_star) {
			if (repoStarred) {
				UnstarRepoClient unstarRepoClient = new UnstarRepoClient(this, repoInfo.owner, repoInfo.repo);
				unstarRepoClient.setOnResultCallback(new UnstarActionResult());
				unstarRepoClient.execute();
			} else {
				StarRepoClient starRepoClient = new StarRepoClient(this, repoInfo.owner, repoInfo.repo);
				starRepoClient.setOnResultCallback(new StarActionResult());
				starRepoClient.execute();
			}
			showRefresh();
		} else if (item.getItemId() == R.id.action_watch) {
			if (repoWatched) {
				UnwatchRepoClient unwatchRepoClient = new UnwatchRepoClient(this, repoInfo.owner, repoInfo.repo);
				unwatchRepoClient.setOnResultCallback(new UnwatchActionResult());
				unwatchRepoClient.execute();
			} else {
				WatchRepoClient watchRepoClient = new WatchRepoClient(this, repoInfo.owner, repoInfo.repo);
				watchRepoClient.setOnResultCallback(new WatchActionResult());
				watchRepoClient.execute();
			}
			showRefresh();
		} else if (item.getItemId() == R.id.action_show_parent) {
			if (currentRepo != null && currentRepo.parent != null) {
				String parentFullName = currentRepo.parent.full_name;
				String[] split = parentFullName.split("/");
				String owner = split[0];
				String name = split[1];

				Intent launcherActivity = RepoDetailActivity.createLauncherActivity(this, owner, name, currentRepo.parent.description);
				startActivity(launcherActivity);
			}
		}

		return false;
	}

	@Override
	public void showRefresh() {
		smoothBar.progressiveStart();
	}

	@Override
	public void cancelRefresh() {
		smoothBar.progressiveStop();
	}

	@Override
	public void onResponseOk(Repo repo, Response r) {
		if (repo != null) {
			this.currentRepo = repo;
			this.invalidateOptionsMenu();

			cancelRefresh();

			if (issuesFragment != null) {
				issuesFragment.setPermissions(repo.permissions);
			}

			String[] navItems = getResources().getStringArray(R.array.repoDetailNavigation);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.repo_detail_navigation, android.R.id.text1, navItems);
			adapter.setDropDownViewResource(R.layout.repo_detail_navigation_dropdown);
			spinnerNavigation.setAdapter(adapter);

			spinnerNavigation.setVisibility(View.VISIBLE);

			branchesAdapter = new ArrayAdapter<Branch>(this, R.layout.repo_detail_navigation, android.R.id.text1, new ArrayList<Branch>());
			branchesAdapter.setDropDownViewResource(R.layout.repo_detail_navigation_dropdown);
			repoDetailBranches.setAdapter(branchesAdapter);
			Branch branch = new Branch();
			branch.name = currentRepo.default_branch;

			branchesAdapter.add(branch);

			GetRepoBranchesClient client = new GetRepoBranchesClient(this, repoInfo.owner, repoInfo.repo);
			client.setOnResultCallback(new BaseClient.OnResultCallback<ListBranches>() {
				@Override
				public void onResponseOk(ListBranches branches, Response r) {
					ArrayList<Branch> newBranches = new ArrayList<Branch>(branches);
					for (Branch branch : branches) {
						if (branch.name.equals(currentRepo.default_branch)) {
							newBranches.remove(branch);
						}
					}
					branchesAdapter.addAll(newBranches);
					repoDetailBranches.setVisibility(View.VISIBLE);
				}

				@Override
				public void onFail(RetrofitError error) {

				}
			});
			client.execute();
		}


		cancelRefresh();
	}

	@Override
	public void onFail(RetrofitError error) {
		ErrorHandler.onRetrofitError(this, "RepoDetailFragment", error);
		finish();
		cancelRefresh();
	}

	private void setUpFragment(Fragment fragment) {
		if (fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			if (fragment.isAdded()) {
				ft.remove(fragment);
			}
			ft.replace(R.id.content, fragment);
			ft.commit();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.repoDetailNavigation) {
			switchNavigation(position);
		} else if (parent.getId() == R.id.repoDetailBranches && branchesAdapter.getCount() > 0) {
			switchBranch(position);
		}
	}

	private void switchBranch(int position) {
		currentBranch = branchesAdapter.getItem(position);

		if (markDownFragment != null) {
			markDownFragment.setCurrentBranch(currentBranch);
		}
		if (filesTreeFragment != null) {
			filesTreeFragment.setCurrentBranch(currentBranch);
		}
	}

	private void switchNavigation(int position) {
		switch (position) {
			case 0:
				if (markDownFragment == null) {
					markDownFragment = MarkdownFragment.newInstance(repoInfo.owner, repoInfo.repo, this);
				}
				setUpFragment(markDownFragment);
				break;
			case 1:
				if (filesTreeFragment == null) {
					filesTreeFragment = FilesTreeFragment.newInstance(repoInfo.owner, repoInfo.repo, currentBranch.name, this);
				}
				setUpFragment(filesTreeFragment);
				break;
			case 2:
				if (issuesFragment == null) {
					issuesFragment = IssuesFragment.newInstance(repoInfo.owner, repoInfo.repo, this);
				}
				issuesFragment.setPermissions(currentRepo.permissions);
				setUpFragment(issuesFragment);
				break;
			/*case 3:
				if (commitsFragment == null) {
					commitsFragment = ListCommitsFragments.newInstance(owner, repo, refreshListener);
				}
				return commitsFragment;
				break;*/
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/**
	 * Results for STAR
	 */
	private class StarredResult implements BaseClient.OnResultCallback<Object> {

		@Override
		public void onResponseOk(Object o, Response r) {
			if (r != null && r.getStatus() == 204) {
				repoStarred = true;
				invalidateOptionsMenu();
			}
			cancelRefresh();
		}

		@Override
		public void onFail(RetrofitError error) {
			if (error != null) {
				if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
					repoStarred = false;
					invalidateOptionsMenu();
				}
			}
			cancelRefresh();
		}
	}

	private class UnstarActionResult implements BaseClient.OnResultCallback<Object> {

		@Override
		public void onResponseOk(Object o, Response r) {
			if (r != null && r.getStatus() == 204) {
				repoStarred = false;
				Toast.makeText(RepoDetailActivity.this, "Repo unstarred", Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
			}
			cancelRefresh();
		}

		@Override
		public void onFail(RetrofitError error) {
			cancelRefresh();
		}
	}

	private class StarActionResult implements BaseClient.OnResultCallback<Object> {

		@Override
		public void onResponseOk(Object o, Response r) {
			if (r != null && r.getStatus() == 204) {
				repoStarred = true;
				Toast.makeText(RepoDetailActivity.this, "Repo starred", Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
			}
			cancelRefresh();
		}

		@Override
		public void onFail(RetrofitError error) {
			if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
			}
			cancelRefresh();
		}
	}

	/**
	 * RESULTS FOR WATCH
	 */

	private class WatchedResult implements BaseClient.OnResultCallback<Object> {

		@Override
		public void onResponseOk(Object o, Response r) {
			if (r != null && r.getStatus() == 204) {
				repoWatched = true;
				invalidateOptionsMenu();
			}
			cancelRefresh();
		}

		@Override
		public void onFail(RetrofitError error) {
			if (error != null) {
				if (error.getResponse() != null && error.getResponse().getStatus() == 404) {
					repoWatched = false;
					invalidateOptionsMenu();
				}
			}
			cancelRefresh();
		}
	}

	private class UnwatchActionResult implements BaseClient.OnResultCallback<Object> {

		@Override
		public void onResponseOk(Object o, Response r) {
			if (r != null && r.getStatus() == 204) {
				repoWatched = false;
				Toast.makeText(RepoDetailActivity.this, "Not watching repo", Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
			}
			cancelRefresh();
		}

		@Override
		public void onFail(RetrofitError error) {
			cancelRefresh();
		}
	}

	private class WatchActionResult implements BaseClient.OnResultCallback<Object> {

		@Override
		public void onResponseOk(Object o, Response r) {
			if (r != null && r.getStatus() == 204) {
				repoWatched = true;
				Toast.makeText(RepoDetailActivity.this, "Watching repo", Toast.LENGTH_SHORT).show();
				invalidateOptionsMenu();
			}
			cancelRefresh();
		}

		@Override
		public void onFail(RetrofitError error) {
			cancelRefresh();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MainMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener {

		public MainMenu(Context context, View actionView, int id) {
			super(context, actionView);
			inflate(id);
			MenuItem starItem = getMenu().findItem(R.id.action_star);

			if (starItem != null) {
				if (repoStarred) {
					starItem.setTitle(R.string.menu_unstar);
				} else {
					starItem.setTitle(R.string.menu_star);
				}
			}

			MenuItem watchItem = getMenu().findItem(R.id.action_watch);

			if (watchItem != null) {
				if (repoWatched) {
					watchItem.setTitle(R.string.menu_unwatch);
				} else {
					watchItem.setTitle(R.string.menu_watch);
				}
			}

			setOnMenuItemClickListener(this);
		}

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			return onOptionsItemSelected(item);
		}
	}
}
