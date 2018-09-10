package com.tch9.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tch9.criminalintent.database.CrimeBaseHelper;
import com.tch9.criminalintent.database.CrimeCursorWrapper;
//import com.tch9.criminalintent.database.CrimeDbSchema;
import com.tch9.criminalintent.database.CrimeDbSchema;
import com.tch9.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CrimeLab
{
    private static CrimeLab sCrimeLab;
    //private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context)
    {
        if (sCrimeLab == null)
        {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    // Конструктор
    private CrimeLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        //mCrimes = new ArrayList<>();
        //for (int i = 0; i < 10; i++)
        //{
        //    Crime crime = new Crime();
        //    crime.setTitle("Crime #" + Integer.toString(i+1));
        //    crime.setSolved(i % 2 == 0); // для каждого второго объекта
        //    if (i % 5 == 0) // для каждого пятого объекта
        //        crime.setRequiresPolice(Crime.TYPE_POLICE);
        //    else
        //        crime.setRequiresPolice(Crime.TYPE_SIMPLE);
        //    mCrimes.add(crime);
        //}
    }

    public List<Crime> getCrimes()
    {
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID crimeId)
    {
        //if (idx >= 0 && idx < mCrimes.size())
        //{
        //    if (mCrimes.get(idx).getId().equals(crimeId))
        //        return mCrimes.get(idx);
        //}

        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[]{crimeId.toString()});
        try
        {
            if (cursor.getCount() == 0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally
        {
            cursor.close();
        }
    }

    //public int getCrimesSize()
    //{
        //return mCrimes.size();
        //Cursor cursor = mDatabase.query("select count(*) as c from " + CrimeTable.NAME);
        //return 0;
    //}

    public void addCrime(Crime crime)
    {
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void updateCrime(Crime crime)
    {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});

    }

    public void deleteCrime(Crime crime)
    {
        String uuidString = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private static ContentValues getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, (crime.isSolved())?1:0);
        values.put(CrimeTable.Cols.POLICED, crime.getPoliced());
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // all columns
                whereClause,
                whereArgs,
                null,
                null,
                 CrimeTable.Cols._ID + " DESC"
        );
        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(Crime crime)
    {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }
}
