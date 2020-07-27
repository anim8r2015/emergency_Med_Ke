package main.emfk.com.emfklatest;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anim8r on 16/04/2016.
 */
public class EMFUtilities {
    Context ctx;
    String picUrl;
    String picData, picResultUrl;
    String vidimgUrl = null;

    JSONObject jsonObj = null;

    private List<Posts> postsList = new ArrayList<>();
    JSONArray pics = null;
    Posts[] ps;
    private String myJSON = null;

    public EMFUtilities(Context ctx) {
        this.ctx = ctx;
    }

    public static String sanitizeString(String str) {
        String newString = null;
        if (str != null) {
            if (str.contains("&#038;")) {
                str = str.replace("&#038;", "&");
            }
            if (str.contains("&#8211;")) {
                str = str.replace("&#8211;", "-");
            }
            //&#8230;
            if (str.contains("&#8230;")) {
                str = str.replace("&#8230;", "...");
            }
            //&#8217
            if (str.contains("&#8217;")) {
                str = str.replace("&#8217;", "'");
            }
            newString = str.replaceAll("(?i)read more", "");
        } else {
            newString = "";
        }

        return newString;
    }

    public static String getImgUrl(String str) {
        String newString = null;
        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Article Title</title>"
                + "</head>"
                + "<body>"
                + str
                + "</body>"
                + "</html>";

        try {
            Document html = Jsoup.parse(HTMLSTring);

            //String p = html.body().getElementsByTag("p").text();
            //Elements allElements
            Element image = html.select("img").first();
            //Element imageLst = html.select("img").last();
            String imgSrc = null;
            String url = image.attr("abs:src");

            if (imgSrc == null) {
                newString = url;
            } else {
                newString = imgSrc;
            }
            Log.d("SRC","The image source is " + newString);
        } catch (Exception e) {
            //Toast.makeText(this,"Error occured while parsing html!", Toast.LENGTH_LONG);
            e.printStackTrace();
        }

        return newString;
    }

    public static String getImgUrls(String str) {
        String newString = null;
        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Article Title</title>"
                + "</head>"
                + "<body>"
                + str
                + "</body>"
                + "</html>";

        try {
            Document html = Jsoup.parse(HTMLSTring);
            Elements images = html.select("img[src~=(?i)\\.(png|jpe?g|gif)]");

            for (Element image : images) {
                Log.d("SRC","src : " + image.attr("src"));
                Log.d("HGT","height : "+image.attr("height"));
                Log.d("WDT","width : "+image.attr("width"));
                Log.d("ALT","alt : "+image.attr("alt"));
            }
        } catch (Exception e) {
            //Toast.makeText(this,"Error occured while parsing html!", Toast.LENGTH_LONG);
            e.printStackTrace();
        }

        return newString;
    }

    public static String buildTags(String tags) {
        String tgs = null;

        final String TAG_TITLE = "title";

        JSONArray tagArray = null;

        try {
            tagArray = new JSONArray(tags);
            for (int i = 0; i < tagArray.length(); i++) {

                JSONObject c = tagArray.getJSONObject(i);
                String tag_title = c.getString(TAG_TITLE);
                tgs = tgs + " " + tag_title;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tgs;
    }

    public static String getImageInfo(String htmlInput){
        String urls = null;
        Document html = Jsoup.parse(htmlInput);
        Elements images = html.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
        for (Element image : images) {
            Log.d("SRC","src : " + image.attr("src"));
            Log.d("HGT","height : "+image.attr("height"));
            Log.d("WDT","width : "+image.attr("width"));
            Log.d("ALT","alt : "+image.attr("alt"));
        }
        return urls;
    }

    public static String readMoreLink2(String str) {
        String newString = null;
        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Article Title</title>"
                + "</head>"
                + "<body>"
                + str
                + "</body>"
                + "</html>";

        try {
            Document html = Jsoup.parse(HTMLSTring);
            Element anchor = html.select("a").last();
            String aSrc = null; //image.absUrl("src");
            String url = anchor.attr("abs:href");

            if (aSrc == null) {
                newString = url;
            } else {
                newString = "";
            }
        } catch (Exception e) {
            //Toast.makeText(this,"Error occured while parsing html!", Toast.LENGTH_LONG);
            e.printStackTrace();
        }

        return newString;
    }

    public static String parseDate(String somedate) {

        Date date;
        date = null;
        String dateString;
        SimpleDateFormat sdf1 =
                new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf4 = new SimpleDateFormat("EEE MMM dd, yyyy");

        try {
            date = sdf3.parse(somedate);
        } catch (Exception e) {

            try {
                date = sdf1.parse(somedate);
            } catch (ParseException f) {
                f.getMessage();
            }
        }
        dateString = sdf4.format(date);
        return dateString;
    }


    public static String removeImageTag(String str) {
        String newString = null;
        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Article Title</title>"
                + "</head>"
                + "<body>"
                + str
                + "</body>"
                + "</html>";

        try {
            Document html = Jsoup.parse(HTMLSTring);
            html.select("img").remove();
            String p = Jsoup.clean(html.body().html(), Whitelist.relaxed());
            newString = p;
        } catch (Exception e) {

            e.printStackTrace();
        }
        if (newString == null) {
            newString = "";
        }
        return newString;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String removeShareStuff(String str) {
        String newString = null;
        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Article Title</title>"
                + "</head>"
                + "<body>"
                + str
                + "</body>"
                + "</html>";

        try {
            Document html = Jsoup.parse(HTMLSTring);
            //html.getElementById("kpm_content_wrapper").remove();
            html.select("div.sharedaddy").first().remove();
            String p = Jsoup.clean(html.body().html(), Whitelist.relaxed());
            newString = p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (newString == null) {
            newString = "";
        }
        return newString;
    }

    public static String readMoreLink(String str) {
        String newString = null;
        String url;
        String ytb;

        String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>Article Title</title>"
                + "</head>"
                + "<body>"
                + str
                + "</body>"
                + "</html>";

        try {

            Document html = Jsoup.parse(HTMLSTring);
            //html.getElementById("kpm_content_wrapper").remove();
            //html.select("div.sharedaddy").first().remove();

            Element anchor = html.select("a").last();
            url = anchor.attr("abs:href");
            newString = url;

            ytb = null;///searchVideoUrl(HTMLSTring);

            if(ytb !=null){
                newString = ytb;
            }


            Log.d("RDMORELNK", "This " + newString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newString;
    }

    public static String checkLinkType(String str) {
        String s = null;
        str = str.trim();
        Log.d("EST","We are checking the str " + str + " str " + str.trim().length());
        if (str.trim() != null && str.trim().length()>0) {
            //check pdf
            if (str.substring(str.lastIndexOf("."), str.length()).contains("pdf")) {
                s = "pdf";
            } else if (str.substring(str.lastIndexOf("."), str.length()).contains("mp4")) {
                s = "mp4";
            } else if (str.contains("vimeo")) {
                s = "mp4";
            } else if (str.contains("youtube")) {
                s = "ytb";
            } else if (str.substring(str.lastIndexOf("."), str.length()).contains("jpg")
                    || str.substring(str.lastIndexOf("."), str.length()).contains("jpeg")
                    || str.substring(str.lastIndexOf("."), str.length()).contains("png")
                    || str.substring(str.lastIndexOf("."), str.length()).contains("gif")) {
                s = "img";
            } else {
                s = "nrml";
            }
        } else if (str.trim() != null || str.trim().length()==0) {
            s = "ukwn";
        }
        return s;
    }

    //to implement with default data when app is started offline
    public String getOfflineText() {
        String json = null;
        try {
            InputStream is = ctx.getAssets().open("posts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String searchVideoUrl(String str) {
        String videoSource = null;
        try {

            Document doc = Jsoup.parse(str);
            Element iframe = doc.select("iframe").first();
            videoSource = iframe.attr("src");
            Log.d("YTB", "Youtube: " + videoSource);
        } catch (Exception e){
            videoSource = null;
        }
        return videoSource;
    }

    public static String extractVideoId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(".*(?:youtu.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=)([^#\\&\\?]*).*");
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }
        return vId;
        //return "https://img.youtube.com/vi/"+vId +"/hqdefault.jpg";
    }

    public List<Algorithms> read(String file){
        List<Algorithms> resultList = new ArrayList();
        InputStream inputStream = null;

        try {
            inputStream = ctx.getAssets().open(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                Algorithms alms = new Algorithms();
                alms.setAlgorithm(row[0].replace("\"",""));
                alms.setAlgoritmFile(row[1].replace("\"","").trim());
                resultList.add(alms);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }


    public static boolean validateString(String str){
        //String s = null;
        if(str != null){
            if(str.trim().length() > 0){
                return true;
            }
        }
        return false;
    }

    public String fetchVimeoImage(String str) {
        picUrl = str;
         class GetJSonData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                String q = "";
                Log.d("QRY","The query " + q);
                HttpGet httppost = new HttpGet(picUrl);
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
                    while ((line = reader.readLine()) != null){
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
                    }   catch(Exception squish) {
                        squish.printStackTrace();
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                picData=result;
                EMFUtilities.this.picResultUrl = getPicUrl();
                Log.d("PICRES","Pic result " + picResultUrl);
            }

            public String getPicUrl(){

                String picUrl = null;
                Log.d("PICDATA","Pic data url " + picData);
                try {
                    pics = new JSONArray(picData);
                    for(int i=0; i < pics.length(); i++){
                        JSONObject c = pics.getJSONObject(i);
                        picUrl = c.getString("thumbnail_large");
                        vidimgUrl = picUrl;
                        Log.d("PICSUCCESS","The url is " + picUrl);
                    }

                } catch (JSONException e) {
                    Log.d("GETPICERR","Error is " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e){
                    Log.d("GETPICERR","Error is " + e.getMessage());
                    e.printStackTrace();
                }
                return picUrl;
            }
        }

        GetJSonData g = new GetJSonData();
        g.execute();
        Log.d("EXEC2","Exect 2 is here! " + g.getPicUrl());
        return g.getPicUrl();
    }

    /**
     * @param url
     * ababababababab
     * @return
     * bcsdadassda
     */
    public String getImgUrlByType(String url){

        if(url != null){
            if(url.trim().contains("youtube")){
                vidimgUrl = "https://img.youtube.com/vi/" + extractVideoId(url) +"/hqdefault.jpg";
            } else if(url.trim().contains("vimeo")) {
                url = url.substring(url.lastIndexOf("/")+1, url.length());
                Log.d("VIDID","The vimeo vid id is " + url);
                url = "http://vimeo.com/api/v2/video/" + url + ".json";
                try {
                    vidimgUrl = fetchVimeoImage(url);
                    if(vidimgUrl==null){
                        vidimgUrl = "getLocal";
                    }

                    Log.d("FNRES","Finale res " + picResultUrl);
                    Log.d("VIDFTC","Vid img " + vidimgUrl + " var res " + picResultUrl);
                } catch (Exception e) {
                    vidimgUrl = null;
                    e.printStackTrace();
                }
            }
        }
        return vidimgUrl;
    }
	//we'll stick with this
    public boolean listAssetFiles(String path) {
        Log.d("FLSSTRT","Checking!!!!");
        String [] list;
        try {
            list = ctx.getAssets().list(path);

            if (list.length > 0) {
                Log.d("FLSSTRT2","Got folders!!!!");
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                        // This is a file
                        // TODO: add file name to an array list
                        Log.d("FLSHOW","Path " + path +'\n' + "File " + file);
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public int getScreenWidth() {
        DisplayMetrics display = ctx.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        return width;
    }

    public int getScreenHeight() {
        DisplayMetrics display = ctx.getResources().getDisplayMetrics();
        int width = display.heightPixels;
        return width;
    }

    public int  getViewAspectRatio(View v){
        //aspect ratio
        return  (v.getWidth()/v.getHeight());
    }
}