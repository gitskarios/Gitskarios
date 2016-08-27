package com.alorma.github.ui.fragment.content.markdown;

import android.os.Bundle;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.info.FileInfo;
import com.alorma.github.sdk.services.content.GetFileContentClient;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import com.alorma.github.ui.fragment.content.GithubFileDecoder;
import rx.Observable;

public class MarkdownFileFragment extends BaseMarkdownFileFragment {

  public static BaseMarkdownFileFragment getInstance(FileInfo info) {
    BaseMarkdownFileFragment fragment = new MarkdownFileFragment();
    Bundle args = new Bundle();
    args.putParcelable(FILE_INFO, info);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  protected Observable<String> getContentObservable(FileInfo fileInfo) {
    return new GetFileContentClient(fileInfo).observable()
        .map(content -> content.content)
        .flatMap(s -> new GithubFileDecoder(s).decode())
        .map(RequestMarkdownDTO::new)
        .flatMap(r -> new GetMarkdownClient(r).observable());
  }
}
