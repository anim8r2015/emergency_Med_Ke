package main.emfk.com.emfklatest;
/*u: emergen1
  p: ei863t3YNq
*/

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import java.util.List;

import main.emfk.com.emfklatest.Activity.AboutEMFActivity;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;

public class HomeActivity extends AppCompatActivity {
    private PostsDBAdapter mDbHelper;
    private ImageView savedPosts, algosBtn, videosBtn, postsBtn, emsBtn;
    private ImageView fbIconImg, twIconImg, gpIconImg, linkediniconImg, ytbiconImg,
    btnWhatsapp;
    private RelativeLayout mRelativeLayout;
    private CoordinatorLayout coordinator;
    private FloatingActionButton fabSocial;
    JSONArray posts = null;
    public static String FACEBOOK_URL = "https://www.facebook.com/emergencymedkenya/";
    public static String FACEBOOK_PAGE_ID = "emergencymedkenya";
    private Button mButton;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setLogo(R.drawable.enlarged_emf);
        }

        coordinator = (CoordinatorLayout) findViewById(R.id.mainCord);
        savedPosts = (ImageView) findViewById(R.id.imgSavedPosts);
        algosBtn = (ImageView) findViewById(R.id.btnAlgos);
        videosBtn = (ImageView) findViewById(R.id.btnVideos);
        postsBtn = (ImageView) findViewById(R.id.btnPosts);
        emsBtn = (ImageView) findViewById(R.id.btnEms);
        fabSocial = (FloatingActionButton) findViewById(R.id.fabSocial);

        //sendBtn = (Button)findViewById(R.id.btnSend);
        /*
        fbIconImg = (ImageView)findViewById(R.id.fbIcon);
        twIconImg = (ImageView)findViewById(R.id.twIcon);
        gpIconImg = (ImageView)findViewById(R.id.gpIcon);
        linkediniconImg = (ImageView)findViewById(R.id.linkedinicon);
        //rssiconImg = (ImageView)findViewById(R.id.rssicon);
        ytbiconImg = (ImageView)findViewById(R.id.ytbicon);
        btnWhatsapp = (ImageView)findViewById(R.id.icWhatsapp);
        btnGmail = (ImageView)findViewById(R.id.icgmail); */

        SearchView sv = (SearchView) findViewById(R.id.search);
        traverseView(sv, 0);
        sv.setIconifiedByDefault(false);
        //sv.setOnQueryTextListener(this);
        sv.setQueryHint("Search...");

        algosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postsintent = new Intent(getApplicationContext(), AlgorithmsActivity.class);
                startActivity(postsintent);
            }
        });

        videosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postsintent = new Intent(getApplicationContext(), VideoPostsActivity.class);
                startActivity(postsintent);
            }
        });

        postsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postsintent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(postsintent);
            }
        });

        emsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postsintent = new Intent(getApplicationContext(), EMSPostsActivity.class);
                startActivity(postsintent);
            }
        });

        Drawable bg = DrawableCompat.wrap(toolbar.getOverflowIcon());
        Drawable bg2 = DrawableCompat.wrap(ResourcesCompat.getDrawable(getResources(), R.drawable.red_folder, null));
        DrawableCompat.setTint(bg, ContextCompat.getColor(getApplicationContext(), R.color.colorRed));

        sv.setMaxWidth(getScreenWidth() - bg2.getIntrinsicWidth() - 50);

        View.OnClickListener imageClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent savedImages = new Intent(getApplicationContext(), SavedPostsActivity.class);

                startActivity(savedImages);
            }
        };

        savedPosts.setOnClickListener(imageClick);

        mDbHelper = new PostsDBAdapter(this);
        mDbHelper.open();
        int tblCount = 0;
        try {
            tblCount = mDbHelper.getPostCount();
        } catch (Exception e) {
            tblCount = 0;
            if (e.getMessage().contains("no such table")) {
                mDbHelper.createTable();
            }
        }



        fabSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.custom_layout,null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                );

                if(mPopupWindow.isShowing()){
                    mPopupWindow.dismiss();
                }

                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }

                mPopupWindow.setOutsideTouchable(true);

                // Get a reference for the custom view close button
                //ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                fbIconImg = (ImageView) customView.findViewById(R.id.fbIcon);
                twIconImg = (ImageView) customView.findViewById(R.id.twIcon);
                gpIconImg = (ImageView) customView.findViewById(R.id.gpIcon);
                linkediniconImg = (ImageView) customView.findViewById(R.id.linkedinicon);
                //rssiconImg = (ImageView)findViewById(R.id.rssicon);
                ytbiconImg = (ImageView) customView.findViewById(R.id.ytbicon);
                btnWhatsapp = (ImageView) customView.findViewById(R.id.icWhatsapp);
                //btnGmail = (ImageView) customView.findViewById(R.id.icgmail);

                //fb image click
                fbIconImg.setOnClickListener(new View.OnClickListener(){
                                                 @Override
                                                 public void onClick (View v){
                                                     mPopupWindow.dismiss();
                                                     launchFacebook();
                                                 }
                                             }
                );
                //twitter
                twIconImg.setOnClickListener(new View.OnClickListener(){
                                                 @Override
                                                 public void onClick (View v){
                                                     mPopupWindow.dismiss();
                                                     openTwtr("emergencymedke");
                                                 }
                                             }
                );

                //google plus
                gpIconImg.setOnClickListener(new View.OnClickListener(){
                                                 @Override
                                                 public void onClick (View v){
                                                     mPopupWindow.dismiss();
                                                     openGPlus("100364983439395463272");
                                                 }
                                             }
                );

                //linkedin
                linkediniconImg.setOnClickListener(new View.OnClickListener(){
                                                       @Override
                                                       public void onClick (View v){
                                                           mPopupWindow.dismiss();
                                                           openLinkedIn();
                                                       }
                                                   }
                );

                //youtube
                ytbiconImg.setOnClickListener(new View.OnClickListener(){
                                                  @Override
                                                  public void onClick (View v){
                                                      mPopupWindow.dismiss();
                                                      startYoutube();
                                                  }
                                              }
                );

                /*btnGmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                        sendEmail();
                    }
                }); */

                btnWhatsapp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //implement whatsapp here
                        mPopupWindow.dismiss();
                        launchWhatsapp();
                    }
                });


                // Set a click listener for the popup window close button
                /*closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                    }
                });*/

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(coordinator, Gravity.CENTER,0,0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.about) {
            Intent ints = new Intent(this, AboutEMFActivity.class);

            startActivity(ints);
            return true;
        } else if (id == R.id.website) {
            Intent ints = new Intent(this, EMFWebsiteActivity.class);
            ints.putExtra("webUrl", "http://www.emergencymedicinekenya.org/");
            startActivity(ints);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
    @Override
    public boolean onQueryTextChange(String query) {
        try{
            pAdapter.filter(query);
        } catch (Exception e){
            return false;
        }
        return false;
    }*/

    private void traverseView(View view, int index) {
        if (view instanceof SearchView) {
            SearchView v = (SearchView) view;
            for (int i = 0; i < v.getChildCount(); i++) {
                traverseView(v.getChildAt(i), i);
            }
        } else if (view instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) view;
            for (int i = 0; i < ll.getChildCount(); i++) {
                traverseView(ll.getChildAt(i), i);
            }
        } else if (view instanceof EditText) {
            ((EditText) view).setTextColor(Color.BLACK);
            ((EditText) view).setHintTextColor(Color.GRAY);
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(Color.BLUE);
        } else if (view instanceof ImageView) {
            ((ImageView) view).setColorFilter(Color.argb(255, 255, 0, 0));

        } else {
            Log.v("View Scout", "Undefined view type here...");
        }
    }

    public int getScreenWidth() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        //int height = display.heightPixels;
        return width;
    }
    /*
    public int getScreenHeight() {
        DisplayMetrics display = this.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        int height = display.heightPixels;
        return height;
    }*/

    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"emkf@emergencymedicinekenya.org"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);


        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(HomeActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public final void launchFacebook() {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        String facebookUrl = getFacebookPageURL(this);
        facebookIntent.setData(Uri.parse(facebookUrl));
        startActivity(facebookIntent);
    }

    public void launchWhatsapp() {

        final String urlFb = "https://chat.whatsapp.com/FqqlImKRf7yGSuZQtv60t8";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://chat.whatsapp.com/FqqlImKRf7yGSuZQtv60t8";
            intent.setData(Uri.parse(urlBrowser));
        }

        startActivity(intent);
    }

    public void openGPlus(String profile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", profile);
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"+profile+"/posts")));
        }
    }

    public void openTwtr(String twtrName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("twitter://user?screen_name=" + twtrName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/#!/" + twtrName)));
        }
    }

    protected void openLinkedIn(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://emergencymedicinekenya"));
        final PackageManager packageManager = getApplicationContext().getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ke.linkedin.com/in/emergencymedicinekenya"));
        }
        startActivity(intent);
    }


    protected void startYoutube(){
        Intent appIntent = new Intent(Intent.ACTION_VIEW);
        appIntent.setData(Uri.parse("https://www.youtube.com/channel/UCnA7dXBzXr42nB1kcEy2Alg"));
        appIntent.setPackage("com.google.android.youtube");

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/channel/UCnA7dXBzXr42nB1kcEy2Alg"));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

}