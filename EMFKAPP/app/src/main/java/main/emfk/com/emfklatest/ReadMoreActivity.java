package main.emfk.com.emfklatest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by user on 7/10/2016.
 */
public class ReadMoreActivity extends AppCompatActivity {

    private WebView webView;
    private WebView wv;
    private ProgressBar progressBar;
    private ImageView imgHeader;
    private float m_downX;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wv_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wv = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //imgHeader = (ImageView) findViewById(R.id.backdrop);

        // initializing toolbar
        initCollapsingToolbar();

        // load pgbar
        initWv();

        //Get webview
        Intent intentExtra;
        intentExtra = getIntent();
        String readmoreUrl = (String)intentExtra.getExtras().get("readMore");
        webView = (WebView) findViewById(R.id.webView1);

        wv.getSettings().setJavaScriptEnabled(true);

        wv.setHorizontalScrollBarEnabled(false);

        String img =  (String)intentExtra.getExtras().get("imgrs");

        /*Glide.with(getApplicationContext()).load(img)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgHeader); */

        wv.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        if ( !new EMFUtilities(getApplicationContext()).isNetworkAvailable() ) { // loading offline
            wv.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        wv.loadUrl(readmoreUrl);
    }

    private void initCollapsingToolbar() {
        /*final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" "); */
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        /*appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(" ");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });*/

    }

    private void initWv() {
        wv.setWebChromeClient(new myChromeClient(this));
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wv.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
                invalidateOptionsMenu();
            }
        });
        wv.clearCache(true);
        wv.clearHistory();
        wv.setHorizontalScrollBarEnabled(false);

        wv.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        wv.getSettings().setAllowFileAccess( true );
        wv.getSettings().setAppCacheEnabled( true );
        wv.getSettings().setJavaScriptEnabled( true );
        wv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;

                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;
                }

                return false;
            }
        });
    }

    private class myChromeClient extends WebChromeClient {
        Context ctx;

        public myChromeClient(Context ctx) {
            super();
            this.ctx = ctx;
        }


    }


    @SuppressLint("JavascriptInterface")
    private void startWebView(String url) {
        //Create new webview Client to show progress dialog
        //When opening a url or click on link

        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;
            //If you do not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(ReadMoreActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        });
        // Javascript inabled on webview
        //webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        if ( !new EMFUtilities(getApplicationContext()).isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        webView.loadUrl(url);
    }
    // Open previous opened link from history on webview when back button pressed
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(wv.canGoBack()) {
            wv.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }
}
