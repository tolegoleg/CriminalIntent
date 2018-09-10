package com.tch9.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.tch9.criminalintent.Crime;
import com.tch9.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper
{
    public CrimeCursorWrapper(Cursor cursor)
    {
        super(cursor);
    }

    public Crime getCrime()
    {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int isPoliced = getInt(getColumnIndex(CrimeTable.Cols.POLICED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setPoliced(isPoliced);
        crime.setSuspect(suspect);

        return crime;
    }
}
