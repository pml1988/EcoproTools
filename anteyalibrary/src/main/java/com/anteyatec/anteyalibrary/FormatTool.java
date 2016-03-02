package com.anteyatec.anteyalibrary;

/**
 * Created by anteya on 15/8/18.
 */

import android.util.Log;

public class FormatTool {

    private static final String TAG = "FormatTool";

    /**
     *  get checkSum
     *
     * */
    public static byte[] getChecksumArray(byte[] tempArray){

        int checksum = 0;
        // 先加總
        for(int i = 0 ; i < tempArray.length -1;i++){
            checksum = ((int)checksum + (byte)tempArray[i]);
            if(tempArray[i] < 0){
                checksum += 256;
            }
        }
        // 計算 checksum
        checksum = checksum & 0x7f; // 0x7f = 127
        // 再把 checksum 放到最後一個位子
        tempArray[tempArray.length-1] = (byte) checksum;

        return tempArray;
    }
    /**
     * 將 ack 回來的 byte array 丟進來這裡檢查 checkSum 是否正確
     *
     * */
    public static boolean checkChecksumArray(byte[] dataArray){

        byte[] tempArray;
        tempArray = FormatTool.getChecksumArray(dataArray);

        if(tempArray[tempArray.length-1] == dataArray[dataArray.length-1])
            return true;
        else
            return false;
    }

    /**
     * 確認checkSum
     * @param tempArray
     * @return
     */
    public static boolean isCheckSumOK(byte[] tempArray){
        int checksum = 0;
        for(int i = 0 ; i < tempArray.length-1;i++){
            checksum = (checksum + tempArray[i]);
            if(tempArray[i] < 0){
                checksum += 256;
            }
        }
        checksum = checksum & 0x7f;
        Log.d("","計算過的 checksum = " + (byte) checksum);
        if(tempArray[tempArray.length-1] == (byte) checksum){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 將準備要發送的 command 印出來
     * @param tempArray
     */
    public static void printCommandArray(byte[] tempArray){

        String commandString = "{";

        for (int i = 0; i<tempArray.length; i++){
            commandString = commandString + byteToHexString(tempArray[i]);
            if (i < tempArray.length-1)
                commandString = commandString + ", ";
        }
        commandString = commandString + "}";
        Log.d(TAG, "send " + commandString);
    }

    /**
     * 將收到的 Ack 印出來
     * @param tempArray
     */
    public static void printAckArray(byte[] tempArray){
        String commandString = "{";
        if (tempArray.length == 2){

            commandString = new String(tempArray);
        }else{

            for (int i = 0; i<tempArray.length; i++){
                commandString = commandString + byteToHexString(tempArray[i]);
                if (i < tempArray.length-1)
                    commandString = commandString + ", ";
            }
            commandString = commandString + "}";
        }
        Log.d(TAG, "ack: " + commandString);
    }

    /** 16進位的byte    >>>    integer */
    public static int byteToInt(byte b){
        int v = b & 0xFF;
        return v;
    }

    /** 16進位的byte    >>>    String */
    public static String byteToString(byte b){

        byte[] test = new byte[1];
        test[0] = b;
        return new String(test);
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String byteToHexString(byte b) {
        byte[] temp = new byte[1];
        temp[0] = b;
        char[] hexChars = new char[temp.length * 2];
        for ( int j = 0; j < temp.length; j++ ) {
            int v = temp[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /** 16進位的byte    >>>    integer
     * 	跟上面的差別在於 個位數的整數 例如 7 加在小數點後面會變成 1.7
     * 	正確地顯示應該是 1.07
     * 	*/
    public static String byteToIntString(byte _byte){
        char[] hexChars = new char[2];
        int v = _byte & 0xFF;
        hexChars[0] = hexArray[v >>> 4];
        hexChars[1] = hexArray[v & 0x0F];
        int temp =Integer.parseInt(new String(hexChars),16);// 將16進位的字串，轉成10進位的integer

        if(temp < 10){
            return "0"+temp;
        }
        return "" + temp;
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }



    /**
     * 以下是指令專用區塊
     *
     */


    /**
     * 處理 TYPE_SET_MEMORY, TYPE_RECALL_MEMORY, TYPE_RUN_PROGRAM 的 command
     * @param type command type
     * @param position 1~10
     * @return
     */
    public static byte[] convertCommendArray(int type, int position){

        byte[] tempArray = new byte[4];
        // leading code
        tempArray[0]= DataController.returnCommendType(type);

        switch(type){

            case DataController.TYPE_SET_MEMORY:

                tempArray[1] = (byte) 1;
                tempArray[2] = (byte) position; // 1~10
                tempArray = FormatTool.getChecksumArray(tempArray);
                break;

            case DataController.TYPE_RECALL_MEMORY:

                tempArray[1] = (byte)2;
                tempArray[2] = (byte) position; // 1~10
                tempArray = FormatTool.getChecksumArray(tempArray);
                break;

            case DataController.TYPE_RUN_PROGRAM:

                tempArray = new byte[5];
                tempArray[0] = DataController.returnCommendType(type);
                tempArray[1] = (byte) 2;
                tempArray[2] = (byte) position; // 1~10
                tempArray[3] = (byte) 0;
                tempArray = FormatTool.getChecksumArray(tempArray);
                break;

        }
        return tempArray;
    }

    /**
     * 取得 iTouch 情境名稱設定
     * @return
     */
    public static byte[] getITouchSceneCommand(){

        byte[] tempArray = new byte[4];
        tempArray[0] = (byte) 0xfd;
        tempArray[1] = (byte) AnteyaString.LEADING_CODE_GET_SCENE;
        tempArray[2] = (byte) 0x00;
        tempArray[3] = (byte) 0x7e;

        return tempArray;
    }

    /**
     * 儲存情境名稱的command
     * @param iTouch
     * @return
     */
    public static byte[] getITouchSaveSceneCommand(Data_ITouch iTouch){

        int[] sceneArray = iTouch.getSceneArrayInt();
        byte[] tempArray;
        tempArray = new byte[13];
        tempArray[0] = (byte) 0xfd;
        tempArray[1] = (byte) AnteyaString.LEADING_CODE_SET_SCENE;
        tempArray[2] = (byte) (sceneArray[0]+1);
        tempArray[3] = (byte) (sceneArray[1]+1);
        tempArray[4] = (byte) (sceneArray[2]+1);
        tempArray[5] = (byte) (sceneArray[3]+1);
        tempArray[6] = (byte) (sceneArray[4]+1);
        tempArray[7] = (byte) (sceneArray[5]+1);
        tempArray[8] = (byte) (sceneArray[6]+1);
        tempArray[9] = (byte) (sceneArray[7]+1);
        tempArray[10] = (byte) (sceneArray[8]+1);
        tempArray[11] = (byte) (sceneArray[9]+1);

        tempArray = FormatTool.getChecksumArray(tempArray);

        return tempArray;
    }
    /**
     * 取得 iTouch 燈光名稱設定
     * @return
     */
    public static byte[] getITouchLightCommand(){

        byte[] tempArray = new byte[4];
        tempArray[0] = (byte) 0xfd;
        tempArray[1] = (byte) AnteyaString.LEADING_CODE_GET_LIGHT;
        tempArray[2] = (byte) 0x00;
        tempArray[3] = (byte) 0x7f;

        return tempArray;
    }
    /**
     * 儲存情境名稱的command
     * @param iTouch
     * @return
     */
    public static byte[] getITouchSaveLightCommand(Data_ITouch iTouch){

        int[] lightArray = iTouch.getLightArrayInt();
        byte[] tempArray;
        tempArray = new byte[13];
        tempArray[0] = (byte) 0xfd;
        tempArray[1] = (byte) AnteyaString.LEADING_CODE_SET_LIGHT;
        tempArray[2] = (byte) (lightArray[0]+1);
        tempArray[3] = (byte) (lightArray[1]+1);
        tempArray[4] = (byte) (lightArray[2]+1);
        tempArray[5] = (byte) (lightArray[3]+1);
        tempArray[6] = (byte) (lightArray[4]+1);
        tempArray[7] = (byte) (lightArray[5]+1);
        tempArray[8] = (byte) (lightArray[6]+1);
        tempArray[9] = (byte) (lightArray[7]+1);
        tempArray[10] = (byte) (lightArray[8]+1);
        tempArray[11] = (byte) (lightArray[9]+1);

        tempArray = FormatTool.getChecksumArray(tempArray);

        return tempArray;
    }



    public static int[] byteArrayToIntegerArray(byte[] tempArray){
        int[] intArray = new int[tempArray.length];

        for (int i=0; i < tempArray.length; i++){
            intArray[i] = tempArray[i] & 0xff;
        }
        return intArray;
    }
}
