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
        System.out.println("更新資料庫1");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);

        if(newVersion>oldVersion)
        {
            System.out.println("更新資料庫3");

        }
        else
        {
            System.out.println("更新資料庫4");

        }


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("更新資料庫2");
        System.out.println(TAG + " onCreate Database");
        final String INIT_TABLE = "CREATE TABLE " + EcoproString.ECOPRO_TABLE_NAME + " (" +
                EcoproString.ECOPRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EcoproString.ECOPRO_NAME + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_IP_ADDRESS + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_IP_ADDRESS_WAN + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_MAC_ADDRESS + " TEXT NOT NULL DEFAULT '', " +
                EcoproString.ECOPRO_PASSWORD + " TEXT NOT NULL DEFAULT '');";
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        System.out.println(TAG + " onUpgrade Database");

        if(newVersion>oldVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS "+EcoproString.ECOPRO_TABLE_NAME);
            final String INIT_TABLE = "CREATE TABLE " + EcoproString.ECOPRO_TABLE_NAME + " (" +
                    EcoproString.ECOPRO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EcoproString.ECOPRO_NAME + " TEXT NOT NULL DEFAULT '', " +
                    EcoproString.ECOPRO_IP_ADDRESS + " TEXT NOT NULL DEFAULT '', " +
                    EcoproString.ECOPRO_IP_ADDRESS_WAN + " TEXT NOT NULL DEFAULT '', " +
                    EcoproString.ECOPRO_MAC_ADDRESS + " TEXT NOT NULL DEFAULT '', " +
                    EcoproString.ECOPRO_PASSWORD + " TEXT NOT NULL DEFAULT '');";
            db.execSQL(INIT_TABLE);
            System.out.println("更新資料庫成功");
        }
        else
        {
            System.out.println("更新資料庫4");

        }


    }
}
