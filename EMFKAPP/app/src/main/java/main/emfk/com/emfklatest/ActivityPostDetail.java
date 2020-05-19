package main.emfk.com.emfklatest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;


import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import androidx.appcompat.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import java.io.File;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;

/**
 * Created by anthony.anyama on 7/6/2016.
 */
public class ActivityPostDetail extends AppCompatActivity {

    ImageView image;
    TextView textTitle, textDescription, txtPostDateTime;
    String imgSrcString, readMoreUrl, imageSrc, postid, postTable, postSaved = null;
    Spanned titleString = null;
    Spanned detailString = null;
    String dateString = null;
    String shareData = null;
    String htmlString = null;
    int lenghtOfFile = 0;

    private ShareActionProvider mShareActionProvider;
    FloatingActionButton mFabReadMore;
    private String linkType = null;
    private PostsDBAdapter mDbHelper;
    private WebView wvContent;
    byte data[];
    //progress dialogue
    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url = "http://www.qwikisoft.com/demo/ashade/20001.kml";

    private static String DOWNLOAD_LOC = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    private static String DOWNLOAD_LOC2 = Environment.getExternalStorageDirectory().toString();

    Typeface robotoMedium, adampro, japooki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_detail_layout);
        robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
        adampro = Typeface.createFromAsset(getAssets(), "fonts/adam_cg_pro.otf");
        japooki = Typeface.createFromAsset(getAssets(), "fonts/jaapokki_regular.otf");

        mFabReadMore = (FloatingActionButton)findViewById(R.id.fabReadMore);
        wvContent = (WebView) findViewById(R.id.wvContent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // TODO drawable ic back if bar white

        if (toolbar != null) {
            toolbar.setLogo(R.drawable.enlarged_emf);
        }

//        final Drawable upArrow = getResources().getDrawable(R.drawable.arrow_left);
//        upArrow.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        StringBuilder sb = new StringBuilder();

        Intent intentExtra;
        intentExtra = getIntent();

        imgSrcString = (String)intentExtra.getExtras().get("imageUrl");
        titleString = (Spanned)intentExtra.getExtras().get("titleText");
        //detailString = (Spanned)intentExtra.getExtras().get("descText");
        htmlString = (String)intentExtra.getExtras().get("descText");
        dateString = (String)intentExtra.getExtras().get("postTimeText");
        shareData = (String)intentExtra.getExtras().get("postUrl");
        shareData = sb.append(titleString).append("\r\n").append(shareData).toString();
        readMoreUrl = (String)intentExtra.getExtras().get("readMore");
        postid = (String)intentExtra.getExtras().get("postid");
        postTable = (String)intentExtra.getExtras().get("postTable");
        postSaved = (String) intentExtra.getExtras().get("postSaved");


        Log.d("RDMR","Before " + readMoreUrl + " Post id " + postid);
        if(readMoreUrl != null) {
            /*if (readMoreUrl.length() == 0) {
                mFabReadMore.hide();
            } else {*/

                if(EMFUtilities.checkLinkType(readMoreUrl).contains("pdf")==true){
                    linkType = "pdf";
                    mFabReadMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_file_pdf_white_48dp));
                } else if(EMFUtilities.checkLinkType(readMoreUrl).contains("mp4")==true){
                    linkType = "mp4";
                    mFabReadMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_play_white_48dp));
                } else if(EMFUtilities.checkLinkType(readMoreUrl).contains("ytb")==true){
                    linkType = "ytb";
                    mFabReadMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_play_white_48dp));
                } else if(EMFUtilities.checkLinkType(readMoreUrl).contains("img")==true){
                    linkType = "img";
                    mFabReadMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fingerprint_white_48dp));
                } else if(EMFUtilities.checkLinkType(readMoreUrl).contains("nrml")==true){
                    linkType = "nrml";
                    mFabReadMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_fingerprint_white_48dp));
                } else {
                    linkType = "ukwn";
                    mFabReadMore.hide();
                }
                mFabReadMore.show();
            //}
        } else {
            linkType = "ukwn";
            mFabReadMore.hide();
        }
        mDbHelper = new PostsDBAdapter(this);
        mDbHelper.open();
        image = (ImageView) findViewById(R.id.imgView);
        textTitle = (TextView) findViewById(R.id.titleText);
        txtPostDateTime = (TextView) findViewById(R.id.postDateTime);

        textTitle.setTypeface(adampro);

        textTitle.setText(titleString);
        //textDescription.setText(detailString);
        txtPostDateTime.setText(dateString);

        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath() );
        wvContent.getSettings().setAllowFileAccess( true );
        wvContent.getSettings().setAppCacheEnabled( true );

        wvContent.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );

        if (!new EMFUtilities(getApplicationContext()).isNetworkAvailable()) { // loading offline
            wvContent.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        wvContent.loadDataWithBaseURL("file:///android_asset/",htmlString, "text/html", "utf-8", null);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ints = new Intent(getApplicationContext(), PostImageActivity.class);
                ints.putExtra("imageSrc",imgSrcString);
                startActivity(ints);
            }
        });

        mFabReadMore.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

            if(linkType.equalsIgnoreCase("pdf")){
               /*Intent ints = new Intent(Intent.ACTION_VIEW);
                ints.setDataAndType(Uri.parse(readMoreUrl), "application/pdf");
                Intent chooser = Intent.createChooser(ints,"Select Reader"); */

                Intent int2 = new Intent(getApplicationContext(),PDFViewActivity.class);
                int2.putExtra("online","Y");
                int2.putExtra("readMoreUrl",readMoreUrl);
                //make modification here
                //download and load locally
                startActivity(int2);
            } else {
               Intent ints = new Intent(getApplicationContext(), ReadMoreActivity.class);
                ints.putExtra("readMore",readMoreUrl);
                ints.putExtra("imgrs", imgSrcString);
                startActivity(ints);
            }

                }
        });

        if(imgSrcString.trim().length() > 0) {
            if (imgSrcString.equalsIgnoreCase("getLocal")) {
                image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.vimeo_logo));
            } else if (imgSrcString.equalsIgnoreCase("whiteLoad")){
                image.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.white_line));
            } else {
                Picasso.with(this)
                        .load(imgSrcString)
                        .noFade()
                        .tag(this)
                        .into(image);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_details, menu);
        MenuItem item = menu.findItem(R.id.savePost);
        MenuItem item2 = menu.findItem(R.id.menu_item_share);
        String rs = postSaved;
        if (rs.equalsIgnoreCase("Y")) {
            item.setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.delete));
            //Toast.makeText(getApplicationContext(),"Article Saved!",Toast.LENGTH_SHORT).show();
        } else {
            item.setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.download_ic_red));
            //Toast.makeText(getApplicationContext(),"Article Not Saved!",Toast.LENGTH_SHORT).show();
        }
        //MenuItemCompat.getActionProvider(menuItem)
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item2);
        setShareIntent(createShareIntent());
        // Return true to display menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.savePost) {

            String rs = mDbHelper.toggleSavePost(postid,postTable);
            if (rs.equalsIgnoreCase("Y")) {
				item.setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.delete));
                Toast.makeText(getApplicationContext(),"Article Saved!",Toast.LENGTH_SHORT).show();
            } else {
				item.setIcon(ContextCompat.getDrawable(getApplicationContext(),R.drawable.download_ic_red));
                Toast.makeText(getApplicationContext(),"Article Removed from Saved!",Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                shareData);
        return shareIntent;
    }

    /* Background Async Task to download file
     * */


    private boolean fetchFileFromDisk(String downloadedFile){
        //implement file fetch from internal storage after download
        File f = new File(downloadedFile);

        if(f.exists()){
            return true;
        } else {
            return false;
        }
    }
}
