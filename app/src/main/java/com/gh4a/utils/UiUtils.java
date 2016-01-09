package com.gh4a.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.alorma.github.UrlsManager;

import java.util.List;

public class UiUtils {
    public static final LinkMovementMethod CHECKING_LINK_METHOD2 = new LinkMovementMethod() {
        @Override
        public boolean onTouchEvent(@NonNull TextView widget, @NonNull Spannable buffer, @NonNull MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (ActivityNotFoundException e) {
                return true;
            }
        }
    };

    public static final LinkMovementMethod CHECKING_LINK_METHOD = new LinkMovementMethod() {

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    String url = link[0].getURL();

                    if (url != null) {
                        Intent intent = new UrlsManager(widget.getContext()).checkUri(Uri.parse(url));

                        if (intent != null) {
                            widget.getContext().startActivity(intent);
                        } else {
                            PackageManager packageManager = widget.getContext().getPackageManager();
                            Intent intentGeneric = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intentGeneric, PackageManager.GET_RESOLVED_FILTER);
                            if (resolveInfos.size() > 0) {
                                widget.getContext().startActivity(intentGeneric);
                            }
                        }

                        return true;
                    }
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }
    };
}