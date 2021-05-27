package com.example.btl.data;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
// /data/data/com.bnb.binh.kimtinnsng/databases/trangbi.sql

    @SuppressLint("SdCardPath")
    private static String DB_PATH = "/data/data/com.example.btl/databases/";
    private static final String DB_NAME = "db.db";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    @SuppressLint("ObsoleteSdkInt")
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= 15) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/";
        }

        this.myContext = context;
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void deleteDataBase() {
        String myPath = DB_PATH + DB_NAME;
        SQLiteDatabase.deleteDatabase(new File(myPath));
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database chua ton tai
        }

        if (checkDB != null)
            checkDB.close();

        return checkDB != null;
    }

    private void copyDataBase() throws IOException {

        InputStream myInput = myContext.getAssets().open(DB_NAME);       //mo db trong thu muc assets nhu mot input stream
        String outFileName = DB_PATH + DB_NAME;                           //duong dan den db se tao
        OutputStream myOutput = new FileOutputStream(outFileName);        //mo db giong nhu mot output stream

        byte[] buffer = new byte[1024];                                    //truyen du lieu tu inputfile sang outputfile
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Dong luon
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();      //kiem tra db

        if (dbExist) {
//            copyDataBase();        //khong lam gi ca, database da co roi
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();         //chep du lieu
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}