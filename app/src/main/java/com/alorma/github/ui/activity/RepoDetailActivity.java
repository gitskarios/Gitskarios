package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.security.ApiConstants;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.detail.repo.RepoDetailFragment;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Created by Bernat on 17/07/2014.
 */
public class RepoDetailActivity extends BackActivity {

    private Uri shareUri;
    private String description;
    private String owner;
    private String repo;

    public static Intent createLauncherActivity(Context context, String owner, String repo, String description) {
        Bundle bundle = new Bundle();
        bundle.putString(RepoDetailFragment.OWNER, owner);
        bundle.putString(RepoDetailFragment.REPO, repo);
        bundle.putString(RepoDetailFragment.DESCRIPTION, description);
        bundle.putBoolean(RepoDetailFragment.FROM_INTENT_FILTER, false);

        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent createIntentFilterLauncherActivity(Context context, String owner, String repo, String description) {
        Bundle bundle = new Bundle();
        bundle.putString(RepoDetailFragment.OWNER, owner);
        bundle.putString(RepoDetailFragment.REPO, repo);
        bundle.putString(RepoDetailFragment.DESCRIPTION, description);
        bundle.putBoolean(RepoDetailFragment.FROM_INTENT_FILTER, true);

        Intent intent = new Intent(context, RepoDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            owner = getIntent().getExtras().getString(RepoDetailFragment.OWNER);
            repo = getIntent().getExtras().getString(RepoDetailFragment.REPO);

            setUpShare(owner, repo);

            description = getIntent().getExtras().getString(RepoDetailFragment.DESCRIPTION);
            boolean from = getIntent().getExtras().getBoolean(RepoDetailFragment.FROM_INTENT_FILTER);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(android.R.id.content, RepoDetailFragment.newInstance(owner, repo, description, from));
            ft.commit();
        } else {
            finish();
        }
    }

    private void setUpShare(String owner, String repo) {
        shareUri = Uri.parse(ApiConstants.WEB_URL);
        shareUri = shareUri.buildUpon().appendPath(owner).appendPath(repo).build();

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.repo_detail_activity, menu);

        if (menu != null) {
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
            intent.putExtra(Intent.EXTRA_SUBJECT, owner + "/" + repo);
            intent.putExtra(Intent.EXTRA_TEXT, description + "\n\n" + shareUri);

            startActivity(Intent.createChooser(intent, "Share repository!"));
        }

        return false;
    }
}
