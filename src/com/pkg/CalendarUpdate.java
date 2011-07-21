package com.pkg;

import java.util.ArrayList;
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

        String selection;
        String[] selectionArgs = { nows, nows };
        ArrayList<Event> events;

        // mute | unmute
        selection = "dtstart < ? and dtend > ?";
        events = Event.getEvents(context, selection, selectionArgs, null);
        if (events.size() > 0) {
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
        selection = "dtstart > ? and dtstart < ?";
        events = Event.getEvents(context, selection, selectionArgs, "dtstart");
        if (events.size() > 0) {
            long next = events.get(0).mStart;
            then = next < then ? next : then;
        }
        selection = "dtend > ? and dtend < ?";
        events = Event.getEvents(context, selection, selectionArgs, "dtend");
        if (events.size() > 0) {
            long next = events.get(0).mStart;
            then = next < then ? next : then;
        }

        // launch next event intent
        Intent updateIntent = new Intent(context, CalendarUpdate.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.set(AlarmManager.RTC_WAKEUP, then, sender);
        Toast.makeText(context, "Next", Toast.LENGTH_SHORT).show();
    }

}
