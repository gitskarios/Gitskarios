package com.alorma.gistsapp.ui.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alorma.gistsapp.BuildConfig;
import com.alorma.gistsapp.R;
import com.alorma.gistsapp.ui.activity.CreateGistActivity;
import com.alorma.gistsapp.ui.adapter.GistDetailFilesAdapter;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.gists.GetGistDetailClient;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailFragment extends Fragment implements BaseClient.OnResultCallback<Gist> {

    public static final String GIST_ID = "GIST_ID";
    private RecyclerView recyclerView;
    private GistDetailFilesAdapter adapter;
    private GistDetailListener gistDetailListener;
    private Gist gist;

    public static GistDetailFragment newInstance(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(GIST_ID, id);

        GistDetailFragment f = new GistDetailFragment();
        f.setArguments(bundle);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gist_detail_layout, null, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.gist_files_count), StaggeredGridLayoutManager.VERTICAL));

        adapter = new GistDetailFilesAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            String id = getArguments().getString(GIST_ID);

            GetGistDetailClient detailClient = new GetGistDetailClient(getActivity(), id);
            detailClient.setOnResultCallback(this);
            detailClient.execute();
        }
    }

    @Override
    public void onResponseOk(Gist gist, Response r) {
        this.gist = gist;
        if (gistDetailListener != null) {
            gistDetailListener.onGistLoaded(gist);
        }
        adapter.addAll(gist.files);
    }

    public int getMenuId() {
        return R.menu.menu_gist;
    }

    @Override
    public void onStart() {
        super.onStart();
        enableCreateGist(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_gist_share) {

            Uri uri = Uri.parse(gist.htmlUrl);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND);

            startActivity(Intent.createChooser(intent, getString(R.string.send_gist_to)));

        }
        return true;
    }

    @Override
    public void onStop() {
        enableCreateGist(true);
        super.onStop();
    }

    private void enableCreateGist(boolean b) {
        int flag = b ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        ComponentName componentName = new ComponentName(getActivity(), CreateGistActivity.class);
        getActivity().getPackageManager().setComponentEnabledSetting(componentName, flag, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    public void setGistDetailListener(GistDetailListener gistDetailListener) {
        this.gistDetailListener = gistDetailListener;
    }

    public interface GistDetailListener {
        void onGistLoaded(Gist gist);
    }
}
