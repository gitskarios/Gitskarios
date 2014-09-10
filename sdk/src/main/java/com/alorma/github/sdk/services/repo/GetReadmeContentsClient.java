package com.alorma.github.sdk.services.repo;

import android.content.Context;
import android.util.Base64;

import com.alorma.github.sdk.bean.dto.request.RequestMarkdownDTO;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.Content;
import com.alorma.github.sdk.services.content.GetMarkdownClient;

import java.io.UnsupportedEncodingException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 20/07/2014.
 */
public class GetReadmeContentsClient extends BaseRepoClient<String> {

    private OnResultCallback<String> callback;
    private Branch currentBranch;

    public GetReadmeContentsClient(Context context, String owner, String repo) {
        super(context, owner, repo);
    }

    @Override
    protected void executeService(RepoService repoService) {
        if (currentBranch == null) {
            repoService.readme(owner, repo, new ContentCallback());
        } else {
            repoService.readme(owner, repo, currentBranch.name, new ContentCallback());
        }
    }

    public void setCallback(OnResultCallback<String> callback) {
        this.callback = callback;
    }

    public void setCurrentBranch(Branch currentBranch) {
        this.currentBranch = currentBranch;
    }

    private class ContentCallback implements Callback<Content> {

        @Override
        public void success(Content content, Response response) {
            RequestMarkdownDTO requestMarkdownDTO = new RequestMarkdownDTO();
            if ("base64".equals(content.encoding)) {
                byte[] data = Base64.decode(content.content, Base64.DEFAULT);
                try {
                    content.content = new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            requestMarkdownDTO.text = content.content;
            GetMarkdownClient markdownClient = new GetMarkdownClient(context, requestMarkdownDTO);
            markdownClient.setOnResultCallback(callback);
            markdownClient.execute();
        }

        @Override
        public void failure(RetrofitError error) {
            if (getOnResultCallback() != null) {
                getOnResultCallback().onFail(error);
            }
        }
    }
}
