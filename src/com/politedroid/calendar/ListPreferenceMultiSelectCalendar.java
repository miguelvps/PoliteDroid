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

import java.util.ArrayList;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.preference.ListPreferenceMultiSelect;
import android.util.AttributeSet;


public class ListPreferenceMultiSelectCalendar extends ListPreferenceMultiSelect {

    public ListPreferenceMultiSelectCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        ArrayList<Calendar> calendars = Calendar.getCalendars(getContext());
        CharSequence entries[] = new String[calendars.size()];
        CharSequence entryValues[] = new String[calendars.size()];
        int i = 0;
        for (Calendar c : calendars) {
            entries[i] = c.mName;
            entryValues[i] = c.mId.toString();
            i++;
        }

        setEntries(entries);
        setEntryValues(entryValues);
        super.onPrepareDialogBuilder(builder);
    }
}
