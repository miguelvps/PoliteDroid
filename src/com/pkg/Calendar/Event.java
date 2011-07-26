package com.pkg.Calendar;

import java.lang.reflect.Field;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Event {

    private static final String BASE_EVENTS_URI = Calendar.getBaseCalendarUri() + "/events";
    private static final Uri CONTENT_URI = getUri();

    public static final String ID = "_id";
    public static final String DTSTART = "dtstart";
    public static final String DTEND = "dtend";
    public static final String CALENDAR_ID = "calendar_id";

    private static Uri getUri() {
        try {
            Class<?> calendarEventsProviderClass = Class.forName("android.provider.Calendar.Events");
            Field uriField = calendarEventsProviderClass.getField("CONTENT_URI");
            Uri eventsUri = (Uri) uriField.get(null);
            return eventsUri;
        }
        catch (Exception e) {
            return Uri.parse(BASE_EVENTS_URI);
        }
    }

    public static EventCursor getEvents(Context context, String selection, String[] selectionArgs, String sortOrder) {
        String[] projection = new String[] { Event.ID, Event.CALENDAR_ID, Event.DTSTART, Event.DTEND };
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        return cursor == null ? null : new EventCursor(cursor);
    }

    public static EventCursor getCurrentEvents(Context context) {
        String now = Long.toString(System.currentTimeMillis());
        String selection = "dtstart < ? and dtend > ?";
        String[] selectionArgs = { now, now };
        return getEvents(context, selection, selectionArgs, Event.DTSTART);
    }


    public Long mId;
    public Long mCalendarId;
    public Long mStart;
    public Long mEnd;

    public Event(Long id, Long calendarId, Long dtstart, Long dtend) {
        this.mId = id;
        this.mCalendarId = calendarId;
        this.mStart = dtstart;
        this.mEnd = dtend;
    }

}
