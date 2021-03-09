package com.simpla.turnedTables.Utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.simpla.turnedTables.MainActivity;
import com.simpla.turnedTables.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static final String CHANNEL_ID = "default_channel_id";

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel1"; //getString(R.string.channel_name);
            String description = "ChannelNotification";  //getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotificationChannel();
        assert remoteMessage.getNotification() != null;
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String data = remoteMessage.getData().get("likeRatingIDm");
        String data1 = remoteMessage.getData().get("dislikeRatingIDm");
        assert title != null;
        switch (title) {
            case "Yeni Begeni":
                showNotificationLike(title, body, data);
                break;
            case "Yeni Begenmedim"    :
                showNotificationDislike(title, body, data1);
                break;
        }

    }

    private void showNotificationLike(String title,String body,String data){
        Intent pendingIntent = new Intent(this, MainActivity.class);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent.putExtra("likeRatingIDm",data);

        PendingIntent pendingIntent1 = PendingIntent.getActivity(this,40,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_comment_like_full)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.not_icon))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(body)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent1)
                .build();
        @SuppressLint("ServiceCast")
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis(),builder );
    }

    private void showNotificationDislike(String title,String body,String data){
        Intent pendingIntent = new Intent(this, MainActivity.class);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent.putExtra("dislikeRatingIDm",data);

        PendingIntent pendingIntent1 = PendingIntent.getActivity(this,40,pendingIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_comment_dislike_full)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.not_icon))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(body)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent1)
                .build();
        @SuppressLint("ServiceCast")
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify((int) System.currentTimeMillis(),builder );
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        writeToStorage(s);
    }

    private void writeToStorage(String token){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("fcm_token").setValue(token);
        }
    }


}
