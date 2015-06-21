package com.alorma.github.bean;

import com.alorma.github.sdk.bean.dto.response.Notification;

/**
 * Created by Bernat on 01/03/2015.
 */
public class ClearNotification {

    private Notification notification;
    private boolean allRepository;

    public ClearNotification(Notification notification, boolean allRepository) {
        this.notification = notification;
        this.allRepository = allRepository;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isAllRepository() {
        return allRepository;
    }

    public void setAllRepository(boolean allRepository) {
        this.allRepository = allRepository;
    }
}
