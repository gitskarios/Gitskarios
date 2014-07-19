package com.alorma.github.ui.fragment.base;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alorma.github.BuildConfig;
import com.alorma.github.R;

/**
 * Created by Bernat on 19/07/2014.
 */
public abstract class FabListFragment extends ListFragment implements View.OnClickListener {

    private boolean useFab = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.list_fragment, null, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button fab = (Button) view.findViewById(R.id.fabbutton);

        if (BuildConfig.PRO_VERSION && useFab) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                Outline mOutlineCircle;
                int shapeSize = getResources().getDimensionPixelSize(R.dimen.shape_size);
                mOutlineCircle = new Outline();
                mOutlineCircle.setRoundRect(0, 0, shapeSize, shapeSize, shapeSize / 2);

                fab.setOutline(mOutlineCircle);
                fab.setClipToOutline(true);
            }*/
            fab.setOnClickListener(this);
        } else {
            fab.setVisibility(View.GONE);
            fab.setOnClickListener(null);
        }
    }

    public void setUseFab(boolean useFab) {
        this.useFab = useFab;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fabbutton) {
            onFabClick();
        }
    }

    protected abstract void onFabClick();
}
