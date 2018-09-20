package davi.liceodavinci;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
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
        notifications.setOnPreferenceClickListener(preference -> {
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
            positive.setOnClickListener(view -> {
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
            });

            dialog.show();
            return false;
        });

        ListPreference startupFragment = (ListPreference)findPreference("startup_fragment");
        startupFragment.setEntries(new String[]{"Agenda del Liceo", "Orario personale", "Comunicati studenti", "Comunicati genitori", "Comunicati docenti"});
        startupFragment.setEntryValues(new String[]{"5", "4", String.valueOf(Communication.COMM_STUDENTS), String.valueOf(Communication.COMM_PARENTS), String.valueOf(Communication.COMM_PROFS)});

        /*Preference theme = findPreference(null);
        theme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                ColorPickerPalette colorPickerPalette =
                        (ColorPickerPalette) layoutInflater.inflate(R.layout.custom_color_picker, null);

                final AlertDialog alert = new AlertDialog.Builder(getActivity())
                        .setTitle("Seleziona un tema")
                        .setView(colorPickerPalette)
                        .create();

                colorPickerPalette.init(getResources().getIntArray(R.array.picker_colors).length, 5, new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        ConfigurationManager.getIstance().saveTheme(color);
                        alert.dismiss();
                    }
                });
                colorPickerPalette.drawPalette(getResources().getIntArray(R.array.picker_colors), ConfigurationManager.getIstance().getTheme());

                alert.show();
                return false;
            }
        });*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
