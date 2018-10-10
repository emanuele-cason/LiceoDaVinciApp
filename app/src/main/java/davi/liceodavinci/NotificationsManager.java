package davi.liceodavinci;


import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import davi.liceodavinci.communications.Communication;

/**
 * Created by Emanuele on 15/01/2018 at 20:06!
 */

public class NotificationsManager extends FirebaseMessagingService {

    public static final String COMM_STUDENTS_TOPIC = "comunicati-studenti";
    public static final String COMM_PARENTS_TOPIC = "comunicati-genitori";
    public static final String COMM_PROFS_TOPIC = "comunicati-docenti";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        int topicID = -1;
        if (remoteMessage.getNotification().getTitle().contains("studenti")) {
            if (ConfigurationManager.getIstance().isNotificationEnabled(Communication.COMM_STUDENTS))
                topicID = Communication.COMM_STUDENTS;
        }
        if (remoteMessage.getNotification().getTitle().contains("genitori")) {
            if (ConfigurationManager.getIstance().isNotificationEnabled(Communication.COMM_PARENTS))
                topicID = Communication.COMM_PARENTS;
        }
        if (remoteMessage.getNotification().getTitle().contains("docenti")) {
            if (ConfigurationManager.getIstance().isNotificationEnabled(Communication.COMM_PROFS))
                topicID = Communication.COMM_PROFS;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (topicID != -1){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this, "comunicati")
                            .setSmallIcon(R.drawable.ic_communication)
                            .setContentTitle(remoteMessage.getNotification().getTitle() == null ? "" : remoteMessage.getNotification().getTitle())
                            .setGroup(String.valueOf(topicID))
                            .setGroupSummary(true)
                            .setContentText(remoteMessage.getNotification().getBody());
            notificationManager.notify(topicID, mBuilder.build());
        }
    }
}