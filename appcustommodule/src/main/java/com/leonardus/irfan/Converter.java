package com.leonardus.irfan;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converter {

    private static SimpleDateFormat dt_format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private static SimpleDateFormat d_format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static String doubleToRupiah(double value){
        NumberFormat rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        return "Rp " + rupiahFormat.format(Double.parseDouble(String.valueOf(value)));
    }

    public static String getURLForResource (int resourceId) {
        return Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Date stringDTToDate(String date){
        try {
            return dt_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static Date stringDToDate(String date){
        try {
            return d_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String DTToString(int year, int month, int date, int hour, int minute, int second){
        return String.format(Locale.getDefault(), "%4d-%02d-%02d %02d:%02d:%02d",
                year, month, date, hour, minute, second);
    }

    public static String DToString(Date date){
        return d_format.format(date);
    }

    public static String DToString(int year, int month, int date){
        return String.format(Locale.getDefault(), "%4d-%02d-%02d",
                year, month, date);
    }
}

