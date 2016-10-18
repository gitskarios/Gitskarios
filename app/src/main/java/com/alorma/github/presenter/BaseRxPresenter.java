package com.alorma.github.presenter;

import android.support.annotation.Nullable;
import com.alorma.github.injector.named.IOScheduler;
import com.alorma.github.injector.named.MainScheduler;
import core.datasource.SdkItem;
import core.repository.GenericRepository;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Represents base RxJava presenter.
 * Just only one Observable could be subscribed to it.
 */
public abstract class BaseRxPresenter<REQUEST, RESPONSE, VIEW extends View<RESPONSE>> extends BasePresenter<VIEW> {

  protected final Scheduler mainScheduler;
  protected final Scheduler ioScheduler;
  private final GenericRepository<REQUEST, RESPONSE> genericRepository;
  protected Subscriber<RESPONSE> subscriber;
  private Integer page;

  public BaseRxPresenter(@MainScheduler Scheduler mainScheduler, @IOScheduler Scheduler ioScheduler,
      GenericRepository<REQUEST, RESPONSE> genericRepository) {
    this.mainScheduler = mainScheduler;
    this.ioScheduler = ioScheduler;
    this.genericRepository = genericRepository;
  }

  /**
   * Executes request doesn't assign page to request.
   *
   * @param request item to execute
   * @see #executePaginated(Object)
   */
  public void execute(REQUEST request) {
    if (!isViewAttached()) return;

    SdkItem<REQUEST> sdkItem = new SdkItem<>(request);
    subscribe(genericRepository.execute(sdkItem), false);
  }

  /**
   * Executes paginated request, automagically assigns next page if is requested more than one time.
   *
   * @param request item to execute
   * @see #execute(Object)
   */
  public void executePaginated(REQUEST request) {
    if (!isViewAttached() || page == null || page <= 0) return;

    SdkItem<REQUEST> sdkItem = new SdkItem<>(request);
    sdkItem.setPage(page);

    subscribe(genericRepository.execute(sdkItem), true);
  }

  /**
   * Creates internal subscriber and attaches it to observable argument.
   *
   * @param observable the object to subscribe
   * @param isFromPaginated indicates that request is paginated or not
   */
  protected void subscribe(Observable<SdkItem<RESPONSE>> observable, boolean isFromPaginated) {
    if (!isViewAttached()) return;

    getView().showLoading();

    unsubscribe();

    subscriber = new Subscriber<RESPONSE>() {
      @Override
      public void onCompleted() {
        BaseRxPresenter.this.onCompleted();
      }

      @Override
      public void onError(Throwable e) {
        BaseRxPresenter.this.onError(e);
      }

      @Override
      public void onNext(RESPONSE response) {
        BaseRxPresenter.this.onNext(response, isFromPaginated);
      }
    };

    observable.subscribeOn(ioScheduler).observeOn(mainScheduler).timeout(20, TimeUnit.SECONDS).retry(3).map(obs -> {
      if (obs.getPage() != null && obs.getPage() > 0) {
        this.page = obs.getPage();
      } else {
        this.page = null;
      }
      return obs.getK();
    }).subscribe(subscriber);
  }

  /**
   * Unsubscribes internal subscriber and set it to null.
   */
  protected void unsubscribe() {
    if (subscriber != null && !subscriber.isUnsubscribed()) {
      subscriber.unsubscribe();
    }

    subscriber = null;
  }

  protected void onCompleted() {
    if (isViewAttached()) {
      getView().hideLoading();
    }
    unsubscribe();
  }

  protected void onError(Throwable throwable) {
    if (isViewAttached()) {
      getView().showError(throwable);
      getView().hideLoading();
      throwable.printStackTrace();
    }
    unsubscribe();
  }

  protected void onNext(RESPONSE response, boolean isFromPaginated) {
    if (isViewAttached()) {
      getView().onDataReceived(response, isFromPaginated);
    }
  }

  @Override
  public void detachView() {
    super.detachView();
    if (isViewAttached()) {
      getView().hideLoading();
    }
    unsubscribe();
  }

  @Nullable
  public Integer getPage() {
    return page;
  }
}
