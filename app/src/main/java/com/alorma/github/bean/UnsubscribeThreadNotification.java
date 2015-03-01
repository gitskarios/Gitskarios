package com.alorma.github.bean;

import com.alorma.github.sdk.bean.dto.response.Notification;

/**
 * Created by Bernat on 01/03/2015.
 */
public class UnsubscribeThreadNotification {
	private Notification notification;

	public UnsubscribeThreadNotification(Notification notification) {
		this.notification = notification;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}
