package com.alorma.github.ui.renderers.user;

import com.alorma.github.sdk.bean.dto.response.User;
import com.pedrogomez.renderers.RendererBuilder;

/**
 * Created by a557114 on 30/07/2015.
 */
public class UsersRendererBuilder extends RendererBuilder<User> {
    @Override
    protected Class getPrototypeClass(User user) {
        return UserRender.class;
    }
}
