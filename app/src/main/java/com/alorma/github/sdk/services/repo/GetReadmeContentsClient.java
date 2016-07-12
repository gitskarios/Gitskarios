package com.alorma.github.sdk.services.repo;

import android.text.TextUtils;
import android.util.Base64;
import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.content.GetMarkdownClient;
import java.io.UnsupportedEncodingException;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetReadmeContentsClient extends GithubRepoClient<String> {

  public GetReadmeContentsClient(RepoInfo info) {
    super(info);
  }

  @Override
  protected Observable<String> getApiObservable(RestAdapter restAdapter) {
    RepoService repoService = restAdapter.create(RepoService.class);

    Observable<Content> contentObservable;

    if (getBranch() == null) {
      contentObservable = repoService.readme(getOwner(), getRepo());
    } else {
      contentObservable = repoService.readme(getOwner(), getRepo(), getBranch());
    }

    return contentObservable.filter(
        content -> content != null && !TextUtils.isEmpty(content.content))
        .filter(content -> "base64".equals(content.encoding))
        .map(content -> {
          byte[] data = Base64.decode(content.content, Base64.DEFAULT);
          try {
            content.content = new String(data, "UTF-8");
            return content.content;
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
          return "";
        })
        .filter(s -> !TextUtils.isEmpty(s))
        .map(s -> {
          RequestMarkdownDTO requestMarkdownDTO = new RequestMarkdownDTO();
          requestMarkdownDTO.text = s;
          return requestMarkdownDTO;
        })
        .flatMap(requestMarkdownDTO -> new GetMarkdownClient(requestMarkdownDTO).observable());
  }
}
