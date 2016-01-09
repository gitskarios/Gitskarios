package com.alorma.github.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alorma.github.R;
import com.alorma.github.emoji.Emoji;
import com.alorma.github.emoji.EmojisAdapter;
import com.alorma.github.emoji.EmojisFragment;

import java.util.List;

/**
 * Created by Bernat on 08/07/2015.
 */
public class ShowEmojisFragment extends EmojisFragment implements EmojisAdapter.OnEmojiSelectedListener {

    private EmojisAdapter emojisAdapter;
    private EditText filterEdit;
    private EmojisAdapter.OnEmojiSelectedListener onEmojiSelectedListener;

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
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        emojisAdapter = new EmojisAdapter();
        emojisAdapter.setOnEmojiSelectedListener(this);
        recyclerView.setAdapter(emojisAdapter);

        filterEdit = (EditText) view.findViewById(R.id.filter);
        filterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(filterEdit.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onEmojisLoaded(List<Emoji> emojis) {
        emojisAdapter.clear();
        emojisAdapter.addAll(emojis);
    }

    @Override
    public void onEmojisLoadFail() {

    }

    @Override
    public void onEmojiSelected(Emoji emoji) {
        if (onEmojiSelectedListener != null) {
            onEmojiSelectedListener.onEmojiSelected(emoji);
        }
    }

    public void setOnEmojiSelectedListener(EmojisAdapter.OnEmojiSelectedListener onEmojiSelectedListener) {
        this.onEmojiSelectedListener = onEmojiSelectedListener;
    }
}
