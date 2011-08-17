package com.politedroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public final static String TAG = "PoliteDroid";

    private AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        am = (AlarmManager)getSystemService(ALARM_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(Preferences.TAG, "Preference changed: " + key);
        if (key.startsWith("options")) {
            if (sharedPreferences.getBoolean("options_enabled", false)) {
                // start alarm
                Intent intent = new Intent(Preferences.this, Update.class);
                sendBroadcast(intent);

                Log.d(Preferences.TAG, "Set update alarm");
            }
            else {
                // unmute if self muted
                if (sharedPreferences.getBoolean("isMute", false)) {
                    AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    audio.setRingerMode(sharedPreferences.getInt("ringer_mode", AudioManager.RINGER_MODE_NORMAL));
                    sharedPreferences.edit().putBoolean("isMute", false).commit();
                }

                // stop alarm
                Intent intent = new Intent(Preferences.this, Update.class);
                PendingIntent sender = PendingIntent.getBroadcast(Preferences.this, 0, intent, 0);
                am.cancel(sender);

                Log.d(Preferences.TAG, "Unset update alarm");
            }
        }

    }

}
