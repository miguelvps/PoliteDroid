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

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;

public class Calendar {

    private static final String[] CALENDAR_PROJECTION = new String[] {
            Calendars._ID,  // 0
            Calendars.NAME  // 1
    };

    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_NAME_INDEX = 1;

    public static Calendar[] getCalendars(Context context) {
        Cursor cursor = context.getContentResolver().query(Calendars.CONTENT_URI, CALENDAR_PROJECTION, null, null, null);
        if (cursor != null) {
            CalendarCursor calendarCursor = new CalendarCursor(cursor);
            Calendar[] calendars = new Calendar[calendarCursor.getCount()];
            for (int i = 0; i < calendars.length && calendarCursor.moveToNext(); i++) {
                calendars[i] = calendarCursor.getCalendar();
            }
            cursor.close();
            return calendars;
        }
        return null;
    }

    public long mId;
    public String mName;

    public Calendar(long id, String name) {
        this.mId = id;
        this.mName = name == null ? "My calendar" : name;
    }

}
