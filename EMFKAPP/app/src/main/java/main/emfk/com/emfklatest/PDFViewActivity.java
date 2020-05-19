package main.emfk.com.emfklatest;

/**
 * Created by User on 12/6/2016.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.sql.Timestamp;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class PDFViewActivity extends AppCompatActivity {
    byte data[];
    String urlStr[];
    int lenghtOfFile = 0;
    String readMoreUrl, fileName = null;
    private static String DOWNLOAD_LOC2 = null;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_viewer_activity);
        String title, pdfUrl = null;
        Intent intentExtra;
        InputStream inpt = null;
        intentExtra = getIntent();
        //data = new byte[1024];
        data = intentExtra.getByteArrayExtra("data");
        title = (String)intentExtra.getExtras().get("algoTitle");

        //this is used to hold the algorithms file names
        pdfUrl = (String)intentExtra.getExtras().get("algoSrc");
        String pdfFromUri = (String)intentExtra.getExtras().get("fromUri");
        String online = (String) intentExtra.getExtras().get("online");

        //this is used to hold the pdf source if I get it from the articles
        readMoreUrl = (String) intentExtra.getExtras().get("readMoreUrl");
        pdfView = (PDFView) findViewById(R.id.pdfView);

        if (Environment.getExternalStorageState() != null) {
            DOWNLOAD_LOC2 = Environment.getExternalStorageDirectory().toString();
        }else {
            DOWNLOAD_LOC2 = Environment.getDataDirectory().toString();
        }
        //check if the algorithm url is null
        //if it is we are reading a pdf from an online source
        if (pdfUrl == null) {
            //had an online variable from postdetails
            if (online.equalsIgnoreCase("Y")) {
                //check if we have an internet connection
                if (new EMFUtilities(getApplicationContext()).isNetworkAvailable()) {
                    new DownloadFileFromURL().execute(readMoreUrl);
                    //urlStr = readMoreUrl.split("/");
                } else {
                    Toast.makeText(getApplicationContext(), "Internet not available. Please check and try again!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            String SAMPLE_FILE = pdfUrl;
            if(SAMPLE_FILE.length() > 0) {
                try {
                    pdfView.fromAsset(SAMPLE_FILE).load();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                urlStr = f_url[0].split("/");
                String downloadFileName = urlStr[urlStr.length];
                lenghtOfFile = conection.getContentLength();
                //download file size warning 30mb
                if(lenghtOfFile > 8*1024*30){
                    //Toast.makeText(getApplicationContext(),"WARNING! File over 30 MB in size.",Toast.LENGTH_LONG).show();
                }
                Log.d("FLN","Length of file " + lenghtOfFile + " downloadFileName " + downloadFileName);
                InputStream input = new BufferedInputStream(url.openStream(), lenghtOfFile);
                // Output stream
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                fileName = DOWNLOAD_LOC2 +"/"+timestamp.getTime()  + ".pdf";
                Log.d("FLNAME",fileName);
                OutputStream output = new FileOutputStream(fileName);
                data = new byte[lenghtOfFile];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    //publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    // writing data to file
                    Log.d("DATA",total + " data");
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();

                Log.d("FLSZ","File Size: " + lenghtOfFile + " File url: " + f_url[0]
                        + " Data size: " + data.length);
            } catch (Exception e) {
                Log.e("Error downloading pdf: ", e.getMessage());
            }
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            /*pDialog.setProgress(Integer.parseInt(progress[0]));*/
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            //(progress_bar_type);

            //pDialog.dismiss();
            loadPDFInView();
        }
    }

    private void loadPDFInView(){
        try {
           // Log.d("FLNAME2",fileName);
            pdfView.fromFile(new File(fileName)).load();
        } catch(Exception ex) {
            ex.printStackTrace();
            try {
                Intent ints = new Intent(getApplicationContext(), ReadMoreActivity.class);
                ints.putExtra("readMore",readMoreUrl);
                startActivity(ints);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
