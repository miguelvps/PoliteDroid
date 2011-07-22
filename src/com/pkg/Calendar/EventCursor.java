package com.pkg.Calendar;

import android.database.Cursor;
import android.database.CursorWrapper;

public class EventCursor extends CursorWrapper {

    public EventCursor(Cursor cursor) {
        super(cursor);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    public Event getEvent() {
        return new Event(getLong(getColumnIndex(Event.ID)),
                         getLong(getColumnIndex(Event.CALENDAR_ID)),
                         getLong(getColumnIndex(Event.DTSTART)),
                         getLong(getColumnIndex(Event.DTEND)));
    }
}
