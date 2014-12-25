package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.diff.lib.DiffTextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.dto.response.GitCommitFiles;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitFilesAdapter extends RecyclerView.Adapter<CommitFilesAdapter.FileVH> {

	private final Context context;
	private final LayoutInflater inflater;
	private final GitCommitFiles files;
	private final ImageLoader uil;

	public CommitFilesAdapter(Context context, GitCommitFiles files) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.files = files;
		this.uil = ImageLoader.getInstance();
	}

	@Override
	public FileVH onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FileVH(inflater.inflate(R.layout.single_commit_file_row, parent, false));
	}

	@Override
	public void onBindViewHolder(FileVH holder, int position) {
		CommitFile commitFile = files.get(position);
		holder.fileName.setText(commitFile.filename);
		if (commitFile.patch != null) {
			holder.diffTextView.setMaxLines(8);
			holder.diffTextView.setText(commitFile.patch);
		}
	}

	@Override
	public int getItemCount() {
		return files != null ? files.size() : 0;
	}

	public class FileVH extends RecyclerView.ViewHolder implements View.OnClickListener {

		public DiffTextView diffTextView;
		public TextView fileName;

		public FileVH(View itemView) {
			super(itemView);
			diffTextView = (DiffTextView) itemView.findViewById(R.id.diffText);
			fileName = (TextView) itemView.findViewById(R.id.fileName);
			diffTextView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {

		}
	}
}
