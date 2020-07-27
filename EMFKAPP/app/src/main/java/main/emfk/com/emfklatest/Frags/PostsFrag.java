package main.emfk.com.emfklatest.Frags;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import main.emfk.com.emfklatest.DownloadReceiver;
import main.emfk.com.emfklatest.EMFUtilities;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;
import main.emfk.com.emfklatest.Posts;
import main.emfk.com.emfklatest.PostsAdapter;
import main.emfk.com.emfklatest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostsFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener, IOnBackPressed {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


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

    private Boolean flag = false;
    JSONObject jsonObj = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Posts> postsList = new ArrayList<>();
    private static RecyclerView recyclerView;
    private static PostsAdapter pAdapter;

    JSONArray posts = null;
    Posts[] ps;
    private String myJSON = null;

    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PostsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFrag newInstance(String param1, String param2) {
        PostsFrag fragment = new PostsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        recyclerView = (RecyclerView) view.findViewById(R.id.rvPostList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout.setOnRefreshListener(this);
        mDbHelper = new PostsDBAdapter(context);
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
        EMFUtilities ut = new EMFUtilities(getActivity().getApplicationContext());
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
        //pAdapter.setHasStableIds(true);
        recyclerView.setAdapter(pAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void loadData(String newText){
        if(pAdapter!=null) {
            pAdapter.filter(newText);
            recyclerView.setAdapter(pAdapter);
        }
    }

    public List<Posts> getPostSuggestions(){
        return postsList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void fetchData() {

        class GetJSonData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpGet httppost = new HttpGet("https://www.emergencymedicinekenya.org/?json=get_recent_posts&count=20");
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

                if(EMFUtilities.validateString(videoUrl) && !id.equalsIgnoreCase("6409")){
                    readMore = videoUrl;
                    imageSrc = new EMFUtilities(getActivity().getApplicationContext()).getImgUrlByType(videoUrl);

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
            if(swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }

    private void populatePostsData(){

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
                    //Log.d("THECONTENT","Content " + cursor.getString(cursor.getColumnIndexOrThrow("content_sanitized")));
                    p.setSearch_params(cursor.getString(cursor.getColumnIndexOrThrow("searchData")));
                    p.setPost_saved(cursor.getString(cursor.getColumnIndexOrThrow("postSaved")));

                    postsList.add(p);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                pAdapter = new PostsAdapter(postsList, getActivity().getApplicationContext(), FTS_VIRTUAL_TABLE);
                pAdapter.notifyDataSetChanged();
                pAdapter.setHasStableIds(true);
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

    @Override
    public boolean onBackPressed() {
        /*if (mPopupWindow.isShowing()) {
            //action not popBackStack
            mPopupWindow.dismiss();
            return true;
        } else {
           return false;
        }*/
        return true;

    }
}
