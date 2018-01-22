package davi.liceodavinci;


import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/**
 * Created by Emanuele on 15/01/2018 at 20:06!
 */

public class NotificationsManager extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        int topicID = -1;
        if (remoteMessage.getNotification().getTitle().contains("studenti")) topicID = Communication.COMM_STUDENTS;
        if (remoteMessage.getNotification().getTitle().contains("genitori")) topicID = Communication.COMM_PARENTS;
        if (remoteMessage.getNotification().getTitle().contains("docenti")) topicID = Communication.COMM_PROFS;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "comunicati")
                        .setSmallIcon(R.drawable.ic_menu_send)
                        .setContentTitle(remoteMessage.getNotification().getTitle() == null ? "" : remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setGroup(String.valueOf(topicID))
                        .setGroupSummary(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(topicID, mBuilder.build());
    }
}