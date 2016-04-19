package com.anteya.ecoprotools.object;

/**
 * Created by yenlungchen on 2016/2/24.
 * <p/>
 * 以 Ecopro 為單位的物件
 * <p/>
 * 裡面包含了 Ecopro 的所有資訊
 */
public class Ecopro {

    /**
     * SQLite table 自動配發的 id
     */
    private int id = 0;

    /**
     * Ecopro 名稱
     */
    private String name = "";

    /**
     * Ecopro IP 位址
     */
    private String ipAddress = "";

    /**
     * Ecopro Mac 位址
     */
    private String macAddress = "";
    /**
     * Ecopro Password 密碼
     */
    private String password = "";


    public Ecopro() {
    }

    // region getter & setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
// endregion
}
