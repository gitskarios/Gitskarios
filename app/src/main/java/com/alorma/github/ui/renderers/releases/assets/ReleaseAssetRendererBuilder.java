package com.alorma.github.ui.renderers.releases.assets;

import com.alorma.github.sdk.bean.dto.response.ReleaseAsset;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseAssetRendererBuilder extends RendererBuilder<ReleaseAsset> {

  public ReleaseAssetRendererBuilder(ReleaseAssetsRenderer.OnReleaseAssetClicked onReleaseAssetClicked) {
    setPrototypes(generatePrototypes(onReleaseAssetClicked));
  }

  private Collection<Renderer<ReleaseAsset>> generatePrototypes(ReleaseAssetsRenderer.OnReleaseAssetClicked onReleaseAssetClicked) {
    List<Renderer<ReleaseAsset>> list = new ArrayList<>();

    ReleaseAssetsRenderer renderer = new ReleaseAssetsRenderer();
    renderer.setOnReleaseAssetClicked(onReleaseAssetClicked);

    list.add(renderer);

    return list;
  }

  @Override
  protected Class getPrototypeClass(ReleaseAsset releaseAsset) {
    return ReleaseAssetsRenderer.class;
  }
}
