package main.emfk.com.emfklatest.Frags;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import main.emfk.com.emfklatest.Algorithms;
import main.emfk.com.emfklatest.AlgorithmsRuralActivity;
import main.emfk.com.emfklatest.EMFUtilities;
import main.emfk.com.emfklatest.Helper.AlgoFilesAdapter;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;
import main.emfk.com.emfklatest.R;

//import static br.com.mauker.materialsearchview.R.array.suggestions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlgorithmsFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlgorithmsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlgorithmsFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mSuggestionsListView;

    private PostsDBAdapter mDbHelper;

    private List<Algorithms> algoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlgoFilesAdapter fAdapter;
    private Boolean flag = false;
    private String algoType = null;
    private ImageView ruralBtn;
    MaterialSearchView searchView;
    private OnFragmentInteractionListener mListener;

    public AlgorithmsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlgorithmsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AlgorithmsFrag newInstance(String param1, String param2) {
        AlgorithmsFrag fragment = new AlgorithmsFrag();
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
        View view = inflater.inflate(R.layout.fragment_algorithms, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rvAlgosList);
        ruralBtn = (ImageView) view.findViewById(R.id.ruralsetting);
        searchView = (MaterialSearchView) view.findViewById(R.id.search_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Passionate from miles awat", Toast.LENGTH_SHORT).show();
                Log.d("TSTCLK","This is a test!!");
                String suggestion = searchView.getSuggestionAtPosition(position);

                searchView.setQuery(suggestion, true);
            }
        });

        searchView.adjustTintAlpha(0.8f);

        Intent intentExtra;
        intentExtra = getActivity().getIntent();

        //algoType = (String)intentExtra.getExtras().get("emfTitle");


        EMFUtilities ut = new EMFUtilities(getActivity().getApplicationContext());
        if(ut.isNetworkAvailable()){
            flag = true;
        } else {
            flag = false;
        }

        View.OnClickListener imageClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                algoType = "rcLimited";
                Intent ints = new Intent(getActivity().getApplicationContext(), AlgorithmsRuralActivity.class);
                ints.putExtra("emfTitle","rcLimited");
                startActivity(ints);
            }
        };

        ruralBtn.setOnClickListener(imageClick);
        //populate map data
        populatePostsData();
        fAdapter = new AlgoFilesAdapter(algoList, getActivity().getApplicationContext());
        recyclerView.setAdapter(fAdapter);

        return view;
    }

    private void populatePostsData(){
        EMFUtilities ut = new EMFUtilities(getActivity().getApplicationContext());
        algoList = ut.read("algos_mapped.csv");
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

    private void showSuggestions() {
        mSuggestionsListView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
