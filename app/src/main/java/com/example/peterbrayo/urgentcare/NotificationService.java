package com.example.peterbrayo.urgentcare;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import static com.example.peterbrayo.urgentcare.UtilityFunctions.decodeFromFirebaseBase64;
import static com.example.peterbrayo.urgentcare.UtilityFunctions.getCircleBitmap;
import static com.example.peterbrayo.urgentcare.UtilityFunctions.getResizedBitmap;

public class NotificationService extends FirebaseMessagingService {
    SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String LOCATION = "location";
        sharedPreferences = getSharedPreferences(LOCATION, Context.MODE_PRIVATE);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String content = remoteMessage.getData().get("title");
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.notification_logo);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        Bitmap circularBitmap = getCircleBitmap(bitmap);

        Intent resultIntent = new Intent(this, ReplyActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        String NOTIFICATION_WITHOUT_PIC = "notificationWithoutPic";
        String REPLY_NOTIFICATION = "reply_notification";
        int NOTIFICATION_ID = 123;

        //notification without image, to volunteers
        String NOTIFICATION_WITH_PIC = "notificationWithPic";
        if(remoteMessage.getData().get("notificationType").equals(NOTIFICATION_WITHOUT_PIC)){

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, Html.fromHtml(getString(R.string.action_reply)), pendingIntent)
                .build();


        @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Accident Has Happened")
                .setContentText(content)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.hospital_notification_small_icon)
                .setLargeIcon(circularBitmap);

        builder.addAction(replyAction);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);

                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
    }

    //notification showing reply from volunteer
            else if (remoteMessage.getData().get("notificationType").equals(REPLY_NOTIFICATION)){

            String reply = remoteMessage.getData().get("reply");
            String name = sharedPreferences.getString("replyName", "Anonymous");
            String image = sharedPreferences.getString("replyImage", "");
            String contact = sharedPreferences.getString("replyContact","0700908030");
            Bitmap dp = getResizedBitmap(decodeFromFirebaseBase64(image), 500, 500);
            Bitmap circularDp = getCircleBitmap(dp);

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + contact));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            PendingIntent pIntent = PendingIntent.getActivity(this,0, callIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Action callAction = new NotificationCompat.Action.Builder(R.drawable.ic_call_black_24dp, Html.fromHtml(getString(R.string.action_call)), pIntent)
                    .build();

            @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.hospital_notification_small_icon)
                    .setContentTitle("Reply from " + name)
                    .setContentText(reply)
                    .addAction(callAction)
                    .setSound(sound)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(reply));

            if(!image.equals("")){
                builder.setLargeIcon(circularDp);
            }
            else {
                builder.setLargeIcon(circularBitmap);
            }

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
            }

            //notification with image, to volunteer
        else if(remoteMessage.getData().get("notificationType").equals(NOTIFICATION_WITH_PIC)){

            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, Html.fromHtml(getString(R.string.action_reply)), pendingIntent)
                    .build();
            String image = sharedPreferences.getString("accidentPhoto","");
            Bitmap accidentBitmap = decodeFromFirebaseBase64(image);

            @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.hospital_notification_small_icon)
                    .setContentTitle("Accident Has Happened")
                    .setContentText("Expand to view")
                    .setSound(sound)
                    .setLargeIcon(circularBitmap)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Photo of Accident"))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(accidentBitmap).bigLargeIcon(null));

            builder.addAction(replyAction);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(NOTIFICATION_ID);
                notificationManager.notify(NOTIFICATION_ID,builder.build());
            }
        }
    }

}
