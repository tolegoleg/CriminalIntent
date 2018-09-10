package com.tch9.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment
{
    public static final String EXTRA_TIME = "com.tch9.criminalintent.time";
    private static final String ARG_TIME = "time";

    private EditText mHourEditText;
    private EditText mMinuteEditText;
    private EditText mSecondEditText;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mSecond;

    public static TimePickerFragment newInstance(Date date)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState)
    {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mHourEditText = (EditText) v.findViewById(R.id.et_hour);
        mMinuteEditText = (EditText) v.findViewById(R.id.et_minute);
        mSecondEditText = (EditText) v.findViewById(R.id.et_second);

        mHourEditText.setText(Integer.toString(mHour));
        mMinuteEditText.setText(Integer.toString(mMinute));
        mSecondEditText.setText(Integer.toString(mSecond));


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mHour = Integer.parseInt(mHourEditText.getText().toString());
                        mMinute = Integer.parseInt(mMinuteEditText.getText().toString());
                        mSecond = Integer.parseInt(mSecondEditText.getText().toString());

                        Date date = new GregorianCalendar(mYear, mMonth, mDay, mHour, mMinute, mSecond).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                }).setNegativeButton(android.R.string.no, null)
                .create();
    }

    private void sendResult(int resultCode, Date date)
    {
        if (getTargetFragment() == null)
        {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
