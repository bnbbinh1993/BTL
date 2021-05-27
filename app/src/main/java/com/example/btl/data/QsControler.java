package com.example.btl.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class QsControler {
    private DBHelper dbHelper;


    public QsControler(Context context) {
        dbHelper = new DBHelper(context);
    }

    public ArrayList<QsMd> getTrangBiDBS() {
        ArrayList<QsMd> lsData = new ArrayList<QsMd>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM book1", null);
        cursor.moveToFirst();
        do {
            QsMd item;
            item = new QsMd(cursor.getString(0), cursor.getString(1), cursor.getString(2)
                    , cursor.getString(3), cursor.getString(4), cursor.getString(5)
            );

            lsData.add(item);
        } while (cursor.moveToNext());
        return lsData;

    }

}