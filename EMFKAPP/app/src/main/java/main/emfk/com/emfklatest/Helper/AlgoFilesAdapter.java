package main.emfk.com.emfklatest.Helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import main.emfk.com.emfklatest.Algorithms;
import main.emfk.com.emfklatest.PDFViewActivity;
import main.emfk.com.emfklatest.R;

/**
 * Created by anim8r on 26/03/2016.
 */
public class AlgoFilesAdapter extends RecyclerView.Adapter<AlgoFilesAdapter.MyViewHolder> implements Filterable {

    private Typeface adampro;
    private List<Algorithms> algorithmsList;
    private List<Algorithms> filterList;

    Context context;

    @Override
    public android.widget.Filter getFilter() {
        return null;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;

        public MyViewHolder(View view) {
            super(view);
            textTitle = (TextView) view.findViewById(R.id.titleText);

        }
    }

    public AlgoFilesAdapter(List<Algorithms> algos, Context context) {
        this.algorithmsList = algos;
        this.context = context;
        adampro = Typeface.createFromAsset(context.getAssets(), "fonts/adam_cg_pro.otf");
        this.filterList = new ArrayList<>();
        filterList.addAll(algorithmsList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list_item, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Algorithms algorithm = filterList.get(position);

                String algoTitle = algorithm.getAlgorithm();
                String algoSrc = algorithm.getAlgoritmFile();
                //put get my stuff

                Intent ints = new Intent(context, PDFViewActivity.class);
                ints.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ints.putExtra("algoSrc",algoSrc);
                ints.putExtra("algoTitle",algoTitle);
                ints.putExtra("online","N");
                context.startActivity(ints);

            }
        });

        return holder;
    }

    public void remove(Algorithms algo) {
        int position = filterList.indexOf(algo);
        // Avoid double tap remove
        if (position != -1) {
            filterList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Algorithms algorithm = filterList.get(position);
        holder.textTitle.setTypeface(adampro);

        //process text to be displayed
        String algoTitle = algorithm.getAlgorithm();
        //set texts to views
        holder.textTitle.setText(position+1 + ". " + algoTitle);
        holder.textTitle.setTag(holder);

    }

    @Override
    public int getItemCount() {
        if(null != filterList) {
            return filterList.size();
        } else {
            return 0;
        }
    }

    public void filter(String text) {
        filterList.clear();
        // If there is no search value, then add all original list items to filter list
        if (TextUtils.isEmpty(text)) {
            filterList.addAll(algorithmsList);
        } else {
            // Iterate in the original List and add it to filter list...
            for (Algorithms item : algorithmsList) {
                if (item.getAlgorithm().toLowerCase().contains(text.toLowerCase()) ||
                        item.getAlgorithm().toLowerCase().contains(text.toLowerCase())) {
                    // Adding Matched items
                    filterList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
