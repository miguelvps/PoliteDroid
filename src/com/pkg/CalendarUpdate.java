package com.pkg;

import java.util.Arrays;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.pkg.Calendar.Event;
import com.pkg.Calendar.EventCursor;
import com.pkg.android.preference.ListPreferenceMultiSelect;
import com.pkg.util.StringUtil;

public class CalendarUpdate extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (!sp.getBoolean("options_enabled", false))
            return;

        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        long now = System.currentTimeMillis();
        String nows = Long.toString(now);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);
        calendar.add(Calendar.MINUTE, 15); // check calendar every 15m by default
        long then = calendar.getTimeInMillis();

        String calendar_filter = "";
        String[] calendars = ListPreferenceMultiSelect.parseStoredValue(sp.getString("options_calendars", ""));
        if (calendars != null) {
            calendar_filter = " and calendar_id in (" + StringUtil.join(Arrays.asList(calendars), ", ") + ")";
        }

        String selection;
        String[] selectionArgs = { nows, nows };
        EventCursor events;

        // mute | unmute
        selection = "dtstart < ? and dtend > ?" + calendar_filter;
        events = Event.getEvents(context, selection, selectionArgs, null);
        if (events.moveToNext()) {
            // mute
            if (!sp.getBoolean("isMute", false)) {
                int ringerMode = audio.getRingerMode();
                sp.edit().putInt("ringer_mode", ringerMode).commit();
                audio.setRingerMode(Math.min(ringerMode, AudioManager.RINGER_MODE_VIBRATE));
                sp.edit().putBoolean("isMute", true).commit();
            }
        }
        else {
            // unmute
            audio.setRingerMode(sp.getInt("ringer_mode", AudioManager.RINGER_MODE_NORMAL));
            sp.edit().putBoolean("isMute", false).commit();
        }

        // find next event start or end
        selectionArgs[1] = Long.toString(then);
        selection = "dtstart > ? and dtstart < ?" + calendar_filter;
        events = Event.getEvents(context, selection, selectionArgs, "dtstart");
        if (events.moveToNext()) {
            long next = events.getEvent().mStart;
            then = next < then ? next : then;
        }
        selection = "dtend > ? and dtend < ?" + calendar_filter;
        events = Event.getEvents(context, selection, selectionArgs, "dtend");
        if (events.moveToNext()) {
            long next = events.getEvent().mStart;
            then = next < then ? next : then;
        }

        // launch next event intent
        Intent updateIntent = new Intent(context, CalendarUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.set(AlarmManager.RTC_WAKEUP, then, sender);
        Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
    }

}
