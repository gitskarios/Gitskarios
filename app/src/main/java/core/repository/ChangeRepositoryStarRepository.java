package core.repository;

import com.alorma.github.sdk.services.repo.actions.StarRepoClient;
import com.alorma.github.sdk.services.repo.actions.UnstarRepoClient;
import rx.Observable;

public class ChangeRepositoryStarRepository {

  public ChangeRepositoryStarRepository() {

  }

  public Observable<Boolean> star(String owner, String name) {
    StarRepoClient client = new StarRepoClient(owner, name);
    return client.observable();
  }

  public Observable<Boolean> unstar(String owner, String name) {
    UnstarRepoClient client = new UnstarRepoClient(owner, name);
    return client.observable();
  }
}
