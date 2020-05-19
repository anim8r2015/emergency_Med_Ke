package main.emfk.com.emfklatest;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import main.emfk.com.emfklatest.Activity.MainActivity;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;


public class SplashActivity extends AppCompatActivity {
    private PostsDBAdapter mDbHelper;
    private static final String TAG_RESULTS="posts";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT ="content";
    private static final String TAG_URL ="url";
    private static final String TAG_SLUG = "slug";
    private static final String TAG_ATTACH_TAG = "attachments";
    private static final String TAG_POST_DATE = "date";
    private static final String TAG_POST_TAG = "tags";
    private Boolean flag = false;
    JSONObject jsonObj = null;
    JSONArray posts = null;
    Posts[] ps;
    private String myJSON = null;
    private int POST_LIMIT = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDbHelper = new PostsDBAdapter(this);
        mDbHelper.open();
        int tblCount = 0;
        int tblEmsCount = 0;
        try{
            tblCount = mDbHelper.getPostCount();
        }   catch (Exception e){
            tblCount = 0;
            if(e.getMessage().contains("no such table")){
                mDbHelper.createTable();
            }
        }

        try{
            tblEmsCount = mDbHelper.getEmsPostCount();
        }   catch (Exception e){
            tblEmsCount = 0;
            if(e.getMessage().contains("no such table")){
                mDbHelper.createTable();
            }
        }

        Log.d("TBCOUNT","EMSFrag: " + tblEmsCount + " NORM: " + tblCount);

        EMFUtilities ut = new EMFUtilities(getApplicationContext());
        if(ut.isNetworkAvailable() && tblCount < 100){
            flag = true;
            //POST_LIMIT = 100;
        } else {
            if( tblCount > 0) {
                flag = false;
            } else {
                flag = true;
            }
        }

        if(flag==false) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            fetchData();
        }

    }

    private void fetchData() {

        class GetJSonData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                String q = "http://www.emergencymedicinekenya.org/?json=get_recent_posts&count=50"; //+ POST_LIMIT;
                Log.d("QRY","The query " + q);
                HttpGet httppost = new HttpGet(q);
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
                    }   catch(Exception squish)
                    {

                    }
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                Log.d("DATA","Fetched data " + myJSON);
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

            ps = new Posts[posts.length()];

            for(int i=0;i<posts.length();i++){

                JSONObject c = posts.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String tags = c.getString(TAG_POST_TAG);
                String title = EMFUtilities.sanitizeString(c.getString(TAG_TITLE));
                String content = c.getString(TAG_CONTENT);
                String tagArray = EMFUtilities.buildTags(tags);
                String slug = c.getString(TAG_SLUG);
                String contentSanitized = EMFUtilities.sanitizeString(content);
                String videoUrl = EMFUtilities.searchVideoUrl(contentSanitized);
                contentSanitized = EMFUtilities.removeShareStuff(contentSanitized);
                String readMore = EMFUtilities.readMoreLink(contentSanitized);
                String imageSrc = EMFUtilities.getImgUrl(content);
                //fetchWebData(imageSrc);

                if(EMFUtilities.validateString(videoUrl) && !id.equalsIgnoreCase("6409")){
                    readMore = videoUrl;
                    imageSrc = new EMFUtilities(getApplicationContext()).getImgUrlByType(videoUrl);

                }

                String attach = c.getString(TAG_ATTACH_TAG);
                String url = c.getString(TAG_URL);
                String postDate = c.getString(TAG_POST_DATE);

                if(id.equalsIgnoreCase("6409")) {
                    readMore = url;
                }

                Log.d("CHECKEXST", "CHECKEXST is " + mDbHelper.checkPostExistsCount(id) + " ID is " + id
                + " Title " + title);
                if (mDbHelper.checkPostExistsCount(id) == 0) {
                    mDbHelper.createPost(id, title, tagArray, url, slug, content, contentSanitized, postDate,
                            imageSrc, attach, readMore);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fetchEMSData();
        }

    }

    private void fetchEMSData() {

        class GetJSonData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("http://www.emergencymedicinekenya.org/?json=get_category_posts&slug=ems&count=50");//+POST_LIMIT);
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
                    }catch(Exception squish) {

                    }
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result){

                myJSON=result;
                populateEMSDB();
            }
        }

        GetJSonData g = new GetJSonData();
        g.execute();

    }

    private void populateEMSDB() {

        try {

            jsonObj = new JSONObject(myJSON);
            posts = jsonObj.getJSONArray(TAG_RESULTS);
            ps = new Posts[posts.length()];
            for (int i = 0; i < posts.length(); i++) {

                JSONObject c = posts.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String tags = c.getString(TAG_POST_TAG);
                String title = EMFUtilities.sanitizeString(c.getString(TAG_TITLE));
                String content = c.getString(TAG_CONTENT);
                String tagArray = EMFUtilities.buildTags(tags);
                String slug = c.getString(TAG_SLUG);
                String contentSanitized = EMFUtilities.sanitizeString(content);
                String videoUrl = EMFUtilities.searchVideoUrl(contentSanitized);
                contentSanitized = EMFUtilities.removeShareStuff(contentSanitized);
                //String testUrls = EMFUtilities.getImgUrls(contentSanitized);
                String imageSrc = EMFUtilities.getImgUrl(contentSanitized);
                String readMore = EMFUtilities.readMoreLink(contentSanitized);

                if(EMFUtilities.validateString(videoUrl) && !id.equalsIgnoreCase("6409")){
                    readMore = videoUrl;
                    imageSrc = new EMFUtilities(getApplicationContext()).getImgUrlByType(videoUrl);
                }

                String url = c.getString(TAG_URL);
                String attach = c.getString(TAG_ATTACH_TAG);
                String postDate = c.getString(TAG_POST_DATE);

                if(id.equalsIgnoreCase("6409")) {
                    readMore = url;
                }

                if (mDbHelper.checkEMSPostExistsCount(id) == 0) {
                    mDbHelper.createEMSPost(id, title, tagArray, url, slug, content, contentSanitized, postDate,
                            imageSrc, attach, readMore);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fetchVideosData();
        }
    }

    private void fetchVideosData() {

        class GetJSonData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("http://www.emergencymedicinekenya.org/?json=get_category_posts&slug=videos&count=50");//+POST_LIMIT);
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
                    }catch(Exception squish) {

                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                populateVideosDB();
            }
        }

        GetJSonData g = new GetJSonData();
        g.execute();

    }

    private void populateVideosDB() {

        try {

            jsonObj = new JSONObject(myJSON);
            posts = jsonObj.getJSONArray(TAG_RESULTS);
            ps = new Posts[posts.length()];
            for (int i = 0; i < posts.length(); i++) {

                JSONObject c = posts.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String tags = c.getString(TAG_POST_TAG);
                String title = EMFUtilities.sanitizeString(c.getString(TAG_TITLE));
                String content = c.getString(TAG_CONTENT);
                String tagArray = EMFUtilities.buildTags(tags);
                String slug = c.getString(TAG_SLUG);
                String contentSanitized = EMFUtilities.sanitizeString(content);
                String videoUrl = EMFUtilities.searchVideoUrl(contentSanitized);
                contentSanitized = EMFUtilities.removeShareStuff(contentSanitized);
                String readMore = EMFUtilities.readMoreLink(contentSanitized);
                String imageSrc = EMFUtilities.getImgUrl(content);

                if(EMFUtilities.validateString(videoUrl) && !id.equalsIgnoreCase("6409")){
                    readMore = videoUrl;
                    imageSrc = new EMFUtilities(getApplicationContext()).getImgUrlByType(videoUrl);
                }

                String url = c.getString(TAG_URL);
                String attach = c.getString(TAG_ATTACH_TAG);
                String postDate = c.getString(TAG_POST_DATE);

                if(id.equalsIgnoreCase("6409")) {
                    readMore = url;
                }

                if (mDbHelper.checkVideoPostExistsCount(id) == 0) {
                    mDbHelper.createVideosPost(id, title, tagArray, url, slug, content, contentSanitized, postDate,
                            imageSrc, attach, readMore);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Intent msgIntent = new Intent(this, DownloadsService.class);
            Log.d("DWLSTART","Download starting .......");
            msgIntent.putExtra(DownloadsService.PARAM_IN_MSG, "ServiceIsStarting..");
            startService(msgIntent);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
