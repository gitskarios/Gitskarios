package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.diff.lib.DiffTextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.dto.response.GitCommitFiles;
import com.alorma.github.ui.activity.FileActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitFilesAdapter extends RecyclerView.Adapter<CommitFilesAdapter.FileVH> {

	private final Context context;
	private final LayoutInflater inflater;
	private final GitCommitFiles files;
	private OnFileRequestListener onFileRequestListener;
	private boolean firstTimeUsed = false;

	public CommitFilesAdapter(Context context, GitCommitFiles files) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.files = files;
	}

	@Override
	public FileVH onCreateViewHolder(ViewGroup parent, int viewType) {
		return new FileVH(inflater.inflate(R.layout.single_commit_file_row, parent, false));
	}

	@Override
	public void onBindViewHolder(FileVH holder, int position) {
		CommitFile commitFile = files.get(position);
		String fileName = commitFile.getFileName();
		if (fileName != null) {
			holder.fileName.setText(fileName);
		}

		String additions = this.context.getResources().getString(R.string.commit_additions, commitFile.additions);
		String deletions = this.context.getResources().getString(R.string.commit_deletions, commitFile.deletions);

		holder.txtAdditions.setText(additions);
		holder.txtDeletions.setText(deletions);
		
		holder.fileStatus.setText(commitFile.status);
		
		if (onFileRequestListener != null && onFileRequestListener.openFirstFile() && position == 0 && !firstTimeUsed) {
			firstTimeUsed = true;
			onFileRequestListener.onFileRequest(commitFile);
		}
	}

	@Override
	public int getItemCount() {
		return files != null ? files.size() : 0;
	}

	public void setOnFileRequestListener(OnFileRequestListener onFileRequestListener) {
		this.onFileRequestListener = onFileRequestListener;
	}

	public class FileVH extends RecyclerView.ViewHolder implements View.OnClickListener {

		public TextView fileName;
		public TextView txtAdditions;
		public TextView txtDeletions;
		public TextView fileStatus;

		public FileVH(View itemView) {
			super(itemView);
			fileName = (TextView) itemView.findViewById(R.id.fileName);
			txtAdditions = (TextView) itemView.findViewById(R.id.additions);
			txtDeletions = (TextView) itemView.findViewById(R.id.deletions);
			fileStatus = (TextView) itemView.findViewById(R.id.fileStatus);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (onFileRequestListener != null) {
				onFileRequestListener.onFileRequest(files.get(getPosition()));
			}
		}
	}
	
	public interface OnFileRequestListener {
		void onFileRequest(CommitFile file);
		boolean openFirstFile();
	}
}
