package com.alorma.github.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.actions.ShareRawAction;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.TextUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import java.io.IOException;

public class GistDetailFilesAdapter extends RecyclerArrayAdapter<GistFile, GistDetailFilesAdapter.ViewHolder> {

  boolean isInEditMode = false;
  private IconicsDrawable noPreviewDrawable;

  public GistDetailFilesAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (noPreviewDrawable == null) {
      noPreviewDrawable = new IconicsDrawable(parent.getContext(), Octicons.Icon.oct_package);
      noPreviewDrawable.sizeDp(100);
      noPreviewDrawable.colorRes(R.color.secondary_text);
    }
    return new ViewHolder(getInflater().inflate(viewType, parent, false));
  }

  @Override
  protected void onBindViewHolder(ViewHolder holder, GistFile gistFile) {

    if (holder.textFileName != null) {
      holder.textFileName.setText(gistFile.filename);
    }

    if (gistFile.type != null) {
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

    if (holder.toolbar != null) {
      if (isInEditMode) {
        holder.toolbar.setVisibility(View.GONE);
      } else {
        holder.toolbar.setVisibility(View.VISIBLE);
      }
    }
  }

  private void printContent(TextView textContent, String content) {
    try {
      content = TextUtils.splitLines(content, 10);
    } catch (IOException exc) {
      exc.printStackTrace();
    }
    textContent.setText(content);
  }

  @Override
  public int getItemViewType(int position) {
    GistFile gistFile = getItem(position);
    if (gistFile.type != null) {
      if (gistFile.type.contains("image")) {
        return R.layout.row_gist_detail_binari;
      } else {
        return R.layout.row_gist_detail_text;
      }
    } else {
      return R.layout.row_gist_detail_empty;
    }
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
      CardView cardView = (CardView) itemView.findViewById(R.id.cardView);

      toolbar = (Toolbar) itemView.findViewById(R.id.toolbar);

      if (toolbar != null) {
        toolbar.inflateMenu(R.menu.row_gist_file);
        toolbar.setOnMenuItemClickListener(this);
      }

      if (cardView != null) {
        cardView.setOnClickListener(view -> {
          if (getCallback() != null) {
            GistFile file = getItem(getAdapterPosition());
            getCallback().onItemSelected(file);
          }
        });
      }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
      if (menuItem.getItemId() == R.id.action_toolbar_share) {
        GistFile gistFile = getItem(getAdapterPosition());
        new ShareRawAction(textContent.getContext(), gistFile.filename, gistFile.rawUrl, gistFile.content).execute();
      }
      return false;
    }
  }
}
