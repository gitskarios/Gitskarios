package com.alorma.github.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.emoji.Emoji;
import com.alorma.github.emoji.EmojisAdapter;
import com.alorma.github.emoji.EmojisFragment;

import java.util.List;

/**
 * Created by Bernat on 08/07/2015.
 */
public class ShowEmojisFragment extends EmojisFragment {

    private EmojisAdapter emojisAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.test_emojis_fragment, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        emojisAdapter = new EmojisAdapter();
        recyclerView.setAdapter(emojisAdapter);
    }

    @Override
    public void onEmojisLoaded(List<Emoji> emojis) {
        emojisAdapter.addAll(emojis);
    }

    @Override
    public void onEmojisLoadFail() {

    }
}
