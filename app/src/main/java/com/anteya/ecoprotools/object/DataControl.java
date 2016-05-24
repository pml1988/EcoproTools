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
    private final static String PREF_IP_ADDRESS_wan = "PREF_IP_ADDRESS_wan";
    private final static String PREF_PORT = "PREF_PORT";
    private final static String PREF_MAC_ADDRESS = "PREF_MAC_ADDRESS";

    private int port_local =8023;

    private int port_wan = 80;

    private int port_use =0;
    private String ipaddress_wan ="";

    private int pd_one = 0;
    private int pd_two = 0;
    private int pd_three = 0;
    private int pd_four = 0;


    public String getIpaddress_wan() {
        return ipaddress_wan;
    }

    public void setIpaddress_wan(String ipaddress_wan) {
        this.ipaddress_wan = ipaddress_wan;
    }

    public int getPort_use() {
        return port_use;
    }

    public void setPort_use(int port_use) {
        this.port_use = port_use;
    }

    public int getPort_wan() {
        return port_wan;
    }

    public int getPort_local() {
        return port_local;
    }

    public void setPort_local(int port_local) {
        this.port_local = port_local;
    }

    public void setPort_wan(int port_wan) {
        this.port_wan = port_wan;
    }

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
    public void saveIpAddress(String ipAddress ,String ipAddress_wan, int port) {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IP_ADDRESS, ipAddress).commit();
        userData.edit().putString(PREF_IP_ADDRESS_wan, ipAddress_wan).commit();
        userData.edit().putInt(PREF_PORT, port).commit();
    }

    public String getIpAddress() {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IP_ADDRESS, "");
        String tempStr_wan = userData.getString(PREF_IP_ADDRESS_wan, "");
        int port = userData.getInt(PREF_PORT,0);
        port_use = port;
        ipaddress_wan = tempStr_wan;
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
