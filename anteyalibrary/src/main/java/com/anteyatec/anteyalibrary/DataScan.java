package com.anteyatec.anteyalibrary;

/**
 * Created by anteya on 15/10/2.
 */
public class DataScan {

    /*
	0.E1
	1.82
	2.01(OK) 03(NoData)
	3.Mac1   Point
	4.Mac2
	5.Mac3
	6.STX
	7.MTX
	8.Power Integer
	9.Power Float
	10.Temperature
	11.Bright
	12.ColorTemp
	13.GID
	14.ErrorState
	//從第 15 個是否為100(64)，來判斷是新或舊
	*/
    private String bulbGID;
    private String bulbPoint;
    private String bulbMac;
    private String bulbLinkState;
    private String bulbPowerState;
    private String bulbPower;
    private String bulbTemperation;
    private String bulbBrightness;
    private String bulbColorTemp;
    private String bulbMTX;
    private String bulbSTX;

    private String lux;
    private String luxInfo;

    private int objectType = 0;
    private int plugStateInteger;

    public static final int HOST_OFF = 1;
    public static final int HOST_ON = 2;
    public static final int SW_OFF = 3;
    public static final int SW_ON = 4;
    public static final int RELAY_OVER_TEMP = 5;

    public DataScan(byte[] dataArray, int point){

        if(dataArray.length == 15){ // 第一版的RFHost MAC 格式
            bulbGID = "" + dataArray[13];
            bulbPoint = "" + point;
            bulbMac = byteToHexString(dataArray[3]) + ":" + byteToHexString(dataArray[4]) + ":" + byteToHexString(dataArray[5]);
            bulbLinkState = linkState(dataArray[14]);
            bulbPowerState = powerState(dataArray[14]);
            bulbPower = dataArray[8] + "." + dataArray[9];
            bulbTemperation = dataArray[10] + "";
            bulbBrightness = dataArray[11] + "";
            bulbColorTemp = dataArray[12] + "";
            bulbSTX = dataArray[6] + "";
            bulbMTX = dataArray[7] + "";

        }else if(dataArray.length == 16){ // 第二版的RFHost MAC 格式
            bulbGID = "" + dataArray[14];
            bulbPoint = "" + dataArray[3];
            bulbMac = byteToHexString(dataArray[4]) + ":" + byteToHexString(dataArray[5]) + ":" + byteToHexString(dataArray[6]);
            bulbLinkState = linkState(dataArray[15]);
            bulbPowerState = powerState(dataArray[15]);
            bulbPower = dataArray[9] + "." + dataArray[10];
            bulbTemperation = dataArray[11] + "";
            bulbBrightness = dataArray[12] + "";
            bulbColorTemp = dataArray[13] + "";
            bulbSTX = dataArray[7] + "";
            bulbMTX = dataArray[8] + "";

        }else if(dataArray.length == 17){ // 第三版的RFHost MAC 格式
            objectType = FormatTool.byteToInt(dataArray[1]);
            bulbGID = "" + dataArray[14];
            bulbPoint = "" + dataArray[3];
            bulbMac = byteToHexString(dataArray[4]) + ":" + byteToHexString(dataArray[5]) + ":" + byteToHexString(dataArray[6]);
            bulbLinkState = linkState(dataArray[15]);
            bulbPowerState = powerState(dataArray[15]);
            bulbTemperation = dataArray[11] + "";
            bulbBrightness = dataArray[12] + "";
            bulbColorTemp = dataArray[13] + "";
            bulbSTX = dataArray[7] + "";
            bulbMTX = dataArray[8] + "";
            if (objectType == DataController.RF_ACK_TYPE_GetMac_PLUG) {
                plugStateInteger = dataArray[12];
                bulbPower = ((FormatTool.byteToInt(dataArray[16])*256) + FormatTool.byteToInt(dataArray[9])) + "." + FormatTool.byteToIntString(dataArray[10]);
            }else if (objectType == DataController.RF_ACK_TYPE_GetMac_LUXER) {
                lux = ((FormatTool.byteToInt(dataArray[16])*256) + FormatTool.byteToInt(dataArray[9])) + "";
                luxInfo = ((FormatTool.byteToInt(dataArray[12])*256) + FormatTool.byteToInt(dataArray[10])) + "";
            }else {
                bulbPower = FormatTool.byteToInt(dataArray[9]) + "." + FormatTool.byteToIntString(dataArray[10]);
            }
        }
    }
    public String getPlugStateStringTW(){
        return byteToPlugStateTW((byte)plugStateInteger);
    }
    public String getPlugStateStringEN(){
        return byteToPlugStateEN((byte)plugStateInteger);
    }
    public int getPlugStateInteger(){
        return plugStateInteger;
    }
    private String byteToPlugStateTW(byte b){
        String tempStr = "";
        switch(b){
            case HOST_OFF:
                tempStr = "自動關";
                break;
            case HOST_ON:
                tempStr = "自動開";
                break;
            case SW_OFF:
                tempStr = "手動關";
                break;
            case SW_ON:
                tempStr = "手動開";
                break;
            case RELAY_OVER_TEMP:
                tempStr = "過溫關閉";
                break;
        }
        return tempStr;
    }
    private String byteToPlugStateEN(byte b){
        String tempStr = "";
        switch(b){
            case HOST_OFF:
                tempStr = "HostOff";
                break;
            case HOST_ON:
                tempStr = "HostOn";
                break;
            case SW_OFF:
                tempStr = "SW Off";
                break;
            case SW_ON:
                tempStr = "SW On";
                break;
            case RELAY_OVER_TEMP:
                tempStr = "Over Temp";
                break;
        }
        return tempStr;
    }

    public boolean isiPlug(){
        return (objectType == DataController.RF_ACK_TYPE_GetMac_PLUG);
    }
    public boolean isALS(){
        return (objectType == DataController.RF_ACK_TYPE_GetMac_LUXER);
    }
    public boolean isLight(){
        return (objectType == DataController.RF_ACK_TYPE_GetMac_LIGHT);
    }
    public boolean isiIrDa(){
        return (objectType == DataController.RF_ACK_TYPE_GetMac_iIrDa);
    }

    private String linkState(byte b){
        String tempStr = "";
        switch(b){
            case (byte)0x00:
                tempStr = "OK";
                break;
            case (byte)0xf0:
                tempStr = "OK";
                break;
            case (byte)0x0f:
                tempStr = "Error";
                break;
            case (byte)0xff:
                tempStr = "Error";
                break;
        }
        return tempStr;
    }
    private String powerState(byte b){
        String tempStr = "";
        switch(b){
            case (byte)0x00:
                tempStr = "OK";
                break;
            case (byte)0xf0:
                tempStr = "Error";
                break;
            case (byte)0x0f:
                tempStr = "OK";
                break;
            case (byte)0xff:
                tempStr = "Error";
                break;
        }
        return tempStr;
    }




    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    private String byteToHexString(byte _byte){
        char[] hexChars = new char[2];
        int v = _byte & 0xFF;
        hexChars[0] = hexArray[v >>> 4]; // 往右移4個 bite，即可取得5-8bite的數據，轉為16進位的數字 0~f
        hexChars[1] = hexArray[v & 0x0F];// & 0x0F 等於 & 1111  取1-4bite的數據，轉為16進位的數字 0~f
        return new String(hexChars);
    }










    public String getGID_Str(){
        return bulbGID;
    }
    public int getGID_int(){
        int temp = Integer.parseInt(bulbGID);
        return temp;
    }
    public String getPoint_Str(){
        return bulbPoint;
    }
    public int getPoint_int(){
        int temp = Integer.parseInt(bulbPoint);
        return temp;
    }
    public String getMac(){
        return bulbMac;
    }
    public String getLinkState(){
        return bulbLinkState;
    }
    public boolean getLinkStateBoolean(){
        if(bulbLinkState.equals("OK")){
            return true;
        }else{
            return false;
        }
    }
    public String getPowerState(){
        return bulbPowerState;
    }
    public String getPower(){
        return bulbPower;
    }
    public String getTemperature(){
        return bulbTemperation;
    }
    public String getBrightness(){
        return bulbBrightness;
    }
    public String getColorTemp(){
        return bulbColorTemp;
    }
    public String getMTX(){
        return bulbMTX;
    }
    public String getSTX(){
        return bulbSTX;
    }


    public String getLux() {
        return lux;
    }

    public void setLux(String lux) {
        this.lux = lux;
    }

    public String getLuxInfo() {
        return luxInfo;
    }

    public void setLuxInfo(String luxInfo) {
        this.luxInfo = luxInfo;
    }
}
