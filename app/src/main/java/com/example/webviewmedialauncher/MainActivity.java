package com.example.webviewmedialauncher;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.webviewmedialauncher.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WebViewStateAdapter webViewStateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        webViewStateAdapter = new WebViewStateAdapter(this);
        binding.viewPager.setAdapter(webViewStateAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(getString(R.string.tab_youtube));
                            break;
                        case 1:
                            tab.setText(getString(R.string.tab_kinopoisk));
                            break;
                    }
                }).attach();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save state for all WebViews
        webViewStateAdapter.saveAllWebViewStates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore state for all WebViews
        webViewStateAdapter.restoreAllWebViewStates();
    }
}