package com.example.webviewmedialauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class WebViewStateAdapter extends PagerAdapter {

    private static final String YOUTUBE_URL = "https://m.youtube.com";
    private static final String KINOPOISK_URL = "https://www.kinopoisk.ru";

    private final Context context;
    private final Map<Integer, PersistentWebView> webViews;
    private final String[] tabTitles;

    public WebViewStateAdapter(Context context) {
        this.context = context;
        this.webViews = new HashMap<>();
        this.tabTitles = new String[]{
                context.getString(R.string.tab_youtube),
                context.getString(R.string.tab_kinopoisk)
        };
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PersistentWebView webView;
        if (webViews.containsKey(position)) {
            webView = webViews.get(position);
        } else {
            webView = new PersistentWebView(context);
            webView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            String urlToLoad;
            String stateKey;

            if (position == 0) {
                urlToLoad = YOUTUBE_URL;
                stateKey = "webview_state_youtube";
            } else {
                urlToLoad = KINOPOISK_URL;
                stateKey = "webview_state_kinopoisk";
            }
            webView.setStateKey(stateKey);
            webView.loadUrl(urlToLoad);
            webViews.put(position, webView);
        }
        container.addView(webView);
        return webView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof PersistentWebView) {
            PersistentWebView webView = (PersistentWebView) object;
            container.removeView(webView);
            // Do not remove from webViews map, keep it for state saving/restoring
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public void saveAllWebViewStates() {
        for (PersistentWebView webView : webViews.values()) {
            webView.saveState();
        }
    }

    public void restoreAllWebViewStates() {
        for (PersistentWebView webView : webViews.values()) {
            webView.restoreState();
        }
    }
}