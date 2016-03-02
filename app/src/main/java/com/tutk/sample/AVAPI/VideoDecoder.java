package com.tutk.sample.AVAPI;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * 解碼器, 將 H264 編碼資料 解碼成影像畫面
 * Created by anteya on 15/10/22.
 */
public class VideoDecoder {
    // 解碼器
    private MediaCodec mediaCodec;

    private String mime = "video/avc";

    private int width = 640;
    private int height = 352;

    private int mCount = 1;

    public VideoDecoder(Surface surface){

        try {
            mediaCodec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(mime, width, height);

        mediaCodec.configure(mediaFormat, surface, null, 0);
        mediaCodec.start();
    }

    /**
     * 輸入 data
     * @param data
     * @param offset
     * @param length
     * @param flag
     */
    public void onFrame(byte[] data, int offset, int length, int flag){

        // 這裡出現 Exception 的話代表 mediaCodec 可能被關閉, 或尚未開啟
        ByteBuffer[] inputBuffers;
        try{
            inputBuffers = mediaCodec.getInputBuffers();
        }catch(IllegalStateException e){
            return;
        }

        int inputBufferIndex = mediaCodec.dequeueInputBuffer(0);

        if(inputBufferIndex >= 0){
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(data, offset, length);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount * 1000000/ 15, 0);
            mCount++;
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        while(outputBufferIndex >= 0){
            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

    public void stopDecode(){
        mediaCodec.stop();
        mediaCodec.release();
    }
}
