package com.tch9.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment
{
    private static final String SAVED_SUBTITLE_VISIBLED = "subtitle";
    private static final int REQUEST_CRIME = 1;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private boolean mChangedData;
    private int mX;

    private boolean mSubtitleVisible;

    private Button mBtnIfEmptyAdd;

    private void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (crimes.size() == 0)
        {
            mBtnIfEmptyAdd.setVisibility(View.VISIBLE);
        }
        else
        {
            mBtnIfEmptyAdd.setVisibility(View.GONE);
        }
        if (mAdapter == null)
        {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemChanged(mX);
        }
        updateSubtitle();
    }

    private void updateSubtitle()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimesSize = mAdapter.mCrimes.size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimesSize, crimesSize);
        subtitle += ", fotos: ";
        String[] ss = getActivity().getApplicationContext().getFilesDir().list();
        subtitle += Integer.toString(ss.length);
        if (!mSubtitleVisible)
        {
            subtitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.new_crime:
                mChangedData = true;
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId(), 0 /*mAdapter.mCrimes.size() - 1*/);
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mChangedData)
        {
            Toast.makeText(getActivity(), "Данные изменены...", Toast.LENGTH_SHORT).show();
            updateUI();
            mChangedData = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLED, mSubtitleVisible);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mBtnIfEmptyAdd = (Button) view.findViewById(R.id.btn_if_empty_add);
        mBtnIfEmptyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mChangedData = true;
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId(), 0 /*CrimeLab.get(getActivity()).getCrimesSize() - 1*/);
                startActivity(intent);
            }
        });
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCrimeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mChangedData = false;
        if (savedInstanceState != null)
        {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLED);
        }
        updateUI();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CRIME)
        {
            // Обработка результата
            if (resultCode == Activity.RESULT_OK)
            {
                mChangedData = true;
            }
            else
            {
                mChangedData = false;
            }
        }
    }

    private class SimpleCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;

        public SimpleCrimeHolder(View view)
        {
            super(view);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle()/* + " (" + mCrime.getId().toString() + ")"*/);
            CharSequence ts = DateFormat.format("dd.MM.yyyy kk:mm:ss",mCrime.getDate().getTime());
            mDateTextView.setText(ts.toString());
            mSolvedImageView.setVisibility(mCrime.isSolved()?View.VISIBLE:View.GONE);
        }

        @Override
        public void onClick(View view)
        {
            mX = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId(), mX);
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    private class PoliceCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mUUID;
        private Button mBtnCall;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public PoliceCrimeHolder(View view)
        {
            super(view);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mUUID = (TextView) itemView.findViewById(R.id.crime_uuid);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            mBtnCall = (Button) itemView.findViewById(R.id.button_call);
            mBtnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Toast.makeText(getActivity(), "SOS SOS SOS !!!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mUUID.setText(mCrime.getId().toString().toUpperCase());
            mTitleTextView.setText(mCrime.getTitle()/* + " (" + mCrime.getId().toString() + ")"*/);
            CharSequence ts = DateFormat.format("dd.MM.yyyy kk:mm:ss",mCrime.getDate().getTime());
            mDateTextView.setText(ts.toString());
            mSolvedImageView.setVisibility(mCrime.isSolved()?View.VISIBLE:View.GONE);
        }

        @Override
        public void onClick(View view)
        {
            mX = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId(), mX);
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes)
        {
            this.mCrimes = crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            //Log.d("OLEG", "viewType: " + Integer.toString(viewType));

            View view;

            switch (viewType)
            {
                case Crime.TYPE_SIMPLE:
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_crime, parent, false);
                    return new SimpleCrimeHolder(view);
                case Crime.TYPE_POLICE:
                    view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_crime_police, parent, false);
                    return new PoliceCrimeHolder(view);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
        {
            Crime object = mCrimes.get(position);
            if (object != null)
            {
                switch (object.getType())
                {
                    case Crime.TYPE_SIMPLE:
                        ((SimpleCrimeHolder)holder).bind(object);
                        break;
                    case Crime.TYPE_POLICE:
                        ((PoliceCrimeHolder)holder).bind(object);
                        break;
                }
            }

        }

        @Override
        public int getItemCount()
        {
            if (mCrimes == null)
                return 0;
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position)
        {
            if (mCrimes != null)
            {
                Crime object = mCrimes.get(position);
                if (object != null)
                    return object.getType();
            }
            return 0;
        }

        public void setCrimes(List<Crime> crimes)
        {
            mCrimes = crimes;
        }
    }
}
