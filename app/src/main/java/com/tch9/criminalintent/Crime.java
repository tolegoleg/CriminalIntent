package com.tch9.criminalintent;

import java.util.Date;
import java.util.UUID;

public class Crime
{
    public static final int TYPE_SIMPLE = 0;
    public static final int TYPE_POLICE = 1;

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private int mPoliced;
    private String mSuspect;

    public Crime()
    {
        this(UUID.randomUUID());
    }

    public Crime(UUID uuid)
    {
        mId = uuid;
        mDate = new Date();
        mSolved = false;
        mPoliced = TYPE_SIMPLE;
        mSuspect = "";
        mTitle = "";
    }

    public UUID getId()
    {
        return mId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        this.mTitle = title;
    }

    public Date getDate()
    {
        return mDate;
    }

    public void setDate(Date mDate)
    {
        this.mDate = mDate;
    }

    public boolean isSolved()
    {
        return mSolved;
    }

    public void setSolved(boolean solved)
    {
        this.mSolved = solved;
    }

    public void setPoliced(int policed)
    {
        this.mPoliced = policed;
    }

    public int getPoliced()
    {
        return mPoliced;
    }

    public int getType()
    {
        return mPoliced;
    }

    public String getSuspect()
    {
        return mSuspect;
    }

    public void setSuspect(String suspect)
    {
        mSuspect = suspect;
    }

    public String getPhotoFilename()
    {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
