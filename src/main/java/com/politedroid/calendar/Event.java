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

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.politedroid.PoliteDroid;

public class Event {

    private static final String BASE_EVENTS_URI = Calendar.BASE_CALENDAR_URI + "/instances/when";

    public static final Uri CONTENT_URI = getUri();

    public static final String ID = "_id";
    public static final String BEGIN = "begin";
    public static final String END = "end";
    public static final String CALENDAR_ID = "calendar_id";
    public static final String ALL_DAY = "allDay";
    public static final String TRANSPARENCY = getTransparencyKey();

    private static Uri getUri() {
        try {
            Class<?> calendarEventsProviderClass = Class.forName("android.provider.Calendar$Instances");
            Field uriField = calendarEventsProviderClass.getField("CONTENT_URI");
            Uri eventsUri = (Uri) uriField.get(null);
            Log.d(PoliteDroid.TAG, "Event.getInstancesUri() - URI (reflection): " + eventsUri.toString());
            return eventsUri;
        }
        catch (Exception e) {
            Log.d(PoliteDroid.TAG, "Event.getInstancesUri() - URI (reflection) failed: " + e.toString());
            return Uri.parse(BASE_EVENTS_URI);
        }
    }

    private static String getTransparencyKey() {
        try {
            Class<?> calendarEventsProviderClass = Class.forName("android.provider.CalendarContract$Instances");
            Field uriField = calendarEventsProviderClass.getField("AVAILABILITY");
            String transparency = (String) uriField.get(null);
            Log.d(PoliteDroid.TAG, "Event.getTransparencyKey() - String (reflection): " + transparency);
            return transparency;
        }
        catch (Exception e) {
            Log.d(PoliteDroid.TAG, "Event.getTransparencyKey() - String (reflection) failed: " + e.toString());
            return "transparency";
        }
    }

    public static EventCursor getEvents(Context context, long begin, long end, String selection, String sortOrder) {
        String[] projection = new String[] { Event.ID, Event.CALENDAR_ID, Event.BEGIN, Event.END, Event.ALL_DAY, Event.TRANSPARENCY };
        Uri.Builder builder = CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        Cursor cursor = context.getContentResolver().query(builder.build(), projection, selection, null, sortOrder);
        return cursor == null ? null : new EventCursor(cursor);
    }

    public Long mId;
    public Long mCalendarId;
    public Long mBegin;
    public Long mEnd;
    public boolean mAllDay;
    public boolean mBusy;

    public Event(Long id, Long calendarId, Long begin, Long end, boolean allDay, boolean busy) {
        this.mId = id;
        this.mCalendarId = calendarId;
        this.mBegin = begin;
        this.mEnd = end;
        this.mAllDay = allDay;
        this.mBusy = busy;
    }

}
