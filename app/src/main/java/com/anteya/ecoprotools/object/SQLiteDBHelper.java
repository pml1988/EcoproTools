package com.anteya.ecoprotools.object;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anteyatec.anteyalibrary.AnteyaString;

/**
 * Created by yenlungchen on 2016/2/24.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper{

    private final static int DATABASE_VERSION = 1;

    private final static String TAG = "SQLiteDBHelper";

    public SQLiteDBHelper(Context context) {
        super(context, EcoproString.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        System.out.println(TAG + " onCreate Database");
        final String INIT_TABLE = "CREATE TABLE " + EcoproString.ECOPRO_TABLE_NAME + " (" +
                EcoproString.ECOPRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EcoproString.ECOPRO_NAME + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_IP_ADDRESS + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_MAC_ADDRESS + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_PASSWORD + " TEXT NOT NULL DEFAULT '');";
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        System.out.println(TAG + " onUpgrade Database");
    }
}
