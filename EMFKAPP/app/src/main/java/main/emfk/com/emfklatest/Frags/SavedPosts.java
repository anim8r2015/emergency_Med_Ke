package main.emfk.com.emfklatest.Frags;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import main.emfk.com.emfklatest.EMFUtilities;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;
import main.emfk.com.emfklatest.Posts;
import main.emfk.com.emfklatest.PostsAdapter;
import main.emfk.com.emfklatest.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SavedPosts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SavedPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedPosts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SavedPosts() {
        // Required empty public constructor
    }

    private PostsDBAdapter mDbHelper;
    private Boolean flag = false;

    private List<Posts> postsList = new ArrayList<>();
    private static RecyclerView recyclerView;
    private static PostsAdapter pAdapter;



    // TODO: Rename and change types and number of parameters
    public static SavedPosts newInstance(String param1, String param2) {
        SavedPosts fragment = new SavedPosts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_downloads, container, false);


        recyclerView = view.findViewById(R.id.rvPostList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mDbHelper = new PostsDBAdapter(this.getActivity());
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
        Cursor cursor = mDbHelper.searchAllSavedPosts();
        if(cursor.getCount()==0) {
            Toast.makeText(getActivity().getApplicationContext(),"There are currently no saved posts!",Toast.LENGTH_LONG).show();
        }
        populatePostsData();
        pAdapter = new PostsAdapter(postsList, getActivity().getApplicationContext());
        recyclerView.setAdapter(pAdapter);

        return view;
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

    public List<Posts> getPostSuggestions(){
        return postsList;
    }

    public static void loadData(String newText){
        if(pAdapter!=null) {
            pAdapter.filter(newText);
            recyclerView.setAdapter(pAdapter);
        }
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

}
