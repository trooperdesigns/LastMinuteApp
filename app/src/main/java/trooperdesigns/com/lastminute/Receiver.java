package trooperdesigns.com.lastminute;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.parse.ParsePushBroadcastReceiver;

public class Receiver extends ParsePushBroadcastReceiver {

    Context context;

    public Receiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("receive", "received");
        //super.onReceive(context, intent);

        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        Notification noti = new Notification.Builder(context)
                .setContentTitle("___ has invited you to an event!")
                .setContentText("Subject").setSmallIcon(R.drawable.twitter_icon)
                .setContentIntent(pIntent)
                .addAction(R.drawable.twitter_icon, "Call", pIntent)
                .addAction(R.drawable.twitter_icon, "More", pIntent)
                .addAction(R.drawable.twitter_icon, "And more", pIntent)
                //Vibration
                .setVibrate(new long[]{0, 300, 300, 300})
                //LED
                .setLights(Color.RED, 3000, 3000).build();

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