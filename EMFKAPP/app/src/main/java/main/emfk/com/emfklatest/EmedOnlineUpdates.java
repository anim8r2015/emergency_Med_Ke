package main.emfk.com.emfklatest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Created by user on 7/10/2016.
 */
public class EmedOnlineUpdates extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private WebView webView;
    private SwipeRefreshLayout swipe_refresh_layout_emed;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_web_emed_layout);
        String s = getResources().getString(R.string.emed_online_url);
        webView = (WebView) findViewById(R.id.webView1);
        swipe_refresh_layout_emed = findViewById(R.id.swipe_refresh_layout_emed);
        startWebView(s);
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
                /*if (progressDialog == null) {
                    // in standard case YourActivity.this
                    //progressDialog = new ProgressDialog(EMLocationMapActivity.this);
                    //progressDialog.setMessage("Loading...");
                    //progressDialog.show();
                }*/
                if(swipe_refresh_layout_emed.isRefreshing()) {
                    swipe_refresh_layout_emed.setRefreshing(false);
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    /*if (progressDialog.isShowing()) {
                        //progressDialog.dismiss();
                       // progressDialog = null;
                    }*/
                    if(swipe_refresh_layout_emed.isRefreshing()) {
                        swipe_refresh_layout_emed.setRefreshing(false);
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        });
        // Javascript inabled on webview
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );



        if ( !new EMFUtilities(getApplicationContext()).isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        } else {
            webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
        }
        webView.loadUrl(url);


    }
    // Open previous opened link from history on webview when back button pressed
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        swipe_refresh_layout_emed.setRefreshing(true);
        webView.reload();
        /*Runnable thread = new Runnable(){
            @Override
            public void run() {

            }
        };*/



    }
}
