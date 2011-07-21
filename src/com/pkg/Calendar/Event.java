package com.pkg.Calendar;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Event {

    private static final String BASE_EVENTS_URI = Calendar.getBaseCalendarUri() + "/events";

    private static final String ID = "_id";
    private static final String DTSTART = "dtstart";
    private static final String DTEND = "dtend";

    // TODO: CursorWrapper
    public static ArrayList<Event> getEvents(Context context, String selection, String[] selectionArgs, String sortOrder) {
        Uri events_uri = Uri.parse(BASE_EVENTS_URI);
        String[] projection = new String[] { Event.ID, Event.DTSTART, Event.DTEND};
        Cursor cursor = context.getContentResolver().query(events_uri, projection, selection, selectionArgs, sortOrder);

        int count = cursor.getCount();
        ArrayList<Event> events = new ArrayList<Event>(count);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                events.add(new Event(cursor.getLong(cursor.getColumnIndex(Event.ID)),
                        cursor.getLong(cursor.getColumnIndex(Event.DTSTART)),
                        cursor.getLong(cursor.getColumnIndex(Event.DTEND))));
            }
        }
        cursor.close();

        return events;
    }

    public static ArrayList<Event> getCurrentEvents(Context context) {
        String now = Long.toString(System.currentTimeMillis());
        String selection = "dtstart < ? and dtend > ?";
        String[] selectionArgs = { now, now };
        return getEvents(context, selection, selectionArgs, Event.DTSTART);
    }


    public Long mId;
    public Long mStart;
    public Long mEnd;

    public Event(Long id, Long dtstart, Long dtend) {
        this.mId = id;
        this.mStart = dtstart;
        this.mEnd = dtend;
    }

}
