package com.alorma.github.emoji;

import com.alorma.github.ui.fragment.base.BaseFragment;

/**
 * Created by Bernat on 08/07/2015.
 */
public abstract class EmojisFragment extends BaseFragment implements EmojisProvider.EmojisCallback {

    @Override
    public void onStart() {
        super.onStart();

        EmojisProvider emojisProvider = new EmojisProvider(getContext());
        emojisProvider.getEmojis(this);
    }

    public void filter(String filter) {
        EmojisProvider emojisProvider = new EmojisProvider(getContext());
        emojisProvider.getEmojis(this, filter);
    }
}
