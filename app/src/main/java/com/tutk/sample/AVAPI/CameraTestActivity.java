//package com.tutk.sample.AVAPI;
//
//import android.app.Activity;
//import android.graphics.ImageFormat;
//import android.hardware.Camera;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import java.io.IOException;
//
//public class CameraTestActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback{
//
//    private Camera camera;
//    private Surface surface;
//    private SurfaceHolder surfaceHolder;
//
//    private String TAG = "CameraTestActivity";
//
//    private int camWidth = 720;
//    private int camHeight = 1280;
//
//    private byte[] buf;
//
//    private VideoEncoder videoEncoder;
//
//    private SurfaceView surfaceView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_camera_test);
//
//        surfaceView = new SurfaceView(this);
//        surfaceView.getHolder().addCallback(this);
//        setContentView(surfaceView);
//
//
//
//        Log.d(TAG, "Camera.open()");
//        camera = Camera.open();
//
//        try{
//            camera.setPreviewDisplay(surfaceHolder);
//        } catch(IOException e){
//            e.printStackTrace();
//        }
//
//        Camera.Parameters parameters = camera.getParameters();
//        parameters.setFlashMode("off");
//        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
//        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        parameters.setPreviewFormat(ImageFormat.YV12);
////        parameters.setPictureSize(camWidth, camHeight);
////        parameters.setPreviewSize(camWidth, camHeight);
//
//        camera.setParameters(parameters);
//
//        buf = new byte[camWidth * camHeight * 3 / 2];
//        camera.addCallbackBuffer(buf);
//
//        camera.setPreviewCallbackWithBuffer(this);
//
//        Log.d(TAG, "camera.startPreview()");
//        camera.startPreview();
//    }
//
//    @Override
//    public void onPreviewFrame(byte[] data, Camera camera) {
//
//        Log.d(TAG, "onPreviewFrame , data.length = " + data.length);
//
//
//
//
//        if(videoEncoder != null){
//            videoEncoder.onFrame(data, 0, data.length, 0);
//        }
//
//        camera.addCallbackBuffer(buf);
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//        if(videoEncoder == null){
//            videoEncoder = new VideoEncoder(holder.getSurface());
//        }
//
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }
//
//}
