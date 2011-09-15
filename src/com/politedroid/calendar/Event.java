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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.politedroid.PoliteDroid;

public class Event {

    private static final String BASE_EVENTS_URI = Calendar.getBaseCalendarUri() + "/events";
    public static final Uri CONTENT_URI = getUri();

    public static final String ID = "_id";
    public static final String DTSTART = "dtstart";
    public static final String DTEND = "dtend";
    public static final String CALENDAR_ID = "calendar_id";
    public static final String ALL_DAY = "allDay";
    public static final String TRANSPARENCY = "transparency";

    private static Uri getUri() {
        try {
            Class<?> calendarEventsProviderClass = Class.forName("android.provider.Calendar$Events");
            Field uriField = calendarEventsProviderClass.getField("CONTENT_URI");
            Uri eventsUri = (Uri) uriField.get(null);
            Log.d(PoliteDroid.TAG, "Event.getUri() - URI (reflection): " + eventsUri.toString());
            return eventsUri;
        }
        catch (Exception e) {
            Log.d(PoliteDroid.TAG, "Event.getUri() - URI (reflection) failed: " + e.toString());
            return Uri.parse(BASE_EVENTS_URI);
        }
    }

    public static EventCursor getEvents(Context context, String selection, String[] selectionArgs, String sortOrder) {
        String[] projection = new String[] { Event.ID, Event.CALENDAR_ID, Event.DTSTART, Event.DTEND, Event.ALL_DAY, Event.TRANSPARENCY };
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        return cursor == null ? null : new EventCursor(cursor);
    }

    public Long mId;
    public Long mCalendarId;
    public Long mStart;
    public Long mEnd;
    public boolean mAllDay;
    public boolean mBusy;

    public Event(Long id, Long calendarId, Long dtstart, Long dtend, boolean allDay, boolean busy) {
        this.mId = id;
        this.mCalendarId = calendarId;
        this.mStart = dtstart;
        this.mEnd = dtend;
        this.mAllDay = allDay;
        this.mBusy = busy;
    }

}
