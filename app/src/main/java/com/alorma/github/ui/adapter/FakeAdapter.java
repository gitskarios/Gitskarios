package com.alorma.github.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FakeAdapter extends RecyclerView.Adapter<FakeAdapter.VH> {

	private final LayoutInflater inflater;

	public FakeAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		return new VH(inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
	}

	@Override
	public void onBindViewHolder(VH holder, int position) {
		holder.text.setText("Pos: " + position);
	}

	@Override
	public int getItemCount() {
		return 100;
	}

	public class VH extends RecyclerView.ViewHolder {

		public TextView text;

		public VH(View itemView) {
			super(itemView);
			text = (TextView) itemView.findViewById(android.R.id.text1);
		}
	}
}