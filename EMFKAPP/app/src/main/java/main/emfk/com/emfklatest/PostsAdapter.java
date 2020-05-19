package main.emfk.com.emfklatest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Filterable;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anim8r on 26/03/2016.
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> implements Filterable {

    private Typeface adampro;
    private List<Posts> postsList;
    private List<Posts> filterList;
    private String TableName = null;
    //private ImageLoader imageLoader;
    private FloatingActionButton lstmFabReadMore;

    Context context;
    //DisplayImageOptions defaultOptions;

    @Override
    public android.widget.Filter getFilter() {
        return null;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ProportionalImageView image;

        TextView textTitle, textDescription, txtPostDateTime;
        WebView wvContent, wvImageContent;


        public MyViewHolder(View view) {
            super(view);
            image = (ProportionalImageView) view.findViewById(R.id.imgView);
            textTitle = (TextView) view.findViewById(R.id.titleText);
            //textDescription = (TextView) view.findViewById(R.id.descriptionText);
            txtPostDateTime = (TextView) view.findViewById(R.id.postDateTime);
            wvContent = (WebView) view.findViewById(R.id.wvContent);
            lstmFabReadMore = (FloatingActionButton)view.findViewById(R.id.lstfabReadMore);
            wvImageContent  = (WebView) view.findViewById(R.id.wvImageContent);

            lstmFabReadMore.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fingerprint_white_36px));

        }
    }

    public PostsAdapter(List<Posts> postsList, Context context, String tableName) {
        this.postsList = postsList;
        this.context = context;
        this.TableName = tableName;
        //setHasStableIds(true);
        adampro = Typeface.createFromAsset(context.getAssets(), "fonts/adam_cg_pro.otf");
        this.filterList = new ArrayList<>();
        filterList.addAll(postsList);
    }

    public PostsAdapter(List<Posts> postsList, Context context) {
        this.postsList = postsList;
        this.context = context;
        //setHasStableIds(true);
        adampro = Typeface.createFromAsset(context.getAssets(), "fonts/adam_cg_pro.otf");
        this.filterList = new ArrayList<>();
        filterList.addAll(postsList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_itemlist, parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);

        //setting on click itemViewListener
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Posts post = filterList.get(position);

                //put get my stuff
                String titleText = "<strong>"+post.getTitle().toUpperCase()+ "</strong>";
                Spanned spTitle = Html.fromHtml(titleText);
                String newDate = EMFUtilities.parseDate(post.getPost_date());
                String htmltext2 = "<!DOCTYPE html><head>"+
                        context.getString(R.string.stylertext) +
                        //getTableCSS() +
                        "</head><body>" + EMFUtilities.removeImageTag(post.getContent_sanitized())
                        .replaceAll("READ MORE","").replaceAll("read more","").replaceAll("Read More","")+"</body></html>";
                String htmltext = EMFUtilities.removeImageTag(post.getContent_sanitized()).replace("READ MORE", "").replace("READ MORe", "");
                Spanned spDetailText = Html.fromHtml(htmltext);
                String postUrl = post.getPost_url();
                String imgUrl = post.getImg_url_web();

                if(post.getImg_url_web() != null) {

                    if (post.getImg_url_web().equalsIgnoreCase("getLocal")) {
                        imgUrl = "getLocal";
                    }

                    if (post.getImg_url_web().toLowerCase().contains("read-more.png")) {
                        imgUrl = "whiteLoad";
                    }
                } else {
                    imgUrl = "whiteLoad";
                }

                String postid = post.getPostid();
                String readMoreUrl = post.getRead_more_link();
                String postSaved = post.getPost_saved();

                Intent ints = new Intent(context,ActivityPostDetail.class);
                ints.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ints.putExtra("imageUrl",imgUrl);
                ints.putExtra("titleText",spTitle);
                ints.putExtra("descText",htmltext2);
                ints.putExtra("postTimeText",newDate);
                ints.putExtra("postUrl",postUrl);
                ints.putExtra("readMore",readMoreUrl);
                ints.putExtra("postid",postid);
                ints.putExtra("postTable",TableName == null ? post.getPostTable() : TableName);
                ints.putExtra("postSaved",postSaved);
                context.startActivity(ints);

            }
        });

        lstmFabReadMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final int position = holder.getAdapterPosition();
                Posts post = filterList.get(position);

                //put get my stuff
                String titleText = "<strong>"+post.getTitle().toUpperCase()+ "</strong>";
                Spanned spTitle = Html.fromHtml(titleText);
                String newDate = EMFUtilities.parseDate(post.getPost_date());
                String htmltext2 = "<!DOCTYPE html><head>"+
                        context.getString(R.string.stylertext) +
                        //getTableCSS() +
                        "</head><body>" + EMFUtilities.removeImageTag(post.getContent_sanitized())
                        .replaceAll("READ MORE","").replaceAll("read more","").replaceAll("Read More","")+"</body></html>";
                String htmltext = EMFUtilities.removeImageTag(post.getContent_sanitized()).replace("READ MORE", "").replace("READ MORe", "");
                Spanned spDetailText = Html.fromHtml(htmltext);
                String postUrl = post.getPost_url();
                String imgUrl = post.getImg_url_web();

                if(post.getImg_url_web() != null) {

                    if (post.getImg_url_web().equalsIgnoreCase("getLocal")) {
                        imgUrl = "getLocal";
                    }

                    if (post.getImg_url_web().toLowerCase().contains("read-more.png")) {
                        imgUrl = "whiteLoad";
                    }
                } else {
                    imgUrl = "whiteLoad";
                }

                String postid = post.getPostid();
                String readMoreUrl = post.getRead_more_link();
                String postSaved = post.getPost_saved();

                Intent ints = new Intent(context,ActivityPostDetail.class);
                ints.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ints.putExtra("imageUrl",imgUrl);
                ints.putExtra("titleText",spTitle);
                ints.putExtra("descText",htmltext2);
                ints.putExtra("postTimeText",newDate);
                ints.putExtra("postUrl",postUrl);
                ints.putExtra("readMore",readMoreUrl);
                ints.putExtra("postid",postid);
                ints.putExtra("postTable",TableName == null ? post.getPostTable() : TableName);
                ints.putExtra("postSaved",postSaved);
                context.startActivity(ints);
            }
        });
        //holder end
        return holder;
    }

    public void remove(Posts post) {
        int position = filterList.indexOf(post);
        // Avoid double tap remove
        if (position != -1) {
            filterList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public long getItemId(int position){
        //Posts post = postsList.get(position);
        return position;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Posts post = filterList.get(position);
        holder.textTitle.setTypeface(adampro);
        holder.txtPostDateTime.setTypeface(adampro);

        //process text to be displayed
        String titleText = "<strong>"+post.getTitle().toUpperCase()+ "</strong>";
        Spanned spTitle = Html.fromHtml(titleText);
        String newDate = EMFUtilities.parseDate(post.getPost_date());
        String htmltext = "<!DOCTYPE html><head>"+
                context.getString(R.string.stylertext) +
                //getTableCSS() +
                "</head><body>" + EMFUtilities.removeImageTag(post.getContent_sanitized())
                .replaceAll("READ MORE","").replaceAll("read more","").replaceAll("Read More","")+"</body></html>";
        Spanned spDetailText = Html.fromHtml(htmltext);

        //set texts to views
        holder.textTitle.setText(spTitle);
        //holder.textDescription.setText(spDetailText);
        holder.txtPostDateTime.setText(newDate);
        holder.wvContent.getSettings().setJavaScriptEnabled(true);

        holder.wvContent.getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath() );
        holder.wvContent.getSettings().setAllowFileAccess(true);
        holder.wvContent.getSettings().setAppCacheEnabled(true);
        holder.wvContent.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );



        if ( !new EMFUtilities(context).isNetworkAvailable() ) { // loading offline
            holder.wvContent.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );

        }
        holder.wvContent.loadDataWithBaseURL("file:///android_asset/",htmltext, "text/html", "utf-8", null);
        //Set Image to view
        if(post.getImg_url_web() != null){
            if( post.getImg_url_web().length() > 0){
                if(post.getImg_url_web().equalsIgnoreCase("getLocal")) {
                    holder.image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.vimeo_logo));
                } else {
                    if(post.getImg_url_web().toLowerCase().contains("read-more.png")){
                        holder.image.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.white_line));
                    } else {
                        //holder.wvImageContent.loadUrl(post.getImg_url_web());
                        String data = "<html><body><img id=\"resizeImage\" src=\""
                                +post.getImg_url_web()+"\" width=\"100%\" alt=\"\" align=\"middle\" /></body></html>";
                      //  holder.wvImageContent.loadData(data, "text/html; charset=UTF-8", null);

                        Picasso.with(context)
                                .load(post.getImg_url_web())
                                .noFade()
                                //.placeholder()
                                .tag(context).error(R.drawable.caution)
                                .into(holder.image);/*, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        //Log.d()
                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.`
                                    }
                                });*/
                        Picasso.Builder builder = new Picasso.Builder(context);
                        builder.listener(new Picasso.Listener()
                        {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                            {
                                exception.printStackTrace();
                                Log.d("PERR","Picasso error " + exception.getMessage()
                                + " Image not found is " + uri.toString());

                            }
                        });
                        builder.build().load(post.getImg_url_web()).into(holder.image);

                        /*ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                                .build();

                        imageLoader = imageLoader.getInstance();

                        if (!imageLoader.isInited()) {
                            imageLoader.init(config);
                        }
                        defaultOptions = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisc(true)
                                .build();
                        imageLoader = ImageLoader.getInstance(); // Get singleton instance
                        imageLoader.displayImage(post.getImg_url_web(), holder.image);*/
                    }
                }
            } else {
                holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.white_line));
            }
        } else {
            holder.image.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.white_line));
        }

        holder.image.setTag(holder);
        holder.textTitle.setTag(holder);
        holder.wvContent.setTag(holder);

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
            filterList.addAll(postsList);
        } else {
            // Iterate in the original List and add it to filter list...
            for (Posts item : postsList) {
                if (item.search_params.toLowerCase().contains(text.toLowerCase()) ||
                        item.search_params.toLowerCase().contains(text.toLowerCase())) {
                    // Adding Matched items
                    filterList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    /*
    private String getTableCSS(){
        String css = "<style> table, th, td { border: 1px solid black; } </style>";
        return css;
    }*/
}
