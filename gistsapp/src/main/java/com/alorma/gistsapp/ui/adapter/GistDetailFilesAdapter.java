package com.alorma.gistsapp.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.gistsapp.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.githubicons.GithubIconDrawable;
import com.alorma.githubicons.GithubIconify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bernat on 02/04/2015.
 */
public class GistDetailFilesAdapter extends RecyclerView.Adapter<GistDetailFilesAdapter.ViewHolder> {

    private final GithubIconDrawable noPreviewDrawable;
    private final int colorAccent;
    private Map<String, GistFile> files;
    private List<GistFile> gistFileList;

    public GistDetailFilesAdapter(Context context) {
        files = new HashMap<>();
        noPreviewDrawable = new GithubIconDrawable(context, GithubIconify.IconValue.octicon_package);
        noPreviewDrawable.sizeDp(100);
        noPreviewDrawable.colorRes(R.color.secondary_text);

        colorAccent = context.getResources().getColor(R.color.accent);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GistFile gistFile = gistFileList.get(position);

        holder.textFileName.setText(gistFile.filename);

        if (gistFile.type.contains("image")) {
            if (holder.imageContent != null) {
                holder.imageContent.setImageDrawable(noPreviewDrawable);
            }
        } else {
            if (holder.textContent != null) {
                printContent(holder.textContent, gistFile.content);
            }
        }
    }

    private void printContent(TextView textContent, String content) {
        try {
            content = splitLines(content);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        textContent.setText(content);
    }

    private String splitLines(String content) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String line = reader.readLine();
        while (line != null) {
            result.add(line);
            line = reader.readLine();
        }
        if (result.size() > 10) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                if (result.get(i).isEmpty()) {
                    builder.append("\n");
                } else {
                    builder.append(result.get(i));
                    builder.append("\n");
                }
            }
            content = builder.toString();
        }
        return content;
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public int getItemViewType(int position) {
        GistFile gistFile = gistFileList.get(position);
        if (gistFile.type.contains("image")) {
            return R.layout.row_gist_detail_binari;
        } else {
            return R.layout.row_gist_detail_text;
        }
    }

    public void add(String key, GistFile item) {
        files.put(key, item);
        gistFileList = new ArrayList<>(files.values());
        notifyItemInserted(files.size());
    }

    public void addAll(Map<String, GistFile> items) {
        files.putAll(items);
        gistFileList = new ArrayList<>(files.values());
        notifyItemInserted(files.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements Toolbar.OnMenuItemClickListener {
        public final TextView textFileName;
        public final TextView textContent;
        public final ImageView imageContent;
        private final Toolbar toolbar;

        public ViewHolder(View itemView) {
            super(itemView);
            textFileName = (TextView) itemView.findViewById(R.id.textFileName);
            textContent = (TextView) itemView.findViewById(R.id.textContent);
            imageContent = (ImageView) itemView.findViewById(R.id.imageContent);

            toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);

            if (toolbar != null) {
                toolbar.inflateMenu(R.menu.row_gist_file);
                toolbar.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.action_toolbar_share) {
                GistFile gistFile = gistFileList.get(getAdapterPosition());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, gistFile.filename);
                intent.putExtra(Intent.EXTRA_SUBJECT, gistFile.filename);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(gistFile.rawUrl));
                intent.putExtra(Intent.EXTRA_TEXT, gistFile.content);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
                textFileName.getContext().startActivity(Intent.createChooser(intent, "Send to"));
            }
            return false;
        }
    }
}
