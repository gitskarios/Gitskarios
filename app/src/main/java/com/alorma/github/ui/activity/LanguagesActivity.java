package com.alorma.github.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.CompoundButton;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.adapter.LanguagesAdapter;

import java.util.Arrays;
import java.util.List;

public class LanguagesActivity extends BackActivity implements LanguagesAdapter.LanguageSelectedListener {

    public static final String EXTRA_LANGUAGE = "EXTRA_LANGUAGE";
    private LanguagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.languages_activity);

        if (getToolbar() != null) {
            ViewCompat.setElevation(getToolbar(), getResources().getDimension(R.dimen.gapSmall));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        adapter = new LanguagesAdapter(LayoutInflater.from(this));
        adapter.setLanguageSelectedListener(this);
        recyclerView.setAdapter(adapter);

        CompoundButton switchAllLanguages = (CompoundButton) findViewById(R.id.allLanguagesSwitch);

        loadList(false);

        switchAllLanguages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                loadList(isChecked);
            }
        });
    }

    private void loadList(boolean showAllLanguages) {
        int languagesRes = showAllLanguages ? R.array.default_languages : R.array.available_languages;
        String[] languages = getResources().getStringArray(languagesRes);
        updateAdapter(Arrays.asList(languages));
    }

    private void updateAdapter(List<String> strings) {
        adapter.clear();
        adapter.addAll(strings);
    }

    @Override
    public void onLanguageSelected(String language) {
        Intent data = new Intent();
        data.putExtra(EXTRA_LANGUAGE, language);
        setResult(RESULT_OK, data);
        finish();
    }
}
