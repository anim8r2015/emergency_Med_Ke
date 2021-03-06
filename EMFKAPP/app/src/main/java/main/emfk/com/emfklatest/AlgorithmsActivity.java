package main.emfk.com.emfklatest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import main.emfk.com.emfklatest.Helper.AlgoFilesAdapter;
import main.emfk.com.emfklatest.Helper.PostsDBAdapter;

import static android.graphics.Typeface.BOLD;

public class AlgorithmsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private PostsDBAdapter mDbHelper;

    private List<Algorithms> algoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlgoFilesAdapter fAdapter;
    private Boolean flag = false;
    private String algoType = null;
    //private ImageView ruralBtn;
    private AppCompatButton covidAlgorithms, ruralBtn;
    MaterialSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algos_listed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        recyclerView = (RecyclerView) findViewById(R.id.rvAlgosList);
        ruralBtn = findViewById(R.id.ruralsettingbtn);
        covidAlgorithms = findViewById(R.id.covidAlgorithms);
        //covidAlgorithms.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD_ITALIC);
        getSupportActionBar().setTitle("Emergency Care Algorithms");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String suggestion = searchView.getSuggestionAtPosition(position);
                searchView.setQuery(suggestion, true);
                fAdapter.filter(suggestion);
                if(fAdapter.getItemCount()==0){
                    fAdapter.filter("");
                    Toast.makeText(getApplicationContext(),"No search results found!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        searchView.adjustTintAlpha(0.8f);

        View.OnClickListener imageClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                algoType = "rcLimited";
                Intent ints = new Intent(getApplicationContext(), AlgorithmsRuralActivity.class);
                //ints.putExtra("emfTitle","rcLimited");
                startActivity(ints);
            }
        };

        View.OnClickListener covidClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                algoType = "covidAlgos";
                Intent ints = new Intent(getApplicationContext(), CovidAlgorithmsActivity.class);
                //ints.putExtra("emfTitle","rcLimited");
                startActivity(ints);
            }
        };

        ruralBtn.setOnClickListener(imageClick);
        covidAlgorithms.setOnClickListener(covidClick);

        EMFUtilities ut = new EMFUtilities(getApplicationContext());
        if(ut.isNetworkAvailable()){
            flag = true;
        } else {
            flag = false;
        }

        //populate map data
        populatePostsData();
        fAdapter = new AlgoFilesAdapter(algoList, getApplicationContext());
        recyclerView.setAdapter(fAdapter);

    }

    public boolean onQueryTextChange(String newText) {
        fAdapter.filter(newText);

        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        fAdapter.filter(query);
        return false;
    }

    @Override
    public boolean onClose() {
        fAdapter.filter(null);
        return false;
    }

    private void populatePostsData(){
        EMFUtilities ut = new EMFUtilities(getApplicationContext());
        //determine what csv to load when resource limited or resource unlimited
        algoList = ut.read("algos_mapped.csv");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar item clicks here. It'll
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.seach:
                // Open the search view on the menu item click.

                searchView.openSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isOpen()) {
            // Close the search on the back button press.
            searchView.closeSearch();
        } else {
            if (fAdapter.getItemCount() < algoList.size()){
                fAdapter.filter("");
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchView.activityResumed();
        //String[] arr = getResources().getStringArray(R.array.algos_normal);
        fAdapter.filter("");
        String[] arr = new String[algoList.size()];

        for(int i=0; i<algoList.size(); i++){
            arr[i] = algoList.get(i).getAlgorithm();
        }
        searchView.addSuggestions(arr);
    }

    public void clearHist(MenuItem item) {

    }

    public void bleh(MenuItem item) {

        searchView.openSearch();
    }
}