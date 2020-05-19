package main.emfk.com.emfklatest.Helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import main.emfk.com.emfklatest.R;

/**
 * Created by anim8r on 17/04/2016.
 */
public class PostsCustomAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;
    private Typeface robotoBold, robotoLight, robotoCondensed, robotoMedium, robotoRegular,
            robotoThin;

    public PostsCustomAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        robotoBold = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_bold.ttf");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
        robotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf");
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        robotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf");
        robotoCondensed  = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_condensed.ttf");

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.list_itemlist, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder;
        String url = null;
        String topic = null;

        if (cursor != null){
            url =  cursor.getString(cursor.getColumnIndexOrThrow("img_url_web"));
            topic =  cursor.getString(cursor.getColumnIndexOrThrow("title"));
            Log.d("GOT TOPIC AND URL ","url " + url + " topic " + topic);
        }

        //  if (view == null) {

        holder = new ViewHolder();
        holder.image = (ImageView) view.findViewById(R.id.imgView);
        //holder.text = (TextView) view.findViewById(R.id.address);
        //holder.textId = (TextView) view.findViewById(R.id.id);
        //holder.textTitle = (TextView) view.findViewById(R.id.name);
        view.setTag(holder);
        // } else {
        //     holder = (ViewHolder)view.getTag();
        // }

        //holder.textId.setText(topic);
        holder.textTitle.setText(topic);
        holder.textTitle.setTypeface(robotoBold);

        //Picasso Baby
        Picasso.with(context)
                .load(url)
                //.placeholder(R.drawable.placeholder1)
                .noFade()
                .resize(200,150)
                .tag(context)
                .into(holder.image);
    }

    static class ViewHolder {
        ImageView image;
        TextView text, textId, textTitle;
    }
}