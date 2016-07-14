package com.alorma.github.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GistFile;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

public class GistSmallFilesAdapter extends RecyclerArrayAdapter<GistFile, GistSmallFilesAdapter.ViewHolder> {

  private IconicsDrawable noPreviewDrawable;

  public GistSmallFilesAdapter(LayoutInflater inflater) {
    super(inflater);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (noPreviewDrawable == null) {
      noPreviewDrawable = new IconicsDrawable(parent.getContext(), Octicons.Icon.oct_package);
      noPreviewDrawable.sizeDp(24);
      noPreviewDrawable.colorRes(R.color.secondary_text);
    }
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
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
      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    GistFile gistFile = getItem(position);
    if (gistFile.type != null) {
      if (gistFile.type.contains("image")) {
        return R.layout.row_gist_small_binari;
      } else {
        return R.layout.row_gist_small_text;
      }
    } else {
      return R.layout.row_gist_small_empty;
    }
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final TextView textFileName;
    public final ImageView imageContent;

    public ViewHolder(View itemView) {
      super(itemView);
      textFileName = (TextView) itemView.findViewById(R.id.textFileName);
      imageContent = (ImageView) itemView.findViewById(R.id.imageContent);
      CardView cardView = (CardView) itemView.findViewById(R.id.cardView);

      if (cardView != null) {
        cardView.setOnClickListener(view -> {
          if (getCallback() != null) {
            GistFile file = getItem(getAdapterPosition());
            getCallback().onItemSelected(file);
          }
        });
      }
    }
  }
}
