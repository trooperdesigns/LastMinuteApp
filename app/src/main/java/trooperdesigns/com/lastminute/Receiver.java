/**
 * For receiving push notifications
 */
package trooperdesigns.com.lastminute;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class Receiver extends ParsePushBroadcastReceiver {

    String title, message;

    public Receiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receive", "received");
        //super.onReceive(context, intent);

        String jsonData = intent.getStringExtra("com.parse.Data");

        try {
            JSONObject notification = new JSONObject(jsonData);
            //title = notification.getString("title");
            message = notification.getString("alert");

        } catch (JSONException e){
            Toast.makeText(context, "Something went wrong with the notification: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.d("receive", "message: " + message);

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Last Minute App")
                .setContentText(message).setSmallIcon(R.drawable.twitter_icon_small)
                .setContentIntent(pIntent)
                .addAction(R.drawable.twitter_icon_small, "Call", pIntent)
                .addAction(R.drawable.twitter_icon_small, "More", pIntent)
                .addAction(R.drawable.twitter_icon_small, "And more", pIntent)
                //Vibration
                .setVibrate(new long[]{0, 300, 300, 300})
                //LED
                .setLights(1001, 1000, 1000).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

        Log.d("receive", "sent");

    }

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return super.getSmallIconId(context, intent);
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        return super.getNotification(context, intent);
    }

}