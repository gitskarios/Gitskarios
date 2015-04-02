package com.alorma.gistsapp.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.gistsapp.R;
import com.alorma.gistsapp.ui.adapter.GistDetailFilesAdapter;
import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.gists.GetGistDetailClient;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailFragment extends Fragment implements BaseClient.OnResultCallback<Gist> {

    public static final String GIST_ID = "GIST_ID";
    private RecyclerView recyclerView;
    private GistDetailFilesAdapter adapter;

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

        adapter = new GistDetailFilesAdapter();
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
        adapter.addAll(gist.files);
    }

    @Override
    public void onFail(RetrofitError error) {

    }
}
