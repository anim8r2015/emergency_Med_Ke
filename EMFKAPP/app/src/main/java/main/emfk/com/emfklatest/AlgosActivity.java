package main.emfk.com.emfklatest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by anim8r on 10/04/2016.
 */
public class AlgosActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    private Button rclimitedBtnInst, rcfullBtnInst;
    ImageView imgView, imgView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.algos_activity);
        //getSupportActionBar().setLogo(R.drawable.enlarged_emf);\
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setLogo(R.drawable.enlarged_emf);
        }

        rclimitedBtnInst = (Button)findViewById(R.id.rclimitedBtn);
        rcfullBtnInst = (Button)findViewById(R.id.rcfullBtn);

        imgView = (ImageView)findViewById(R.id.imgView);
        imgView2 = (ImageView)findViewById(R.id.imgView2);

        rclimitedBtnInst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resource limited
                startAlgoDetails("rcLimited");

            }
        });

        rcfullBtnInst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //non limited
                startAlgoDetails("rcNormal");
            }
        });

    }

    // convert InputStream to String
    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    private Uri getFileFromAssets()
    {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "test.pdf");
        try
        {
            in = assetManager.open("test.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();


            out = null;
        } catch (Exception e)
        {
            Log.e("URITAG", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/test.pdf"),
                "application/pdf");
        Uri path = Uri.parse(getFilesDir() + "/test.pdf");
       return path;
    }

    private void getResourceLimited()
    {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "Emergency_Care_Algorithms_for_Resource_Limited_Settings_2015.pdf");
        try
        {
            in = assetManager.open("Emergency_Care_Algorithms_for_Resource_Limited_Settings_2015.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.putExtra("emfTitle","rcLimited");
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/Emergency_Care_Algorithms_for_Resource_Limited_Settings_2015.pdf"),
                "application/pdf");

        startActivity(intent);

    }

    private void getNormal()
    {
        //Emergency_Care_Algorithms_2015
        //Emergency_Care_Algorithms_for_Resource_Limited_Settings_2015
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "Emergency_Care_Algorithms_2015.pdf");
        try
        {
            in = assetManager.open("Emergency_Care_Algorithms_2015.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("emfTitle","rcNormal");
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/Emergency_Care_Algorithms_2015.pdf"),
                "application/pdf");

        startActivity(intent);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }

    private void startAlgoDetails(String extraData){
        Intent intent = new Intent(getApplicationContext(), AlgorithmsActivity.class);
        intent.putExtra("emfTitle",extraData);
        startActivity(intent);
    }

}
