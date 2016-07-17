package com.alorma.github.emoji;

import com.alorma.github.ui.fragment.base.BaseFragment;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Subscription;

public abstract class EmojisFragment extends BaseFragment {

  private Subscription subscribe;

  @Override
  public void onStart() {
    super.onStart();

    EmojisPresenter emojisPresenter = new EmojisPresenter();
    subscribe = emojisPresenter.getEmojis().subscribe(new EmojiSubscriber());
  }

  public void filter(String filter) {
    if (subscribe != null) {
      subscribe.unsubscribe();
    }
    EmojisPresenter emojisPresenter = new EmojisPresenter();
    subscribe = emojisPresenter.getEmojis(filter)
        .throttleLast(100, TimeUnit.MILLISECONDS)
        .debounce(250, TimeUnit.MILLISECONDS)
        .subscribe(new EmojiSubscriber());
  }

  @Override
  public void onStop() {
    if (subscribe != null) {
      subscribe.unsubscribe();
    }
    super.onStop();
  }

  private class EmojiSubscriber extends rx.Subscriber<List<Emoji>> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(List<Emoji> emojis) {
      onEmojisLoaded(emojis);
    }
  }

  protected abstract void onEmojisLoaded(List<Emoji> emojis);
}
