package core.repository;

import com.alorma.github.sdk.services.repo.actions.UnwatchRepoClient;
import com.alorma.github.sdk.services.repo.actions.WatchRepoClient;
import rx.Observable;

public class ChangeRepositoryWatchRepository {

  public ChangeRepositoryWatchRepository() {

  }

  public Observable<Boolean> watch(String owner, String name) {
    WatchRepoClient client = new WatchRepoClient(owner, name);
    return client.observable();
  }

  public Observable<Boolean> unwatch(String owner, String name) {
    UnwatchRepoClient client = new UnwatchRepoClient(owner, name);
    return client.observable();
  }
}
