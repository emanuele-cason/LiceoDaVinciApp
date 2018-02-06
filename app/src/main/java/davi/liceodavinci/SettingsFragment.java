package davi.liceodavinci;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Emanuele on 27/01/2018 at 22:55!
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_STUDENTS))
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_STUDENTS_TOPIC);
        else FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_STUDENTS_TOPIC);

        if (ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_PARENTS))
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_PARENTS_TOPIC);
        else FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_PARENTS_TOPIC);

        if (ConfigurationManager.getIstance().getCommNotificationEnabled(Communication.COMM_PROFS))
            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_PROFS_TOPIC);
        else FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_PROFS_TOPIC);
    }
}
