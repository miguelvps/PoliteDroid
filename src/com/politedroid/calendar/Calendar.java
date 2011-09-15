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

package com.politedroid.calendar;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.politedroid.PoliteDroid;

public class Calendar {

    private static final String BASE_CALENDARS_URI = getBaseCalendarUri() + "/calendars";
    private static final Uri CONTENT_URI = getUri();

    public static String getBaseCalendarUri() {
        try {
            Class<?> calendarProviderClass = Class.forName("android.provider.Calendar");
            Field uriField = calendarProviderClass.getField("CONTENT_URI");
            Uri calendarUri = (Uri) uriField.get(null);
            Log.d(PoliteDroid.TAG, "Calendar.getBaseCalendarUri() - URI (reflection): " + calendarUri.toString());
            return calendarUri.toString();
        }
        catch (Exception e) {
            Log.d(PoliteDroid.TAG, "Calendar.getBaseCalendarUri() - URI (reflection) failed: " + e.toString());
            return "content://com.android.calendar";
        }
    }

    private static Uri getUri() {
        try {
            Class<?> calendarsProviderClass = Class.forName("android.provider.Calendar$Calendars");
            Field uriField = calendarsProviderClass.getField("CONTENT_URI");
            Uri calendarsUri = (Uri) uriField.get(null);
            Log.d(PoliteDroid.TAG, "Calendars.getUri() - URI (reflection): " + calendarsUri.toString());
            return calendarsUri;
        }
        catch (Exception e) {
            Log.d(PoliteDroid.TAG, "Calendars.getUri() - URI (reflection) failed: " + e.toString());
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
