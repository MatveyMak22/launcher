package com.example.webviewmedialauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebStorage;

public class PersistentWebView extends WebView {

    private static final String TAG = "PersistentWebView";
    private static final String PREF_NAME = "WebViewStates";
    private String stateKey;

    public PersistentWebView(Context context) {
        super(context);
        init();
    }

    public PersistentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersistentWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setStateKey(String stateKey) {
        this.stateKey = stateKey;
    }

    private void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Enable third-party cookies (for persistent login sessions)
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(this, true);

        setWebViewClient(new CustomWebViewClient());
        setWebChromeClient(new CustomWebChromeClient());
    }

    public void saveState() {
        if (stateKey == null) {
            Log.e(TAG, "State key not set, cannot save state.");
            return;
        }
        SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save URL
        editor.putString(stateKey + "_url", getUrl());

        // Save scroll position
        editor.putInt(stateKey + "_scroll_x", getScrollX());
        editor.putInt(stateKey + "_scroll_y", getScrollY());

        // WebView's saveState() method is deprecated but still the simplest way for API 21 compatibility
        // For newer APIs, consider alternatives if needed, but for simplicity and minSdk 21, this is used.
        Bundle bundle = new Bundle();
        saveState(bundle);
        editor.putString(stateKey + "_bundle", bundle.toString()); // Simple way to store Bundle, might need custom serialization for complex data

        editor.apply();
        Log.d(TAG, "State saved for key: " + stateKey + ", URL: " + getUrl());
    }

    public void restoreState() {
        if (stateKey == null) {
            Log.e(TAG, "State key not set, cannot restore state.");
            return;
        }
        SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Restore URL
        String savedUrl = prefs.getString(stateKey + "_url", null);
        if (savedUrl != null && !savedUrl.isEmpty()) {
            loadUrl(savedUrl);
            Log.d(TAG, "State restored for key: " + stateKey + ", URL: " + savedUrl);
        } else {
            Log.d(TAG, "No URL found for key: " + stateKey);
        }

        // Restore scroll position (will be applied after page load)
        final int scrollX = prefs.getInt(stateKey + "_scroll_x", 0);
        final int scrollY = prefs.getInt(stateKey + "_scroll_y", 0);
        if (scrollX != 0 || scrollY != 0) {
            post(() -> scrollTo(scrollX, scrollY));
        }

        // Restore bundle state - this part is tricky with Bundle.toString().
        // A proper solution would involve serializing/deserializing the Bundle contents.
        // For this minimal project, if state needs deeper persistence, a more robust serialization
        // of Bundle contents (e.g., to Base64) would be required.
        String bundleString = prefs.getString(stateKey + "_bundle", null);
        if (bundleString != null) {
            // Reconstruct Bundle from string (this is a placeholder, actual Bundle reconstruction
            // from a simple toString() is not directly possible without custom parsing).
            // For API 21, loadState is the method to use, but requires a Bundle, not a string.
            // This part demonstrates the intent but needs a robust Bundle -> String -> Bundle conversion.
            Log.w(TAG, "Bundle state restoration is complex with simple string storage.");
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "Page started loading: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "Page finished loading: " + url);
            // Restore scroll position after the page has loaded
            SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            final int scrollX = prefs.getInt(stateKey + "_scroll_x", 0);
            final int scrollY = prefs.getInt(stateKey + "_scroll_y", 0);
            if (scrollX != 0 || scrollY != 0) {
                view.post(() -> view.scrollTo(scrollX, scrollY));
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            // Handle new windows/popups by loading them in the current WebView
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(view);
            resultMsg.sendToTarget();
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
            // Grant permissions for geolocation requests
            callback.invoke(origin, true, false);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // Handle full-screen video, etc.
            // For simplicity, we are not implementing full screen logic for now.
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            // Hide full-screen view
            super.onHideCustomView();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            // You can add a progress bar here if needed
        }
    }
}