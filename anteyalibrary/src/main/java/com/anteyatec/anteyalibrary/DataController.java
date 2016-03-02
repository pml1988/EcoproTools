package com.anteyatec.anteyalibrary;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by anteya on 15/2/9.
 */
public class DataController extends Application {

    private final String TAG = "Controller";

    private final static String PREF = "Anteya_Pref";
    private final static String PREF_IPADDRESS = "PREF_IPAddress";
    private final static String PREF_IPADDRESS_ARRAY = "PREF_IPAddressArray";
    private final static String PREF_IPADDRESS_CURRENT_INDEX = "PREF_IPAddressCurrentIndex";
    private final static String PREF_CHANNEL_NAME = "PREF_ChannelName";
    private final static String PREF_CHANNEL_COLOR_TYPE = "PREF_ChannelColorType";
    private final static String PREF_CHANNEL_LIGHT_TYPE = "PREF_ChannelLightType";
    private final static String PREF_CHANNEL_LIGHT_NAME = "PREF_ChannelLightName";
    private final static String PREF_WIDTH = "PREF_WIDTH";
    private final static String PREF_Language = "PREF_Language";
    private final static String PREF_CONNECTED_NAME = "PREF_ConnectedName";
    private final static String PREF_HOMEMODE = "PREF_HOMEMODE";
    private final static String PREF_ID_COUNT = "PREF_ID_COUNT";
    private final static String PREF_IMAGE_PATH = "PREF_IMAGE_PATH";
    private final static String PREF_MAINPAGE_WIDTH = "PREF_MAINPAGE_WIDTH";
    private final static String PREF_MAINPAGE_HEIGHT = "PREF_MAINPAGE_HEIGHT";
    private final static String PREF_SETTINGPAGE_WIDTH = "PREF_SETTINGPAGE_WIDTH";
    private final static String PREF_SETTINGPAGE_HEIGHT = "PREF_SETTINGPAGE_HEIGHT";
    private final static String PREF_MAINPAGE_MODE = "PREF_MAINPAGE_MODE";
    private final static String PREF_ITOUCH_WIDTH = "PREF_ITOUCH_WIDTH";
    private final static String PREF_INIT_MODE = "PREF_INIT_MODE";
    /**
     *  儲存情境類型
     */
    private final static String PREF_SCENE_TITLE = "PREF_SCENE_TITLE";
    /**
     *  儲存燈光類型
     */
    private final static String PREF_LIGHT_TITLE = "PREF_LIGHT_TITLE";
    /**
     *  儲存螢幕Density
     */
    private final static String PREF_DENSITY = "PREF_DENSITY";


    public final static int TYPE_BRIGHTNESS = 1;
    public final static int TYPE_COLORTEMP = 2;
    public final static int TYPE_SET_MEMORY = 3;
    public final static int TYPE_RECALL_MEMORY = 4;
    public final static int TYPE_RUN_PROGRAM = 5;
    public final static int TYPE_AIRCON = 6;


    public final static byte ACKTYPE_Error 		= (byte)0xef;
    public final static int RF_ACK_TYPE_GetMacCount = 0x81;
    public final static int RF_ACK_TYPE_GetMac_LIGHT = 0x82;
    public final static int RF_ACK_TYPE_SetGroupID = 0x83;
    public final static int RF_ACK_TYPE_Select = 0x84;
    public final static int RF_ACK_TYPE_GetMac_LUXER = 0x92;
    public final static int RF_ACK_TYPE_GetMac_iIrDa = 0xa2;
    public final static int RF_ACK_TYPE_GetMac_MOTION = 0xb2;
    public final static int RF_ACK_TYPE_GetMac_PLUG = 0xc2;

    public final static int RF_ACK_TYPE_ACK_SUCCESS = 0x01;
    public final static int RF_ACK_TYPE_ACK_LINK_ERROR = 0x02;
    public final static int RF_ACK_TYPE_ACK_NO_DATA= 0x03;
    public final static int RF_CMD_MAC_COUNT = 0x01;
    public final static int RF_CMD_MAC= 0x02;
    public final static int RF_CMD_SET_GROUP_ID= 0x03;
    public final static int RF_CMD_SELECT= 0x04;



    public final static int TYPE_AIRCON_ONOFF 	= 1;
    public final static int TYPE_AIRCON_TEMPUP 	= 2;
    public final static int TYPE_AIRCON_TEMPDOWN= 3;
    public final static int TYPE_AIRCON_FANSPEED= 4;

    public final static byte CMD_TYPE_BRIGHTNESS = (byte)0xff;
    public final static byte CMD_TYPE_COLORTEMP  = (byte)0xfe;
    public final static byte CMD_TYPE_MEMORY     = (byte)0xfd;
    public final static byte CMD_TYPE_PROGRAM    = (byte)0xfc;
    public final static byte CMD_TYPE_AIRCON     = (byte)0xfa;
    public final static byte CMD_TYPE_SETGROUPID = (byte)0xf1;

    public final static int ADD = -1;

    public final static String STRING_CCT = "CCT";
    public final static String STRING_RGB = "RGB";

    public final static String TAG_HOMEMODE = "mode:";

    public final static String[] LIGHT_TYPE = {"Default","Bulb","Downlight","Tube","Panel"};


    /******* 紀錄是否有初始化Mode  *******/
    public void changeModeState(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_INIT_MODE, "1").commit();
    }
    public boolean isModeInit(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_INIT_MODE, "");
        return (tempStr.length()>0);
    }

    /*****************    儲存IP    *****************/
    public void saveIPAddress_ArrayList(ArrayList<Data_ITouch> list){
        String result = "";
        for(int i=0; i< list.size(); i++){
            String temp = "memory:" +
                    list.get(i).getIntId() + "," +
                    list.get(i).getName() + "," +
                    list.get(i).getIpAddress() + "," +
                    list.get(i).getX() + "," +
                    list.get(i).getY();
            result = result + temp;
        }
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IPADDRESS_ARRAY, result).commit();
    }
    /***
     *      iTouch 儲存在 Preference的格式為
     *      <PREF_IPADDRESS_ARRAY>memory:id,name,ip,x,ymemory:id,name,ip,x,y</PREF_IPADDRESS_ARRAY>
     *
     *
     */
    public ArrayList<Data_ITouch> getArrayListIPAddress(){

        ArrayList<Data_ITouch> templist = new ArrayList<Data_ITouch>();

        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IPADDRESS_ARRAY, "");
        if(tempStr.length() > 0){
            // 用"memory:"切開儲存的IP字串
            String[] tempStrArray = tempStr.split("memory:");
            for(int i=1; i<tempStrArray.length; i++){
                // 用","切開ＩＰ名稱跟位址
                String[] tempStrArray2 = tempStrArray[i].split(",");
                Data_ITouch tempData =
                        new Data_ITouch(Integer.parseInt(tempStrArray2[0]),
                                tempStrArray2[1],
                                tempStrArray2[2],
                                Double.parseDouble(tempStrArray2[3]),
                                Double.parseDouble(tempStrArray2[4]));
                templist.add(tempData);
            }
        }

        return templist;
    }
    public void cleanOldVersioniTouch(){

        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IPADDRESS_ARRAY, "").commit();
    }
    // delete 2015.7.31
//    /**
//     * 如果 index = -1，代表是新增 ip
//     * */
//    public ArrayList<Data_ITouch> changeIPAddress_ArrayList(int index, int id, String title, String ip){
//
//        ArrayList<Data_ITouch> templist = new ArrayList<Data_ITouch>();
//
//        SharedPreferences userData = getSharedPreferences(PREF, 0);
//        String tempStr = userData.getString(PREF_IPADDRESS_ARRAY, "");
//        if(tempStr.length() > 0){
//            // 用"memory:"切開儲存的IP字串
//            String[] tempStrArray = tempStr.split("memory:");
//            for(int i=1; i<tempStrArray.length; i++){
//                String[] tempStrArray2 = tempStrArray[i].split(",");
//                Data_ITouch tempData;
//                if(i == index+1){
//                    tempData = new Data_ITouch(id, title, ip,
//                            Double.parseDouble(tempStrArray2[3]),
//                            Double.parseDouble(tempStrArray2[4]));
//                }else{
//                    // 用","切開ＩＰ名稱跟位址
//                    tempData = new Data_ITouch(Integer.parseInt(tempStrArray2[0]),
//                                    tempStrArray2[1],
//                                    tempStrArray2[2],
//                                    Double.parseDouble(tempStrArray2[3]),
//                                    Double.parseDouble(tempStrArray2[4]));
//                }
//                templist.add(tempData);
//            }
//            saveIPAddress_ArrayList(templist);
//        }else{
//            addIPAddress_ArrayList(title, ip);
//        }
//        return templist;
//    }
//    /**
//     *      新增 ip
//     *      也新增ID count
//     *
//     * */
//    public ArrayList<Data_ITouch> addIPAddress_ArrayList(String title, String ip){
//
//        SharedPreferences userData = getSharedPreferences(PREF, 0);
//        String tempStr = userData.getString(PREF_IPADDRESS_ARRAY, "");
//        int intCount = userData.getInt(PREF_ID_COUNT, 0) + 1;
//        tempStr = tempStr + "memory:" + intCount + "," + title + "," + ip + ",0,0";
//
//        userData.edit().putString(PREF_IPADDRESS_ARRAY, tempStr).commit();
//        userData.edit().putInt(PREF_ID_COUNT, intCount).commit();
//
//        return getArrayListIPAddress();
//    }
    public ArrayList<Data_ITouch> deleteIPAddress_ArrayList(int index){

        ArrayList<Data_ITouch> templist = new ArrayList<Data_ITouch>();


        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IPADDRESS_ARRAY, "");
        if(tempStr.length() > 0){
            // 用"memory:"切開儲存的IP字串
            String[] tempStrArray = tempStr.split("memory:");
            for(int i=1; i<tempStrArray.length; i++){
                if(i == index+1){
                    continue;
                }
                // 用","切開ＩＰ名稱跟位址
                String[] tempStrArray2 = tempStrArray[i].split(",");
                Data_ITouch tempData = new Data_ITouch(Integer.parseInt(tempStrArray2[0]),
                        tempStrArray2[1],
                        tempStrArray2[2],
                        Double.parseDouble(tempStrArray2[3]),
                        Double.parseDouble(tempStrArray2[4]));
                templist.add(tempData);
            }
            saveIPAddress_ArrayList(templist);
        }
        return templist;
    }
    /***
     *      iTouch 儲存在 Preference的格式為
     *      <PREF_IPADDRESS_ARRAY>memory:id,name,ip,x,ymemory:id,name,ip,x,y</PREF_IPADDRESS_ARRAY>
     *
     *
     */
    public ArrayList<Data_ITouch> getArrayListIPAddress2(){

        ArrayList<Data_ITouch> templist = new ArrayList<Data_ITouch>();

        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IPADDRESS_ARRAY, "");
        if(tempStr.length() > 0){
            // 用"memory:"切開儲存的IP字串
            String[] tempStrArray = tempStr.split("memory:");
            for(int i=1; i<tempStrArray.length; i++){
                // 用","切開ＩＰ名稱跟位址
                String[] tempStrArray2 = tempStrArray[i].split(",");
                Data_ITouch tempData =
                        new Data_ITouch(Integer.parseInt(tempStrArray2[0]),
                                tempStrArray2[1],
                                tempStrArray2[2],
                                Double.parseDouble(tempStrArray2[3]),
                                Double.parseDouble(tempStrArray2[4]));
                templist.add(tempData);
            }
        }

        return templist;
    }
    /** 從0開始 */
    public void saveCurrentIpIndex(int index) {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putInt(PREF_IPADDRESS_CURRENT_INDEX, index).commit();
    }

    /** 從0開始 */
    public int getCurrentIpIndex() {
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getInt(PREF_IPADDRESS_CURRENT_INDEX, -1);
    }

    // delete. 因為 getArrayListIPAddress()現在已經不在這裡謢行，改到SQLiteController
//    /** 以 default 的index 去取得現在的ip */
//    public String getCurrentIp() {
//        ArrayList<Data_ITouch> list = getArrayListIPAddress();
//        return list.get(getCurrentIpIndex()).getIpAddress();
//    }

    /*****************    儲存語言    *****************/
    public final int LANGUAGE_TW = 1;
    public final int LANGUAGE_EN = 2;
    public void changeLanguage(int index){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        switch(index) {
            case LANGUAGE_TW:
                userData.edit().putString(PREF_Language, "tw").commit();
                break;
            case LANGUAGE_EN:
                userData.edit().putString(PREF_Language, "en").commit();
                break;
        }
    }

    public void changeLanguage(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        if(getLanguageIsTW()) {
            userData.edit().putString(PREF_Language, "en").commit();
        }else{
            userData.edit().putString(PREF_Language, "tw").commit();
        }
    }
    public String getLanguage(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_Language, "tw");
        return tempStr;
    }
    public boolean getLanguageIsTW(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_Language, "tw");
        if(tempStr.equals("tw"))
            return true;
        else
            return false;
    }

    /*****************    儲存已連接的名稱    *****************/
    public void saveConnectedName(String name){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_CONNECTED_NAME, name).commit();
    }
    public String getConnectedName(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_CONNECTED_NAME, "ipHost");
        return tempStr;
    }


    /*****************    儲存螢幕寬度(For AirCon ModeIcon)    *****************/
    public void saveWidth(int width){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_WIDTH, "" + width).commit();
    }
    public int getWidth(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_WIDTH, "" + 0);
        return Integer.parseInt(tempStr);
    }


    /*****************    儲存情境名稱    *****************/
    public void setSceneTitle(String title){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_SCENE_TITLE, title).commit();
    }
    public String getSceneTitle(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_SCENE_TITLE, "0,1,2,3,4,5,6,7,8,9");
        return tempStr;
    }

    public int[] getSceneTitleIntArray(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_SCENE_TITLE, "0,1,2,3,4,5,6,7,8,9");

        int[] intArraySceneTitleIndex = new int[10];

        String[] tempStringArray = tempStr.split(",");
        for (int i = 0; i < 10; i++){
            intArraySceneTitleIndex[i] = Integer.parseInt(tempStringArray[i]);
        }

        return intArraySceneTitleIndex;
    }

    /** 取得燈具名稱的 */
    public void setLightTitle(String title){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_LIGHT_TITLE, title).commit();
    }
    public int[] getLightTitleIntArray(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_LIGHT_TITLE, "0,1,2,3,4,5,6,7,8,9");

        int[] intArraySceneTitleIndex = new int[10];

        String[] tempStringArray = tempStr.split(",");
        for (int i = 0; i < 10; i++){
            intArraySceneTitleIndex[i] = Integer.parseInt(tempStringArray[i]);
        }

        return intArraySceneTitleIndex;
    }

    /*****************    ipAddress    *****************/
    public void saveIPAddress(String ip){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IPADDRESS, ip).commit();
    }
    public String getIPAddress(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getString(PREF_IPADDRESS, "");
    }

    public ArrayList<Data_IPAddress> getIPAddress_ArrayList(){

        ArrayList<Data_IPAddress> templist = new ArrayList<Data_IPAddress>();

        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_IPADDRESS_ARRAY, "");
        if(tempStr.length() > 0){
            // 用"memory:"切開儲存的IP字串
            String[] tempStrArray = tempStr.split("memory:");
            for(int i=1; i<tempStrArray.length; i++){
                // 用","切開ＩＰ名稱跟位址
                String[] tempStrArray2 = tempStrArray[i].split(",");
                Data_IPAddress tempData = new Data_IPAddress(tempStrArray2[0], tempStrArray2[1]);
                templist.add(tempData);



            }
        }

        return templist;
    }
    /*****************    ChannelName    *****************/
    public void saveChannelName(int index, String Name){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_CHANNEL_NAME+index, Name).commit();
    }
    public String getChannelName(int index){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getString(PREF_CHANNEL_NAME+index, "Channel "+index);
    }
    /*****************    ChannelColorType    *****************/
    public void saveChannelColorType(int index, String colorType){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_CHANNEL_COLOR_TYPE + index, colorType).commit();
    }
    public int getChannelColorTypeByIndex(int index){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_CHANNEL_COLOR_TYPE + index, STRING_CCT);
        if(tempStr.equals(STRING_CCT)){
            return 0;
        }else if(tempStr.equals(STRING_RGB)){
            return 1;
        }else{
            return -1;
        }
    }
    /*****************    ChannelLightType    *****************/
    public void saveChannelLightType(int index, String type){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_CHANNEL_LIGHT_TYPE + index, type).commit();
    }
    public int getChannelLightTypeByIndex(int index){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_CHANNEL_LIGHT_TYPE + index, LIGHT_TYPE[0]);

        for(int i=0; i< LIGHT_TYPE.length; i++){
            if(tempStr.equals(LIGHT_TYPE[i])){
                return i;
            }
        }
        return -1;
    }

    /**
     * 回傳 開啟的 燈具圖片
     * @param type
     * @return
     */
    public Bitmap getBulbOnBitmap_ByBulbIndex(int type){

        switch(getChannelLightTypeByIndex(type)){
            case 0:
                return decodeFile_int(R.drawable.light_on, 100);
            case 1:
                return decodeFile_int(R.drawable.light_type_bulb_on, 100);
            case 2:
                return decodeFile_int(R.drawable.light_type_downlight_on, 100);
            case 3:
                return decodeFile_int(R.drawable.light_type_tube_on, 100);
            case 4:
                return decodeFile_int(R.drawable.light_type_panel_on, 100);
            default:
                break;
        }
        return null;
    }
    /**
     * 回傳 關閉的 燈具圖片
     * @param type 燈具類型
     * @return
     */
    public Bitmap getBulbOffBitmap_ByBulbIndex(int type){

        switch(getChannelLightTypeByIndex(type)){
            case 0:
                return decodeFile_int(R.drawable.light_off, 100);
            case 1:
                return decodeFile_int(R.drawable.light_type_bulb_off, 100);
            case 2:
                return decodeFile_int(R.drawable.light_type_downlight_off, 100);
            case 3:
                return decodeFile_int(R.drawable.light_type_tube_off, 100);
            case 4:
                return decodeFile_int(R.drawable.light_type_panel_off, 100);
            default:
                break;
        }
        return null;
    }


    /***
     *      常用模式 儲存在 Preference的格式為
     *      <PREF_HOMEMODE>mode:回家模式,1:1&2:3&3:0mode:出門模式,1:0&2:0&3:0</PREF_HOMEMODE>
     *      以這個標籤(PREF_HOMEMODE)取得的字串為  mode:回家模式,1:1&2:3&3:0mode:出門模式,1:0&2:0&3:0
     *      用"mode:"去切開 取得 回家模式,1:1&2:3&3:0
     *      逗點前為模式名稱，逗點後再以&切開取得各個iTouch預設定的狀態
     *
     *      數字代表的狀態意義：
     *      -1為Off
     *      0為On
     *      1-10對應各個Memory狀態
     *
     *
     *      saveModeArrayList(ArrayList<Data_Mode> list)
     *          儲存常用模式到Preference
     *      getModeArrayList()
     *          取得常用模式
     *
     *      delete at 2015/08/04
     * ***/
//    public void saveModeArrayList(ArrayList<Data_Mode> list){
//
//        ArrayList<Data_ITouch> tempList = getArrayListIPAddress();
//        String tempStr = "";
//        for(int i=0; i< list.size(); i++){
//            list.get(i).checkArrayCount(tempList);
//            tempStr = tempStr + "mode:" + list.get(i).getName() + "," + list.get(i).getDataArrayString();
//        }
//        SharedPreferences userData = getSharedPreferences(PREF, 0);
//        userData.edit().putString(PREF_HOMEMODE, tempStr).commit();
//    }

    /**
     * 取得所有模式，並存到ArrayList
     * delete at 2015/08/04
     * */
//    public ArrayList<Data_Mode> getModeArrayList() {
//
//        ArrayList<Data_ITouch> tempList = getArrayListIPAddress();
//        ArrayList<Data_Mode> templist = new ArrayList<Data_Mode>();
//
//        SharedPreferences userData = getSharedPreferences(PREF, 0);
//        String strHomeMode = userData.getString(PREF_HOMEMODE, "");
//        if(strHomeMode.length() > 0){
//            // 用"memory:"切開儲存的IP字串
//            String[] tempStrArray = strHomeMode.split("mode:");
//            for(int i=1; i<tempStrArray.length; i++) {
//                // 用","切開ＩＰ名稱跟位址
//                String[] tempStrArray2 = tempStrArray[i].split(",");
//                Data_Mode tempData = new Data_Mode(tempStrArray2[0], tempStrArray2[1], tempList);
//                templist.add(tempData);
//            }
//        }else{
//            // 若檢查發現內部記憶體無任何常用模式
//            // 手動新增兩種模式，並設定為全關閉
//            ArrayList<Data_ITouch> iTouchList = getArrayListIPAddress();
//
//            // 如果連一台 iTouch 都沒有儲存的話就不執行這裡。
//            if(iTouchList.size() > 0){
//                // 出門模式
//                String tempString = "";
//                for(int i=0 ; i < iTouchList.size() ; i++) {
//                    tempString = tempString + iTouchList.get(i).getIntId() + ":" + "0";
//                    if((i+1) < iTouchList.size()) {
//                        tempString = tempString + "&";
//                    }
//                }
//
//                // 預設的模式名稱，根據當時的語言去做預設名稱的變換。
//                String modeTitle1, modeTitle2;
//                if(getLanguageIsTW()){
//                    modeTitle1 = "出門模式";
//                    modeTitle2 = "回家模式";
//                }else{
//                    modeTitle1 = "Leave";
//                    modeTitle2 = "Arrive";
//                }
//
//                Data_Mode tempData = new Data_Mode(modeTitle1, tempString, tempList);
//                templist.add(tempData);
//
//
//                // 回家模式
//                tempString = "";
//                for(int i=0 ; i < iTouchList.size() ; i++) {
//                    tempString = tempString + iTouchList.get(i).getIntId() + ":" + "-1";
//                    if((i+1) < iTouchList.size()) {
//                        tempString = tempString + "&";
//                    }
//                }
//                tempData = new Data_Mode(modeTitle2, tempString, tempList);
//                templist.add(tempData);
//                saveModeArrayList(templist);
//            }
//        }
//        return templist;
//    }
    public void addToModeArrayList(Data_Mode data){

        SharedPreferences userData = getSharedPreferences(PREF, 0);
        String tempStr = userData.getString(PREF_HOMEMODE, "");

        if(tempStr.equals("")) {
            tempStr = data.getName() + "," + data.getDataArrayString();
        }else {
            tempStr = tempStr + "&" + data.getName() + "," + data.getDataArrayString();
        }

        userData.edit().putString(PREF_HOMEMODE, tempStr).commit();
    }

    public String getImagePath(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getString(PREF_IMAGE_PATH, null);
    }

    public void saveImagePath(String str){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putString(PREF_IMAGE_PATH, str).commit();
    }

    /**
     * 儲存 主畫面的 RelativeLayout 的寬高
     * @param w
     * @param h
     */

    public void saveMainPageWidthHeight(int w, int h){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putInt(PREF_MAINPAGE_WIDTH, w).commit();
        userData.edit().putInt(PREF_MAINPAGE_HEIGHT, h).commit();
    }

    public int getMainPageWidth(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getInt(PREF_MAINPAGE_WIDTH, 0);
    }

    public int getMainPageHeight(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getInt(PREF_MAINPAGE_HEIGHT, 0);
    }

    /**
     * 剛進入 HomeFragment 就把 parentView 的 寬跟高記錄下來，iTouchImage在移動時要以這個數據設定邊界。
     * 並將所有的 iTouchImage裏的數據更新過
     *
     * 這裏的 parentView 指的是 HomeFragment 中 裝載 iTouchImage的 ViewGroup
     *
     * @param w
     * @param h
     */
    public void saveSettingPageWidthHeight(int w, int h){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putInt(PREF_SETTINGPAGE_WIDTH, w).commit();
        userData.edit().putInt(PREF_SETTINGPAGE_HEIGHT, h).commit();
    }

    /**
     * 取得 裝載 iTouchImage 的 ViewGroup 的 寬
     * @return
     */
    public int getSettingPageWidth(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getInt(PREF_SETTINGPAGE_WIDTH, 0);
    }

    /**
     * 取得 裝載 iTouchImage 的 ViewGroup 的 高
     * @return
     */
    public int getSettingPageHeight(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getInt(PREF_SETTINGPAGE_HEIGHT, 0);
    }


    /*****************    列表模式，還是底圖模式    *****************/
    public void saveITouchImageWidth(int w){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        userData.edit().putInt(PREF_ITOUCH_WIDTH, w).commit();
    }

    public int getITouchImageWidth(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        return userData.getInt(PREF_ITOUCH_WIDTH, 0);
    }
    /*****************    列表模式，還是底圖模式    *****************/
    /**
     * 設定主頁模式
     * @param mode true = 底圖模式 , false = 列表模式
     */

    public void saveMainPageMode(boolean mode){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        if(mode){
            // 底圖模式
            userData.edit().putInt(PREF_MAINPAGE_MODE, 1).commit();
        }else {
            // 列表模式
            userData.edit().putInt(PREF_MAINPAGE_MODE, 0).commit();
        }
    }
    public boolean getMainPageMode(){
        SharedPreferences userData = getSharedPreferences(PREF, 0);
        if(userData.getInt(PREF_MAINPAGE_MODE, 1) == 0){
            return false;
        }else{
            return true;
        }
    }








    public static byte returnCommendType(int commendType){
        switch(commendType){
            case TYPE_BRIGHTNESS:
                return (byte)0xff;
            case TYPE_COLORTEMP:
                return (byte)0xfe;
            case TYPE_SET_MEMORY:
                return (byte)0xfd;
            case TYPE_RECALL_MEMORY:
                return (byte)0xfd;
            case TYPE_RUN_PROGRAM:
                return (byte)0xfc;
            default:
                return (byte)0;
        }
    }
    private Bitmap decodeFile_int(int id, int size){
        int new_SIZE=size;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), id, options);
        int scale=1;
        while(options.outWidth/scale/2>=new_SIZE && options.outHeight/scale/2>=new_SIZE)
            scale*=2;
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        return BitmapFactory.decodeResource(getResources(), id, o2);
    }

    public static String getLinkStateMessage_ErrorXXX(String ssid, String ipAddress){
        return ipAddress + "連線失敗";
    }
    public static String getLinkStateMessage_SuccessXXX(String ssid, String ipAddress){
        return ipAddress + "連線成功";
    }


}
