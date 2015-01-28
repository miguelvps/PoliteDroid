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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

public class Preferences extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new Fragment()).commit();
    }

    public static class Fragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.d(PoliteDroid.TAG, "Preferences.Fragment.onCreate()");
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            Log.d(PoliteDroid.TAG, "Preferences.Fragment.onResume()");
            super.onResume();

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            Log.d(PoliteDroid.TAG, "Preferences.Fragment.onPause()");
            super.onPause();

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(PoliteDroid.TAG, "Preferences.Fragment.onSharedPreferenceChanged(" + key + ")");
            if (key.startsWith("options")) {
                PackageManager packageManager = getActivity().getPackageManager();
                ComponentName componentName = new ComponentName(getActivity(), Update.class);

                if (sharedPreferences.getBoolean("options_enabled", false)) {
                    packageManager.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);

                    // start alarm
                    Intent intent = new Intent(getActivity(), Update.class);
                    if (key.equals("options_vibrate")) {
                        intent.putExtra("options_vibrate_changed", true);
                    }
                    getActivity().sendBroadcast(intent);

                    Log.d(PoliteDroid.TAG, "enabled");
                }
                else if (key.equals("options_enabled")) {
                    packageManager.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                    // stop alarm
                    Intent intent = new Intent(getActivity(), Update.class);
                    PendingIntent sender = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                    ((AlarmManager)getActivity().getSystemService(ALARM_SERVICE)).cancel(sender);
                    sender.cancel();

                    // unmute if self muted
                    if (sharedPreferences.getBoolean("isMute", false)) {
                        AudioManager audio = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
                        audio.setRingerMode(sharedPreferences.getInt("ringer_mode", AudioManager.RINGER_MODE_NORMAL));
                        sharedPreferences.edit().putBoolean("isMute", false).apply();
                    }

                    Log.d(PoliteDroid.TAG, "disabled");
                }
            }

        }
    }

}
