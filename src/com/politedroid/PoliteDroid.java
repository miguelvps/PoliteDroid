package com.politedroid;

import android.app.Application;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.politedroid.calendar.Event;

public class PoliteDroid extends Application {

    public final static String TAG = "PoliteDroid";

    private EventsContentObserver mEventContentObserver;

    private class EventsContentObserver extends ContentObserver {

        public EventsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(PoliteDroid.TAG, "EventsContentObserver.onChange(" + selfChange + ")");
            super.onChange(selfChange);
            Intent intent = new Intent(getApplicationContext(), Update.class);
            sendBroadcast(intent);
        }

    }

    @Override
    public void onCreate() {
        Log.d(PoliteDroid.TAG, "PoliteDroid.onCreate()");
        super.onCreate();

        mEventContentObserver = new EventsContentObserver(new Handler());
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("options_enabled", false)) {
            registerEventsContentObserver();
        }
    }

    public void registerEventsContentObserver() {
        Log.d(PoliteDroid.TAG, "PoliteDroid.registerEventsContentObserver()");
        getApplicationContext().getContentResolver().registerContentObserver(Event.CONTENT_URI, true, mEventContentObserver);
    }

    public void unregisterEventsContentObserver() {
        Log.d(PoliteDroid.TAG, "PoliteDroid.unregisterEventsContentObserver()");
        getApplicationContext().getContentResolver().unregisterContentObserver(mEventContentObserver);
    }

}
