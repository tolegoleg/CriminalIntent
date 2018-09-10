package com.tch9.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
{
    private static final String EXTRA_CRIME_ID = "com.tch9.criminalintent.crime_id";
    private static final String EXTRA_CRIME_IDX = "com.tch9.criminalintent.crime_idx";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private Button mBtnFirst;
    private Button mBtnLast;

    public static Intent newIntent(Context packageContext, UUID crimeId, int idx)
    {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(EXTRA_CRIME_IDX, idx);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int idx = (int) getIntent().getIntExtra(EXTRA_CRIME_IDX, -1);

        mBtnFirst = (Button) findViewById(R.id.btn_first);
        mBtnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mBtnLast = (Button) findViewById(R.id.btn_last);
        mBtnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager)
        {
            @Override
            public Fragment getItem(int positin)
            {
                Crime crime = mCrimes.get(positin);
                return CrimeFragment.newInstance(crime.getId(), positin);
            }

            @Override
            public int getCount()
            {
                return mCrimes.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pageIndex) {
                if (pageIndex == 0)
                {
                    mBtnFirst.setEnabled(false);
                    mBtnLast.setEnabled(true);
                }
                else if (pageIndex == mCrimes.size() - 1)
                {
                    mBtnFirst.setEnabled(true);
                    mBtnLast.setEnabled(false);
                }
                else
                {
                    mBtnFirst.setEnabled(true);
                    mBtnLast.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // ####
        if ( idx >= 0 && idx < mCrimes.size())
        {
            if (mCrimes.get(idx).getId().equals(crimeId))
            {
                mViewPager.setCurrentItem(idx);
            }
        }
    }
}
