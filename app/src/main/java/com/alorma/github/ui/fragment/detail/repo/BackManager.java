package com.alorma.github.ui.fragment.detail.repo;

/**
 * Created by Bernat on 14/06/2015.
 */
public interface BackManager {

    /**
     *
     * @return true to for allow activity to handle onBack, false Otherwise
     */
    boolean onBackPressed();
}
