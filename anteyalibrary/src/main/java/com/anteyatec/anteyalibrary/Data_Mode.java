package com.anteyatec.anteyalibrary;

import java.util.ArrayList;

/**
 * Created by anteya on 15/3/6.
 */
public class Data_Mode {

    private String name;

    private String ip;

    private ArrayList<String> dataIdArrayList = new ArrayList<String>();
    private ArrayList<String> dataIpArrayList = new ArrayList<String>();
    private ArrayList<String> dataContentArrayList = new ArrayList<String>();

    /**
     *  2015.07.31 update
     *
     */
    private int modeId = 0;
    private String modeSettings = "";


    public Data_Mode(String modeName, String modeData, ArrayList<Data_ITouch> dataIpAddressArrayList) {
        name = modeName;


        ArrayList<String> tempIdList = new ArrayList<String>();
        ArrayList<String> tempContentList = new ArrayList<String>();


        // 用 "&" 切開
        String[] tempStrArray = modeData.split("&");
        for(int i=0; i < tempStrArray.length; i++){
            // 用":"切開ＩＤ 跟 內容
            String[] tempStrArray2 = tempStrArray[i].split(":");
            tempIdList.add(tempStrArray2[0]);
            tempContentList.add(tempStrArray2[1]);
        }

        for(int i = 0; i < dataIpAddressArrayList.size(); i++){
            check(i,i, tempIdList, tempContentList, dataIpAddressArrayList);
        }

        check2(dataIpAddressArrayList);
    }

    public Data_Mode() {
    }

    /** 比對用的遞迴函式 */
    private void check(int iValue,int iValue2, ArrayList<String> tempListA, ArrayList<String> tempListB, ArrayList<Data_ITouch> dataIpAddressArrayList){
        if(iValue2 >= dataIpAddressArrayList.size() || iValue >= tempListA.size()){
            return;
        }
        if(tempListA.get(iValue).equals(dataIpAddressArrayList.get(iValue2).getStringId())){
            dataIdArrayList.add(tempListA.get(iValue));
            dataContentArrayList.add(tempListB.get(iValue));
            dataIpArrayList.add(dataIpAddressArrayList.get(iValue2).getIpAddress());

        }else{
            if(tempListA.size() > iValue+1){
                check(iValue+1, iValue2, tempListA, tempListB, dataIpAddressArrayList);
            }
        }
    }
    /**比對完，若ipList還有沒放進Data的就手動新增進去，代表有新的iTouch進來，而舊有資料裡檢查發現沒有*/
    private void check2(ArrayList<Data_ITouch> dataIpAddressArrayList){
        if(dataIpAddressArrayList.size() > dataIdArrayList.size()){
            dataIpArrayList.add(dataIpAddressArrayList.get(dataIdArrayList.size()).getIpAddress());
            dataIdArrayList.add(dataIpAddressArrayList.get(dataIdArrayList.size()).getIntId()+"");
            dataContentArrayList.add("-1");
            check2(dataIpAddressArrayList);
        }
    }
    public Data_Mode(String modeName) {
        name = modeName;
//        if ()
//        for(int i=0; i < dataIpAddressArrayList.size(); i++){
//            dataIdArrayList.add(dataIpAddressArrayList.get(i).getIntId()+"");
//            dataContentArrayList.add("-1");
//        }
    }

    public void setId(int mId){
        modeId = mId;
    }
    public int getId(){
        return modeId;
    }
    public String getIdString(){
        return modeId+"";
    }

    public void setName(String tempStr) {
        name = tempStr;
    }
    public String getName() {
        return name;
    }

    public int getModeIndexByiTouchIndex(int index) {
        if(hadNewiTouchToOutOfIndex(index)) {
            return 0;
        }else{
            int tempInt = Integer.parseInt(dataContentArrayList.get(index));
            if(tempInt == -1){
                return 0;
            }else if(tempInt == 0){
                return 1;
            }else if(tempInt >0 && tempInt < 11){
                return tempInt+1;
            }else{
                return 0;
            }
        }
    }

    /**
     * @param index iTouch 的index
     * @param mode 該 iTouch選擇了第幾個模式
     * */
    public void setSelectByIndex(int index, int mode) {
        if(hadNewiTouchToOutOfIndex(index)){
            return;
        }else{
            if(mode == 0) {
                dataContentArrayList.set(index,"-1");
            }else if(mode == 1) {
                dataContentArrayList.set(index,"0");
            }else if(mode >1 && mode <12) {
                dataContentArrayList.set(index,""+(mode-1));
            }
        }
    }


    public boolean hadNewiTouchToOutOfIndex(int index){
        if(index >= dataContentArrayList.size())
            return true;
        else
            return false;
    }


    public String getDataArrayString() {
        String tempStr = "";
        for(int i=0 ; i < dataContentArrayList.size() ; i++){
            tempStr = tempStr + dataIdArrayList.get(i) + ":" + dataContentArrayList.get(i);
            if((i+1) < dataContentArrayList.size()){
                tempStr = tempStr + "&";
            }
        }

        return tempStr;
    }
    public void checkArrayCount(ArrayList<Data_ITouch> list){
        if(list.size() > dataContentArrayList.size()) {
            dataIdArrayList.add(""+list.get(dataIdArrayList.size()-1).getIntId());
            dataContentArrayList.add("-1");
        }
        // 若上面的加完，還是比Touch的數量少就進去地回繼續累加
        if(list.size() > dataContentArrayList.size()) {
            checkArrayCount(list);
        }
    }

    public String getIpAddress(int index){
        return dataIpArrayList.get(index);
    }

    public String getContent(int index){
        return dataContentArrayList.get(index);
    }

    public int getCount(){
        return dataIdArrayList.size();
    }


    /**
     * update 2015.07.31
     */
    public void setModeSettings(String mModeSettings){
        modeSettings = mModeSettings;
    }
    public String getModeSettings(){
        return modeSettings;
    }


}
