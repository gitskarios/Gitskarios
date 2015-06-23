package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.diff.lib.DiffTextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.utils.TextUtils;

import java.io.IOException;
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
        return new FileVH(inflater.inflate(R.layout.commit_file_row, parent, false));
    }

    @Override
    public void onBindViewHolder(FileVH holder, int position) {
        CommitFile commitFile = files.get(position);
        String fileName = commitFile.getFileName();

        if (fileName != null) {
            holder.fileName.setText(fileName);
        }

        if (commitFile.patch != null) {
            printContent(holder.textContent, commitFile.patch);
        }

        int additions = commitFile.additions;
        int deletions = commitFile.deletions;

        holder.toolbar.setTitle("");

        String textCommitsStr = null;
        if (additions > 0 && deletions > 0) {
            textCommitsStr = context.getString(R.string.commit_file_add_del, additions, deletions);
            holder.toolbar.setVisibility(View.VISIBLE);
        } else if (additions > 0) {
            textCommitsStr = context.getString(R.string.commit_file_add, additions);
            holder.toolbar.setVisibility(View.VISIBLE);
        } else if (deletions > 0) {
            textCommitsStr = context.getString(R.string.commit_file_del, deletions);
            holder.toolbar.setVisibility(View.VISIBLE);
        } else {
            holder.toolbar.setVisibility(View.GONE);
        }

        if (textCommitsStr != null) {
            holder.toolbar.setTitle(Html.fromHtml(textCommitsStr));
        }
    }

    private void printContent(TextView textContent, String content) {
        try {
            content = TextUtils.splitLines(content, 6);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        textContent.setText(content);
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
        public DiffTextView textContent;
        private Toolbar toolbar;

        public FileVH(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.textFileName);
            textContent = (DiffTextView) itemView.findViewById(R.id.textContent);
            toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);
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
