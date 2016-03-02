package com.tutk.IOTC;


import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Client {

    private OnTestListener listener;

    public Client(String uid, OnTestListener listener){

        this.listener = listener;






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

        int sid = IOTCAPIs.IOTC_Connect_ByUID(uid);
        System.out.printf("Step 2: call IOTC_Connect_ByUID(%s).......\n", uid);

        int[] srvType = new int[1];
        int avIndex = AVAPIs.avClientStart(sid, "admin", "admin", 20000, srvType, 0);
        System.out.printf("Step 2: call avClientStart(%d).......\n", avIndex);

        if (avIndex < 0) {
            System.out.printf("avClientStart failed[%d]\n", avIndex);
            return;
        }

        if (startIpcamStream(avIndex)) {

            videoT = new VideoThread(avIndex);
            audioT = new AudioThread(avIndex);

            Thread videoThread = new Thread(videoT,
                    "Video Thread");
            Thread audioThread = new Thread(audioT,
                    "Audio Thread");

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


    public interface OnTestListener{
        void onImageByteReceive();
    }




    private static VideoThread videoT;
    private static AudioThread audioT;


    public static void start(String uid) {

        
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

        int sid = IOTCAPIs.IOTC_Connect_ByUID(uid);
        System.out.printf("Step 2: call IOTC_Connect_ByUID(%s).......\n", uid);

        int[] srvType = new int[1];
        int avIndex = AVAPIs.avClientStart(sid, "admin", "admin", 20000, srvType, 0);
        System.out.printf("Step 2: call avClientStart(%d).......\n", avIndex);

        if (avIndex < 0) {
            System.out.printf("avClientStart failed[%d]\n", avIndex);
            return;
        }

        if (startIpcamStream(avIndex)) {

            videoT = new VideoThread(avIndex);
            audioT = new AudioThread(avIndex);

            Thread videoThread = new Thread(videoT,
                    "Video Thread");
            Thread audioThread = new Thread(audioT,
                    "Audio Thread");

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
            System.out.printf("start_ipcam_stream failed[%d]\n", ret);
            return false;
        }

        return true;
    }

    public static class VideoThread implements Runnable {
        static final int VIDEO_BUF_SIZE = 100000;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public VideoThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {

            System.out.printf("[%s] Start\n",
                    Thread.currentThread().getName());

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] videoBuffer = new byte[VIDEO_BUF_SIZE];

            while (true) {


                System.out.printf("[%s] while going \n",
                        Thread.currentThread().getName());

                int[] frameNumber = new int[1];
                int ret = av.avRecvFrameData(avIndex, videoBuffer,
                        VIDEO_BUF_SIZE, frameInfo, FRAME_INFO_SIZE,
                        frameNumber);

                if (ret == AVAPIs.AV_ER_DATA_NOREADY) {
                    try {
                        Thread.sleep(130);
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



                break;


                // Now the data is ready in videoBuffer[0 ... ret - 1]
                // Do something here
            }
            
            System.out.printf("[%s] Exit\n",
                    Thread.currentThread().getName());
        }
    }

    public static class AudioThread implements Runnable {
        static final int AUDIO_BUF_SIZE = 1024;
        static final int FRAME_INFO_SIZE = 16;

        private int avIndex;

        public AudioThread(int avIndex) {
            this.avIndex = avIndex;
        }

        @Override
        public void run() {
            System.out.printf("[%s] Start\n",
                    Thread.currentThread().getName());

            AVAPIs av = new AVAPIs();
            byte[] frameInfo = new byte[FRAME_INFO_SIZE];
            byte[] audioBuffer = new byte[AUDIO_BUF_SIZE];
            while (true) {


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

                MainActivity.printByte(frameInfo);


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

                break;


            }

            System.out.printf("[%s] Exit\n",
                    Thread.currentThread().getName());
        }
    }



    /**
     * 使用預設的路徑 名稱，產出 File 物件 並return
     * @return
     */
    private static File getPathFile(){
        // 設定路徑 存到 "/data/ipHost/"
        File sdDir = Environment.getExternalStorageDirectory();
        File fileDir = new File(sdDir, "/avtest/");
        if (!fileDir.exists()) {
            fileDir.mkdirs();// 如果資料夾不存在則新增資料夾
        }
        String fileName = String.format("%d.mp4", System.currentTimeMillis());
        File file = new File(fileDir, fileName);
        return file;
    }

}
