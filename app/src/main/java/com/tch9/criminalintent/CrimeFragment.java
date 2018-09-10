package com.tch9.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment
{
    private static final String ARG_CRIME_ID = "crimeId";
    //private static final String ARG_CRIME_IDX = "crimeIdx";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_TIME = 1;

    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private static final String DIALOG_PHOTO = "DialogPhoto";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private CheckBox mPolicedCheckBox;

    private Button mSuspectButton;
    private Button mReportButton;

    private ImageView mPhotoView;
    private ImageButton mPhotoButton;

    private File mPhotoFile;

    private void updatePhotoView()
    {
        if (mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
        }
        else
        {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private String getCrimeReport()
    {
        String solvedString = null;
        if (mCrime.isSolved())
        {
            solvedString = getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null)
        {
            suspect = getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updateDate()
    {
        CharSequence ts = DateFormat.format("dd.MM.yyyy (EEEE)", mCrime.getDate().getTime()); //  kk:mm:ss
        mDateButton.setText(ts.toString());
        ts = DateFormat.format("kk:mm:ss", mCrime.getDate().getTime()); //  kk:mm:ss
        mTimeButton.setText(ts.toString());
    }

    public static CrimeFragment newInstance(UUID crimeId, int idx)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        //args.putInt(ARG_CRIME_IDX, idx);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void returnResultChangeData(boolean in_bool)
    {
        if (in_bool)
            getActivity().setResult(Activity.RESULT_OK, null);
        else
            getActivity().setResult(Activity.RESULT_CANCELED, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if (requestCode == REQUEST_DATE)
        {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);

            updateDate();
            returnResultChangeData(true);
        }
        else if (requestCode == REQUEST_TIME)
        {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(date);

            updateDate();
            returnResultChangeData(true);
        }
        else if (requestCode == REQUEST_CONTACT && data != null)
        {
            Uri contactUri = data.getData();
            // Определение полей, значения которых должны быть возвращены запросом
            String queryFilds[] = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            // Выполнение запроса - contactUri здесь выполняет функцию условия "where"
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFilds, null, null, null);
            try // проверка получения результатов
            {
                if (c.getCount() == 0)
                {
                    return;
                }
                // Извлечение первого столбца данных - имени подозреваемого
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                returnResultChangeData(true);
            }
            finally
            {
                c.close();
            }
        }
        else if (requestCode == REQUEST_PHOTO)
        {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.tch9.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
            returnResultChangeData(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.delete_crime:
                AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
                alertdialog.setTitle("Вы уверены ?");
                alertdialog.setPositiveButton("Да", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                                // Удаляем фото
                                File photo = new File(getActivity().getApplicationContext().getFilesDir(), mCrime.getPhotoFilename());
                                if (photo != null && photo.exists())
                                {
                                    photo.delete();
                                }
                                // ############
                                returnResultChangeData(true);
                                getActivity().finish();
                            }
                        });
                alertdialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertdialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //int idx = (int) getArguments().getInt(ARG_CRIME_IDX);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (count != after)
                    returnResultChangeData(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                //
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        mTimeButton = (Button) v.findViewById(R.id.crime_time);

        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });


        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                mCrime.setSolved(isChecked);
                returnResultChangeData(true);
            }
        });

        mPolicedCheckBox = (CheckBox) v.findViewById(R.id.crime_policed);
        mPolicedCheckBox.setChecked(mCrime.getPoliced()==Crime.TYPE_SIMPLE?false:true);
        mPolicedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                mCrime.setPoliced(isChecked?Crime.TYPE_POLICE:Crime.TYPE_SIMPLE);
                returnResultChangeData(true);
            }
        });

        if (mCrime.getSuspect() != null && !mCrime.getSuspect().isEmpty())
        {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        else
        {
            mSuspectButton.setText(R.string.crime_suspect_text);
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null)
        {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.tch9.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities)
                {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FragmentManager manager = getFragmentManager();
                PhotoFragment dialog = PhotoFragment.newInstance(mCrime.getPhotoFilename());
                dialog.show(manager, DIALOG_PHOTO);
            }
        });
        updatePhotoView();

        return v;
    }
}
