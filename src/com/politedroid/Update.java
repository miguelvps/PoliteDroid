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
        String nows = Long.toString(now);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.add(Calendar.MINUTE, 20);
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
        String[] selectionArgs = { nows, nows };
        EventCursor events;

        // mute | unmute
        selection = "dtstart < ? and dtend > ?" + filter;
        events = Event.getEvents(context, selection, selectionArgs, null);
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
        selectionArgs[1] = Long.toString(then);
        selection = "dtstart > ? and dtstart < ?" + filter;
        events = Event.getEvents(context, selection, selectionArgs, "dtstart");
        if (events != null && events.moveToNext()) {
            long next = events.getEvent().mStart;
            if (next < then)
                then = next;
        }
        selection = "dtend > ? and dtend < ?" + filter;
        events = Event.getEvents(context, selection, selectionArgs, "dtend");
        if (events != null && events.moveToNext()) {
            long next = events.getEvent().mEnd;
            if (next < then)
                then = next;
        }

        // launch next event intent
        Intent updateIntent = new Intent(context, Update.class);
        if (then == calendar.getTimeInMillis()) {
            // if alarm is not set
            if (PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_NO_CREATE) == null) {
                PendingIntent sender = PendingIntent.getBroadcast(context, 0, updateIntent, 0);
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, then, AlarmManager.INTERVAL_FIFTEEN_MINUTES, sender);
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
