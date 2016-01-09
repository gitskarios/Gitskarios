package com.alorma.github.bean;

import com.alorma.github.sdk.bean.dto.response.Notification;
import com.alorma.github.sdk.bean.dto.response.Repo;

import java.util.List;

/**
 * Created by a557114 on 06/09/2015.
 */
public class NotificationsParent {
    public Repo repo;
    public List<Notification> notifications;
}
