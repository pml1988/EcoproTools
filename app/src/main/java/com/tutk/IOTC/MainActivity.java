package com.tutk.IOTC;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tutk.sample.AVAPI.IpCamThread;
import com.tutk.sample.AVAPI.*;

public class MainActivity extends Activity implements IpCamThread.DataReceiveListener, Callback {

    private String UID = "C8Z91Y73GPS38D65111A";

    private Button buttonStart;
    private Button buttonStop;
    private Button buttonTest1;
    private Button buttonTest2;

    private Button buttonTest3;
    private Button buttonTest4;

    private Button buttonScan;

    private SurfaceView surfaceView;

    private VideoDecoder decoder;

    private IpCamThread ipCamThread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
//
//
//        // init View
//        buttonStart = (Button) findViewById(R.id.buttonStart);
//        buttonStop = (Button) findViewById(R.id.buttonStop);
//        buttonTest1 = (Button) findViewById(R.id.buttonTest1);
//        buttonTest2 = (Button) findViewById(R.id.buttonTest2);
//        buttonTest3 = (Button) findViewById(R.id.buttonTest3);
//        buttonTest4 = (Button) findViewById(R.id.buttonTest4);
//        buttonScan = (Button) findViewById(R.id.buttonScan);
//        surfaceView = (SurfaceView) findViewById(R.id.testSurfaceView);
//        surfaceView.getHolder().addCallback(this);


        // set click listener
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ipCamThread == null){

                    UID = getUID();

                    ipCamThread = new IpCamThread(UID, MainActivity.this);
                    ipCamThread.start();
                }
            }
        });
        buttonStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(ipCamThread != null){
                    ipCamThread.closeThread();
                    ipCamThread = null;
                }
            }
        });
        buttonTest1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("buttonTest1", "buttonTest1 has clicked.");
                if(ipCamThread != null){
                    ipCamThread.sendIOCtrl_2((byte) 3);
                }
            }
        });
        buttonTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("buttonTest2", "buttonTest2 has clicked.");
                if (ipCamThread != null){
                    ipCamThread.sendIOCtrl_2((byte)6);
                }
            }
        });

        buttonTest3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("buttonTest3","buttonTest3 has clicked.");
                if (ipCamThread != null){
                    ipCamThread.sendIOCtrl_2((byte)1);
                }
            }
        });



        buttonTest4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.e("buttonTest4","buttonTest4 has clicked.");
                if (ipCamThread != null){
//                    ipCamThread.sendIOCtrl_3();
                    ipCamThread.sendIOCtrl_2((byte)2);
                }
            }
        });

        buttonScan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(ipCamThread != null){
                    ipCamThread.closeThread();
                    ipCamThread = null;
                }

                // for zxing
                IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                scanIntegrator.initiateScan();
            }
        });

    }


    private void setUID(String uid){

        SharedPreferences userData = getSharedPreferences("PREF", 0);
        userData.edit().putString("UID", uid).commit();
    }

    private String getUID(){

        SharedPreferences userData = getSharedPreferences("PREF",0);
        String tempStr = userData.getString("UID", "0");

        return tempStr;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult!=null){
            String scanContent=scanningResult.getContents();
            String scanFormat=scanningResult.getFormatName();

            setUID(scanContent);

            if(ipCamThread != null){
                ipCamThread.closeThread();
                ipCamThread = null;
            }
            if(ipCamThread == null){
                ipCamThread = new IpCamThread(scanContent, MainActivity.this);
                ipCamThread.start();
            }

        }else{
//            Toast.makeText(getApplicationContext(),"nothing",Toast.LENGTH_SHORT).show();
        }
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

    public static void printByte(byte[] dataArray){
        String commandString = "{";

        for (int i = 0; i<dataArray.length; i++){
            commandString = commandString + MainActivity.byteToHexString(dataArray[i]);
            if (i < dataArray.length-1)
                commandString = commandString + ", ";
        }
        commandString = commandString + "}";

        Log.d("", "send " + commandString);
    }

    @Override
    public void onVideoDataReceive(byte[] data) {

        frameToBuffer(data);
    }

    @Override
    public void onProgressbarReceive(boolean close) {

    }

    private byte[] tempSPSData;

    private void frameToBuffer(byte[] data) {

        Log.d("frameToBuffer","data.length = " + data.length);
//        if(data[3] == (byte)0x01 && data[4] == (byte)0x67){ // it is i frame
//            Log.d("frameToBuffer","It is I Frame");
//            tempSPSData = new byte[100];
//            for(int i = 0;i < data.length;i++){
//                if(data[i] == 0 &&
//                    data[i+1] == 0 &&
//                    data[i+2] == 0 &&
//                    data[i+3] == (byte)0x01 &&
//                    data[i+4] == (byte)0x65) {
//                    break;
//                }else{
//                    tempSPSData[i] = data[i];
//                }
//            }
//
//        }


//        printByteArray("tempSPSData", tempSPSData);
        printByteArray("receive frame data", data);


        System.out.println("frametobuffer");
        if(decoder != null){
            decoder.onFrame(data,0, data.length, 0);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("", "surfaceCreated");
        System.out.println("畫面更新2");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("","surfaceChanged");
        System.out.println("畫面更新1");
        if(decoder == null){
            decoder = new VideoDecoder(holder.getSurface());


        }
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("","surfaceDestroyed");



        if(ipCamThread != null){
            ipCamThread.closeThread();
        }
        if(decoder != null){
            decoder.stopDecode();
            decoder = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("","onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("","onDestroy");
    }

    public void printByteArray(String tagTitle, byte[] tempArray){

        String commandString = "{";

        int tempLength = tempArray.length;

        if(tempLength > 100){
            tempLength = 100;
        }

        for (int i = 0; i< tempLength ; i++){
            commandString = commandString + byteToHexString(tempArray[i]);
            if (i < tempArray.length-1)
                commandString = commandString + ", ";
        }
        commandString = commandString + "}";
        Log.d("MainActivity", tagTitle + " byte array:  " + commandString);
    }

}
