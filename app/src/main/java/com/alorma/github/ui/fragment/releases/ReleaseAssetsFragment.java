package com.alorma.github.ui.fragment.releases;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.alorma.github.ui.listeners.TitleProvider;
import com.alorma.github.ui.renderers.releases.assets.ReleaseAssetRendererBuilder;
import com.alorma.github.ui.renderers.releases.assets.ReleaseAssetsRenderer;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseAssetsFragment extends BaseFragment implements ReleaseAssetsRenderer.OnReleaseAssetClicked, TitleProvider {

    private static final String RELEASE_ASSETS = "RELEASE_ASSETS";

    public static ReleaseAssetsFragment newInstance(List<ReleaseAsset> releaseAssets) {
        ReleaseAssetsFragment releaseAssetsFragment = new ReleaseAssetsFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList(RELEASE_ASSETS, new ArrayList<Parcelable>(releaseAssets));

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

        List<ReleaseAsset> assets = getArguments().getParcelableArrayList(RELEASE_ASSETS);

        if (assets != null) {
            RVRendererAdapter<ReleaseAsset> adapter =
                    new RVRendererAdapter<>(LayoutInflater.from(getActivity()), new ReleaseAssetRendererBuilder(this),
                            new ListAdapteeCollection<>(assets));

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onReleaseAssetCLicked(ReleaseAsset asset) {
        downloadFile(getActivity(), asset);
    }

    private void downloadFile(Context context, ReleaseAsset asset) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(asset.browser_download_url));

        String fileName = asset.name;
        request.setTitle(fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "gitskarios/" + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.allowScanningByMediaScanner();
        dm.enqueue(request);

        Toast.makeText(context, "Download started in gitskarios/" + fileName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getTitle() {
        return R.string.repo_release_fragment_assets_title;
    }

    @Override
    public IIcon getTitleIcon() {
        return Octicons.Icon.oct_package;
    }
}
