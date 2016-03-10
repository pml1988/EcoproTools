package com.anteya.ecoprotools;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.anteya.ecoprotools.object.DataControl;
import com.google.zxing.integration.android.IntentIntegrator;


import com.google.zxing.integration.android.IntentResult;
import com.tutk.sample.AVAPI.IpCamThread;
import com.tutk.sample.AVAPI.VideoDecoder;


import java.lang.ref.WeakReference;

public class IpCameraActivity extends Activity implements IpCamThread.DataReceiveListener, SurfaceHolder.Callback {

    private final String TAG = "IpCameraActivity";

    //LinearLayout
    private ViewGroup viewGroupQRCode;

    private SurfaceView surfaceView;

    private IpCamThread ipCamThread;

    private VideoDecoder decoder;

    private DataControl dataControl;

    private String strUid;

    private ImageButton activity_ipcam_left;

    private ImageButton activity_ipcam_down;

    private ImageButton activity_ipcam_right;

    private ImageButton activity_ipcam_up;

    private boolean flag = false;

    // region service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        System.out.println("創立");

        setContentView(R.layout.activity_ip_camera);

        initData();

        initView();

        strUid = dataControl.getIpCameraUid();
        if (strUid.length() > 0) {
            Log.d(TAG, "strUid.length() > 0");
            System.out.println("偵測到UID");
            flag_uid = true;
            viewGroupQRCode.setVisibility(View.INVISIBLE);
            ipCamThread = new IpCamThread(strUid, IpCameraActivity.this);
            ipCamThread.start();
            System.out.println("啟動IPcam");


        } else {
            Log.d(TAG, "strUid.length() <= 0");
            // Don't do anything
            System.out.println("無偵測到UID");
            surfaceView.setVisibility(View.INVISIBLE);
            viewGroupQRCode.setVisibility(View.VISIBLE);
        }
    }

    Thread time_control = null;
    private boolean flag_restart = true;
    private boolean flag_uid = false;

    @Override
    protected void onResume() {
        super.onResume();
        time_control = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                flag_restart = true;
                while (flag_restart) {

                    while (flag_uid) {
                        try {
                            Thread.sleep(1000);
                            count++;

                            if (count % 10 == 0) {
                                System.out.println("數數中：" + count);
                            }
                            if (strUid.length() > 1 && flag && count > 180) {

                                if (ipCamThread != null) {
                                    ipCamThread.closeThread();
                                    ipCamThread = null;
                                }
                                if (ipCamThread == null) {
                                    Thread.sleep(1000);
                                    ipCamThread = new IpCamThread(strUid, IpCameraActivity.this);
                                    ipCamThread.start();

                                    count = 0;
                                    System.out.println("循環重新畫面：" + count);
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        time_control.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        System.out.println("啟動 onStart()：");
        strUid = dataControl.getIpCameraUid();
//        Log.d(TAG, "onStart strUid = " + strUid);
        if (strUid.length() < 1) {
            Log.d(TAG, "strUid.length() > 0");
            flag_uid = false;
            System.out.println("無偵測到UID：" + strUid);
            surfaceView.setVisibility(View.INVISIBLE);
            viewGroupQRCode.setVisibility(View.VISIBLE);

        } else if (strUid.length() > 1 && flag) {
            System.out.println("偵測到UID：" + strUid);
            if (ipCamThread != null) {
                ipCamThread.closeThread();
                ipCamThread = null;
            }
            if (ipCamThread == null) {
                viewGroupQRCode.setVisibility(View.INVISIBLE);
                surfaceView.setVisibility(View.VISIBLE);
                flag_restart = true;
                flag_uid = true;
                ipCamThread = new IpCamThread(strUid, IpCameraActivity.this);
                ipCamThread.start();
                System.out.println("啟動IPcam");


            }
        }
        flag = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        flag_restart = false;
        flag_uid = false;
        time_control.interrupt();
        time_control = null;
        System.out.println("停止 onPause()");
        if (ipCamThread != null && strUid.length() > 0) {
            System.out.println("Pause 停止 ipcamthread");
            ipCamThread.closeThread();
            ipCamThread = null;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("中止");
        Log.d(TAG, "onDestroy strUid = " + strUid);
    }

    /**
     * 在一個主界面(主Activity)上能連接往許多不同子功能模塊(子Activity上去)，
     * 當子模塊的事情做完之後就回到主界面，
     * 或許還同時返回一些子模塊完成的數據交給主Activity處理。
     * 這樣的數據交流就要用到回調函數onActivityResult。
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //QRcode

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            strUid = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            dataControl.saveIpCameraUid(strUid);

            if (strUid != null) {
                flag_uid = true;
            }


            if (ipCamThread != null) {
                ipCamThread.closeThread();
                ipCamThread = null;
            }
            if (ipCamThread == null) {
                viewGroupQRCode.setVisibility(View.INVISIBLE);
                surfaceView.setVisibility(View.VISIBLE);
                flag = false;
                ipCamThread = new IpCamThread(strUid, IpCameraActivity.this);
                ipCamThread.start();
                System.out.println("啟動IPcam");


            }


            Toast.makeText(getApplicationContext(), "掃到 " + scanningResult.getContents(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ActionBar Menu OnclickListener

        System.out.println("onOptionsItemSelected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // endregion

    // region initial

    /**
     * 初始化 data
     */
    private void initData() {

        myHandler = new MyHandler(this);

        dataControl = (DataControl) getApplicationContext();

        strUid = dataControl.getIpCameraUid();
    }

    /**
     * 權限用户請求回應後回調
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.i(TAG, "onRequestPermissionsResult granted=" + granted);
            System.out.println("權限" + granted);
            if (granted) {
                checkVersion_and_CameraPermission();
            } else {
                Toast.makeText(this, "請確定開啟權限", Toast.LENGTH_SHORT);
                System.out.println("請確定開啟權限");
            }


        }
    }

    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {  //進行請求權限
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA_CODE);
    }


    /**
     * 接收IPCAM方向的指令
     **/
    private View.OnClickListener acitvity_ipcam_direction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.activity_ipcam_down:
                    if (ipCamThread != null) {
                        ipCamThread.sendIOCtrl_2((byte) 2);
                    }
                    System.out.println("下");
                    break;
                case R.id.activity_ipcam_up:
                    if (ipCamThread != null) {
                        ipCamThread.sendIOCtrl_2((byte) 1);
                    }
                    System.out.println("上");
                    break;
                case R.id.activity_ipcam_left:
                    if (ipCamThread != null) {
                        ipCamThread.sendIOCtrl_2((byte) 3);
                    }
                    System.out.println("左");
                    break;
                case R.id.activity_ipcam_right:
                    if (ipCamThread != null) {
                        ipCamThread.sendIOCtrl_2((byte) 6);
                    }
                    System.out.println("右");
                    break;

            }


        }
    };


    /**
     * 初始化 UI
     */
    private void initView() {


        activity_ipcam_right = (ImageButton) findViewById(R.id.activity_ipcam_right);
        activity_ipcam_left = (ImageButton) findViewById(R.id.activity_ipcam_left);
        activity_ipcam_down = (ImageButton) findViewById(R.id.activity_ipcam_down);
        activity_ipcam_up = (ImageButton) findViewById(R.id.activity_ipcam_up);

        activity_ipcam_right.setOnClickListener(acitvity_ipcam_direction);
        activity_ipcam_left.setOnClickListener(acitvity_ipcam_direction);
        activity_ipcam_down.setOnClickListener(acitvity_ipcam_direction);
        activity_ipcam_up.setOnClickListener(acitvity_ipcam_direction);

        viewGroupQRCode = (ViewGroup) findViewById(R.id.activityIpCamera_layoutQRCode);
        viewGroupQRCode.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "viewGroupQRCode onClick");
                checkVersion_and_CameraPermission();
            }
        });


        surfaceView = (SurfaceView) findViewById(R.id.activityIpCamera_surfaceView);
        surfaceView.getHolder().addCallback(this);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("重新連結");
            }
        });


        initActionBar();
    }

    private void checkVersion_and_CameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //判斷buile.version版本是否大於MARSHMALLOW ＝Ｍ
            int csp = checkSelfPermission(Manifest.permission.CAMERA); //用來檢測是否有權限,6.0後的規則
            if (csp != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();//跳出視窗使用者是否允許
            } else {
                IntentIntegrator scanIntegrator = new IntentIntegrator(IpCameraActivity.this);
                scanIntegrator.initiateScan();
            }
        } else {
            IntentIntegrator scanIntegrator = new IntentIntegrator(IpCameraActivity.this);
            scanIntegrator.initiateScan();
        }
    }


    private void initActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_ip_camera_activity, null);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(true); //IP Camera 標題文字
        actionBar.setDisplayHomeAsUpEnabled(true); //返回 < 的符號
        actionBar.setDisplayShowCustomEnabled(true); //自定義的icon
        actionBar.setCustomView(actionBarLayout);

        Button buttonSetting = (Button) actionBarLayout.findViewById(R.id.actionBar_ipCameraActivity_buttonSetting);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "Setting Button was clicked");

                Intent intent = new Intent(IpCameraActivity.this, IpCameraSettingsActivity.class);
                startActivity(intent);

            }
        });

    }

    // endregion

    // region surface callback

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("", "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("", "surfaceChanged");
        if (decoder == null) {
            decoder = new VideoDecoder(holder.getSurface());
        }
    }

    /**
     * surface破壞 關閉
     **/
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("", "surfaceDestroyed");
        if (ipCamThread != null) {
            ipCamThread.closeThread();
            ipCamThread = null;
        }
        if (decoder != null) {
            decoder.stopDecode();
            decoder = null;
        }
    }

    //endregion


    // region MyHandler

    public void updateView(byte[] data) {
        // 由 myHandler通知主執行緒更新UI




    //    System.out.println("data[]:"+data.length);

//        for(int i =  10   ;i<20;i++  )
//        {
//            System.out.print(data[i]+" ");
//
//        }
       // System.out.println("");
      //  System.out.println("updateView");

        /**更新畫面最後地方**/
        if (decoder != null) {
           decoder.onFrame(data, 0, data.length, 0);
        }
    }

    @Override
    public void onVideoDataReceive(byte[] data) {
        // 收到 IpCamThread 傳來的Data, 將Data傳回主執行緒進行UI更新
        Message message = new Message();
        message.what = MyHandler.RECEIVE_DATA;
        message.obj = data;
        myHandler.sendMessage(message);
    }

    private MyHandler myHandler;

    private static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<IpCameraActivity> mOuter;

        public MyHandler(IpCameraActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        public static final int RECEIVE_DATA = 1;
        public static final int CHECK_LINK = 2;

        @Override
        public void handleMessage(Message msg) {
            IpCameraActivity activity = mOuter.get();
            if (activity != null) {
                // Do something with outer as your wish.


                switch (msg.what) {
                    case RECEIVE_DATA:
                        Log.d("", "RECEIVE_DATA");
                        activity.updateView((byte[]) msg.obj);
                        break;
                    case CHECK_LINK:

                        break;
                }
            }
        }
    }

    // endregion
}