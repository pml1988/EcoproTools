package com.anteya.ecoprotools.object;

import android.util.Log;

import com.anteyatec.anteyalibrary.FormatTool;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by yenlungchen on 2016/2/17.
 */
public class ProjectTools {

    public final static String TAG = "ProjectTools";

    // 定時詢問 裝置的各種狀態 (植物燈、空氣幫浦、風扇、水位), command 已經經過 Checksum 計算
    public static final byte[] COMMAND_POLLING = new byte[]{(byte) 0xf0, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    // 停止, command 已經經過 Checksum 計算
    public static final byte[] COMMAND_CHANGE_MODE_0 = new byte[]{(byte) 0xf0, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x73};
    // F1 育苗, command 已經經過 Checksum 計算
    public static final byte[] COMMAND_CHANGE_MODE_1 = new byte[]{(byte) 0xf0, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x73};
    // F2 生長, command 已經經過 Checksum 計算
    public static final byte[] COMMAND_CHANGE_MODE_2 = new byte[]{(byte) 0xf0, (byte) 0x02, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x74};
    // F3 開花, command 已經經過 Checksum 計算
    public static final byte[] COMMAND_CHANGE_MODE_3 = new byte[]{(byte) 0xf0, (byte) 0x02, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x75};
    // 手動, command 已經經過 Checksum 計算
    public static final byte[] COMMAND_CHANGE_MODE_4 = new byte[]{(byte) 0xf0, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x76};

    // 設定手動時間
    public static final byte[] COMMAND_MANUAL = new byte[]{(byte) 0xf0, (byte) 0x03, (byte) 0x04
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
            , (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};


    // region data format

    /**
     * 輸入 幾點, 轉成格式化輸出 12 小時制
     *
     * @param hourTime
     * @return AM 12:00
     */
    public static String getTimeString(int hourTime) {
        return getTimeString(hourTime, 0);
    }

    /**
     * 輸入 幾點幾分, 轉成格式化輸出 12 小時制
     *
     * @param hourTime
     * @param minuteTime
     * @return AM 05:06
     */
    public static String getTimeString(int hourTime, int minuteTime) {

        // 時區轉美國是為了要顯示 AM/PM, 若沒有設定則顯示中文的上午下午
        Locale locale = new Locale("en", "US");
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", locale);

        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hourTime, minuteTime);

        return formatter.format(calendar.getTime());
    }

    /**
     * 取得 Checksum, 並放回 byte array 最後一個位置, 作為 確認資料傳輸正確的依據
     * 但有一點需要注意的就是, 有的指令沒有 Checksum
     *
     * @param byteArray 待計算的指令陣列
     * @return 計算完 Checksum 並將 Checksum 放回最後一位的 byte array
     */
    public static byte[] getChecksumArray(byte[] byteArray) {

        int checksum = 0;
        // 先將 array 裡 除了 checksum 以外的所有 byte 加總
        for (int i = 0; i < byteArray.length - 1; i++) {
            checksum = (checksum + (byteArray[i] & 0xff));
        }

        // 計算 checksum, 只取 bit0 ~ bit6, 也就是 bit7 保持 0
        // 舉例：如果 byte array length = 3
        // array 值為  0xf0, 0x01, 0x00(checksum)
        // 去計算加總後為 0xf1 也就是 十進位的 241 , 換成 二進位為 1111 0001
        // 把 bit7 改為 0, 就變成 0111 0001 也就是 十進位的 113 = 0x71, 再將其放回陣列最後一位便完成計算
        checksum = checksum & 0x7f; // 0x7f = 127
        // 再把 checksum 放到最後一個位子
        byteArray[byteArray.length - 1] = (byte) checksum;

        return byteArray;
    }

    public static byte[] getCommandChangeMode(int mode) {
        switch (mode) {
            case 0:
                return COMMAND_CHANGE_MODE_0;
            case 1:
                return COMMAND_CHANGE_MODE_1;
            case 2:
                return COMMAND_CHANGE_MODE_2;
            case 3:
                return COMMAND_CHANGE_MODE_3;
            case 4:
                return COMMAND_CHANGE_MODE_4;
        }
        return null;
    }

    // endregion

    // region Extract from ASIX protocol ack

    /**
     * ASIX UDP protocol 回傳的 byte array
     * 從 byte array 裡取出 Mac Address
     *
     * @param tempArray 收到的 byteArray
     * @return 格式化過後的 Mac Address String
     */
    public static String getMacFromAck(byte[] tempArray) {

        String commandString = "";

        for (int i = 26; i <= 31; i++) {
            commandString = commandString + convertByteToHexString(tempArray[i]);
            if (i < 31)
                commandString = commandString + " : ";
        }
        return commandString;
    }

    /**
     * ASIX UDP protocol 回傳的 byte array
     * 從 byte array 裡取出 SSID
     *
     * @param tempArray 收到的 byteArray
     * @return SSID String
     */
    public static String getSSIDFromAck(byte[] tempArray) {

        String commandString;
        int ssidLength = tempArray[55]; // 先取得SSID長度

        byte[] ssidArray = new byte[ssidLength];
        System.arraycopy(tempArray, 56, ssidArray, 0, ssidArray.length);

        commandString = new String(ssidArray);

        return commandString;
    }

    /**
     * ASIX UDP protocol 回傳的 byte array
     * 從 byte array 裡取出 password
     *
     * @param tempArray 收到的 byteArray
     * @return password String
     */
    public static String getPasswordFromAck(byte[] tempArray) {

        String commandString;
        int passwordLength = tempArray[95]; // 先取得密碼長度

        if (passwordLength == 0)
            return "";

        byte[] ssidArray = new byte[passwordLength];
        System.arraycopy(tempArray, 168, ssidArray, 0, ssidArray.length);

        commandString = new String(ssidArray);

        return commandString;
    }


    /**
     * ASIX UDP protocol 回傳的 byte array
     * 從 byte array 裡取出 Network Mode
     *
     * @param tempArray 收到的 byteArray
     * @return 0:Client, 1:server
     */
    public static int getNetworkModeFromAck(byte[] tempArray) {
        int networkMode = tempArray[53];
        return networkMode;
    }

    /**
     * ASIX UDP protocol 回傳的 byte array
     * 從 byte array 裡取出 Channel number
     *
     * @param tempArray 收到的 byteArray
     * @return 1 ~ 11
     */
    public static int getChannelFromAck(byte[] tempArray) {
        int channel = tempArray[54];
        return channel;
    }

    /**
     * ASIX UDP protocol 回傳的 byte array
     * 從 byte array 裡取出 Encryption Mode 加密選項
     * Encryption Mode
     * 0:No Security,
     * 1:WEP 64 (本案未使用),
     * 2:WEP 128 (本案未使用),
     * 3:TKIP,
     * 4:AES
     * <p/>
     * 但本案只有三個選項 0:No Security, 1:TKIP, 2:AES
     * 會轉成這三個選項的對應參數再返回
     *
     * @param tempArray 收到的 byteArray
     * @return 0:No Security,
     * 1:TKIP,
     * 2:AES
     */
    public static int getEncryptionModeFromAck(byte[] tempArray) {

        int encryptionMode = tempArray[92];

        return convertASIXEncryptionToLocal(encryptionMode);
    }
    // endregion

    // region Extract from Anteya protocol ack

    /**
     * 從回傳的資料中 取出 目前模式
     *
     * @param byteArray
     * @return
     */
    public static int getEcoproModeIndex(byte[] byteArray) {
        switch (byteArray[2]) {
            case 0:
                Log.d(TAG, "模式 " + byteArray[2] + " 停止");
                break;
            case 1:
                Log.d(TAG, "模式 " + byteArray[2] + " 育苗");
                break;
            case 2:
                Log.d(TAG, "模式 " + byteArray[2] + " 生長");
                break;
            case 3:
                Log.d(TAG, "模式 " + byteArray[2] + " 開花");
                break;
            case 4:
                Log.d(TAG, "模式 " + byteArray[2] + " 手動");
                break;
        }
        return byteArray[2] & 0xff;
    }


    public static final int ECOPRO_ON_TIME = 0;
    public static final int ECOPRO_OFF_TIME = 2;
    public static final int ECOPRO_LIGHT = 3;
    public static final int ECOPRO_AIR = 9;
    public static final int ECOPRO_FAN = 15;

    public static String getEcoproOnTime(int deviceType, int onOffType, byte[] byteArray) {

        try {

            System.out.println("ProjectTools："+(byteArray[deviceType + onOffType])+":"+convertByteToHexString(byteArray[deviceType + onOffType + 1]));



            return convertByteToHexString(byteArray[deviceType + onOffType]) + ":" + convertByteToHexString(byteArray[deviceType + onOffType + 1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("錯誤ProjectTools_getEcoproOnTime:" + e);
            return "00:00";
        }

    }

    public static int getEcoproWorkStatus(int deviceType, byte[] byteArray) {

        try {
            return byteArray[deviceType + 5];
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("錯誤ProjectTools_getEcoproWorkStatus:" + e);
            return 0;
        }

    }

    public static int getEcoproWaterStatus(byte[] byteArray) {

        try {
            return byteArray[21];
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ProjectTools_getEcoproWaterStatus:" + e);
            return 0;
        }


    }


    public static int getEcoproOnOffStatus(int deviceType, byte[] byteArray) {

        try {
            return byteArray[deviceType + 4];
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("錯誤ProjectTools_getEcoproOnOffStatus:" + e);
            return 0;
        }


    }

    // endregion

    // region IOTC protocol

    public static final int AVIOTC_WIFI_APENC_WEP = 0x02;
    public static final int AVIOTC_WIFI_APENC_WPA_TKIP = 0x03;
    public static final int AVIOTC_WIFI_APENC_WPA_AES = 0x04;

    public static byte[] getIOControlCommand_wifiSettings(String ssid, String password, int securityMode) {

        byte[] byteArray = new byte[76];

        // 設定 SSID byte
        byte[] ssidArray = convertSsidToByteArray(ssid);

        // 設定 password byte
        byte[] passwordArray = convertSsidToByteArray(password);

        System.arraycopy(ssidArray, 0, byteArray, 0, ssidArray.length);
        System.arraycopy(passwordArray, 0, byteArray, 32, passwordArray.length);

        byteArray[65] = (byte) 0x01;
        byteArray[66] = (byte) securityMode;
        return byteArray;

    }
    //endregion

    // region convert

    /**
     * 因本案僅只使用 3種 encryption, 故將ASIX版本轉成本案版本的 encryption mode number
     *
     * @param encryptionMode
     * @return
     */
    public static int convertASIXEncryptionToLocal(int encryptionMode) {
        switch (encryptionMode) {
            case 0:
                break;
            case 1:// 若 WEP 64 一律轉成 AES
                encryptionMode = 2;
                break;
            case 2:// 若 WEP 128 一律轉成 AES
                encryptionMode = 2;
                break;
            case 3:
                encryptionMode = 1;
                break;
            case 4:
                encryptionMode = 2;
                break;
        }
        return encryptionMode;
    }

    /**
     * 因本案僅只使用 3種 encryption, 故須轉成ASIX版本對應的 encryption mode number
     *
     * @param encryptionMode
     * @return
     */
    public static int convertLocalEncryptionToASIX(int encryptionMode) {
        switch (encryptionMode) {
            case 0:
                break;
            case 1:
                encryptionMode = 3;
                break;
            case 2:
                encryptionMode = 4;
                break;
        }
        return encryptionMode;
    }

    /**
     * 將 Wi-Fi SSID 轉成 byte array 放入 ASIX格式(32bytes) 並回傳
     *
     * @param ssid
     * @return
     */
    public static byte[] convertSsidToByteArray(String ssid) {

        byte[] ssidArray = new byte[32];

        // 初始化預設放 0xff
        Arrays.fill(ssidArray, (byte) 0xff);

        if (ssid.length() > 0) {
            try {
                byte[] bytes = ssid.getBytes("UTF-8");
                System.arraycopy(bytes, 0, ssidArray, 0, bytes.length);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ssidArray;
    }

    /**
     * 將 Wi-Fi password 轉成 byte array 放入 ASIX格式(64bytes) 並回傳
     *
     * @param password
     * @return
     */
    public static byte[] convertPasswordToByteArray(String password) {

        byte[] passwordArray = new byte[64];

        // 初始化預設放 0xff
        Arrays.fill(passwordArray, (byte) 0xff);

        if (password.length() > 0) {
            try {
                byte[] bytes = password.getBytes("UTF-8");
                System.arraycopy(bytes, 0, passwordArray, 0, bytes.length);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return passwordArray;
    }

    /**
     * 將 ASIXXISA 轉成 byte array, 作為 亞信 API 的 Identification
     *
     * @return ASIXXISA 的 byte array
     */
    public static byte[] convertASIXToByteArray() {

        String asixxisa = "ASIXXISA";

        byte[] bytes = new byte[8];

        try {
            bytes = asixxisa.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * 將 byte 轉為 Hex String
     *
     * @param b
     * @return Hex String
     */
    public static String convertByteToHexString(byte b) {
        byte[] temp = new byte[1];
        temp[0] = b;
        char[] hexChars = new char[temp.length * 2];
        for (int j = 0; j < temp.length; j++) {
            int v = temp[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        //    int intValue = Integer.parseInt(new String(hexChars));

           System.out.println("幹怎麼回事2："+new String(hexChars));



        return new String(hexChars);
    }

    // endregion

    // region print data

    /**
     * 將 ASIX UDP protocol 回傳的 byte array 用Log印出來
     * 可設定一行顯示幾個 bytes
     *
     * @param byteArray    需整理輸出的 byte array
     * @param logTitle     Log 的標題
     * @param columnNumber 一行顯示幾個 bytes
     */
    public static void printByteArray(byte[] byteArray, String logTitle, int columnNumber) {

        String commandString = "";

        for (int i = 0; i < byteArray.length; i++) {
            commandString = commandString + convertByteToHexString(byteArray[i]);
            // 判斷是否換行
            if (i % columnNumber == (columnNumber - 1)) {
                // 換行加行目
                commandString = commandString + "\n " + ((i / columnNumber) + 1 + " ");
            } else {
                // 不換行就加逗號
                commandString = commandString + ", ";
            }
        }
        Log.d(TAG, logTitle + " : \n 0 " + commandString);
        //  System.out.println("控制機資料：\n"+commandString);
    }

    /**
     * 顯示 list 中的所有 Ecopro 資訊
     *
     * @param list
     */
    public static void printEcoproList(List<Ecopro> list) {

        Log.d(TAG, "=====================================\n");
        Log.d(TAG, "Print Ecopro List : \n");
        Log.d(TAG, "-");

        for (Ecopro ecopro : list) {

            Log.d(TAG, "| Id   : " + ecopro.getId());
            Log.d(TAG, "| Name : " + ecopro.getName());
            Log.d(TAG, "| Ip   : " + ecopro.getIpAddress());
            Log.d(TAG, "| Mac  : " + ecopro.getMacAddress());
            Log.d(TAG, "-");
        }
        Log.d(TAG, "=====================================\n");
    }

    /**
     * 將 Ecopro polling command ack 回來的 資料做分析並 Log 出來
     *
     * @param byteArray
     */
    public static void printEcoproStatusArray(byte[] byteArray) {

        switch (byteArray[2]) {
            case 0:
                Log.d(TAG, "模式 " + byteArray[2] + "停止");
                break;
            case 1:
                Log.d(TAG, "模式 " + byteArray[2] + "育苗");
                break;
            case 2:
                Log.d(TAG, "模式 " + byteArray[2] + "生長");
                break;
            case 3:
                Log.d(TAG, "模式 " + byteArray[2] + "開花");
                break;
            case 4:
                Log.d(TAG, "模式 " + byteArray[2] + "手動");
                break;
        }

//        Log.d(TAG, "燈光 開啟時間 " + convertByteToHexString(byteArray[3]) + ":" + convertByteToHexString(byteArray[4]) +  ", 關閉時間 " + convertByteToHexString(byteArray[5]) + ":" + convertByteToHexString(byteArray[6])
//                + " 控制狀態 " + tempOnOff(byteArray[7]) + ", 負載狀態 " + tempWork(byteArray[8]));
//        Log.d(TAG, "幫浦 開啟時間 " + convertByteToHexString(byteArray[9]) + ":" + convertByteToHexString(byteArray[10]) +  ", 關閉時間 " + convertByteToHexString(byteArray[11]) + ":" + convertByteToHexString(byteArray[12])
//                + " 控制狀態 " + tempOnOff(byteArray[13]) + ", 負載狀態 " + tempWork(byteArray[14]));
//        Log.d(TAG, "風扇 開啟時間 " + convertByteToHexString(byteArray[15]) + ":" + convertByteToHexString(byteArray[16]) +  ", 關閉時間 " + convertByteToHexString(byteArray[17]) + ":" + convertByteToHexString(byteArray[18])
//                + " 控制狀態 " + tempOnOff(byteArray[19]) + ", 負載狀態 " + tempWork(byteArray[20]));
//        Log.d(TAG, "水位 " + tempWater(byteArray[21]));

    }


    public static String tempOnOff(int value) {
        if (value == 0) {
            return "關";
        } else if (value == 1) {
            return "開";
        } else {
            return "" + value;
        }
    }

    public static String tempWork(int value) {
        if (value == 0) {
            return "正常";
        } else if (value == 1) {
            return "錯誤";
        } else {
            return "" + value;
        }
    }

    public static String tempWater(int value) {
        if (value == 0) {
            return "正常";
        } else if (value == 1) {
            return "低水位";
        } else {
            return "" + value;
        }
    }

    // endregion
}
