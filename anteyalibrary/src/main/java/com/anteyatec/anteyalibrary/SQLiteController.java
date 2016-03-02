package com.anteyatec.anteyalibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anteya on 15/7/28.
 */
public class SQLiteController {

    private final String TAG = "SQLiteController";

    private DBHelper dbhelper = null;
    private DataController dataController;

    public SQLiteController(Context context){
        dbhelper = new DBHelper(context);
        dataController = (DataController)context.getApplicationContext();
    }

    /**
     * 舊版本的 iHome Android版 是將 iTouch的 Data存在 user preference裡
     * 開啟 App時發現裡面尚有儲存 Data 的話
     * 將該 Data 轉存到 SQLite 裡，並刪除 user preference
     * @param list 將舊的資料取出 Data List
     */
    public void addOldVersioniTouch(List<Data_ITouch> list){

        for ( Data_ITouch data : list ) {

            SQLiteDatabase db = dbhelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(AnteyaString.ITOUCH_NAME, data.getName());
            values.put(AnteyaString.ITOUCH_IP, data.getIpAddress());
            values.put(AnteyaString.ITOUCH_GEO_POINT_X, data.getX());
            values.put(AnteyaString.ITOUCH_GEO_POINT_Y, data.getY());
            values.put(AnteyaString.ITOUCH_SCENE_TITLE, "0,1,2,3,4,5,6,7,8,9");
            values.put(AnteyaString.ITOUCH_LIGHT_TITLE, "0,1,2,3,4,5,6,7,8,9");
            values.put(AnteyaString.ITOUCH_MODE, "0");
            db.insert(AnteyaString.ITOUCH_TABLE, "0", values);
        }

    }

    public void testAddiTouch(){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.ITOUCH_NAME, "testName");
        values.put(AnteyaString.ITOUCH_IP, "192.168.1.1");
        values.put(AnteyaString.ITOUCH_GEO_POINT_X, "39");
        values.put(AnteyaString.ITOUCH_GEO_POINT_Y, "39");
        values.put(AnteyaString.ITOUCH_SCENE_TITLE, "3,9");
        values.put(AnteyaString.ITOUCH_LIGHT_TITLE, "3,9");
        long id = db.insert(AnteyaString.ITOUCH_TABLE, null, values);

        Log.d("test", "insert success : " + id);
    }

    /**
     * 列印出 iTouch table 所有資料
     */
    public void printITouchTable(){

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        Cursor cursor = db.query(AnteyaString.ITOUCH_TABLE, null, null, null, null, null, null, null);

        Log.d("printITouchTable", "iTouch 搜尋結果：共 " + cursor.getCount() + "筆資料");
        Log.d("printITouchTable", "---");

        while (cursor.moveToNext()) {

            Log.d("printITouchTable", "SID:" + cursor.getLong(0));
            Log.d("printITouchTable", "iTouch name:" + cursor.getString(1));
            Log.d("printITouchTable", "iTouch ip:" + cursor.getString(2));
            Log.d("printITouchTable", "iTouch X:" + cursor.getFloat(3));
            Log.d("printITouchTable", "iTouch Y:" + cursor.getFloat(4));
            Log.d("printITouchTable", "iTouch scene:" + cursor.getString(5));
            Log.d("printITouchTable", "iTouch light:" + cursor.getString(6));
            Log.d("printITouchTable", "iTouch mode:" + cursor.getString(7));
            Log.d("printITouchTable", "---");
        }
        cursor.close();
    }


    /**
     * 列印出 Mode table 所有資料
     */
    public void printModeTable(){

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        Cursor cursor = db.query(AnteyaString.MODE_TABLE, null, null, null, null, null, null, null);

        Log.d("printModeTable", "Mode 搜尋結果：共 " + cursor.getCount() + "筆資料");
        Log.d("printModeTable", "---");

        while (cursor.moveToNext()) {

            Log.d("printModeTable", "MID:" + cursor.getLong(0));
            Log.d("printModeTable", "Mode name:" + cursor.getString(1));
            Log.d("printModeTable", "Mode settings:" + cursor.getString(2));
            Log.d("printModeTable", "---");
        }
        cursor.close();
    }

    /**
     * 新增 iTouch
     * @param name iTouch 名稱
     * @param ipAddress iTouch ip
     */
    public void addiTouch(String name, String ipAddress){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.ITOUCH_NAME, name);
        values.put(AnteyaString.ITOUCH_IP, ipAddress);
        values.put(AnteyaString.ITOUCH_GEO_POINT_X, "0");
        values.put(AnteyaString.ITOUCH_GEO_POINT_Y, "0");
        values.put(AnteyaString.ITOUCH_SCENE_TITLE, "0,1,2,3,4,5,6,7,8,9");
        values.put(AnteyaString.ITOUCH_LIGHT_TITLE, "0,1,2,3,4,5,6,7,8,9");
        values.put(AnteyaString.ITOUCH_MODE, "0");
        long id = db.insert(AnteyaString.ITOUCH_TABLE, null, values);
        Log.d(TAG + "addiTouch", "新增 iTouch 成功, id:" + id);
    }

    /**
     * 修改 iTouch
     * @param iTouch iTouch資料
     */
    public void updateiTouch(Data_ITouch iTouch){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.ITOUCH_NAME, iTouch.getName());
        values.put(AnteyaString.ITOUCH_IP, iTouch.getIpAddress());
        values.put(AnteyaString.ITOUCH_GEO_POINT_X, iTouch.getX());
        values.put(AnteyaString.ITOUCH_GEO_POINT_Y, iTouch.getY());
        values.put(AnteyaString.ITOUCH_SCENE_TITLE, iTouch.getSceneArrayString());
        values.put(AnteyaString.ITOUCH_LIGHT_TITLE, iTouch.getLightArrayString());
        values.put(AnteyaString.ITOUCH_MODE, iTouch.getModeString());

        String where = AnteyaString.ITOUCH_SID + "=" + iTouch.getIntId();

        int result = db.update(AnteyaString.ITOUCH_TABLE, values, where, null);
        db.close();
    }

    /**
     * 刪除 iTouch
     * @param idString 輸入ID 以刪除 ID相符的iTouch
     */
    public void deleteiTouchByID(String idString){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String where = AnteyaString.ITOUCH_SID + "=" + idString;
        // 刪除指定編號資料並回傳刪除是否成功
        db.delete(AnteyaString.ITOUCH_TABLE, where , null);
        db.close();
    }


    public String getCurrentIpByIndex(int index){
        return getiTouchArray().get(index).getIpAddress();
    }

    /**
     * get iTouchList
     * @return iTouch list
     */
    public List<Data_ITouch> getiTouchArray(){

        List<Data_ITouch> list = new ArrayList<Data_ITouch>();

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        Cursor result = db.query(AnteyaString.ITOUCH_TABLE, null, null, null, null, null, null, null);

        Log.d("getiTouchArray", "iTouch 搜尋結果：共 " + result.getCount() + "筆資料");
        Log.d("getiTouchArray", "---");


        while (result.moveToNext()) {

            Data_ITouch tempiTouch = new Data_ITouch();

            tempiTouch.setId(result.getLong(0));
            tempiTouch.setName(result.getString(1));
            tempiTouch.setIpAddress(result.getString(2));
            tempiTouch.setX(result.getFloat(3));
            tempiTouch.setY(result.getFloat(4));
            tempiTouch.setSceneArray(result.getString(5));
            tempiTouch.setLightArray(result.getString(6));
            tempiTouch.setMode(result.getString(7));

            list.add(tempiTouch);

            Log.d("getiTouchArray", "SID:" + result.getLong(0));
            Log.d("getiTouchArray", "iTouch name:" + result.getString(1));
            Log.d("getiTouchArray", "iTouch ip:" + result.getString(2));
            Log.d("getiTouchArray", "iTouch X:" + result.getFloat(3));
            Log.d("getiTouchArray", "iTouch Y:" + result.getFloat(4));
            Log.d("getiTouchArray", "iTouch scene:" + result.getString(5));
            Log.d("getiTouchArray", "iTouch light:" + result.getString(6));
            Log.d("getiTouchArray", "iTouch mode:" + result.getString(7));
            Log.d("getiTouchArray", "---");
        }
        result.close();
        db.close();


        return list;
    }

    /**
     * 以 ID 搜尋 iTouch
     * @param idString id 字串
     * @return 回傳一個 Data_ITouch
     */
    public Data_ITouch getiTouchById(String idString){

        Data_ITouch tempiTouch = new Data_ITouch();

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String where = AnteyaString.ITOUCH_SID + "=" + idString;

        Cursor result = db.query(
                AnteyaString.ITOUCH_TABLE, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {

            // 讀取包裝一筆資料的物件
            tempiTouch.setId(result.getLong(0));
            tempiTouch.setName(result.getString(1));
            tempiTouch.setIpAddress(result.getString(2));
            tempiTouch.setX(result.getFloat(3));
            tempiTouch.setY(result.getFloat(4));
            tempiTouch.setSceneArray(result.getString(5));
            tempiTouch.setLightArray(result.getString(6));
            tempiTouch.setMode(result.getString(7));
        }

        result.close();
        db.close();

        return tempiTouch;
    }

    public Data_ITouch getiTouchByIndex(int index){
        return getiTouchArray().get(index);
    }

    /**
     * 將 iTouch 所有的 mode 欄位都改成 0
     */
    public void cleaniTouchMode(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.ITOUCH_MODE, "0");
        int result = db.update(AnteyaString.ITOUCH_TABLE, values, null, null);
        db.close();
        Log.d(TAG + "cleaniTouchMode", "cleaniTouchMode : " + result);
    }

    /**
     * 將 iTouch 所有的 mode 欄位都改成 mMode
     * @param mMode
     */
    public void cleaniTouchMode(String mMode){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.ITOUCH_MODE, mMode);
        int result = db.update(AnteyaString.ITOUCH_TABLE, values, null, null);
        db.close();
        Log.d(TAG + "cleaniTouchMode", "cleaniTouchMode : " + result);
    }





    // 預設新增兩種模式
    public void initMode(){

        String strMode1, strMode2;
        if (dataController.getLanguageIsTW()){
            strMode1 = "出門模式";
            strMode2 = "回家模式";
        }else{
            strMode1 = "Leave Mode";
            strMode2 = "Arrive Mode";
        }


        cleaniTouchMode("1");
        addMode(strMode1);

        cleaniTouchMode("0");
        addMode(strMode2);

    }


    /**
     * 新增 mode
     * @param mModeName
     */
    public void addMode(String mModeName){

        String tempModeDataStr = getModeSettingsString();

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AnteyaString.MODE_NAME, mModeName);
        values.put(AnteyaString.MODE_SETTINGS, tempModeDataStr);

        long id = db.insert(AnteyaString.MODE_TABLE, null, values);
        Log.d(TAG + "addMode", "新增 Mode, id:" + id);
        db.close();
    }
    public void addMode(Data_Mode mMode){
        addMode(mMode.getName());
    }

    /**
     * 從 iTouch table 的 mode 欄位 取出資料合併成一個String
     *
     * get mode settings string from iTouch table
     *
     * @return "ID:1,2ID:2,0"
     */
    private String getModeSettingsString(){

        String tempModeDataStr = "";
        List<Data_ITouch> tempList = getiTouchArray();
        for (int i=0; i<tempList.size(); i++) {
            Data_ITouch tempiTouch = tempList.get(i);
            tempModeDataStr = tempModeDataStr + "ID:" + tempiTouch.getIntId() + "," + tempiTouch.getModeString();
        }
        return tempModeDataStr;
    }

    /**
     * update Mode
     * @param dataMode
     */
    public void updateMode(Data_Mode dataMode){

        String tempModeDataStr = getModeSettingsString();

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.MODE_NAME, dataMode.getName());
        values.put(AnteyaString.MODE_SETTINGS, tempModeDataStr);

        String where = AnteyaString.MODE_ID + "=" + dataMode.getId();

        int result = db.update(AnteyaString.MODE_TABLE, values, where, null);
        Log.d(TAG + "updateMode", "update Mode, result:" + result);
        db.close();
    }
    public void deleteMode(Data_Mode mMode){

        deleteModeById(mMode.getIdString());
    }
    public void deleteModeById(String idString){

        SQLiteDatabase db = dbhelper.getWritableDatabase();

        String where = AnteyaString.MODE_ID + "=" + idString;
        // 刪除指定編號資料並回傳刪除是否成功
        int result = db.delete(AnteyaString.MODE_TABLE, where , null);
        Log.d(TAG + "deleteModeById", "delete Mode, result:" + result);
        db.close();
    }


    /**
     * 取得 mode list
     * @return
     */
    public List<Data_Mode> getModeArray(){

        List<Data_Mode> list = new ArrayList<>();

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        Cursor cursor = db.query(AnteyaString.MODE_TABLE, null, null, null, null, null, null, null);

        Log.d("getModeArray", "Mode 搜尋結果：共 " + cursor.getCount() + "筆資料");
        Log.d("getModeArray", "---");

        while (cursor.moveToNext()) {

            Data_Mode modeData = new Data_Mode();

            modeData.setId(cursor.getInt(0));
            modeData.setName(cursor.getString(1));
            modeData.setModeSettings(cursor.getString(2));

            list.add(modeData);

            Log.d("getModeArray", "MID:" + cursor.getLong(0));
            Log.d("getModeArray", "Mode name:" + cursor.getString(1));
            Log.d("getModeArray", "Mode settings:" + cursor.getString(2));
            Log.d("getModeArray", "---");
        }
        cursor.close();
        db.close();

        if(list == null){
            Log.e(TAG, "list == null");
        }

        return list;
    }
    public Data_Mode getModeByIndex(int index){

        if(getModeArray().size() == 0){
            Log.e(TAG + "getModeByIndex", "getModeArray().size() == 0");
            return null;
        }else{
            return getModeArray().get(index);
        }
    }
    /**
     * 取得 mode data
     * @param idString
     * @return
     */
    public Data_Mode getModeById(String idString){

        Data_Mode modeData = new Data_Mode();

        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String where = AnteyaString.MODE_ID + "=" + idString;

        Cursor cursor = db.query(
                AnteyaString.MODE_TABLE, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (cursor.moveToFirst()) {
            // 讀取包裝一筆資料的物件

            modeData.setId(cursor.getInt(0));
            modeData.setName(cursor.getString(1));
            modeData.setModeSettings(cursor.getString(2));
        }

        cursor.close();
        db.close();
        return modeData;
    }

    /**
     * get iTouchList
     * @return iTouch list
     */
    public List<Data_ITouch> getiTouchArrayWithMode(String modeDataSettings){

        cleaniTouchMode();

        String[] modeDataArray = modeDataSettings.split("ID:");
        String tempId, tempMode;

        for (int i=1; i<modeDataArray.length; i++){
            String tempString = modeDataArray[i];

            Log.e("", "tempString = " + tempString);

            tempId = tempString.split(",")[0];
            tempMode = tempString.split(",")[1];
            Log.e("", "tempId = " + tempId);
            Log.e("", "tempMode = " + tempMode);
            updateiTouchMode(tempId, tempMode);
        }

        return getiTouchArray();
    }
    public void updateiTouchMode(String idString, String mode){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnteyaString.ITOUCH_MODE, mode);

        String where = AnteyaString.ITOUCH_SID + "=" + idString;

        int result = db.update(AnteyaString.ITOUCH_TABLE, values, where, null);
        Log.d(TAG + "updateiTouchMode", "update iTouch, result:" + result);
        db.close();
    }






    private void closeDatabase(){
        dbhelper.close();
    }

}
