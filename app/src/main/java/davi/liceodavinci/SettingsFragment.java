package davi.liceodavinci;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.messaging.FirebaseMessaging;

import davi.liceodavinci.communications.Communication;

/**
 * Created by Emanuele on 27/01/2018 at 22:55!
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference notifications = findPreference("notifications");
        notifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final MaterialDialog dialog =
                        new MaterialDialog.Builder(getActivity())
                                .title("Notifiche attive")
                                .customView(R.layout.notifications_dialog, true)
                                .build();

                final CheckBox studentCB = (CheckBox) dialog.findViewById(R.id.notifications_students_checkbox);
                final CheckBox parentsCB = (CheckBox) dialog.findViewById(R.id.notifications_parents_checkbox);
                final CheckBox profsCB = (CheckBox) dialog.findViewById(R.id.notifications_profs_checkbox);

                if (ConfigurationManager.getIstance().isNotificationEnabled(Communication.COMM_STUDENTS)) studentCB.setChecked(true);
                else studentCB.setChecked(false);

                if (ConfigurationManager.getIstance().isNotificationEnabled(Communication.COMM_PARENTS)) parentsCB.setChecked(true);
                else parentsCB.setChecked(false);

                if (ConfigurationManager.getIstance().isNotificationEnabled(Communication.COMM_PROFS)) profsCB.setChecked(true);
                else profsCB.setChecked(false);

                dialog.setActionButton(DialogAction.POSITIVE, "Fatto");
                dialog.setActionButton(DialogAction.NEGATIVE, "Annulla");
                final View positive = dialog.getActionButton(DialogAction.POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (studentCB.isChecked()){
                            ConfigurationManager.getIstance().setCommNotificationEnabled(Communication.COMM_STUDENTS, true);
                            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_STUDENTS_TOPIC);
                        }else {
                            ConfigurationManager.getIstance().setCommNotificationEnabled(Communication.COMM_STUDENTS, false);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_STUDENTS_TOPIC);
                        }

                        if (parentsCB.isChecked()){
                            ConfigurationManager.getIstance().setCommNotificationEnabled(Communication.COMM_PARENTS, true);
                            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_PARENTS_TOPIC);
                        }else {
                            ConfigurationManager.getIstance().setCommNotificationEnabled(Communication.COMM_PARENTS, false);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_PARENTS_TOPIC);
                        }

                        if (profsCB.isChecked()){
                            ConfigurationManager.getIstance().setCommNotificationEnabled(Communication.COMM_PROFS, true);
                            FirebaseMessaging.getInstance().subscribeToTopic(NotificationsManager.COMM_PROFS_TOPIC);
                        }else {
                            ConfigurationManager.getIstance().setCommNotificationEnabled(Communication.COMM_PROFS, false);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationsManager.COMM_PROFS_TOPIC);
                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
