package com.pkg.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Event {

    private static final String BASE_EVENTS_URI = Calendar.getBaseCalendarUri() + "/events";

    public static final String ID = "_id";
    public static final String DTSTART = "dtstart";
    public static final String DTEND = "dtend";

    public static EventCursor getEvents(Context context, String selection, String[] selectionArgs, String sortOrder) {
        Uri events_uri = Uri.parse(BASE_EVENTS_URI);
        String[] projection = new String[] { Event.ID, Event.DTSTART, Event.DTEND};
        Cursor cursor = context.getContentResolver().query(events_uri, projection, selection, selectionArgs, sortOrder);
        return new EventCursor(cursor);
    }

    public static EventCursor getCurrentEvents(Context context) {
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
