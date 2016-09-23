package core.repositories.releases.tags;

import com.alorma.github.sdk.bean.info.RepoInfo;
import core.datasource.CloudDataSource;
import core.datasource.RestWrapper;
import core.datasource.SdkItem;
import core.repositories.Commit;
import core.repositories.releases.Release;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

public class TagsCloudDataSource extends CloudDataSource<RepoInfo, List<Tag>> {
    private final RestWrapper restWrapper;
    private String sortOrder;

    public TagsCloudDataSource(RestWrapper restWrapper, String sortOrder) {
        super(restWrapper);
        this.restWrapper = restWrapper;
        this.sortOrder = sortOrder;
    }

    @Override
    protected Observable<SdkItem<List<Tag>>> execute(SdkItem<RepoInfo> data, RestWrapper service) {
        final RepositoryTagsService repositoryTagsService = restWrapper.get();
        final RepoInfo repoInfo = data.getK();
        Integer page = data.getPage();

        return Observable.fromCallable(() -> {
            Call<List<Tag>> call;
            if (page != null && page > 0) {
                call = repositoryTagsService.tags(repoInfo.owner, repoInfo.name, page, sortOrder);
            } else {
                call = repositoryTagsService.tags(repoInfo.owner, repoInfo.name, sortOrder);
            }
            Response<List<Tag>> response = call.execute();
            int page1 = Integer.MIN_VALUE;
            if (restWrapper.isPaginated(response)) {
                page1 = restWrapper.getPage(response);
            }

            List<Tag> tags = response.body();
            //fetchCommits(tags, repositoryTagsService, repoInfo);
            fetchRelease(tags, repositoryTagsService, repoInfo);
            return new SdkItem<>(page1, tags);
        });
    }

    private void fetchRelease(List<Tag> tags, RepositoryTagsService repositoryTagsService, RepoInfo repo) {
        for (Tag tag : tags) {
            Call<Release> commitCall = repositoryTagsService.release(repo.owner, repo.name, tag.getName());
            try {
                Response<Release> commitResponse = commitCall.execute();
                tag.release = commitResponse.body();
            } catch (IOException e) {/*do nothing release doesnt exist*/}
        }
    }

    private void fetchCommits(List<Tag> tags,
                                   RepositoryTagsService repositoryTagsService, RepoInfo repo)
            throws IOException {
        for (Tag tag : tags) {
            Call<Commit> commitCall =
                    repositoryTagsService.commit(repo.owner, repo.name, tag.getSha().sha);
            Response<Commit> commitResponse = commitCall.execute();
            tag.commit = commitResponse.body();
        }
    }
}
