package com.alorma.github.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;

/**
 * Created by bernat.borras on 13/12/15.
 */
public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new DummyAdapter());
    }

    private class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.DummyAdapterHolder> {
        @Override
        public DummyAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DummyAdapterHolder(new TextView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(DummyAdapterHolder holder, int position) {
            holder.textView.setText("Item " + position);
        }

        @Override
        public int getItemCount() {
            return 1000;
        }

        public class DummyAdapterHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public DummyAdapterHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
}
