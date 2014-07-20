package com.alorma.github.ui.popup;

import android.content.Context;
import android.widget.ListPopupWindow;

import com.alorma.github.sdk.bean.dto.response.ListContributors;
import com.alorma.github.ui.adapter.ContributorsAdapter;

/**
 * Created by Bernat on 20/07/2014.
 */
public class PopUpContributors extends ListPopupWindow {

    private ListContributors contributors;

    public PopUpContributors(Context context, ListContributors contributors) {
        super(context);
        this.contributors = contributors;

        setAdapter(new ContributorsAdapter(context, contributors));
    }
}
