
package main.emfk.com.emfklatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DownloadReceiver extends BroadcastReceiver {
    public static final String ACTION_RESP =
            "com.emfk.intent.action.MESSAGE_PROCESSED";


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //String text = intent.getStringExtra(DownloadsService.PARAM_OUT_MSG);
        Log.d("DWL", "Download complete!!!");
        //Toast.makeText(context, "Data download complete!", Toast.LENGTH_LONG).show();
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
