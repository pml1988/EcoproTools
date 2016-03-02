package com.anteyatec.anteyalibrary;

/**
 * Created by anteya on 15/2/9.
 */
public class Data_ITouch {

    private int intId = 0;

    private String iTouchName = "";

    private String iTouchIpAddress = "";

    private double positionX = 0, positionY = 0;

    private String[] iTouchSceneArray;
    private String[] iTouchLightArray;

    private String iTouchMode;

    private ITouchImage iTouchImage;

    private float watt;


    public Data_ITouch(){

    }

    public Data_ITouch(int id, String str1, String str2, double x, double y) {
        intId = id;
        iTouchName = str1;
        iTouchIpAddress = str2;
        positionX = x;
        positionY = y;
    }

    /**
     * IP
     * @param mId
     */
    public void setId(long mId) {
        intId = (int)mId;
    }
    public int getIntId() {
        return intId;
    }
    public String getStringId() {
        return intId+"";
    }


    /**
     * Name
     * @return
     */
    public String getName(){
        return iTouchName;
    }
    public void setName(String mName){
        iTouchName = mName;
    }


    /**
     * IpAddress
     * @return
     */
    public String getIpAddress(){
        return iTouchIpAddress;
    }
    public void setIpAddress(String mIpAddress){
        iTouchIpAddress = mIpAddress;
    }

    /**
     * GeoPoint
     * @return
     */
    public void setX(double mX){
        positionX = mX;
    }
    public double getX(){
        return positionX;
    }
    public void setY(double mY){
        positionY = mY;
    }
    public double getY(){
        return positionY;
    }

    /**
     * iTouch Image
     * @param iTouch
     */
    public void setITouchImage(ITouchImage iTouch){
        iTouchImage = iTouch;
        if(positionX > 0 || positionY > 0){
            iTouchImage.setInitPosition((float)positionX, (float)positionY);
        }
        iTouchImage.setText(iTouchName);
    }
    public ITouchImage getITouchImage(){
        return iTouchImage;
    }
    public void savePosition(){
        positionX = iTouchImage.getXAtPercent();
        positionY = iTouchImage.getYAtPercent();
    }


    public void setWatt(float getWatt){
        watt = getWatt;
    }
    public float getWatt(){
        return watt;
    }
    public String getWattString(){
        return ""+watt;
    }

    /**
     * scene data
     * @param mSceneString
     */
    public void setSceneArray(String mSceneString){
        iTouchSceneArray = mSceneString.split(",");
    }

    public void setSceneArray(String[] mSceneString){
        iTouchSceneArray = mSceneString;
    }
    public void setSceneArray(int[] mSceneString){
        iTouchSceneArray = new String[]{"" + mSceneString[0],
                "" + mSceneString[1],
                "" + mSceneString[2],
                "" + mSceneString[3],
                "" + mSceneString[4],
                "" + mSceneString[5],
                "" + mSceneString[6],
                "" + mSceneString[7],
                "" + mSceneString[8],
                "" + mSceneString[9]};
    }

    public String[] getSceneArray(){
        return iTouchSceneArray;
    }

    public int[] getSceneArrayInt(){
        int[] tempSceneArray = new int[10];
        tempSceneArray[0] = Integer.parseInt(iTouchSceneArray[0]);
        tempSceneArray[1] = Integer.parseInt(iTouchSceneArray[1]);
        tempSceneArray[2] = Integer.parseInt(iTouchSceneArray[2]);
        tempSceneArray[3] = Integer.parseInt(iTouchSceneArray[3]);
        tempSceneArray[4] = Integer.parseInt(iTouchSceneArray[4]);
        tempSceneArray[5] = Integer.parseInt(iTouchSceneArray[5]);
        tempSceneArray[6] = Integer.parseInt(iTouchSceneArray[6]);
        tempSceneArray[7] = Integer.parseInt(iTouchSceneArray[7]);
        tempSceneArray[8] = Integer.parseInt(iTouchSceneArray[8]);
        tempSceneArray[9] = Integer.parseInt(iTouchSceneArray[9]);
        return tempSceneArray;
    }
    public String getSceneArrayString(){
        String tempStr =
                iTouchSceneArray[0] + ","+
                iTouchSceneArray[1] + ","+
                iTouchSceneArray[2] + ","+
                iTouchSceneArray[3] + ","+
                iTouchSceneArray[4] + ","+
                iTouchSceneArray[5] + ","+
                iTouchSceneArray[6] + ","+
                iTouchSceneArray[7] + ","+
                iTouchSceneArray[8] + ","+
                iTouchSceneArray[9] ;

        return tempStr;
    }


    /**
     * light data
     * @param mLightString
     */
    public void setLightArray(String mLightString){
        iTouchLightArray = mLightString.split(",");
    }
    public void setLightArray(String[] mLightString){
        iTouchLightArray = mLightString;
    }

    public void setLightArray(int[] mLightString){
        iTouchLightArray = new String[]{"" + mLightString[0],
                "" + mLightString[1],
                "" + mLightString[2],
                "" + mLightString[3],
                "" + mLightString[4],
                "" + mLightString[5],
                "" + mLightString[6],
                "" + mLightString[7],
                "" + mLightString[8],
                "" + mLightString[9]};
    }
    public String[] getLightArray(){
        return iTouchLightArray;
    }
    public int[] getLightArrayInt(){
        int[] tempLightArray = new int[10];
        tempLightArray[0] = Integer.parseInt(iTouchLightArray[0]);
        tempLightArray[1] = Integer.parseInt(iTouchLightArray[1]);
        tempLightArray[2] = Integer.parseInt(iTouchLightArray[2]);
        tempLightArray[3] = Integer.parseInt(iTouchLightArray[3]);
        tempLightArray[4] = Integer.parseInt(iTouchLightArray[4]);
        tempLightArray[5] = Integer.parseInt(iTouchLightArray[5]);
        tempLightArray[6] = Integer.parseInt(iTouchLightArray[6]);
        tempLightArray[7] = Integer.parseInt(iTouchLightArray[7]);
        tempLightArray[8] = Integer.parseInt(iTouchLightArray[8]);
        tempLightArray[9] = Integer.parseInt(iTouchLightArray[9]);
        return tempLightArray;
    }
    public String getLightArrayString(){
        String tempStr =
                iTouchLightArray[0] + ","+
                iTouchLightArray[1] + ","+
                iTouchLightArray[2] + ","+
                iTouchLightArray[3] + ","+
                iTouchLightArray[4] + ","+
                iTouchLightArray[5] + ","+
                iTouchLightArray[6] + ","+
                iTouchLightArray[7] + ","+
                iTouchLightArray[8] + ","+
                iTouchLightArray[9] ;

        return tempStr;
    }


    /**
     * mode
     * @param mMode
     */
    public void setMode(String mMode){
        iTouchMode = mMode;
    }
    public void setMode(int mMode){
        iTouchMode = mMode+"";
    }
    public int getModeInteger(){
        return Integer.parseInt(iTouchMode);
    }
    public String getModeString(){
        return iTouchMode;
    }
}
