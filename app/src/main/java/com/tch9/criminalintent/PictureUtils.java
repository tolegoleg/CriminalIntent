package com.tch9.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;

public class PictureUtils
{
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight)
    {
        // Чтение размеров изображения на диске
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // Вычисление степени масштабирования
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth)
        {
            float heightScale = (float) srcHeight / destHeight;
            float widthScale = (float) srcWidth / destWidth;
            inSampleSize = Math.round(heightScale > widthScale?heightScale:widthScale);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Чтение данных и создание итогового изображения
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity)
    {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.y, size.x);
    }

    public static Bitmap getBitmap(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        // Чтение данных и создание итогового изображения
        return BitmapFactory.decodeFile(path, options);
    }
}
