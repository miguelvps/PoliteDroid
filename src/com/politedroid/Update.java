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

import java.util.Arrays;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.ListPreferenceMultiSelect;
import android.preference.PreferenceManager;
import android.util.Log;

import com.politedroid.calendar.Event;
import com.politedroid.calendar.EventCursor;
import com.politedroid.util.StringUtil;

public class Update extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(PoliteDroid.TAG, "Update.onReceive()");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sp.getBoolean("options_enabled", false))
            return;

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        long now = System.currentTimeMillis();

        int update_interval = Integer.parseInt(sp.getString("options_update_interval",
                                                            context.getResources().getStringArray(R.array.update_interval_values)[0]));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.add(Calendar.MILLISECOND, update_interval);
        long then = calendar.getTimeInMillis();

        String filter = "";
        String[] calendars = ListPreferenceMultiSelect.parseStoredValue(sp.getString("options_calendars", ""));
        if (calendars != null) {
            filter += " and calendar_id in (" + StringUtil.join(Arrays.asList(calendars), ", ") + ")";
            Log.d(PoliteDroid.TAG, "filtering some calendars...");
        }
        if (sp.getBoolean("options_events_all_day", false) == false) {
            filter += " and " + Event.ALL_DAY + " = 0";
            Log.d(PoliteDroid.TAG, "filtering all day events...");
        }
        if (sp.getBoolean("options_events_busy", false)) {
            filter += " and " + Event.TRANSPARENCY + " = 0";
            Log.d(PoliteDroid.TAG, "filtering only busy events...");
        }

        String selection;
        EventCursor events;

        // mute | unmute
        selection = "begin < " + now + " and end > " + now + filter;
        events = Event.getEvents(context, now, now, selection, null);
        if (events != null && events.moveToNext()) {
            // mute
            int ringerMode = audio.getRingerMode();
            boolean vibrate = sp.getBoolean("options_vibrate", false);
            int options_ringer_mode = vibrate ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT;

            if (!sp.getBoolean("isMute", false)) {
                sp.edit().putInt("ringer_mode", ringerMode).commit();
                audio.setRingerMode(Math.min(ringerMode, options_ringer_mode));
                sp.edit().putBoolean("isMute", true).commit();
            }
            else if (ringerMode != options_ringer_mode) {
                audio.setRingerMode(Math.min(options_ringer_mode,
                                             sp.getInt("ringer_mode", AudioManager.RINGER_MODE_VIBRATE)));
            }
        }
        else if (sp.getBoolean("isMute", false)) {
            // unmute
            audio.setRingerMode(sp.getInt("ringer_mode", AudioManager.RINGER_MODE_NORMAL));
            sp.edit().putBoolean("isMute", false).commit();
        }

        // find next event start or end
        selection = "begin > " + now + " and begin < " + then + filter;
        events = Event.getEvents(context, now, then, selection, "begin");
        if (events != null && events.moveToNext()) {
            long next = events.getEvent().mBegin;
            if (next < then)
                then = next;
        }
        selection = "end > " + now + " and end < " + then + filter;
        events = Event.getEvents(context, now, then, selection, "end");
        if (events != null && events.moveToNext()) {
            long next = events.getEvent().mEnd;
            if (next < then)
                then = next;
        }

        // launch next event intent
        Intent updateIntent = new Intent(context, Update.class);
        if (then == calendar.getTimeInMillis()) {
            // if alarm is not set
            if (PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_NO_CREATE) == null
                    || intent.getBooleanExtra("options_update_interval_changed", false)) {
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, then, update_interval, sender);
                Log.d(PoliteDroid.TAG, "set repeating alarm");
            }
        }
        else {
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_ONE_SHOT);
            alarm.set(AlarmManager.RTC_WAKEUP, then, sender);
            Log.d(PoliteDroid.TAG, "update alarm set in: " + Long.toString((then - now) / 1000 / 60));
        }
        Log.d(PoliteDroid.TAG, "Update done");
    }

}
