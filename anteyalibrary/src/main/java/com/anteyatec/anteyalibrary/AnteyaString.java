package com.anteyatec.anteyalibrary;

import android.util.Log;

/**
 * Created by anteya on 15/7/28.
 */
public class AnteyaString {

/*============================ SQLite table name, and column name ============================*/

    public final static String ITOUCH_TABLE = "iTouch";
    public final static String ITOUCH_SID = "SID";
    public final static String ITOUCH_NAME = "Name";
    public final static String ITOUCH_IP = "IP";
    public final static String ITOUCH_GEO_POINT_X = "GeoPointX";
    public final static String ITOUCH_GEO_POINT_Y = "GeoPointY";
    public final static String ITOUCH_SCENE_TITLE = "SceneTitle";
    public final static String ITOUCH_LIGHT_TITLE = "LightTitle";
    public final static String ITOUCH_MODE = "Mode";

    public final static String MODE_TABLE = "Mode";
    public final static String MODE_ID = "SID";
    public final static String MODE_NAME = "Name";
    public final static String MODE_SETTINGS = "ModeSettings";

    // table Curtain(ID、iTouchID、CurtainIndex、CurtainName、NameListIndex)
    public final static String CURTAIN_TABLE = "Curtain";
    public final static String CURTAIN_ID = "ID";
    public final static String CURTAIN_iTouchID = "iTouchID";
    public final static String CURTAIN_CurtainIndex = "CurtainIndex";
    public final static String CURTAIN_CurtainName = "CurtainName";
    public final static String CURTAIN_NameListIndex = "NameListIndex";

    // table iPlug(ID、iTouchID、iPlugIndex、iPlugName、NameListIndex) 
    public final static String IPLUG_TABLE = "iPlug";
    public final static String IPLUG_ID = "ID";
    public final static String IPLUG_iTouchID = "iTouchID";
    public final static String IPLUG_iPlugIndex = "iPlugIndex";
    public final static String IPLUG_iPlugName = "iPlugName";
    public final static String IPLUG_NameListIndex = "NameListIndex";


    public final static int LEADING_CODE_SET_SCENE = 0x85;
    public final static int LEADING_CODE_GET_SCENE = 0x81;
    public final static int LEADING_CODE_SET_LIGHT = 0x86;
    public final static int LEADING_CODE_GET_LIGHT = 0x82;

    public final static String COLON_HELF = ":";
    public final static String COLON_FULL = "：";



    public static String getIpAddress(String ipString){

        String strArray[] = new String[0];

        if(ipString.contains(COLON_FULL)){
            strArray = ipString.split(COLON_FULL);
        }else if(ipString.contains(COLON_HELF)){
            strArray = ipString.split(COLON_HELF);
        }else{
            return ipString;
        }

        if(strArray.length == 2){
            return strArray[0];
        }else if(strArray.length == 1){
            return strArray[0];
        }else {
            return "";
        }
    }

    public static int getPort(String ipString){

        String strArray[] = new String[0];
        int port = 0;

        if(ipString.contains(COLON_FULL)){
            strArray = ipString.split(COLON_FULL);
        }else if(ipString.contains(COLON_HELF)){
            strArray = ipString.split(COLON_HELF);
        }
        if(strArray.length == 2){
            port = Integer.parseInt(strArray[1]);
        }
        if(port == 0){
            port = 8023;
        }
      //  System.out.println("IP："+ipString+"Port："+port);
        return port;
    }
}
