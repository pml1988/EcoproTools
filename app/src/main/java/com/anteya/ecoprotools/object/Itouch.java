package com.anteya.ecoprotools.object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anteya.ecoprotools.R;


/**
 * Created by marco on 16/5/19.
 */
public class Itouch extends RelativeLayout {

    private final String TAG = "ITouchImage";

    private float currentX = 0, currentY = 0;

    private LayoutInflater layoutInflater;

    private View convertView;

    private ImageView imageButton;

    int screen_x , screen_y;

    private TextView temp_view , temp_hum;

    public Itouch(Context context, int w, int h, int size , int screen_x , int screen_y) {
        super(context);

        this.screen_x = screen_x;
        this.screen_y = screen_y;

        layoutInflater = LayoutInflater.from(context);

        convertView = layoutInflater.inflate(R.layout.layoumove, null);


        System.out.println("sss " + convertView.getHeight() + " " + convertView.getWidth());

        temp_view = (TextView)convertView.findViewById(R.id.temp_view);

        temp_hum =(TextView)convertView.findViewById(R.id.temp_hum);

        imageButton = (ImageView) convertView.findViewById(R.id.imageButto);

        this.addView(convertView);

        this.setX(w);
        this.setY(h);
    }

    public void change_temp_hum (double temp , double hum)
    {
        temp_view.setText("Temp:"+temp);
        temp_hum.setText("Hum:"+hum);

    }


    public void setNewPosition(float x, float y) {


        System.out.println("座標1== X:" + x + " Y:" + y);

        currentX += x;
        currentY += y;

        System.out.println("座標2== X:" + currentX + " Y:" + currentY);
//        this.setX(currentX);
//        this.setY(currentY);


        if (currentX > 0) {
            this.setX(currentX);
        } else {
            currentX=0;
            this.setX(0);
        }

        //物件圖片的的長寬Ｘ2
        if (currentX < screen_x-240) {
            this.setX(currentX);
        } else {
            currentX=screen_x-240;
            this.setX(screen_x-240);
        }
        if (currentY < screen_y-400) {
            this.setY(currentY);
        } else {
            currentY=screen_y-400;
            this.setY(screen_y-400);
        }
        if (currentY > 0) {
            this.setY(currentY);
        } else {
            currentY=0;
            this.setY(0);
        }

    }
}

