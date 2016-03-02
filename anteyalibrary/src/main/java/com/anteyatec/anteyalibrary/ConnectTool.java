package com.anteyatec.anteyalibrary;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import javax.net.SocketFactory;

/**
 * Created by anteya on 15/3/3.
 */
public class ConnectTool {

    private final String TAG = "ConnectTool";

    private int intPort = 8023;

    private int intTimeout = 3000;// 2015/5/28 吳大哥說要改Timeout 3秒

    private DataController dataController;

    private SQLiteController sqliteController;

    private FragmentCallBack listener;

    private byte[] arrayCommand;

    // 新執行緒
    private HandlerThread handlerThread;

    // 新執行緒上的管理員
    private Handler connectHandler;

    // Message Type
    private static final int MAIN_THREAD_HANDLER_RECEIVE = 0;
    private static final int MAIN_THREAD_HANDLER_ERROR = 1;
    private static final int MAIN_THREAD_HANDLER_CONNECTED = 2;
    // 主執行緒上的管理員，將資料傳回主執行緒
    private Handler mainThreadHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MAIN_THREAD_HANDLER_RECEIVE:
                    break;
                case MAIN_THREAD_HANDLER_ERROR:
                    break;
                case MAIN_THREAD_HANDLER_CONNECTED:
                    if (listener != null)
                        listener.OnConnectToolConnected((boolean)msg.obj);
                    break;
            }
        }
    };;

    public ConnectTool(Context context, FragmentCallBack mListener) {
        listener = mListener;

        // 開啟一個新的執行緒名叫 Connect
        handlerThread = new HandlerThread("Connect");
        handlerThread.start();
        connectHandler = new Handler(handlerThread.getLooper());

        dataController = (DataController) context.getApplicationContext();
        sqliteController = new SQLiteController(context);
    }

    /**
     * 使用 Runnable 送出 Command, 會等候前面先發送完再繼續
     * @param getArrayCommand 指令陣列
     */
    public void sendCommend(byte[] getArrayCommand){
        if(getArrayCommand!=null ){
            arrayCommand = getArrayCommand;
            connectHandler.removeCallbacksAndMessages(null);
            connectHandler.post(runSendCommand);
        }
    }

    /**
     * 檢查連線狀態, 測試是否成功連線到iTouch
     * @param strIpAddress
     */
    public void checkITouchConnect(final String strIpAddress){

        new Thread(){
            public void run(){
                Log.d("","connect ip : " + strIpAddress);
                Socket socket;
                try {
                    socket = SocketFactory.getDefault().createSocket();
                    SocketAddress sa = new InetSocketAddress(strIpAddress, intPort);
                    socket.connect(sa, 1500);
                    if(socket.isConnected()) {
                        // 連線成功
                        Message message = new Message();
                        message.what = MAIN_THREAD_HANDLER_CONNECTED;
                        message.obj = true;

                        mainThreadHandler.sendMessage(message);

                    }else {
                        // 連線失敗
                        Message message = new Message();
                        message.what = MAIN_THREAD_HANDLER_CONNECTED;
                        message.obj = false;

                        mainThreadHandler.sendMessage(message);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                    // Exception
                    Message message = new Message();
                    message.what = MAIN_THREAD_HANDLER_CONNECTED;
                    message.obj = false;

                    mainThreadHandler.sendMessage(message);
                }
            }
        }.start();
    }


    private Runnable runSendCommand = new Runnable() {

        @Override
        public void run() {

            // init Object
            Socket socket = null;
            String strIpAddress = sqliteController.getCurrentIpByIndex(dataController.getCurrentIpIndex());
            Log.d(TAG, "strIpAddress = " + strIpAddress);
            // connect and send
            try {
                socket = SocketFactory.getDefault().createSocket();
                SocketAddress sa = new InetSocketAddress(strIpAddress, intPort);
                socket.connect(sa, intTimeout);
                socket.setSoTimeout(intTimeout);
                if(socket.isConnected()) {
                    Log.i(TAG + runSendCommand , "連線成功：" + socket.toString());
                }else {
                    if(listener != null) {
                        listener.OnConnectToolError("連線失敗");
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
                if(listener != null) {
                    listener.OnConnectToolError("Exception");
                }
            }
            if(checkSocketState(socket)) {
                byte[] arrayAckData;
                arrayAckData = sendCommandGetArray(arrayCommand, socket);
                if(arrayAckData == null) {
                    return;
                }

                // 將執行結果回傳給 fragment 或 activity
                if(listener != null){
//                    connectHandler.sendMessage(createMessage(0, arrayAckData));
                    listener.OnConnectToolReceive(arrayAckData);
                }

            }else{
                if(listener != null)
                    listener.OnConnectToolError("尚未連線");
            }


            // 執行完畢，關閉連線。
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 執行 command, 不使用任何 thread, 須自行添加 new Thread
     * @param getArrayCommand 指令陣列
     */
    public void sendCommandNoRunnable(byte[] getArrayCommand){
        if(getArrayCommand!=null ){
            arrayCommand = getArrayCommand;
            // init Object
            Socket socket = null;
            String strIpAddress = sqliteController.getCurrentIpByIndex(dataController.getCurrentIpIndex());
            Log.d(TAG, "%%% start runSendCommand strIpAddress = " + strIpAddress + " %%%");
            // connect and send
            try {
                socket = SocketFactory.getDefault().createSocket();
                SocketAddress sa = new InetSocketAddress(strIpAddress, intPort);
                socket.connect(sa, intTimeout);
                socket.setSoTimeout(intTimeout);
                if(socket.isConnected()) {
                    Log.i(TAG + runSendCommand , "連線成功：" + socket.toString());
                }else {
                    if(listener != null) {
                        listener.OnConnectToolError("連線失敗");
                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
                if(listener != null) {
                    listener.OnConnectToolError("Exception");
                }
            }
            if(checkSocketState(socket)) {

                byte[] arrayAckData;

                showDataArray(arrayCommand);
                arrayAckData = sendCommandGetArray(arrayCommand, socket);
                if(arrayAckData == null)
                    return;
                showDataArray(arrayAckData);

                if(listener != null){
                    listener.OnConnectToolReceive(arrayAckData);
                }
            }
            // 執行完畢，關閉連線。
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

            if(!e.toString().equals("java.net.SocketTimeoutException")){
                if(listener != null)
                    listener.OnConnectToolError(e.toString());
            }
            try {
                socket.close();
            } catch (IOException ee) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public boolean checkSocketState(Socket socket){

        if(socket != null){
            if(socket.isClosed() == false && socket.isConnected() == true){
                return true;
            }else{
                Log.d(TAG,"checkSocketState, socket is not connected");
                return false;
            }
        }else{
            Log.d(TAG,"checkSocketState, socket == null");
            return false;
        }
    }

    /**
     * 印出 byte Array
     * @param mArrayCommand 指令陣列
     */
    private void showDataArray(byte[] mArrayCommand){
        for(int i=0; i< mArrayCommand.length ;i++ ){
            Log.d("showDataArray","byte["+i+"] = 0x"+FormatTool.byteToHexString(mArrayCommand[i]) + "\t"+  mArrayCommand[i] + "\t"
                    + FormatTool.byteToString(mArrayCommand[i]));
        }
    }







    /**
     * 給 index 去執行對應的 Mode 裡面所儲存的每個Touch的情境
     * @param index Mode index
     */
    public void executeModeByIndex(int index){
        Data_Mode mode = sqliteController.getModeArray().get(index);


        final List<Data_ITouch> iTouchList = sqliteController.getiTouchArrayWithMode(mode.getModeSettings());

        for(int i = 0; i < iTouchList.size(); i++){;

            final Data_ITouch iTouch = iTouchList.get(i);

            // 提供給 Thread 的參數必須是 final
            final String strIpAddress = iTouch.getIpAddress();
            final byte[] byteArrayCommand = generateCommand(iTouch.getModeInteger());
            // 用完即關的 Thread
            new Thread(){
                public void run(){
                    recallMode(strIpAddress, byteArrayCommand);
                }
            }.start();
        }
    }




    private void recallMode(String ip, byte[] command){
        Log.d(TAG, "呼叫常用模式n, IP = " + ip);
        // init Object
        Socket socket = null;
        String strIpAddress = ip;
        // connect and send
        try {
            socket = SocketFactory.getDefault().createSocket();
            SocketAddress sa = new InetSocketAddress(strIpAddress, intPort);
            socket.connect(sa, intTimeout);
            socket.setSoTimeout(intTimeout);
            if(socket.isConnected()) {
                Log.i(TAG + runSendCommand , "連線成功：" + socket.toString());
            }else {
                if(listener != null) {
                    listener.OnConnectToolError("連線失敗");
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG,"socket Exception");
            if(listener != null) {
                listener.OnConnectToolError("Exception");
            }
        }
        if(checkSocketState(socket)) {
            byte[] arrayAckData;
            arrayAckData = sendCommandGetArray(command, socket);
            if(arrayAckData == null) {
                return;
            }

            // 將執行結果回傳給 fragment 或 activity
            if(listener != null)
                listener.OnConnectToolReceive(arrayAckData);

        }else{
            if(listener != null)
                listener.OnConnectToolError("尚未連線");
        }


        // 執行完畢，關閉連線。
        try {
            socket.close();
        } catch (IOException e) {
            Log.e(TAG,"socket close IOException");
        }
    }

    public byte[] generateCommand(int index){
        byte[] tempArray;
        switch(index){
            case 0:
            {
                tempArray = new byte[12];
                tempArray[0] = (byte) 0xff;
                tempArray[1] = (byte) 228;
                tempArray[2] = (byte) 228;
                tempArray[3] = (byte) 228;
                tempArray[4] = (byte) 228;
                tempArray[5] = (byte) 228;
                tempArray[6] = (byte) 228;
                tempArray[7] = (byte) 228;
                tempArray[8] = (byte) 228;
                tempArray[9] = (byte) 228;
                tempArray[10] = (byte) 228;
                return FormatTool.getChecksumArray(tempArray);
            }
            case 1:
            {
                tempArray = new byte[12];
                tempArray[0] = (byte) 0xff;
                tempArray[1] = (byte) 0;
                tempArray[2] = (byte) 0;
                tempArray[3] = (byte) 0;
                tempArray[4] = (byte) 0;
                tempArray[5] = (byte) 0;
                tempArray[6] = (byte) 0;
                tempArray[7] = (byte) 0;
                tempArray[8] = (byte) 0;
                tempArray[9] = (byte) 0;
                tempArray[10] = (byte) 0;
                return FormatTool.getChecksumArray(tempArray);
            }
            default:
            {
                return FormatTool.convertCommendArray(DataController.TYPE_RECALL_MEMORY, index - 1);
            }
        }
    }

    /** 創建發送的消息Message **/
    public static Message createMessage(int what, Object object)
    {
        Message message = new Message();
        message.what = what;
        message.obj = object;
        return message;
    }

    /**
     * connect tool call back interface
     * 回傳收到Ack, 或收到Error
     */
    public interface FragmentCallBack {

        void OnConnectToolReceive(byte[] byteArray);
        void OnConnectToolError(String errorMessage);
        void OnConnectToolConnected(boolean isConnected);
    }

}
