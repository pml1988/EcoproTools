package com.anteyatec.anteyalibrary;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

/**
 * Created by anteya on 2015/12/30.
 *
 * 單純做資料的傳輸，使用各自獨立的Thread 去執行command 發送 & 傳輸
 * 使用 listener 回傳到 接收端時還是在 multi Thread 中
 * 所以不管是在 Activity端, 或者 Fragment端都必須實作 Handler 將 data轉回 MainThread處理後續UI
 *
 */
public class NewConnector {


    private final String TAG = "TestActivity";

    private final int timeout = 2000;

    private OnTestConnectorListener listener;

    public NewConnector(){

    }
    /**
     * check the ip address is worked, return by listener.
     * @param ipAddress
     */
    public void checkSocketLink(final String ipAddress){
        new Thread(){
            @Override
            public void run() {

                Log.i(TAG, "checkSocketLink, " + ipAddress);

                Socket socket = null;

                if(listener != null){

                    int port = AnteyaString.getPort(ipAddress);
                    String tempIpAddress = AnteyaString.getIpAddress(ipAddress);

                    // connect and send
                    try {
                        socket = SocketFactory.getDefault().createSocket();
                        SocketAddress sa = new InetSocketAddress(tempIpAddress, port);
                        socket.connect(sa, timeout);
                        socket.setSoTimeout(timeout);
                        if(socket.isConnected()) {
                            Log.i(TAG, "連線成功：" + socket.toString());
                            // 這裡就必須使用 listener 了, 因為這裡無法 return 東西回去, 因為還在 Thread裡面
                            listener.onLinked(true);
                        }else{
                            // 執行完畢，關閉連線。
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            listener.onLinked(true);
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                        listener.onLinked(true);
                    }

                    // 執行完畢，關閉連線。
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d("","listener == null");
                }
            }
        }.start();
    }

    /**
     * send command with new thread, return by listener.
     * @param ipAddress
     * @param commandArray
     */
    public void sendCommand(final String ipAddress, final byte[] commandArray){
        new Thread(){
            @Override
            public void run() {

                if(commandArray != null){
                    byte[] tempArray;
                    tempArray = executeCommand(ipAddress, commandArray);

                    // 務必檢查 data 是否為 null, 有可能就版本不支援此command,tempArray 會 return null
                    if(tempArray != null){
                        // 這裡就必須使用 listener 了, 因為這裡無法 return 東西回去, 因為還在 Thread裡面
                        if(listener == null){
                            Log.d("","listener == null");
                        }else{
                            listener.onReceive(tempArray);
                        }
                    }
                }
            }
        }.start();
    }


    /**
     * SetGroupId 專用的
     * @param ipAddress
     * @param commandArray
     */
    public void pollingMacInfoThread(final String ipAddress, final byte[] commandArray){
        new Thread(){
            @Override
            public void run() {

                byte[] ackDataArray;
                ackDataArray = executeCommand(ipAddress, commandArray);

                int macCount = 0;

            }
        }.start();
    }


    public byte[] executeCommand(String ipAddress, byte[] commandArray){

        Log.i(TAG, "sendCommand, " + ipAddress);

        Socket socket = null;

        int port = AnteyaString.getPort(ipAddress);
        ipAddress = AnteyaString.getIpAddress(ipAddress);

        // connect and send
        try {
            socket = SocketFactory.getDefault().createSocket();
            SocketAddress sa = new InetSocketAddress(ipAddress, port);
            socket.connect(sa, timeout);
            socket.setSoTimeout(timeout);
            if(socket.isConnected()) {
                Log.i(TAG, "連線成功：" + socket.toString());
            }else{
                // 執行完畢，關閉連線。
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        byte[] arrayAckData;

        showDataArray2(commandArray, "send:");
        arrayAckData = sendCommandGetArray(commandArray, socket);
        if(arrayAckData != null) {
            showDataArray2(arrayAckData, "ack :");
        }

        // 執行完畢，關閉連線。
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayAckData;
    }

    /**訊息傳輸
     * 把指令陣列丟進去給 outputStream 發送出去
     * 用 inputStream 接收回來的訊息, 並放到byteArray return回去
     * **/
    private byte[] sendCommandGetArray(byte[] tempArray, Socket socket){
        byte[] array_getEchoFromServer = new byte[1024];

        try {
            if(socket.getOutputStream() != null){
                socket.getOutputStream().write(tempArray);
                socket.getOutputStream().flush();

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                int bytesCount;
                InputStream inputStream = socket.getInputStream();
                if ((bytesCount = inputStream.read(array_getEchoFromServer)) != -1){
                    byteArrayOutputStream.write(array_getEchoFromServer, 0, bytesCount);
                }
                array_getEchoFromServer = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();


            }else{
                Log.d("","bufferOutput == null");
            }
            return array_getEchoFromServer;
        } catch (IOException e) {
            e.printStackTrace();

            try {
                socket.close();
            } catch (IOException ee) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void showDataArray2(byte[] mArrayCommand, String commandType){

        String tempString = "";
        for(int i=0; i< mArrayCommand.length ;i++ ){

            tempString = tempString + FormatTool.byteToHexString(mArrayCommand[i]);

            if(((i+1)%5) == 0 || (i+1) == 5){
                tempString = tempString + "  ";
            }else{
                tempString = tempString + ",";
            }
        }
        Log.d("showDataArray2",commandType + tempString);
    }

    public void setListener(OnTestConnectorListener listener) {
        this.listener = listener;
    }

    public interface OnTestConnectorListener{
        void onReceive(byte[] receiveCommand);
        void onLinked(boolean isLinkSuccess);
    }
}
