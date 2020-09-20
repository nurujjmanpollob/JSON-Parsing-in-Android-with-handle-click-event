package com.nurujjamanpollob.jsonparsing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



public class WebLoader extends AppCompatActivity {

    WebView webView;
    String url;
    private View mCustomView;
    private int mOriginalSystemUiVisibility;
    private int mOriginalOrientation;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    private static final String TAG_STUDENT_SOCIAL_LINK = "sociallink";


    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_app);
        webView = findViewById(R.id.new_webView);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        progressBar = findViewById(R.id.progressBar_webview);
        Intent intent = getIntent();
        url = intent.getStringExtra(TAG_STUDENT_SOCIAL_LINK);
        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAppCacheEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            WebView.enableSlowWholeDocumentDraw();

        }

        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        swipeRefreshLayout.setRefreshing(true);

        webView.setWebChromeClient(new WebChromeClient() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {

                // if a view already exists then immediately terminate the new one

                if (mCustomView != null) {
                    onHideCustomView();
                    return;
                }

                // 1. Stash the current state
                mCustomView = view;


                mOriginalSystemUiVisibility = WebLoader.this.getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();

                // 2. Stash the custom view callback
                mCustomViewCallback = callback;

                // 3. Add the custom view to the view hierarchy
                FrameLayout decor = (FrameLayout) WebLoader.this.getWindow().getDecorView();
                decor.addView(mCustomView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));


                // 4. Change the state of the window
                WebLoader.this.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE);
                WebLoader.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                super.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                // 1. Remove the custom view
                FrameLayout decor = (FrameLayout) WebLoader.this.getWindow().getDecorView();
                decor.removeView(mCustomView);
                mCustomView = null;

                // 2. Restore the state to it's original form
                WebLoader.this.getWindow().getDecorView()
                        .setSystemUiVisibility(mOriginalSystemUiVisibility);
                WebLoader.this.setRequestedOrientation(mOriginalOrientation);

                // 3. Call the custom view callback
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;

                super.onHideCustomView();
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {


                progressBar.setProgress(newProgress);
                if (newProgress < 100) {

                    progressBar.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(true);

                } else {

                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                }



                super.onProgressChanged(view, newProgress);
            }
        });


        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);

                progressBar.setVisibility(View.GONE);

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                swipeRefreshLayout.setRefreshing(false);

                progressBar.setVisibility(View.GONE);

                Toast.makeText(WebLoader.this, "Network Connection Error Code: " + error.toString(), Toast.LENGTH_LONG).show();
                super.onReceivedError(view, request, error);
            }
        });


    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webView.canGoBack()){
                webView.goBack();
            }else {
          //      Toast.makeText(this, " Exiting app...", Toast.LENGTH_SHORT).show();
                WebLoader.this.finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
