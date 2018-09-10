package com.tch9.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;

public class PhotoFragment extends DialogFragment
{
    private static final String ARG_PHOTO = "photo";

    private Context mContext;

    private ImageView mPhotoView;
    private TextView mTextViewPath;

    private File mPhotoFile;

    public static PhotoFragment newInstance(String path)
    {
        Bundle args = new Bundle();
        args.putString(ARG_PHOTO, path);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String path = (String) getArguments().getString(ARG_PHOTO);
        mContext = getActivity().getApplicationContext();
        File filesDir = mContext.getFilesDir();
        mPhotoFile = new File(filesDir, path);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);

        mTextViewPath = (TextView) v.findViewById(R.id.dialog_photo_path);
        mTextViewPath.setText(mPhotoFile.getPath());

        mPhotoView = (ImageView) v.findViewById(R.id.dialog_photo);
        if (mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
        }
        else
        {
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            Bitmap bitmap = PictureUtils.getBitmap(mPhotoFile.getPath());
            mPhotoView.setImageBitmap(bitmap);
        }



        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.photo_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
