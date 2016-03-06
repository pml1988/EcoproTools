package com.tutk.sample.AVAPI;

import android.util.Log;

import com.anteya.ecoprotools.object.ProjectTools;
import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.MainActivity;

/**
 * Created by anteya on 15/10/5.
 */
public class IpCamThread {

    private final int SEND_DATA = 0;

    private final String TAG = "IPCamThread";

    private String UID;
    private int index = -1;

    private boolean going = false;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(int securityMode) {
        this.securityMode = securityMode;
    }

    public interface DataReceiveListener {
        void onVideoDataReceive(byte[] data);
    }

    private DataReceiveListener listener;

    public IpCamThread(String uid, DataReceiveListener listener){
        this.listener = listener;
        this.UID = uid;
    }

    public void start(){

        if(going == false){
            going = true;
            threadIPCam.start();
            System.out.println("threadIPCam.start();");

        }
    }

    public void closeThread(){
        going = false;
    }

    // 整數轉成 byte array
    public static final byte[] intToByteArray_Little(int paramInt)
    {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = ((byte)paramInt);
        arrayOfByte[1] = ((byte)(paramInt >>> 8));
        arrayOfByte[2] = ((byte)(paramInt >>> 16));
        arrayOfByte[3] = ((byte)(paramInt >>> 24));
        return arrayOfByte;
    }

    public static boolean setWifi(int avIndex, byte[] byteArray){
        AVAPIs av = new AVAPIs();

        int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 0x0342;

        byte[] tempArray1 = SMsgAVIoctrlSetStreamCtrlReq.SET_WIFI_COMMAND();

        int ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, byteArray, byteArray.length);

        if (ret < 0) {
            System.out.printf("IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ failed[%d]\n", ret);
            if (ret < 0) {
                System.out.printf("start_ipcam_stream failed[%d]\n", ret);
                return false;
            }
        }

        System.out.printf("IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ success[%d]\n", ret);

        return true;
    }


    public void sendIOCtrl_2(final byte direct){

        System.out.println("啟動這裡1:"+index);
        new Thread(){
            @Override
            public void run(){
                if(index >= 0){

                    System.out.println("啟動這裡2:"+index);

                    AVAPIs av = new AVAPIs();

                    int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 0x1001;

                    byte[] tempArray1 = SMsgAVIoctrlSetStreamCtrlReq.parseContent(direct, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0);

                    int ret = av.avSendIOCtrl(index, IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ, tempArray1, 8);

                    if (ret < 0) {

                        System.out.printf("IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ failed[%d]\n", ret);
                    }else{
                        System.out.printf("IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ success[%d]\n", ret);
                    }


                    if (ret < 0) {
                        System.out.printf("IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ failed[%d]\n", ret);
                    }else{
                        System.out.printf("IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ success[%d]\n", ret);
                    }
                }else{
                    Log.e("","IP Cam is not connect.");
                }

            }
        }.start();
    }


    private String ssid = "";
    private String password = "";
    private int securityMode = 0;





    private Thread threadIPCam = new Thread(){
        @Override
        public void run(){
            System.out.println("StreamClient start...");

            // use which Master base on location, port 0 means to get a random port
            int ret = IOTCAPIs.IOTC_Initialize(0, "m1.iotcplatform.com", "m2.iotcplatform.com", "m4.iotcplatform.com", "m5.iotcplatform.com");
            System.out.printf("IOTC_Initialize() ret = %d\n", ret);
            if (ret != IOTCAPIs.IOTC_ER_NoERROR) {
                System.out.printf("IOTCAPIs_Device exit...!!\n");
                return;
            }

            // alloc 3 sessions for video and two-way audio
            AVAPIs.avInitialize(3);

            int sid = IOTCAPIs.IOTC_Connect_ByUID(UID);
            System.out.printf("Step 2: call IOTC_Connect_ByUID(%s)... return sid(%d)\n", UID, sid);

            int[] srvType = new int[1];
            int avIndex = AVAPIs.avClientStart(sid, "admin", "admin", 20000, srvType, 0);
            System.out.printf("Step 2: call avClientStart(%d).......%d\n", avIndex, srvType[0]);
            Log.e("sendIOCtrl_1", "IP Cam 000 index = " + index);


            System.out.println("avIndex:" + avIndex);
            index = avIndex;
            System.out.println("index:" + index);
            Log.e("sendIOCtrl_1", "IP Cam 0000 index = " + index);

            if (avIndex < 0) {
                System.out.printf("avClientStart failed[%d]\n", avIndex);
                return;
            }

            if (startIpcamStream(avIndex)) {

                VideoThread videoT = new VideoThread(avIndex);
                AudioThread audioT = new AudioThread(avIndex);

                Thread videoThread = new Thread(videoT, "Video Thread");
                Thread audioThread = new Thread(audioT, "Audio Thread");

                videoThread.start();
                audioThread.start();

                try {
                    videoThread.join();
                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    return;
                }
                try {
                    audioThread.join();
                }
                catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }

            AVAPIs.avClientStop(avIndex);
            System.out.printf("avClientStop OK\n");
            IOTCAPIs.IOTC_Session_Close(sid);
            System.out.printf("IOTC_Session_Close OK\n");
            AVAPIs.avDeInitialize();
            IOTCAPIs.IOTC_DeInitialize();
            System.out.printf("StreamClient exit...\n");
        }
    };

    public static boolean startIpcamStream(int avIndex) {
        AVAPIs av = new AVAPIs();
        int ret = av.avSendIOCtrl(avIndex, AVAPIs.IOTYPE_INNER_SND_DATA_DELAY,
                new byte[2], 2);
        if (ret < 0) {
            System.out.printf("start_ipcam_stream failed[%d]\n", ret);
            return false;
        }

        // This IOTYPE constant and its corrsponsing data structure is defined in
        // Sample/Linux/Sample_AVAPIs/AVIOCTRLDEFs.h
        //
        int IOTYPE_USER_IPCAM_START = 0x1FF;
        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_START,
                new byte[8], 8);
        if (ret < 0) {
            System.out.printf("start_ipcam_stream failed[%d]\n", ret);
            return false;
        }

        int IOTYPE_USER_IPCAM_AUDIOSTART = 0x300;
        ret = av.avSendIOCtrl(avIndex, IOTYPE_USER_IPCAM_AUDIOSTART,
                new byte[8], 8);
        if (ret < 0) {
            System.out.printf("start_ipcam_audio_stream failed[%d]\n", ret);
            return false;
        }


        return true;
    }



    public class VideoThread implements Runnable {
        static final int VIDEO_BUF_SIZE = 50000;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public VideoThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {

            System.out.printf("VideoThread implements Runnable　[%s] Start\n", Thread.currentThread().getName());

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] videoBuffer = new byte[VIDEO_BUF_SIZE];

            while (going) {

                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int[] frameNumber = new int[1];
                int ret = av.avRecvFrameData(avIndex, videoBuffer,
                        VIDEO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE,
                        frameNumber);

                if(ret > 0){

                    // return code 代表回傳資料的長度
                    // 準備一個相同長度的 byte array 來放置接收到的資料，不要直接將 100000長度的byte array 回傳到 MainActivity 去解碼
                    byte[] videoBufferNew = new byte[ret];
                    System.arraycopy(videoBuffer, 0, videoBufferNew, 0, videoBufferNew.length);

                    if(listener != null && going){
                        listener.onVideoDataReceive(videoBufferNew);
                    }


                }

                if (ret == AVAPIs.AV_ER_DATA_NOREADY) {
                    try {
                        Thread.sleep(80);
                        continue;
                    }
                    catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
                else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    System.out.printf("[%s] Lost video frame number[%d]\n",
                            Thread.currentThread().getName(), frameNumber[0]);
                    continue;
                }
                else if (ret == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                    System.out.printf("[%s] Incomplete video frame number[%d]\n",
                            Thread.currentThread().getName(), frameNumber[0]);
                    continue;
                }
                else if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    System.out.printf("[%s] AV_ER_SESSION_CLOSE_BY_REMOTE\n",
                            Thread.currentThread().getName());
                    break;
                }
                else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    System.out.printf("[%s] AV_ER_REMOTE_TIMEOUT_DISCONNECT\n",
                            Thread.currentThread().getName());
                    break;
                }
                else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    System.out.printf("[%s] Session cant be used anymore\n",
                            Thread.currentThread().getName());
                    break;
                }


                // Now the data is ready in videoBuffer[0 ... ret - 1]
                // Do something here
            }
            System.out.printf("VideoThread implements Runnable [%s] Exit\n", Thread.currentThread().getName());
        }
    }

    public class AudioThread implements Runnable {
        static final int AUDIO_BUF_SIZE = 1024;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public AudioThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            System.out.printf("AudioThread implements Runnable　[%s] Start\n", Thread.currentThread().getName());

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] audioBuffer = new byte[AUDIO_BUF_SIZE];
            while (going) {


                int ret = av.avCheckAudioBuf(avIndex);

                if (ret < 0) {
                    // Same error codes as below
                    System.out.printf("[%s] avCheckAudioBuf() failed: %d\n",
                            Thread.currentThread().getName(), ret);
                    break;
                }
                else if (ret < 3) {
                    try {
                        Thread.sleep(120);
                        continue;
                    }
                    catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }

                int[] frameNumber = new int[1];
                ret = av.avRecvAudioData(avIndex, audioBuffer,
                        AUDIO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE,
                        frameNumber);



                if (ret == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                    System.out.printf("[%s] AV_ER_SESSION_CLOSE_BY_REMOTE\n",
                            Thread.currentThread().getName());
                    break;
                }
                else if (ret == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                    System.out.printf("[%s] AV_ER_REMOTE_TIMEOUT_DISCONNECT\n",
                            Thread.currentThread().getName());
                    break;
                }
                else if (ret == AVAPIs.AV_ER_INVALID_SID) {
                    System.out.printf("[%s] Session cant be used anymore\n",
                            Thread.currentThread().getName());
                    break;
                }
                else if (ret == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                    //System.out.printf("[%s] Audio frame losed\n",
                    //        Thread.currentThread().getName());
                    continue;
                }

                // Now the data is ready in audioBuffer[0 ... ret - 1]
                // Do something here

//                break;
            }
            System.out.printf("AudioThread implements Runnable [%s] Exit\n",
                    Thread.currentThread().getName());
        }
    }




//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        public void startSetWifi(){

        threadIPCamWifiSettings.start();
    }

    private Thread threadIPCamWifiSettings = new Thread(){
        @Override
        public void run(){
            System.out.println("StreamClient start...");

            // use which Master base on location, port 0 means to get a random port
            int ret = IOTCAPIs.IOTC_Initialize(0, "m1.iotcplatform.com",
                    "m2.iotcplatform.com", "m4.iotcplatform.com",
                    "m5.iotcplatform.com");
            System.out.printf("IOTC_Initialize() ret = %d\n", ret);

            if (ret != IOTCAPIs.IOTC_ER_NoERROR) {
                System.out.printf("IOTCAPIs_Device exit...!!\n");
                return;
            }

            // alloc 3 sessions for video and two-way audio
            AVAPIs.avInitialize(3);

            int sid = IOTCAPIs.IOTC_Connect_ByUID(UID);
            System.out.printf("Step 2: call IOTC_Connect_ByUID(%s)... return sid(%d)\n", UID, sid);

            int[] srvType = new int[1];
            int avIndex = AVAPIs.avClientStart(sid, "admin", "admin", 20000, srvType, 0);
            System.out.printf("Step 2: call avClientStart(%d).......%d\n", avIndex, srvType[0]);
            Log.e("sendIOCtrl_1", "IP Cam 000 index = " + index);


            index = avIndex;
            Log.e("sendIOCtrl_1", "IP Cam 0000 index = " + index);

            if (avIndex < 0) {
                System.out.printf("avClientStart failed[%d]\n", avIndex);
                return;
            }

            setWifi(avIndex, ProjectTools.getIOControlCommand_wifiSettings(ssid, password, securityMode));

            AVAPIs.avClientStop(avIndex);
            System.out.printf("avClientStop OK\n");
            IOTCAPIs.IOTC_Session_Close(sid);
            System.out.printf("IOTC_Session_Close OK\n");
            AVAPIs.avDeInitialize();
            IOTCAPIs.IOTC_DeInitialize();
            System.out.printf("StreamClient exit...\n");
        }
    };
}
