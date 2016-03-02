package com.anteyatec.anteyalibrary;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.net.SocketFactory;

/**
 * Created by anteya on 15/10/1.
 */
public class ScanRfDataTool {

    /*
    * 必須要考量到 3G的速度, 以及重新連線的反應速度
    * 3秒雖然可確保 3G連得到，也能成功收到資料, 但是如果斷線重連時, 3秒可能略嫌久了點
    * */
    private int timeout = 3000;
    private int post = 8023;
    private String ipAddress;

    /**
     * 目前傳送的 Command是哪一個 commandType
     */
    private byte currentCommandType;

    /**
     * 現在 object 的 總數
     */
    private int scanCount = 0;

    /**
     * 現在取得 object 的 index
     */
    private int scanIndex = 0;
    /**
     * 現在 Select 的是哪一個 Object
     */
    private int selectIndex = 0;

    private Socket socket;

    /**
     * thread 是否執行的 boolean
     */
    private boolean goScan;

    private boolean goSelect;
    /**
     * thread 是否暫停的 boolean
     */
    private boolean scanStop;

    private ScanRfDataToolListener listener;

    public ScanRfDataTool(ScanRfDataToolListener listener, String ipAddress){

        this.listener = listener;
        this.ipAddress = ipAddress;

        threadScan.start();
    }

    /**
     * 修改當前連線的 ipAddress
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    /**
     * Turn "go" to false, end the while loop.
     * 切換 goScan 為 false, 讓 Thread 不會再繼續執行下去.
     */
    public void stopScan(){
        goScan = false;
    }

    /**
     * run select thread, with select index
     * @param index
     */
    public void startSelect(int index){
        selectIndex = index;
        threadSelect.start();
    }
    /**
     * Turn "go" to false, end the while loop.
     * 切換 goSelect 為 false, 讓 Thread 不會再繼續執行下去.
     */
    public void stopSelect(){
        goSelect = false;
    }

    /**
     * connect socket
     * 建立 socket 的連線
     * @return
     */
    private boolean socketConnect(){
        try{
            // 如果之前還沒有手動斷線過就一定要切斷連線再繼續。
            if(!socket.isClosed()){
                socket.close();
            }
            socket = SocketFactory.getDefault().createSocket();
            SocketAddress socketAddress = new InetSocketAddress(ipAddress, post);
            socket.connect(socketAddress, timeout);   // connect同時, 也設定timeout防止阻塞, 這裡是連線的Timeout
            socket.setSoTimeout(timeout);			// 這裡是之後sendCommend 的 Timeout
            if(socket.isConnected()){
                return true;
            }
        }catch(Exception e) {
            e.printStackTrace();
            try{
                socket.close();
            }catch(Exception ee){
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 製作 Message
     */
    private void sendMessage(){

        Message message = new Message();
        message.what = 0;
        message.obj = true;

        callbackHandler.sendMessage(message);
    }

    /**
     *  返回主程序的Handler , 使其可以修改UI
     */
    private Handler callbackHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case 0:
                    break;
            }
        }
    };

    /**
     * Scan Object 的 Thread
     */
    private Thread threadScan = new Thread(){
        @Override
        public void run(){

            if (socket == null){
                socketConnect();
            }

            while(goScan){

                try{
                    // 為何要暫停？
                    // 因為如果在 Select 物件的同時, 這裏是不能一直取得資訊的
                    // 必須暫停這個 Thread, 讓他先一直睡覺, 另一個 Thread Select 結束後, 這裏才能繼續執行
                    while(scanStop){
                        Thread.sleep(1);
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                // 指令 Array
                byte[] commandArray;
                byte[] ackDataArray;
                if (scanCount == 0){
                    commandArray = commandGetCount();
                }else{
                    commandArray = commandGetInfo(scanIndex);
                }

                // 執行指令, 並接收回傳 dataArray
                ackDataArray = sendCommandGetArray(commandArray, socket);



                // 如果沒收到東西, 代表 socket 出現 Exception, 這裏要執行重新連線
                if(ackDataArray == null){
                    socketConnect();
                    continue;
                }


                // 確認是否為舊版本
                if(ackDataArray.length == 2){
                    continue;
                }


                // 確認是否系統忙碌中
                if(ackDataArray[1] == DataController.ACKTYPE_Error){
                    continue;
                }


                // 如果回傳的ack 跟送出的不一樣就不做任何事情
                if(currentCommandType != ackDataArray[1]){
                    continue;
                }


                // 開始分析回傳的資料, start analyze data
                if (ackDataArray[1] == DataController.RF_ACK_TYPE_GetMacCount){

                    // 紀錄 Object 總數
                    scanCount = ackDataArray[2];
                    // index 歸零, 使下一次的取得 info 可以從零開始
                    scanIndex = 0;

                }else if (ackDataArray[1] == DataController.RF_ACK_TYPE_GetMac_LIGHT && ackDataArray[3] == scanIndex) {

                    // 將收到的資料丟入 DataScan 去解析





                    // 因為 index 是從零開始, 所以要加一, 才能跟 count 做比較
                    if (scanCount > (scanIndex + 1)){
                        scanIndex ++;
                    }else if (scanCount == (scanIndex + 1)){
                        scanIndex = 0;
                    }else{
                        Log.e("","scanCount < scanIndex");
                    }

                }else{
                    Log.e("","ackDataArray[3] == scanIndex 可能是 false");
                    Log.e("","ackDataArray[3] = " + ackDataArray[3]);
                    Log.e("","scanIndex = " + scanIndex);
                }
            }





        }
    };


    /**
     * Select Object 的 Thread
     */
    private Thread threadSelect = new Thread(){
        @Override
        public void run(){

            if (socket == null){
                socketConnect();
            }

            // 修改這個布林值, 將 scan 的 Thread 先暫時中止, 讓它 sleep, 直到 Select 狀態結束
            scanStop = true;

            while(goSelect){

                // 指令 Array
                byte[] commandArray;
                byte[] ackDataArray;

                commandArray = commandSelect(selectIndex);

                // 執行指令, 並接收回傳 dataArray
                ackDataArray = sendCommandGetArray(commandArray, socket);


                // 如果沒收到東西, 代表 socket 出現 Exception, 這裏要執行重新連線
                if(ackDataArray == null){
                    socketConnect();
                    continue;
                }

                // 因為 Select 狀態不管回傳什麼東西都不重要, 所以就直接繼續跑下去
            }
            // 結束 Select 的狀態, 讓 Scan Thread 繼續跑下去
            scanStop = false;
        }
    };



    /**
     * Select Object 的 Thread
     */
    private Thread threadSetId = new Thread(){
        @Override
        public void run(){

            if (socket == null){
                socketConnect();
            }

            while(goSelect){

                // 指令 Array
                byte[] commandArray;
                byte[] ackDataArray;

                commandArray = commandSelect(selectIndex);

                // 執行指令, 並接收回傳 dataArray
                ackDataArray = sendCommandGetArray(commandArray, socket);


                // 如果沒收到東西, 代表 socket 出現 Exception, 這裏要執行重新連線
                if(ackDataArray == null){
                    socketConnect();
                    continue;
                }

                // 這裏要再確認一下 到底有沒有要去確認回傳狀態
            }
        }
    };


    /**
     * A command to get object count.
     * 取得 總數 的指令
     * @return 指令陣列
     */
    public byte[] commandGetCount(){
        currentCommandType = (byte)DataController.RF_ACK_TYPE_GetMacCount;

        byte[] byteArray = new byte[2];

        byteArray[0] = (byte)0xf1;
        byteArray[1] = (byte)0x01;
        // 不要懷疑，這組 command 就是沒有 checkSum

        return byteArray;
    }

    /**
     * A command to get object info.
     * 取得 詳細資訊 的指令, index 從 0 開始
     * @param index 欲取得的 物件index
     * @return 指令陣列
     */
    public byte[] commandGetInfo(int index){
        currentCommandType = (byte)DataController.RF_ACK_TYPE_GetMac_LIGHT;

        byte[] byteArray = new byte[3];

        byteArray[0] = (byte)0xf1;
        byteArray[1] = (byte)0x02;
        byteArray[2] = (byte)index;

        return byteArray;
    }

    /**
     * A command to make object in selected mode.
     * Select 物件的指令
     * @param index
     * @return 指令陣列
     */
    public byte[] commandSelect(int index){
        currentCommandType = (byte)DataController.RF_ACK_TYPE_Select;

        byte[] byteArray = new byte[3];

        byteArray[0] = (byte)0xf1;
        byteArray[1] = (byte)0x04;
        byteArray[2] = (byte)index;

        return byteArray;
    }

    /**
     * A command to set object Id.
     * 設定物件 ID
     * @param index_groupId
     * @param index_point
     * @return 指令陣列
     */
    public byte[] commandSetGroupId(int index_groupId, int index_point){
        currentCommandType = (byte)DataController.RF_ACK_TYPE_SetGroupID;

        byte[] byteArray = new byte[4];

        byteArray[0] = (byte)0xf1;
        byteArray[1] = (byte)0x03;
        byteArray[2] = (byte)index_point;
        byteArray[3] = (byte)index_groupId;

        return byteArray;
    }

    /**訊息傳輸
     * 把指令陣列丟進去給 outputStream 發送出去
     * 用 inputStream 接收回來的訊息, 並放到byteArray return回去
     * **/
    private byte[] sendCommandGetArray(byte[] tempArray, Socket ss){
        byte[] array_getEchoFromServer = new byte[1024];
        try {
            if(ss.getOutputStream() != null){
                ss.getOutputStream().write(tempArray);
                ss.getOutputStream().flush();

                int bytesRead;
                bytesRead = ss.getInputStream().read(array_getEchoFromServer);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                if (bytesRead  != -1){
                    byteArrayOutputStream.write(array_getEchoFromServer, 0, bytesRead);
                }
                array_getEchoFromServer = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
            }else{
                Log.d("sendCommandGetArray","bufferOutput == null");
            }
            return array_getEchoFromServer;
        } catch (IOException e) {
            e.printStackTrace();
            try { // 有 Error 就把 socket close 掉
                ss.close();
            } catch (IOException ee) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public interface ScanRfDataToolListener{
        void onGetAllData();
    }
}
