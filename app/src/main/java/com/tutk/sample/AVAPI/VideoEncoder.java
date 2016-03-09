//package com.tutk.sample.AVAPI;
//
//import android.media.MediaCodec;
//import android.media.MediaCodecInfo;
//import android.media.MediaFormat;
//import android.util.Log;
//import android.view.Surface;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//
///**
// *
// * 編碼器, 將 Camera 傳回來的 data 編成 H264的編碼
// * Created by anteya on 15/10/22.
// */
//public class VideoEncoder {
//
//    private MediaCodec mediaCodec;
//
//    private String type = "video/avc";
//
//    private int width = 720;
//    private int height = 1280;
//
//    private VideoDecoder videoDecoder;
//
//    public VideoEncoder(Surface surface){
//
//        videoDecoder = new VideoDecoder(surface);
//
//        try{
//
//            mediaCodec = MediaCodec.createEncoderByType(type);
//
//            MediaFormat mediaFormat = MediaFormat.createVideoFormat(type, width, height);
//            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
//            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
//            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
//            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
////            mediaFormat.setInteger(MediaFormat.KEY_CAPTURE_RATE, 15);
//            mediaFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
//
//
//            mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//            mediaCodec.start();
//
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public void onFrame(byte[] data, int offset, int length, int flag){
//        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
//        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
//
//        int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
//        System.out.println("onframe");
//        if(inputBufferIndex >= 0){
//            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//
//
//
//            System.out.println("data.length = " + data.length);
//            Log.d("", "data.length = " + data.length);
//            Log.d("", "inputBuffer.limit() = " + inputBuffer.limit());
//
//
//            inputBuffer.clear();
//
////            inputBuffer.rewind();
//
//            inputBuffer.put(data, offset, length);
//
//            mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, 0, 0);
//        }
//
//        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
//
//        while(outputBufferIndex >= 0){
//            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//            if(videoDecoder != null){
//                videoDecoder.onFrame(outputBuffer.array(), 0, length, flag);
//            }
//            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//        }
//
//    }
//}
