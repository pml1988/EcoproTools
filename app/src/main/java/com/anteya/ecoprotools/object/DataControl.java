package com.anteya.ecoprotools.object;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by yenlungchen on 2016/2/19.
 * <p/>
 * 負責管理 SharedPreferences 的物件
 */
public class DataControl extends Application {


    private final static String PREF = "ANTEYA_PREF";
    private final static String PREF_IP_CAMERA_UID = "PREF_IP_CAMERA_UID";
    private final static String PREF_IP_CAMERA_PASSWORD = "PREF_IP_CAMERA_PASSWORD";
    private final static String PREF_IP_ADDRESS = "PREF_IP_ADDRESS";
    private final static String PREF_MAC_ADDRESS = "PREF_MAC_ADDRESS";

    private int pd_one = 0;
    private int pd_two = 0;
    private int pd_three = 0;
    private int pd_four = 0;

    public void setPd_one(int pd_one) {
        this.pd_one = pd_one;
    }

    public void setPd_two(int pd_two) {
        this.pd_two = pd_two;
    }

    public void setPd_three(int pd_three) {
        this.pd_three = pd_three;
    }

    public void setPd_four(int pd_four) {
        this.pd_four = pd_four;
    }

    public int getPd_one() {
        return pd_one;
    }

    public int getPd_two() {
        return pd_two;
    }

    public int getPd_three() {
        return pd_three;
    }

    public int getPd_four() {
        return pd_four;
    }

    /**
     * 紀錄 UID
     *
     * @param uid 用來連線 IP Camera 的 UID
     */
    public void saveIpCameraUid(String uid) {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IP_CAMERA_UID, uid).commit();
    }

    public String getIpCameraUid() {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IP_CAMERA_UID, "");
        return tempStr;
    }


    /**
     * 紀錄 Password
     *
     * @param password 用來連線 IP Camera 的 Password
     */
    public void saveIpCameraPassword(String password) {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IP_CAMERA_PASSWORD, password).commit();
    }

    public String getIpCameraPassword() {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IP_CAMERA_PASSWORD, "");
        return tempStr;
    }

    /**
     * 紀錄 IP Address
     *
     * @param ipAddress 上次連線 的 IP Address
     */
    public void saveIpAddress(String ipAddress) {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IP_ADDRESS, ipAddress).commit();
    }

    public String getIpAddress() {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IP_ADDRESS, "");
        return tempStr;
    }

    /**
     * 紀錄 Mac Address
     *
     * @param macAddress 上次連線 的 IP 的 Mac
     */
    public void saveMacAddress(String macAddress) {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_MAC_ADDRESS, macAddress).commit();
    }

    public String getMacAddress() {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_MAC_ADDRESS, "");
        return tempStr;
    }
}
