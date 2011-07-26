package com.pkg.Calendar;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Calendar {

    private static final String BASE_CALENDARS_URI = getBaseCalendarUri() + "/calendars";
    private static final Uri CONTENT_URI = getUri();

    public static String getBaseCalendarUri() {
        try {
            Class<?> calendarProviderClass = Class.forName("android.provider.Calendar");
            Field uriField = calendarProviderClass.getField("CONTENT_URI");
            Uri calendarUri = (Uri) uriField.get(null);
            return calendarUri.toString();
        }
        catch (Exception e) {
            return "content://com.android.calendar";
        }
    }

    private static Uri getUri() {
        try {
            Class<?> calendarsProviderClass = Class.forName("android.provider.Calendar.Calendars");
            Field uriField = calendarsProviderClass.getField("CONTENT_URI");
            Uri calendarsUri = (Uri) uriField.get(null);
            return calendarsUri;
        }
        catch (Exception e) {
            return Uri.parse(BASE_CALENDARS_URI);
        }
    }

    public static final String ID = "_id";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "displayName";
    public static final String HIDDEN = "hidden";
    public static final String COLOR = "color";
    public static final String ACCESS_LEVEL = "access_level";
    public static final String SELECTED = "selected";
    public static final String SYNC_EVENTS = "sync_events";
    public static final String LOCATION = "location";
    public static final String TIMEZONE = "timezone";
    public static final String OWNER_ACCOUNT = "ownerAccount";

    public static ArrayList<Calendar> getCalendars(Context context) {
        String[] projection = new String[] { Calendar.ID, Calendar.NAME };
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, projection, null, null, null);

        ArrayList<Calendar> calendars;
        if (cursor != null) {
            calendars = new ArrayList<Calendar>(cursor.getCount());
            while (cursor.moveToNext()) {
                calendars.add(new Calendar(cursor.getLong(cursor.getColumnIndex(Calendar.ID)),
                        cursor.getString(cursor.getColumnIndex(Calendar.NAME))));
            }
            cursor.close();
        }
        else {
            calendars = new ArrayList<Calendar>(0);
        }

        return calendars;
    }

    public Long mId;
    public String mName;

    public Calendar(Long id, String name) {
        this.mId = id;
        this.mName = name;
    }
}
