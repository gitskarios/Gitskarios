package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.dto.response.GitChangeStatus;
import com.alorma.github.sdk.bean.dto.response.GitCommitFiles;
import com.alorma.github.ui.view.GitChangeStatusView;

import java.util.List;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitFilesAdapter extends RecyclerView.Adapter<CommitFilesAdapter.FileVH> {

	private final Context context;
	private final LayoutInflater inflater;
	private final List<CommitFile> files;
	private OnFileRequestListener onFileRequestListener;

	public CommitFilesAdapter(Context context, List<CommitFile> files) {
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

        holder.gitChangeStatusView.setNumbers(commitFile);
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
        private GitChangeStatusView gitChangeStatusView;

        public FileVH(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.fileName);
            gitChangeStatusView = (GitChangeStatusView) itemView.findViewById(R.id.commitNumbers);
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
    }
}
