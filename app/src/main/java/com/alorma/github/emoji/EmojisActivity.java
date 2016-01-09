package com.alorma.github.emoji;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.ShowEmojisFragment;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojisActivity extends BackActivity implements EmojisAdapter.OnEmojiSelectedListener {

    public static final String EMOJI = "EMOJI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ShowEmojisFragment showEmojisFragment = new ShowEmojisFragment();

        showEmojisFragment.setOnEmojiSelectedListener(this);
        ft.replace(R.id.content, showEmojisFragment);
        ft.commit();

        setTitle(R.string.emoji_activity);
    }

    @Override
    public void onEmojiSelected(Emoji emoji) {
        Intent intent = new Intent();
        intent.putExtra(EMOJI, emoji);
        setResult(RESULT_OK, intent);
        finish();
    }
}
