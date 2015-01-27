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

import android.database.Cursor;
import android.database.CursorWrapper;

public class CalendarCursor extends CursorWrapper {

    public CalendarCursor(Cursor cursor) {
        super(cursor);
    }

    public Calendar getCalendar() {
        return new Calendar(getLong(Calendar.PROJECTION_ID_INDEX),
                            getString(Calendar.PROJECTION_NAME_INDEX));
    }
}
