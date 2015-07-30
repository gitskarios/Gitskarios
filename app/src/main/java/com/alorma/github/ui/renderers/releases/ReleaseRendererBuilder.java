package com.alorma.github.ui.renderers.releases;

import com.alorma.github.sdk.bean.dto.response.Release;
import com.pedrogomez.renderers.RendererBuilder;

/**
 * Created by a557114 on 30/07/2015.
 */
public class ReleaseRendererBuilder extends RendererBuilder<Release> {
    @Override
    protected Class getPrototypeClass(Release release) {
        return ReleaseRenderer.class;
    }
}
