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
		String fileName = getFileName(commitFile.filename);
		if (fileName != null) {
			holder.fileName.setText(fileName);
		}
		
		if (commitFile.patch != null) {
			holder.diffTextView.setMaxLines(8);
			holder.diffTextView.setText(commitFile.patch);
		}
	}

	private String getFileName(String filename) {
		if (filename != null) {
			String[] names = filename.split(File.separator);
			if (names.length > 1) {
				int last = names.length - 1;
				return names[last];
			} else {
				return names[0];
			}
		}

		return null;
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
			itemView.setOnClickListener(this);
			diffTextView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			Intent launcherIntent = FileActivity.createLauncherIntent(v.getContext(), files.get(getPosition()).patch);
			v.getContext().startActivity(launcherIntent);
		}
	}
}
