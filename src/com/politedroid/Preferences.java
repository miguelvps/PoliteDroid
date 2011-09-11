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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(PoliteDroid.TAG, "Preferences.onCreate()");
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        Log.d(PoliteDroid.TAG, "Preferences.onResume()");
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        Log.d(PoliteDroid.TAG, "Preferences.onPause()");
        super.onPause();

        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(PoliteDroid.TAG, "Preferences.onSharedPreferenceChanged(" + key + ")");
        if (key.startsWith("options")) {
            if (sharedPreferences.getBoolean("options_enabled", false)) {
                if (key.equals("options_enabled")) {
                    ((PoliteDroid)getApplication()).registerEventsContentObserver();
                }

                // start alarm
                Intent intent = new Intent(Preferences.this, Update.class);
                sendBroadcast(intent);

                Log.d(PoliteDroid.TAG, "enabled");
            }
            else {
                if (key.equals("options_enabled")) {
                    ((PoliteDroid)getApplication()).unregisterEventsContentObserver();
                }

                // unmute if self muted
                if (sharedPreferences.getBoolean("isMute", false)) {
                    AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    audio.setRingerMode(sharedPreferences.getInt("ringer_mode", AudioManager.RINGER_MODE_NORMAL));
                    sharedPreferences.edit().putBoolean("isMute", false).commit();
                }

                // stop alarm
                Intent intent = new Intent(Preferences.this, Update.class);
                PendingIntent sender = PendingIntent.getBroadcast(Preferences.this, 0, intent, 0);
                ((AlarmManager)getSystemService(ALARM_SERVICE)).cancel(sender);
                sender.cancel();

                Log.d(PoliteDroid.TAG, "disabled");
            }
        }

    }

}
