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

    static Camera camera = null;


    // region service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_ip_camera);

        initData();

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        System.out.println("停止");
        if (ipCamThread != null && strUid.length() > 0) {
            Log.d(TAG, "ipCamThread.closeThread()");
            ipCamThread.closeThread();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy strUid = " + strUid);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart strUid = " + strUid);

        strUid = dataControl.getIpCameraUid();

        ipCamThread = null;
        if (strUid.length() > 0) {
            Log.d(TAG, "strUid.length() > 0");
            viewGroupQRCode.setVisibility(View.INVISIBLE);
            // Start link
            ipCamThread = new IpCamThread(strUid, IpCameraActivity.this);
            ipCamThread.start();
        } else {
            Log.d(TAG, "strUid.length() <= 0");
            // Don't do anything
            surfaceView.setVisibility(View.INVISIBLE);
            viewGroupQRCode.setVisibility(View.VISIBLE);
        }
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

            if (ipCamThread != null) {
                ipCamThread.closeThread();
                ipCamThread = null;
            }
            if (ipCamThread == null) {
                ipCamThread = new IpCamThread(strUid, IpCameraActivity.this);
                ipCamThread.start();
                viewGroupQRCode.setVisibility(View.INVISIBLE);
                surfaceView.setVisibility(View.VISIBLE);
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
            if(granted)
            {
                checkVersion_and_CameraPermission();
            }
            else
            {
                Toast.makeText(this , "請確定開啟權限" ,Toast.LENGTH_SHORT );
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
     * 初始化 UI
     */
    private void initView() {


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

        initActionBar();
    }

    private void  checkVersion_and_CameraPermission()
    {
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
