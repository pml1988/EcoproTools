package com.anteyatec.anteyalibrary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by anteya on 15/3/16.
 */
public class ITouchImage extends RelativeLayout {

    private final String TAG = "ITouchImage";

    private float currentX = 100, currentY = 100;
    private float initWidth = 150, initHeight = 100;
    /**
     * parent View 的寬跟高，用來當這裡的邊界， iTouchImage移動不能超出邊界
     */
    private float parentHeight = 100, parentWidth = 100;

    private LayoutInflater layoutInflater;

    private View convertView;

    private TextView textView;
    private ImageView imageView;


    public ITouchImage(Context context, int w, int h, int size){
        super(context);

        layoutInflater = LayoutInflater.from(context);

        convertView = layoutInflater.inflate(R.layout.layout_itouch, null);
        textView = (TextView) convertView.findViewById(R.id.relativeLayout_textView);
        imageView = (ImageView) convertView.findViewById(R.id.relativeLayout_imageView);

        this.addView(convertView);
        // 接收到指定的Size 之後還可以在這裡做調整，決定初始值再放大或縮小
        if (size != 0){
            initWidth = size;
            initHeight = (int)(size*0.8);
        }
        parentWidth = w;
        parentHeight = h;
        this.setX(currentX);
        this.setY(currentY);
//        resize();
    }

    private void resize() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) convertView.getLayoutParams();
        params.height = (int)initHeight;
        params.width = (int)initWidth;
        convertView.setLayoutParams(params);
    }

    public void setParentLayoutWidthHeight(int w, int h){
        parentWidth = w;
        parentHeight = h;
    }

    public void setText(String str){
        textView.setText(str);
    }

    /**
     * 存入 Data_ITouch list時，將iTouch之前紀錄的位置讀進來
     * 這裏進來的y 是百分比，要再乘上parent 的 寬高
     * @param x
     * @param y
     */
    public void setInitPosition(float x, float y){
        if(parentWidth == 0 && parentHeight == 0){
            currentX = 100;
            currentY = 100;
        }else{
            currentX=x * parentWidth;
            currentY=y * parentHeight;
        }
        this.setX(currentX);
        this.setY(currentY);
    }

    /**
     * 這裏進來的 xy是位移量，不是絕對座標
     * @param x
     * @param y
     */
    public void setNewPosition(float x, float y){

        Log.d(TAG, "parentWidth = " + parentWidth);
        Log.d(TAG, "initWidth = " + initWidth);
        Log.d(TAG, "currentX = " + currentX);

        currentX+=x;
        currentY+=y;

        if((currentX < 0)){
            this.setX(0);
        }else if(currentX + initWidth > parentWidth){
            this.setX(parentWidth - initWidth);
        }else{
            this.setX(currentX);
        }

        if((currentY < 0)){
            this.setY(0);
        }else if(currentY + initHeight > parentHeight){
            this.setY(parentHeight - initHeight);
        }else{
            this.setY(currentY);
        }
    }

    public float getXAtPercent(){
        if((currentX < 0)){
            return 0;
        }else if(currentX + initWidth > parentWidth){
            return (parentWidth - initWidth)/parentWidth;
        }else{
            return currentX/parentWidth;
        }
    }
    public float getYAtPercent(){
        if((currentY < 0)){
            return 0;
        }else if(currentY + initHeight > parentHeight){
            return (parentHeight - initHeight)/parentHeight;
        }else{
            return currentY/parentHeight;
        }
    }
}
