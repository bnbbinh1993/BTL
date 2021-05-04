package com.example.btl.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class Pef {
    private static final String SQL = "CREATE TABLE IF NOT EXISTS binh(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(250), time VARCHAR(250),image BLOB)";
    private static final String GETDATA = "SELECT * FROM binh";
    public static final String TAG = "BNB";
    public static final String dayList[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    public static final String monthList[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    public static final String hoursList[] = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    public static final String minuteList[] = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    private static final String listYear[] = new String[200];

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String result;
    private static SharedPreferences preferences;

    public static void getReference(Context c) {
        context = c;
    }

    public static void setLong(String name, long gt) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, gt);
        editor.apply();

    }

    public static long getLong(String name) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        return preferences.getLong(name, 0);
    }

    public static void setInt(String name, int gt) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, gt);
        editor.apply();

    }

    public static int getInt(String name) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        return preferences.getInt(name, 0);
    }

    public static void setString(String name, String gt) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, gt);
        editor.apply();
    }

    public static String getString(String name, String er) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        return preferences.getString(name, er);
    }

    public static void setBoolean(String name, boolean gt) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, gt);
        editor.apply();

    }

    public static boolean getBoolean(String name) {
        preferences = context.getSharedPreferences("HIHI", MODE_PRIVATE);
        return preferences.getBoolean(name, false);
    }

    public static void showMessenger(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hí anh em");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setMessage(content);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static String getDatePicker(Context context) {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener date_listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                result = String.valueOf(day) + "/" + String.valueOf(month + 1)
                        + "/" + String.valueOf(year);

            }
        };

        DatePickerDialog a = new DatePickerDialog(context, date_listener, year, month, day);
        a.create();
        a.show();
        return result;
    }

    public static String[] isListYear() {
        int j = 0;
        for (int i = 1920; i < 2120; i++) {
            listYear[j] = String.valueOf(i);
            j++;
        }
        return listYear;
    }

    @SuppressLint("Recycle")
    public static ArrayList<String> getAllURIImage(Context activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA};

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    public static long getLongTime(String key) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm - dd.MM.yyyy");
        long res = 0;
        try {
            res = Objects.requireNonNull(format.parse(key)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @SuppressLint("ObsoleteSdkInt")
    public static void setFullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
