package com.alorma.github.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.alorma.github.R;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * http://stackoverflow.com/a/29353049/325479
 * Created by mikepenz on 20.06.15.
 */
public class CopyWebView extends WebView {

    // setting custom action bar
    private ActionMode mActionMode;
    private ActionMode.Callback mSelectActionModeCallback;
    private GestureDetector mDetector;
    private WebViewListener mWebViewListener;

    // override all other constructor to avoid crash
    public CopyWebView(Context context) {
        this(context, null);
    }

    public CopyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CopyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        WebSettings webviewSettings = getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        addJavascriptInterface(new WebAppInterface(), "JSInterface");

        mDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                startActionMode(mSelectActionModeCallback);
            }

            /*
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (mActionMode != null) {
                    mActionMode.finish();
                    return true;
                }
                return false;
            }
            */
        });
    }

    // this will over ride the default action bar on long press
    @Override
    public ActionMode startActionMode(android.view.ActionMode.Callback callback) {
        ViewParent parent = getParent();
        if (parent == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String name = callback.getClass().toString();
            if (name.contains("SelectActionModeCallback")) {
                mSelectActionModeCallback = callback;
            }
        }
        CustomActionModeCallback mActionModeCallback = new CustomActionModeCallback();
        return parent.startActionModeForChild(this, mActionModeCallback);
    }

    /**
     * a small helper javascrip function to copy the selected text
     */
    private void getSelectedData() {

        String js = "(function getSelectedText() {" +
                "var txt;" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "JSInterface.getText(txt);" +
                "})()";
        // calling the js function
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:" + js, null);
        } else {
            loadUrl("javascript:" + js);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Send the event to our gesture detector
        // If it is implemented, there will be a return value
        if (mDetector != null) {
            mDetector.onTouchEvent(event);
        }
        // If the detected gesture is unimplemented, send it to the superclass
        return super.onTouchEvent(event);
    }

    public void setWebViewListener(WebViewListener webViewListener) {
        mWebViewListener = webViewListener;
    }

    /**
     * listener interface
     */
    public interface WebViewListener {
        void onTextCopy(String text);
    }

    /**
     * A ActionModeCallback
     */
    private class CustomActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_web_view_copy, menu);

            menu.findItem(R.id.copy)
                    .setIcon(new IconicsDrawable(CopyWebView.this.getContext(), GoogleMaterial.Icon.gmd_content_copy).color(Color.WHITE).actionBar());
            menu.findItem(R.id.share)
                    .setIcon(new IconicsDrawable(CopyWebView.this.getContext(), GoogleMaterial.Icon.gmd_share).color(Color.WHITE).actionBar());

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.copy:
                    getSelectedData();
                    mode.finish();
                    return true;
                case R.id.share:
                    mode.finish();
                    return true;
                default:
                    mode.finish();
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                clearFocus();
            } else {
                if (mSelectActionModeCallback != null) {
                    mSelectActionModeCallback.onDestroyActionMode(mode);
                }
                mActionMode = null;
            }
        }
    }

    /**
     * a helper interface class to call the WebViewListener.onTextCopy
     */
    public class WebAppInterface {
        @JavascriptInterface
        public void getText(String text) {
            if (mWebViewListener != null) {
                mWebViewListener.onTextCopy(text);
            }
        }
    }
}
