package com.alorma.github.ui.renderers.releases;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Release;
import com.pedrogomez.renderers.Renderer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseRenderer extends Renderer<Release> {
    @Override
    protected void setUpView(View view) {

    }

    @Override
    protected void hookListeners(View view) {

    }

    @Bind(R.id.releaseState) ImageView releaseState;
    @Bind(R.id.releaseName)  TextView releaseName;
    @Bind(R.id.filesRelease)  TextView filesRelease;

    @Override
    protected View inflate(LayoutInflater layoutInflater, ViewGroup viewGroup) {
        View view = layoutInflater.inflate(R.layout.row_repo_release, viewGroup, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render() {
        Release release = getContent();
        String name = release.name;
        if (TextUtils.isEmpty(name)) {
            name = release.tag_name;
        }
        releaseName.setText(name);

        if (release.prerelease) {
            releaseState.setImageResource(R.drawable.repo_release_prerelease);
        }

        if (release.assets != null) {
            int size = release.assets.size();
            if (!TextUtils.isEmpty(release.zipball_url)) {
                size = size + 1;
            }

            if (!TextUtils.isEmpty(release.tarball_url)) {
                size = size + 1;
            }

            filesRelease.setText(filesRelease.getContext().getResources().getQuantityString(R.plurals.repo_release_num_files, size, size));
        }
    }
}
