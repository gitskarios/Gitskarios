package com.alorma.github.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.request.RepoRequestDTO;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.RepositoryManagerFragment;

/**
 * Created by a557114 on 01/08/2015.
 */
public class ManageRepositoryActivity extends BackActivity {

    public static final String CONTENT = "CONTENT";
    private static final String REPO_INFO = "REPO_INFO";
    private static final String REQUEST_DTO = "REQUEST_DTO";
    private RepositoryManagerFragment repositoryManagerFragment;

    public static Intent createIntent(Context context, RepoInfo repoInfo, RepoRequestDTO repoRequestDTO) {
        Intent intent = new Intent(context, ManageRepositoryActivity.class);

        intent.putExtra(REPO_INFO, repoInfo);
        intent.putExtra(REQUEST_DTO, repoRequestDTO);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_toolbar);

        if (getIntent() != null && getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey(REPO_INFO) && getIntent().getExtras().containsKey(REQUEST_DTO)) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                RepoInfo repoInfo = (RepoInfo) getIntent().getExtras().getParcelable(REPO_INFO);
                RepoRequestDTO dto = (RepoRequestDTO) getIntent().getExtras().getParcelable(REQUEST_DTO);
                repositoryManagerFragment = RepositoryManagerFragment.newInstance(repoInfo, dto);
                ft.replace(R.id.content, repositoryManagerFragment);
                ft.commit();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void close(boolean navigateUp) {
        RepoRequestDTO repoRequestDTO = repositoryManagerFragment.getRepoRequestDTO();
        if (!TextUtils.isEmpty(repoRequestDTO.name)) {
            Intent data = new Intent();
            data.putExtra(CONTENT, repoRequestDTO);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.edit_repo_title_cannot_empty), Toast.LENGTH_SHORT).show();
        }
    }
}
