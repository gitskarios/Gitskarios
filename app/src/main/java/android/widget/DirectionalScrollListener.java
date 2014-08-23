package android.widget;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

/**
 * Created by a557114 on 20/06/2014.
 */
public class DirectionalScrollListener implements AbsListView.OnScrollListener {

    private int oldTop;
    private int oldFirstVisibleItem;

    private OnDetectScrollListener onDetectScrollListener;
    private AbsListView.OnScrollListener handlerScroll;

    private long countdownStop;
    private boolean enabled;

    public DirectionalScrollListener(OnDetectScrollListener onDetectScrollListener) {
        this.onDetectScrollListener = onDetectScrollListener;
        this.countdownStop = -1;
    }

    public DirectionalScrollListener(OnCancelableDetectScrollListener onDetectScrollListener, AbsListView.OnScrollListener handlerScroll, long countdownStop) {
        this.onDetectScrollListener = onDetectScrollListener;
        this.handlerScroll = handlerScroll;
        this.countdownStop = countdownStop;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (handlerScroll != null) {
            handlerScroll.onScrollStateChanged(view, scrollState);
        }

        enabled = scrollState == SCROLL_STATE_TOUCH_SCROLL;

        if (scrollState == SCROLL_STATE_IDLE && countdownStop > -1) {
            new CountDownTimer(countdownStop, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if (onDetectScrollListener != null && onDetectScrollListener instanceof OnCancelableDetectScrollListener) {
                        ((OnCancelableDetectScrollListener) onDetectScrollListener).onScrollStop();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (handlerScroll != null) {
            handlerScroll.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (onDetectScrollListener != null) {
            onDetectedListScroll(view, firstVisibleItem);
        }
    }

    private void onDetectedListScroll(AbsListView absListView, int firstVisibleItem) {
        if (enabled) {
            View view = absListView.getChildAt(0);
            int top = (view == null) ? 0 : view.getTop();

            if (firstVisibleItem == oldFirstVisibleItem) {
                if (top > oldTop) {
                    up();
                } else if (top < oldTop) {
                    down();
                }
            } else {
                if (firstVisibleItem < oldFirstVisibleItem) {
                    up();
                } else {
                    down();
                }
            }

            oldTop = top;
            oldFirstVisibleItem = firstVisibleItem;
        }
    }

    private void up() {
        if (onDetectScrollListener != null) {
            onDetectScrollListener.onUpScrolling();
        }

        if (handlerScroll != null && handlerScroll instanceof OnDetectScrollListener) {
            ((OnDetectScrollListener) handlerScroll).onUpScrolling();
        }
    }

    private void down() {
        if (onDetectScrollListener != null) {
            onDetectScrollListener.onDownScrolling();
        }

        if (handlerScroll != null && handlerScroll instanceof OnDetectScrollListener) {
            ((OnDetectScrollListener) handlerScroll).onDownScrolling();
        }
    }

    public interface OnDetectScrollListener {

        void onUpScrolling();

        void onDownScrolling();
    }

    public interface OnCancelableDetectScrollListener extends OnDetectScrollListener {
        void onScrollStop();
    }

    public OnDetectScrollListener getOnDetectScrollListener() {
        return onDetectScrollListener;
    }

    public void setOnDetectScrollListener(OnDetectScrollListener onDetectScrollListener) {
        this.onDetectScrollListener = onDetectScrollListener;
    }

    public AbsListView.OnScrollListener getHandlerScroll() {
        return handlerScroll;
    }

    public void setHandlerScroll(AbsListView.OnScrollListener handlerScroll) {
        this.handlerScroll = handlerScroll;
    }

    public long getCountdownStop() {
        return countdownStop;
    }

    public void setCountdownStop(long countdownStop) {
        this.countdownStop = countdownStop;
    }
}

