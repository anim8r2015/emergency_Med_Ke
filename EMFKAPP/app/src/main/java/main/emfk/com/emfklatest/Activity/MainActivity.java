package main.emfk.com.emfklatest.Activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import br.com.mauker.materialsearchview.db.HistoryContract;
import main.emfk.com.emfklatest.DonateWebActivity;
import main.emfk.com.emfklatest.DownloadsService;
import main.emfk.com.emfklatest.EMFContactActivity;
import main.emfk.com.emfklatest.EMFWebsiteActivity;
import main.emfk.com.emfklatest.Frags.EMSFrag;
import main.emfk.com.emfklatest.Frags.HomeFrag;
import main.emfk.com.emfklatest.Frags.SavedPosts;
import main.emfk.com.emfklatest.Frags.PostsFrag;
import main.emfk.com.emfklatest.Frags.VideosFrag;
import main.emfk.com.emfklatest.Frags.WebFrag;
import main.emfk.com.emfklatest.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    // urls to load navigation header background image
    // and profile image

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_POSTS = "posts";
    private static final String TAG_SAVD = "saved_stuff";
    private static final String TAG_WEBSITE = "website";
    private static final String TAG_HOME_FRAG = "home";

    public static String CURRENT_TAG = TAG_HOME_FRAG;

    public static String FACEBOOK_URL = "https://www.facebook.com/emergencymedkenya/";
    public static String FACEBOOK_PAGE_ID = "emergencymedkenya";

    MaterialSearchView searchView;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private View mVoice;

    private ImageView fbIconImg, twIconImg, gpIconImg, linkediniconImg, ytbiconImg,
            btnWhatsapp, btnInstagram;
    private PopupWindow mPopupWindow;
    private FrameLayout frameLayout;

    private AppCompatButton btnDonate;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        if (Build.VERSION.SDK_INT >= 23) {
            permissions();
        }

        mVoice = findViewById(br.com.mauker.materialsearchview.R.id.action_voice);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //fab = (FloatingActionButton) findViewById(R.id.fab);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        frameLayout = (FrameLayout) findViewById(R.id.frame);
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);

        activityTitles = getResources().getStringArray(R.array.navtitles);

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Item " + position, Toast.LENGTH_SHORT).show();
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query){
                Log.d("SRCHTEXT","Post frag 1 " + navItemIndex);
                switch (navItemIndex) {

                    case 1:
                        PostsFrag.loadData(query);
                        break;

                    case 2:
                        SavedPosts.loadData(query);
                        break;
                    default:
                        PostsFrag.loadData(query);
                        break;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                Log.d("SRCHTEXT2"," frag " + navItemIndex);
                switch (navItemIndex) {
                    case 1:
                        PostsFrag.loadData(newText);
                        break;
                    case 2:
                        SavedPosts.loadData(newText);
                        break;
                    default:
                        PostsFrag.loadData(newText);
                        break;
                }
                return false;
            }
        });


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME_FRAG;
            loadHomeFragment();
        }

        mVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyDownloads();
            }
        });

        startService(new Intent(this, DownloadsService.class));
    }

    private void onMyDownloads() {

        SavedPosts fragment = new SavedPosts();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.commitAllowingStateLoss();

        getSupportActionBar().setTitle("Saved Posts");

        navigationView.getMenu().getItem(4).setChecked(true);

        if (searchView.isOpen()) {
            // Close the search on the back button press.
            searchView.closeSearch();
        }
        drawer.closeDrawers();
        //toggleFab();
    }

    private void loadNavHeader() {
        txtName.setText(" ");
        txtWebsite.setText(" ");
        imgNavHeaderBg.setImageResource(R.drawable.emfk_logo_big);
    }

    private void loadHomeFragment() {

        selectNavMenu();
        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            //toggleFab();
            return;
        }


        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //toggleFab();

        drawer.closeDrawers();

        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {

        //TODO Arrange in specific order
        switch (navItemIndex) {
            case 0:
                //This will be the home activity
                HomeFrag homeFrag = new HomeFrag();
                return homeFrag;
            case 1:
                PostsFrag postsfrag = new PostsFrag();
                return postsfrag;
            case 2:
                SavedPosts savedPosts = new SavedPosts();
                return savedPosts;
            case 5:
                //consider
                WebFrag webFrag = new WebFrag();
                return webFrag;
            default:
                return new HomeFrag();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    //Home Frag
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME_FRAG;
                        break;
                        //View Posts
                    case R.id.nav_view_posts:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_POSTS;
                        break;
                        //saved posts
                    case R.id.mydownloads:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SAVD;
                        break;

                    case R.id.nav_website_link:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_WEBSITE;
                        startActivity(new Intent(MainActivity.this, EMFWebsiteActivity.class));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_youtube_channel:
                        startYoutube();
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:
                        //about us
                        startActivity(new Intent(MainActivity.this, AboutEMFActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_contact_link:
                        startActivity(new Intent(MainActivity.this, EMFContactActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_donate:
                        startActivity(new Intent(MainActivity.this, DonateWebActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        if (searchView.isOpen()) {
            searchView.closeSearch();
        }
        if (shouldLoadHomeFragOnBackPress) {

            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME_FRAG;
                loadHomeFragment();
                return;
            }
        }

        //implement dialogue dismiss if open in fragment

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_HOME_FRAG);
        if (!(fragment instanceof HomeFrag) || !((HomeFrag) fragment).onBackPressed()) {
            super.onBackPressed();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (navItemIndex == 1|| navItemIndex == 2 || navItemIndex == 4) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public void bleh(MenuItem item) {
        searchView.openSearch();
    }

    protected void sendEmail() {
        Log.i("Send email", "");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:emkf@emergencymedicinekenya.org"));
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void ShowSocialMediaDialogue(){
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.custom_layout,null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
        fbIconImg = customView.findViewById(R.id.fbIcon);
        twIconImg = customView.findViewById(R.id.twIcon);
        gpIconImg = customView.findViewById(R.id.gpIcon);
        linkediniconImg = customView.findViewById(R.id.linkedinicon);
        ytbiconImg = customView.findViewById(R.id.ytbicon);
        btnWhatsapp = customView.findViewById(R.id.icWhatsapp);
        btnInstagram = customView.findViewById(R.id.icinstagram);
        //btnTelegram;

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

        btnInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
                openInstagramAccount();
            }
        });

        btnWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement whatsapp here
                mPopupWindow.dismiss();
                launchWhatsapp();
            }
        });


        mPopupWindow.setAnimationStyle(R.style.Animation);
        mPopupWindow.showAtLocation(frameLayout, Gravity.CENTER,0,0);
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

    public void openInstagramAccount() {
        Uri uri = Uri.parse("http://instagram.com/_u/emkfoundation");
        Intent insta = new Intent(Intent.ACTION_VIEW, uri);
        insta.setPackage("com.instagram.android");

        if (isIntentAvailable(this, insta)){
            startActivity(insta);
        } else{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/emkfoundation")));
        }
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public void permissions(){

        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("We Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("We Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE,true);
            editor.commit();

        } else {
           //TODO
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    public void clearHist(MenuItem item) {
        clearHistory();
    }

    public synchronized void clearHistory() {
        getApplicationContext().getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                new String[]{"1"}
        );

        if (searchView.isOpen()) {
            // Close the sV
            searchView.closeSearch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.activityResumed();
        String[] arr = null;
        switch (navItemIndex) {
            case 0:
                //PostsFrag
                Log.d("SRCASE","Search case "+ navItemIndex);
                arr = new String[new PostsFrag().getPostSuggestions().size()];
                for(int i=0; i<new PostsFrag().getPostSuggestions().size(); i++){
                    arr[i] = new PostsFrag().getPostSuggestions().get(i).getTitle();
                }
                break;
            case 2:
                //VideosFrag

                arr = new String[new VideosFrag().getPostSuggestions().size()];
                for(int i=0; i<new VideosFrag().getPostSuggestions().size(); i++){
                    arr[i] = new VideosFrag().getPostSuggestions().get(i).getTitle();
                }
                break;
            case 3:
                //EMSFrag

                arr = new String[new EMSFrag().getPostSuggestions().size()];
                for(int i=0; i<new EMSFrag().getPostSuggestions().size(); i++){
                    arr[i] = new EMSFrag().getPostSuggestions().get(i).getTitle();
                }
                break;
            case 4:
                //SavedPosts
                Log.d("SRCASE","Search case "+ navItemIndex);
                arr = new String[new SavedPosts().getPostSuggestions().size()];
                for(int i=0; i<new SavedPosts().getPostSuggestions().size(); i++){
                    arr[i] = new SavedPosts().getPostSuggestions().get(i).getTitle();
                }
                break;
            default:

                arr = new String[new PostsFrag().getPostSuggestions().size()];
                for(int i=0; i<new PostsFrag().getPostSuggestions().size(); i++){
                    arr[i] = new PostsFrag().getPostSuggestions().get(i).getTitle();
                }
                break;
        }

        searchView.addSuggestions(arr);
    }

}
