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

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Instances;

public class Event {

    private static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.EVENT_ID,    // 0
            Instances.CALENDAR_ID, // 1
            Instances.TITLE,       // 2
            Instances.BEGIN,       // 3
            Instances.END,         // 4
            Instances.ALL_DAY,     // 5
            Instances.AVAILABILITY // 6
    };

    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_CALENDAR_ID_INDEX = 1;
    public static final int PROJECTION_TITLE_INDEX = 2;
    public static final int PROJECTION_BEGIN_INDEX = 3;
    public static final int PROJECTION_END_INDEX = 4;
    public static final int PROJECTION_ALL_DAY_INDEX = 5;
    public static final int PROJECTION_AVAILABILITY_INDEX = 6;

    public static Event getEvent(Context context, long begin, long end, String selection, String[] selectionArgs, String sortOrder) {
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, begin);
        ContentUris.appendId(builder, end);
        Cursor cursor = context.getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, sortOrder);
        Event event = null;
        if (cursor != null) {
            EventCursor eventCursor = new EventCursor(cursor);
            if (eventCursor.moveToNext()) {
                event = eventCursor.getEvent();
            }
            cursor.close();
        }
        return event;
    }

    public long mId;
    public long mCalendarId;
    public String mTitle;
    public long mBegin;
    public long mEnd;
    public boolean mAllDay;
    public int mAvailability;

    public Event(long id, long calendarId, String title, long begin, long end, boolean allDay, int availability) {
        this.mId = id;
        this.mCalendarId = calendarId;
        this.mTitle = title;
        this.mBegin = begin;
        this.mEnd = end;
        this.mAllDay = allDay;
        this.mAvailability = availability;
    }

}
