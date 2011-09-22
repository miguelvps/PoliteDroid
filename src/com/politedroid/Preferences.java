/* PoliteDroid: activate silent mode during calendar events
 * Copyright (C) 2011 Miguel Serrano
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
                // start alarm
                Intent intent = new Intent(getApplicationContext(), Update.class);
                sendBroadcast(intent);

                Log.d(PoliteDroid.TAG, "enabled");
            }
            else {
                // unmute if self muted
                if (sharedPreferences.getBoolean("isMute", false)) {
                    AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    audio.setRingerMode(sharedPreferences.getInt("ringer_mode", AudioManager.RINGER_MODE_NORMAL));
                    sharedPreferences.edit().putBoolean("isMute", false).commit();
                }

                // stop alarm
                Intent intent = new Intent(getApplicationContext(), Update.class);
                PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                ((AlarmManager)getSystemService(ALARM_SERVICE)).cancel(sender);
                sender.cancel();

                Log.d(PoliteDroid.TAG, "disabled");
            }
        }

    }

}
