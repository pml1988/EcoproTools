package com.tutk.sample.AVAPI;

import android.content.Context;
import android.graphics.Camera;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by anteya on 15/10/19.
 */
public class Monitor extends SurfaceView {

    private final String strVideoAVC = "video/avc";
    private final String strVideoMP4V = "video/mp4v-es";
    private String strVideo = strVideoAVC;

    public Monitor(Context context) {
        super(context);


    }

}
