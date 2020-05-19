package main.emfk.com.emfklatest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

public class ContactUsActivity extends AppCompatActivity {

    Button sendBtn;
    ImageView fbIconImg, twIconImg, gpIconImg, linkediniconImg, rssiconImg, ytbiconImg;
    EditText input_name, input_text_subject, input_message;
    String strMessage, strName, strSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);


        if (toolbar != null) {
            toolbar.setLogo(R.drawable.enlarged_emf);
        }

        sendBtn = (Button)findViewById(R.id.btnSend);
        fbIconImg = (ImageView)findViewById(R.id.fbIcon);
        twIconImg = (ImageView)findViewById(R.id.twIcon);
        gpIconImg = (ImageView)findViewById(R.id.gpIcon);
        linkediniconImg = (ImageView)findViewById(R.id.linkedinicon);
        rssiconImg = (ImageView)findViewById(R.id.rssicon);
        ytbiconImg = (ImageView)findViewById(R.id.ytbicon);
        input_name = (EditText)findViewById(R.id.input_name);
        input_text_subject = (EditText)findViewById(R.id.input_text_subject);
        input_message = (EditText)findViewById(R.id.input_message);


        //send email click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        //fb image click
        fbIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFacebook();
            }
        });


        //twitter
        twIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwtr("emergencymedke");
            }
        });

        //google plus
        gpIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGPlus("100364983439395463272");
            }
        });

        //linkedin
        linkediniconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedIn();
            }
        });

        //rss
        rssiconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRss();
            }
        });

        //youtube
        ytbiconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startYoutube();
            }
        });

    }

    protected void sendEmail() {
        Log.i("Send email", "");

        strMessage = input_message.getText().toString();
        strName = input_name.getText().toString();
        strSubject = input_text_subject.getText().toString();

        String[] TO = {"anthony.anyama@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, strSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, strMessage + "\n" + "\n" + strName);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.i("Finished sending", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactUsActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
        input_name.setText("");
        input_text_subject.setText("");
        input_message.setText("");
    }

    public final void launchFacebook() {

        final String urlFb = "fb://emergencymedkenya";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://www.facebook.com/"+"emergencymedkenya";
            intent.setData(Uri.parse(urlBrowser));
        }

        startActivity(intent);
    }

    public void openGPlus(String profile) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", profile);
            startActivity(intent);
        } catch(ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"+profile+"/posts")));
        }
    }

    public void openTwtr(String twtrName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("twitter://user?screen_name=" + twtrName)));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/#!/" + twtrName)));
        }
    }

    protected void openLinkedIn(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://emergencymedicinekenya"));
        final PackageManager packageManager = getApplicationContext().getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ke.linkedin.com/in/emergencymedicinekenya"));
        }
        startActivity(intent);
    }

    protected void launchRss() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        final String urlBrowser = "http://www.emergencymedicinekenya.org/feed/";
        intent.setData(Uri.parse(urlBrowser));
        startActivity(intent);
    }

    protected void startYoutube(){
        Intent appIntent = new Intent(Intent.ACTION_VIEW);
        appIntent.setData(Uri.parse("https://www.youtube.com/channel/UCnA7dXBzXr42nB1kcEy2Alg"));
        appIntent.setPackage("com.google.android.youtube");

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/channel/UCnA7dXBzXr42nB1kcEy2Alg"));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
