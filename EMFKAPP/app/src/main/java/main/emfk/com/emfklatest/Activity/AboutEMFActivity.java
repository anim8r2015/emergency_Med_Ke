package main.emfk.com.emfklatest.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import main.emfk.com.emfklatest.EMFUtilities;
import main.emfk.com.emfklatest.R;

/**
 * Created by user on 7/10/2016.
 */
public class AboutEMFActivity  extends AppCompatActivity {

    private String uUrl = null;
    private String webUrl = null;

    //private String aboutUrl = "file:///android_asset/aboutemf_bt2.htm";
    private String aboutUrl = "https://www.emergencymedicinekenya.org/about";
    private WebView webView;
    private ProgressBar progressBar;
    private ImageView imgHeader;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wv_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);

        startWebView(aboutUrl);

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
                    progressDialog = new ProgressDialog(AboutEMFActivity.this);
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
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );

        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        if ( !new EMFUtilities(getApplicationContext()).isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }
        webView.loadUrl(url);

    }



    // Open previous opened link from history on wv when back button pressed
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }
}