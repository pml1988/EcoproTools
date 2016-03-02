package com.anteyatec.anteyalibrary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by anteya on 15/7/28.
 */
public class DBHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "iTouchDB.db";
    private final static int DATABASE_VERSION = 1;

    private final static String TAG = "DBHelper";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate Database");
        final String INIT_TABLE = "CREATE TABLE " + AnteyaString.ITOUCH_TABLE + " (" +
                AnteyaString.ITOUCH_SID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AnteyaString.ITOUCH_NAME + " TEXT, " +
                AnteyaString.ITOUCH_IP + " TEXT, " +
                AnteyaString.ITOUCH_GEO_POINT_X + " float, " +
                AnteyaString.ITOUCH_GEO_POINT_Y + " float, " +
                AnteyaString.ITOUCH_SCENE_TITLE + " TEXT, " +
                AnteyaString.ITOUCH_LIGHT_TITLE + " TEXT, " +
                AnteyaString.ITOUCH_MODE + " TEXT);";
        db.execSQL(INIT_TABLE);

        final String INIT_TABLE2 = "CREATE TABLE " + AnteyaString.MODE_TABLE + " (" +
                AnteyaString.MODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AnteyaString.MODE_NAME + " TEXT, " +
                AnteyaString.MODE_SETTINGS + " TEXT);";
        db.execSQL(INIT_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        final String DROP_TABLE = "DROP TABLE IF EXISTS " + "iTouch";
        db.execSQL(DROP_TABLE);
        final String DROP_TABLE2 = "DROP TABLE IF EXISTS " + "Mode";
        db.execSQL(DROP_TABLE2);
        onCreate(db);
    }
//    public void deleteTable(SQLiteDatabase db){
//        final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_PICTURE;
//        db.execSQL(DROP_TABLE);
//        onCreate(db);
//    }
}