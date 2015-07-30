package com.alorma.github.ui.fragment.releases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.alorma.github.ui.adapter.ReleaseAssetsAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseAssetsFragment extends BaseFragment {

    private static final String RELEASE_ASSETS = "RELEASE_ASSETS";

    public static ReleaseAssetsFragment newInstance(List<ReleaseAsset> releaseAssets) {
        ReleaseAssetsFragment releaseAssetsFragment = new ReleaseAssetsFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(RELEASE_ASSETS, new ArrayList<>(releaseAssets));

        releaseAssetsFragment.setArguments(args);

        return releaseAssetsFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.release_assets_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ReleaseAssetsAdapter releaseAssetsAdapter = new ReleaseAssetsAdapter(LayoutInflater.from(getActivity()));

        ArrayList<ReleaseAsset> assets = getArguments().getParcelableArrayList(RELEASE_ASSETS);

        releaseAssetsAdapter.addAll(assets);

        recyclerView.setAdapter(releaseAssetsAdapter);
    }
}
