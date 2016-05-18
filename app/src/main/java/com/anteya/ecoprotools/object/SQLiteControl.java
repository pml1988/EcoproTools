package com.anteya.ecoprotools.object;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yenlungchen on 2016/2/24.
 */
public class SQLiteControl {

    private static final String TAG = "SQLiteControl";

    private SQLiteDBHelper sqLiteDBHelper = null;

    public SQLiteControl(Context context){
        sqLiteDBHelper = new SQLiteDBHelper(context);
    }

    /**
     * 新增一台 Ecopro 的資訊到資料庫
     * @param ecopro Ecopro 資料
     */
    public void addEcopro(Ecopro ecopro){

        if(ecopro == null || ecopro.getName().length() == 0 || ecopro.getIpAddress().length() == 0){
            System.out.println(TAG + " AddEcopro : Fail");
            System.out.println("Fail reason : ecopro == null || ecopro.getName().length() == 0 || ecopro.getIpAddress().length() == 0");
            return;
        }

        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EcoproString.ECOPRO_NAME, ecopro.getName());
        values.put(EcoproString.ECOPRO_IP_ADDRESS, ecopro.getIpAddress());
        values.put(EcoproString.ECOPRO_IP_ADDRESS_WAN, ecopro.getIpAddress_wan());
        values.put(EcoproString.ECOPRO_MAC_ADDRESS, ecopro.getMacAddress());
        values.put(EcoproString.ECOPRO_PASSWORD, ecopro.getPassword());
        long result = db.insert(EcoproString.ECOPRO_TABLE_NAME, null, values);
        db.close();

        System.out.println(TAG + " AddEcopro : 新增 Ecopro, result : " + result);
    }

    /**
     * 修改 Ecopro 的資訊
     * @param ecopro Ecopro 資料
     */
    public void updateEcopro(Ecopro ecopro){

        if(ecopro == null || ecopro.getId() <= 0){
            System.out.println(TAG + " UpdateEcopro : Fail");
            System.out.println("Fail reason : ecopro == null or ecopro.getId() <= 0");
            return;
        }

        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EcoproString.ECOPRO_NAME, ecopro.getName());
        values.put(EcoproString.ECOPRO_IP_ADDRESS, ecopro.getIpAddress());
        values.put(EcoproString.ECOPRO_IP_ADDRESS_WAN, ecopro.getIpAddress_wan());
        values.put(EcoproString.ECOPRO_MAC_ADDRESS, ecopro.getMacAddress());
        values.put(EcoproString.ECOPRO_PASSWORD, ecopro.getPassword());
        String where = EcoproString.ECOPRO_ID + "=" + ecopro.getId();

        int result = db.update(EcoproString.ECOPRO_TABLE_NAME, values, where, null);
        db.close();

        System.out.println(TAG + " UpdateEcopro : 修改 Ecopro, result : " + result);
    }

    /**
     * 刪除 Ecopro
     * @param ecopro Ecopro 資料
     */
    public void deleteEcopro(Ecopro ecopro){

        if(ecopro == null || ecopro.getId() <= 0){
            System.out.println(TAG + " DeleteEcopro : Fail");
            System.out.println("Fail reason : ecopro == null or ecopro.getId() <= 0");
            return;
        }

        SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();
        String where = EcoproString.ECOPRO_ID + "=" + ecopro.getId();
        // 刪除指定編號資料並回傳刪除是否成功
        int result =  db.delete(EcoproString.ECOPRO_TABLE_NAME, where, null);
        db.close();

        System.out.println(TAG + " DeleteEcopro：刪除 Ecopro, result:" + result);
    }



    /**
     * 取得 表單中的所有資料
     * @return 將取得的資料製成 List<Ecopro> 並回傳
     */
    public List<Ecopro> getEcoproArray(){

        List<Ecopro> list = new ArrayList<>();

        SQLiteDatabase db = sqLiteDBHelper.getReadableDatabase();

        Cursor result = db.query(EcoproString.ECOPRO_TABLE_NAME, null, null, null, null, null, null, null);

        System.out.println(TAG + " GetEcoproArray, Ecopro Table 搜尋結果：共 " + result.getCount() + "筆資料");

        while(result.moveToNext()){

            Ecopro ecopro = new Ecopro();
            ecopro.setId(result.getInt(result.getColumnIndex(EcoproString.ECOPRO_ID)));
            ecopro.setName(result.getString(result.getColumnIndex(EcoproString.ECOPRO_NAME)));
            ecopro.setIpAddress(result.getString(result.getColumnIndex(EcoproString.ECOPRO_IP_ADDRESS)));
            ecopro.setIpAddress_wan(result.getString(result.getColumnIndex(EcoproString.ECOPRO_IP_ADDRESS_WAN)));
            ecopro.setMacAddress(result.getString(result.getColumnIndex(EcoproString.ECOPRO_MAC_ADDRESS)));
            ecopro.setPassword(result.getString(result.getColumnIndex(EcoproString.ECOPRO_PASSWORD)));

            list.add(ecopro);
        }

        result.close();
        db.close();

//        ProjectTools.printEcoproList(list);

        return list;
    }

}
