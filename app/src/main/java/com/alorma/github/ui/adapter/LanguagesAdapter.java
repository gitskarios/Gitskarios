package com.alorma.github.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.alorma.github.R;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;

/**
 * Created by Bernat on 24/09/2015.
 */
public class LanguagesAdapter extends RecyclerArrayAdapter<String, LanguagesAdapter.Holder> {

    private LanguageSelectedListener languageSelectedListener;

    public LanguagesAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    protected void onBindViewHolder(Holder holder, String s) {
        holder.text.setText(s);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = getInflater().inflate(R.layout.simple_selectable_list_item, parent, false);
        return new Holder(v);
    }

    public void setLanguageSelectedListener(LanguageSelectedListener languageSelectedListener) {
        this.languageSelectedListener = languageSelectedListener;
    }

    public interface LanguageSelectedListener {
        void onLanguageSelected(String language);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final CompoundButton text;

        public Holder(View itemView) {
            super(itemView);
            text = (CompoundButton) itemView.findViewById(android.R.id.text1);

            text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (languageSelectedListener != null) {
                        languageSelectedListener.onLanguageSelected(getItem(getAdapterPosition()));
                    }
                }
            });
        }
    }
}
