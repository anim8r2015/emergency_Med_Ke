package main.emfk.com.emfklatest;
/*u: emergen1
  p: ei863t3YNq
*/

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import main.emfk.com.emfklatest.Activity.AboutEMFActivity;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;

public class SavedPostsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private PostsDBAdapter mDbHelper;
    private Boolean flag = false;

    private List<Posts> postsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setLogo(R.drawable.enlarged_emf);
        }

        SearchView sv = (SearchView)findViewById(R.id.search);
        traverseView(sv,0);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(),HomeActivity.class);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                finish();

            }
        });

        sv.setSubmitButtonEnabled(true);
        sv.setIconified(false);
        sv.setOnQueryTextListener(this);
        sv.setQueryHint("Type here to search...");

        Drawable bg = DrawableCompat.wrap(toolbar.getOverflowIcon());
        Drawable bg2 = DrawableCompat.wrap(ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_save_white_24dp, null));

        DrawableCompat.setTint(bg, ContextCompat.getColor(getApplicationContext(),R.color.colorRed));
        DrawableCompat.setTint(bg2, ContextCompat.getColor(getApplicationContext(),R.color.colorRed));
        recyclerView = (RecyclerView) findViewById(R.id.rvPostList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
        Cursor cursor = mDbHelper.searchAllSavedPosts();
        if(cursor.getCount()==0) {
            Toast.makeText(getApplicationContext(),"There are currently no saved PostsFrag!",Toast.LENGTH_LONG).show();
        }
        populatePostsData();
        pAdapter = new PostsAdapter(postsList, getApplicationContext());
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
            startActivity(ints);
            return true;
        } else if (id == R.id.website) {
            Intent ints = new Intent(this, AboutEMFActivity.class);
            startActivity(ints);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextChange(String query) {
        try {
            pAdapter.filter(query);
        } catch (Exception e){
           return false;
        }

        return false;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
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

        Cursor cursor = mDbHelper.searchAllSavedPosts();

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
                    Log.d("READMORE","Read More " + cursor.getString(cursor.getColumnIndexOrThrow("read_more_url")));
                    p.setSearch_params(cursor.getString(cursor.getColumnIndexOrThrow("searchData")));
                    p.setPost_saved(cursor.getString(cursor.getColumnIndexOrThrow("postSaved")));
                    p.setPostTable(cursor.getString(cursor.getColumnIndexOrThrow("tableName")));
                    //postSaved
                    postsList.add(p);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            //((ImageView) view).setImageResource(R.drawable.emf_logo);

            ((ImageView) view).setColorFilter(Color.argb(255, 255, 0, 0));

            Log.d("IMGID","Checkout " + ((ImageView) view).getId() + " i is " + index);
            Log.d("IMGSV", "NAME " + ((ImageView) view).getResources().getResourceName(((ImageView) view).getId())  + " i is " + index);
        } else {
            Log.v("View Scout", "Undefined view type here...");
        }
    }

}