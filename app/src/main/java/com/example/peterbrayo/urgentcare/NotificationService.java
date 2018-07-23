package com.example.peterbrayo.urgentcare;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.RemoteInput;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {
    private String NOTIFICATION_WITH_PIC = "notificationWithPic";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String content = remoteMessage.getData().get("title");
        String replyLabel = "Enter your reply here";

        Intent resultIntent = new Intent(this, ReplyActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        String NOTIFICATION_WITHOUT_PIC = "notificationWithoutPic";
        String REPLY_NOTIFICATION = "reply_notification";
        if(remoteMessage.getData().get("notificationType").equals(NOTIFICATION_WITHOUT_PIC)){

            String KEY_WITHOUT_REPLY = "key_withoutPic_reply";
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_WITHOUT_REPLY)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, "REPLY", pendingIntent)
                .addRemoteInput(remoteInput)
                .build();


        @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Accident Has Happened")
                .setContentText(content)
                .setSound(sound)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.notification_small_icon);

        builder.addAction(replyAction);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(123,builder.build());
    }

            else if (remoteMessage.getData().get("notificationType").equals(REPLY_NOTIFICATION)){

            String reply = remoteMessage.getData().get("reply");

            @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_small_icon)
                    .setContentTitle("Reply")
                    .setSound(sound)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(reply));

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(256,builder.build());

            }

        else {
            String KEY_PIC_REPLY = "key_pic_reply";
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_PIC_REPLY)
                    .setLabel(replyLabel)
                    .build();

            NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_reply_black_24dp, "REPLY", pendingIntent)
                    .addRemoteInput(remoteInput)
                    .build();

            Bitmap bitmap = decodeFromFirebaseBase64(remoteMessage.getData().get("url"));

            @SuppressWarnings("deprecation") NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_small_icon)
                    .setContentTitle("Accident Has Happened")
                    .setSound(sound)
                    .setLargeIcon(bitmap)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null));

            builder.addAction(replyAction);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(145,builder.build());

        }
    }

    public static Bitmap decodeFromFirebaseBase64(String image){
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
