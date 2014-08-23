package com.alorma.github.ui.fragment;

/**
 * Created by Bernat on 23/08/2014.
 */
public interface ActionRepoListener {
    boolean hasPermissionPull();
    boolean hasPermissionPush();
    boolean hasPermissionAdmin();
}
