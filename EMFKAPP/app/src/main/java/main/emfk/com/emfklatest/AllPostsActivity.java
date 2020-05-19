package main.emfk.com.emfklatest;
/*u: emergen1
  p: ei863t3YNq
*/

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import main.emfk.com.emfklatest.Activity.AboutEMFActivity;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;

public class AllPostsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener, SwipeRefreshLayout.OnRefreshListener {
    private PostsDBAdapter mDbHelper;
    private static final String TAG_RESULTS="posts";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT ="content";
    private static final String TAG_URL ="url";
    private static final String TAG_SLUG = "slug";
    private static final String TAG_POST_DATE = "date";
    private static final String TAG_POST_TAG = "tags";
    private static final String TAG_ATTACH_TAG = "attachments";
    //db table constants
    private static final String FTS_VIRTUAL_TABLE = "PostsInfo";
    private static final String FTS_EMS_TABLE = "EmsPostsInfo";

    private Boolean flag = false;
    JSONObject jsonObj = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Posts> postsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter pAdapter;
    private ImageView savedPosts;

    JSONArray posts = null;
    Posts[] ps;
    private String myJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setLogo(R.drawable.enlarged_emf);
        }

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(),HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                finish();

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        savedPosts = (ImageView)findViewById(R.id.imgSavedPosts);
        SearchView sv = (SearchView)findViewById(R.id.search);
        traverseView(sv,0);

        sv.setIconifiedByDefault(false);
        sv.setOnQueryTextListener(this);
        sv.setQueryHint("Search...");


        Drawable bg = DrawableCompat.wrap(toolbar.getOverflowIcon());
        Drawable bg2 = DrawableCompat.wrap(ResourcesCompat.getDrawable(getResources(), R.drawable.red_folder, null));

        DrawableCompat.setTint(bg, ContextCompat.getColor(getApplicationContext(),R.color.colorRed));

        recyclerView = (RecyclerView) findViewById(R.id.rvPostList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        sv.setMaxWidth(getScreenWidth()-bg2.getIntrinsicWidth()-50);

        View.OnClickListener imageClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent savedImages = new Intent(getApplicationContext(),SavedPostsActivity.class);

                startActivity(savedImages);
            }
        };

        savedPosts.setOnClickListener(imageClick);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorRed));
        swipeRefreshLayout.setOnRefreshListener(this);
        mDbHelper = new PostsDBAdapter(this);
        mDbHelper.open();
        int tblCount = 0;
        try{
            tblCount = mDbHelper.getPostCount();
        }   catch (Exception e){
            tblCount = 0;
            if(e.getMessage().contains("no such table")){
                mDbHelper.createTable();
            }
        }
        EMFUtilities ut = new EMFUtilities(getApplicationContext());
        if(ut.isNetworkAvailable()){
            flag = true;
        } else {
            if( tblCount > 0) {
                flag = false;
            } else {
                flag = true;
            }
        }
        populatePostsData();
        recyclerView.setAdapter(pAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.about) {
            Intent ints = new Intent(this, AboutEMFActivity.class);
            ints.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ints);
            finish();
            return true;
        } else if (id == R.id.website) {
            Intent ints = new Intent(this, EMFWebsiteActivity.class);
            ints.putExtra("webUrl", "http://www.emergencymedicinekenya.org/");
            ints.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ints);
            finish();
        }
        /*
            return true;
        } else if (id == R.id.ems) {
            Intent ints = new Intent(this, EMSPostsActivity.class);
            //ints.putExtra("webUrl","http://www.emergencymedicinekenya.org/");
            startActivity(ints);

            return true;
        } else if (id == R.id.algorithms) {
            Intent ints = new Intent(this, AlgorithmsActivity.class);
            ints.putExtra("emfTitle","");
            startActivity(ints);

            return true;
        } else if (id == R.id.videos) {
            Intent ints = new Intent(this, VideoPostsActivity.class);
            ints.putExtra("emfTitle","");
            startActivity(ints);

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void fetchData() {

        class GetJSonData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("http://www.emergencymedicinekenya.org/?json=get_recent_posts&count=20");
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                }
                finally {
                    try{
                        if(inputStream != null)
                            inputStream.close();
                    }catch(Exception squish)
                    {

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                //Log.d("POSTEXEC","Post execute ..... " + result);
                myJSON=result;
                populateDB();
            }
        }

        GetJSonData g = new GetJSonData();
        g.execute();

    }

    private void populateDB() {

         try {

            jsonObj = new JSONObject(myJSON);
            posts = jsonObj.getJSONArray(TAG_RESULTS);
            //ps = new PostsFrag[posts.length()];

            for(int i=0;i<posts.length();i++){

                JSONObject c = posts.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String tags = c.getString(TAG_POST_TAG);
                String title = EMFUtilities.sanitizeString(c.getString(TAG_TITLE));
                String content = c.getString(TAG_CONTENT);
                String tagArray = EMFUtilities.buildTags(tags);
                String slug = c.getString(TAG_SLUG);
                String attach = c.getString(TAG_ATTACH_TAG);
                String contentSanitized = EMFUtilities.sanitizeString(content);

                String videoUrl = EMFUtilities.searchVideoUrl(contentSanitized);
                Log.d("VIDURL","Vid Url " + videoUrl);
                contentSanitized = EMFUtilities.removeShareStuff(contentSanitized);
                String readMore = EMFUtilities.readMoreLink(contentSanitized);
                String imageSrc = EMFUtilities.getImgUrl(content);

                if(EMFUtilities.validateString(videoUrl) && !id.equalsIgnoreCase("6409")){
                    readMore = videoUrl;
                    imageSrc = new EMFUtilities(getApplicationContext()).getImgUrlByType(videoUrl);
                }
                contentSanitized = EMFUtilities.removeShareStuff(contentSanitized);

                //Log.d("PLINK","The plink is " + readMore);
                String url = c.getString(TAG_URL);
                String postDate = c.getString(TAG_POST_DATE);
                if(id.equalsIgnoreCase("6409")) {
                    readMore = url;
                }


                Log.d("CHECKEXST", "CHECKEXST is " + mDbHelper.checkPostExistsCount(id) + " ID is " + id);
                if (mDbHelper.checkPostExistsCount(id) == 0) {
                    mDbHelper.createPost(id, title, tagArray, url, slug, content, contentSanitized, postDate,
                            imageSrc, attach, readMore);
                }
            }

            populatePostsData();

        } catch (JSONException e) {
             //pAdapter = new PostsAdapter(postsList, getApplicationContext(), FTS_VIRTUAL_TABLE);
            e.printStackTrace();

        } catch (Exception e) {
             //pAdapter = new PostsAdapter(postsList, getApplicationContext(), FTS_VIRTUAL_TABLE);
            e.printStackTrace();
        } finally {
            if(swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false); //hide mini loading indicator
            }
        }

    }

    @Override
    public boolean onQueryTextChange(String query) {
        try{
            pAdapter.filter(query);
        } catch (Exception e){
            return false;
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        //Toast.makeText(getApplicationContext(),"This is the app!!",Toast.LENGTH_SHORT).show();
        try{
            pAdapter.filter(query);
        } catch (Exception e){
            return false;
        }
        return false;
    }

    @Override
    public boolean onClose() {
        pAdapter.filter(null);
        return false;
    }

    private void populatePostsData(){

        //Cursor cursor = mDbHelper.searchAllCategPosts();
        Cursor cursor = mDbHelper.searchAllPosts();

        if (cursor.moveToFirst()) {

            postsList.clear();
            try {
                while (!cursor.isAfterLast()) {
                    Posts p = new Posts();
                    p.setPostid(cursor.getString(cursor.getColumnIndexOrThrow("post_id")));
                    p.setImg_url_web(cursor.getString(cursor.getColumnIndexOrThrow("img_url_web")));
                    p.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                    p.setContent_sanitized(cursor.getString(cursor.getColumnIndexOrThrow("content_sanitized")));
                    p.setPost_date(cursor.getString(cursor.getColumnIndexOrThrow("post_date")));
                    p.setPost_url(cursor.getString(cursor.getColumnIndexOrThrow("linkurl")));
                    p.setRead_more_link(cursor.getString(cursor.getColumnIndexOrThrow("read_more_url")));
                    Log.d("THECONTENT","Content " + cursor.getString(cursor.getColumnIndexOrThrow("content_sanitized")));
                    p.setSearch_params(cursor.getString(cursor.getColumnIndexOrThrow("searchData")));
                    p.setPost_saved(cursor.getString(cursor.getColumnIndexOrThrow("postSaved")));

                    postsList.add(p);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pAdapter = new PostsAdapter(postsList, getApplicationContext(), FTS_VIRTUAL_TABLE);
                pAdapter.notifyDataSetChanged();
            }

        }
    }

    private void traverseView(View view, int index) {
        if (view instanceof SearchView) {
            SearchView v = (SearchView) view;
            for(int i = 0; i < v.getChildCount(); i++) {
                traverseView(v.getChildAt(i), i);
            }
        } else if (view instanceof LinearLayout) {
            LinearLayout ll = (LinearLayout) view;
            for(int i = 0; i < ll.getChildCount(); i++) {
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        fetchData();
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

}