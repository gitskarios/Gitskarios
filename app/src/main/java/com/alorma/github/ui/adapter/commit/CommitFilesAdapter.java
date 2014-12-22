package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.diff.lib.DiffTextView;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.dto.response.GitCommitFiles;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitFilesAdapter extends RecyclerView.Adapter<CommitFilesAdapter.FileVH> {

	private final Context context;
	private final LayoutInflater inflater;
	private final GitCommitFiles files;

	public CommitFilesAdapter(Context context, GitCommitFiles files) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.files = files;
	}

	@Override
	public FileVH onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FileVH(new DiffTextView(context));
	}

	@Override
	public void onBindViewHolder(FileVH holder, int position) {
		CommitFile commitFile = files.get(position);
		holder.diffTextView.setText(commitFile.patch);
	}

	@Override
	public int getItemCount() {
		return files != null ? files.size() : 0;
	}
	
	public class FileVH extends RecyclerView.ViewHolder {

		public DiffTextView diffTextView;

		public FileVH(View itemView) {
			super(itemView);
			diffTextView = (DiffTextView) itemView;
		}
	}
}
