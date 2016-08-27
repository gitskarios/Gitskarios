package com.alorma.github.ui.fragment.content.source;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.alorma.github.Base64;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.content.BaseFileFragment;
import uk.co.senab.photoview.PhotoViewAttacher;

public abstract class ImageBaseFileFragment extends BaseFileFragment {

  PhotoViewAttacher mAttacher;
  private ImageView imageView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.image_file_fragment, null, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    imageView = (ImageView) view.findViewById(R.id.imageView);
    mAttacher = new PhotoViewAttacher(imageView);
  }

  @Override
  protected void onContentLoaded(String s) {
    if (getActivity() != null && isAdded()) {
      try {
        byte[] imageAsBytes = Base64.decode(s);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(bitmap);
        mAttacher.update();
      } catch (Exception e) {
        Toast.makeText(getActivity(), R.string.error_loading_image, Toast.LENGTH_SHORT).show();
        e.printStackTrace();
      }
    }
  }
}
