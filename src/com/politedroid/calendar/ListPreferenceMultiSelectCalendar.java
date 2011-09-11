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
