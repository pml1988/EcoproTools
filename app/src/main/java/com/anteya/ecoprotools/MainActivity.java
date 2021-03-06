package com.anteya.ecoprotools;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.anteya.ecoprotools.object.EcoproConnector;
import com.anteya.ecoprotools.object.EcoproConnector.EcoproConnectorCallback;
import com.anteya.ecoprotools.object.SQLiteControl;
import com.anteya.ecoprotools.operatingtype.AirOperatingTime;
import com.anteya.ecoprotools.object.DataControl;
import com.anteya.ecoprotools.operatingtype.FanOperatingTime;
import com.anteya.ecoprotools.operatingtype.LightOperatingTime;
import com.anteya.ecoprotools.object.ProjectTools;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements EcoproConnectorCallback , View.OnClickListener {

    // region Data variables

    private String TAG = "MainActivity";

    private DataControl dataControl;

    private EcoproConnector ecoproConnector;

    private String ipAddress;

    private LightOperatingTime lightOperatingTime;
    private AirOperatingTime airOperatingTime;
    private FanOperatingTime fanOperatingTime;

    /**
     * 暫存目前是哪一種模式
     */
    private int currentModeValue = 1;
    /**
     * 暫存目前點擊的是哪一個時間設定
     */
    private int currentTag = 0;

    // endregion

    // region View variables

    private Button buttonShotDown;
    private Button buttonF1;
    private Button buttonF2;
    private Button buttonF3;
    private Button buttonManual;
    private ImageButton imageButton;
    private TextView textViewLightTurnOnTime, textViewLightTurnOffTime;
    private TextView textViewAirTurnOnTime, textViewAirTurnOffTime;
    private TextView textViewFanTurnOnTime, textViewFanTurnOffTime;
    private ImageView imageViewLightWorkStatus, imageViewAirWorkStatus, imageViewFanWorkStatus, imageViewWaterStatus;
    private TextView textViewLight, textViewAir, textViewFan;
    private LinearLayout manual_layout ;

    private TextView manual_m1,manual_m2,manual_m3,manual_m4,manual_m5;


    // endregion

    // region service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat sdf = new SimpleDateFormat("MM 月 dd 日 HH 時 mm 分 ss 秒");
        System.out.println("======================(校正)" + sdf.format(new Date()) + "(校正)======================");

        initData();

        initView();

        SQLiteControl sqLiteControl = new SQLiteControl(this);


//        dataControl.saveIpCameraUid("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ipAddress = dataControl.getIpAddress();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    // endregion

    // region initial

    private void initData() {

        lightOperatingTime = new LightOperatingTime();
        airOperatingTime = new AirOperatingTime();
        fanOperatingTime = new FanOperatingTime();

        //IP cam
        dataControl = (DataControl) getApplicationContext();

        myHandler = new MyHandler(this);

        ecoproConnector = new EcoproConnector();
        ecoproConnector.setEcoproConnectorCallback(this);  //interface

//        dataControl.saveIpCameraUid("");
    }

    private void initView() {
        buttonShotDown = (Button) findViewById(R.id.button0);
        buttonShotDown.setTag(0);
        buttonF1 = (Button) findViewById(R.id.button1);
        buttonF1.setTag(1);
        buttonF2 = (Button) findViewById(R.id.button2);
        buttonF2.setTag(2);
        buttonF3 = (Button) findViewById(R.id.button3);
        buttonF3.setTag(3);
        buttonManual = (Button) findViewById(R.id.button4);
        buttonManual.setTag(4);

        //控制手動模式 設定組別
        manual_layout = (LinearLayout)findViewById(R.id.activityMain_Manual_control);
        manual_m1 = (TextView)findViewById(R.id.manual_m1);
        manual_m2 = (TextView)findViewById(R.id.manual_m2);
        manual_m3 = (TextView)findViewById(R.id.manual_m3);
        manual_m4 = (TextView)findViewById(R.id.manual_m4);
        manual_m5 = (TextView)findViewById(R.id.manual_m5);
        manual_m1.setOnClickListener(this);
        manual_m2.setOnClickListener(this);
        manual_m3.setOnClickListener(this);
        manual_m4.setOnClickListener(this);
        manual_m5.setOnClickListener(this);



        buttonShotDown.setOnClickListener(buttonClickListener);
        buttonF1.setOnClickListener(buttonClickListener);
        buttonF2.setOnClickListener(buttonClickListener);
        buttonF3.setOnClickListener(buttonClickListener);
        buttonManual.setOnClickListener(buttonClickListener);
        buttonF1.setSelected(true);

        imageButton = (ImageButton) findViewById(R.id.activityMain_imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IpCameraActivity.class);
                startActivity(intent);
            }
        });

        textViewLightTurnOnTime = (TextView) findViewById(R.id.activityMain_textLightTurnOnTime);
        textViewLightTurnOffTime = (TextView) findViewById(R.id.activityMain_textLightTurnOffTime);
        textViewAirTurnOnTime = (TextView) findViewById(R.id.activityMain_textAirTurnOnTime);
        textViewAirTurnOffTime = (TextView) findViewById(R.id.activityMain_textAirTurnOffTime);
        textViewFanTurnOnTime = (TextView) findViewById(R.id.activityMain_textFanTurnOnTime);
        textViewFanTurnOffTime = (TextView) findViewById(R.id.activityMain_textFanTurnOffTime);

        textViewLightTurnOnTime.setTag(11);
        textViewLightTurnOffTime.setTag(12);
        textViewAirTurnOnTime.setTag(21);
        textViewAirTurnOffTime.setTag(22);
        textViewFanTurnOnTime.setTag(31);
        textViewFanTurnOffTime.setTag(32);

        textViewLightTurnOnTime.setOnClickListener(textViewClickListener);
        textViewAirTurnOnTime.setOnClickListener(textViewClickListener);
        textViewFanTurnOnTime.setOnClickListener(textViewClickListener);
        textViewLightTurnOffTime.setOnClickListener(textViewClickListener);
        textViewAirTurnOffTime.setOnClickListener(textViewClickListener);
        textViewFanTurnOffTime.setOnClickListener(textViewClickListener);

//        textViewLightTurnOnTime.setText("AM00:00");
//        textViewLightTurnOffTime.setText("AM00:00");
//        textViewAirTurnOnTime.setText("AM00:00");
//        textViewAirTurnOffTime.setText("AM00:00");
//        textViewFanTurnOnTime.setText("AM00:00");
//        textViewFanTurnOffTime.setText("AM00:00");

//        textViewLightTurnOnTime.setText(ProjectTools.getTimeString(LightOperatingTime.F1Mode_DefaultTurnOnTime));
//        textViewLightTurnOffTime.setText(ProjectTools.getTimeString(LightOperatingTime.F1Mode_DefaultTurnOffTime));
//        textViewAirTurnOnTime.setText(ProjectTools.getTimeString(AirOperatingTime.F1Mode_DefaultTurnOnTime));
//        textViewAirTurnOffTime.setText(ProjectTools.getTimeString(AirOperatingTime.F1Mode_DefaultTurnOffTime));
//        textViewFanTurnOnTime.setText(ProjectTools.getTimeString(FanOperatingTime.F1Mode_DefaultTurnOnTime));
//        textViewFanTurnOffTime.setText(ProjectTools.getTimeString(FanOperatingTime.F1Mode_DefaultTurnOffTime));

        imageViewLightWorkStatus = (ImageView) findViewById(R.id.activityMain_imageLightStatus);
        imageViewAirWorkStatus = (ImageView) findViewById(R.id.activityMain_imageAirStatus);
        imageViewFanWorkStatus = (ImageView) findViewById(R.id.activityMain_imageFanStatus);
        imageViewWaterStatus = (ImageView) findViewById(R.id.activityMain_imageWaterStatus);

        textViewLight = (TextView) findViewById(R.id.activityMain_textLightTitle);
        textViewAir = (TextView) findViewById(R.id.activityMain_textAirTitle);
        textViewFan = (TextView) findViewById(R.id.activityMain_textFanTitle);

        switchEnabled(false);
        initActionBar();
    }

    private void initActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_main_activity, null);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        Button buttonSetting = (Button) actionBarLayout.findViewById(R.id.actionBar_mainActivity_buttonSetting);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "Setting Button was clicked");

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });

    }

    // endregion

    // region view click listener

    private View.OnClickListener textViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tempTextView = (TextView) v;
            int textViewTag = (int) tempTextView.getTag();
            currentTag = textViewTag;
            boolean isOnOff = (textViewTag % 10 == 1);

            switch (textViewTag / 10) {
                case 1:
                    showTimePickerDialog(lightOperatingTime.getTimeByModeOnOff(currentModeValue, isOnOff), lightOperatingTime.getTimeByModeOnOff_minute(currentModeValue, isOnOff), isOnOff);
                    break;
                case 2:
                    showTimePickerDialog(airOperatingTime.getTimeByModeOnOff(currentModeValue, isOnOff), airOperatingTime.getTimeByModeOnOff_minute(currentModeValue, isOnOff), isOnOff);
                    break;
                case 3:
                    showTimePickerDialog(fanOperatingTime.getTimeByModeOnOff(currentModeValue, isOnOff), fanOperatingTime.getTimeByModeOnOff_minute(currentModeValue, isOnOff), isOnOff);
                    break;
            }
        }
    };


    /**
     * 按下主畫面控制的選項 變更 F1 F2 F3 F4
     **/
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //依照button tag 選擇不同功能 0=停止 1=育苗模式 2=生長模式 3= 開花模式 4=手動模式
            changeMode((int) v.getTag());

            if (ipAddress != null && ipAddress.length() > 0) {

                System.out.println("按下主控鍵傳遞的訊息：" + ProjectTools.getCommandChangeMode((int) v.getTag()));

                ecoproConnector.sendCommand(ipAddress, ProjectTools.getCommandChangeMode((int) v.getTag()));
            }
        }
    };

    // endregion

    // region function

    /**
     * 手動模式 修改時間
     **/
    private void showTimePickerDialog(int hourTime, int minuteTime, boolean onOff) {

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d(TAG, "hourOfDay = " + hourOfDay);
                        Log.d(TAG, "minute = " + minute);

                        switch (currentTag / 10) {
                            case 1:
                                if (currentTag % 10 == 1) {
                                    lightOperatingTime.ManualMode_TurnOnTime = hourOfDay;
                                    lightOperatingTime.ManualMode_TurnOnTime_minute = minute;
                                } else if (currentTag % 10 == 2) {
                                    lightOperatingTime.ManualMode_TurnOffTime = hourOfDay;
                                    lightOperatingTime.ManualMode_TurnOffTime_minute = minute;
                                }
                                break;
                            case 2:
                                if (currentTag % 10 == 1) {
                                    airOperatingTime.ManualMode_TurnOnTime = hourOfDay;
                                    airOperatingTime.ManualMode_TurnOnTime_minute = minute;
                                } else if (currentTag % 10 == 2) {
                                    airOperatingTime.ManualMode_TurnOffTime = hourOfDay;
                                    airOperatingTime.ManualMode_TurnOffTime_minute = minute;
                                }
                                break;
                            case 3:
                                if (currentTag % 10 == 1) {
                                    fanOperatingTime.ManualMode_TurnOnTime = hourOfDay;
                                    fanOperatingTime.ManualMode_TurnOnTime_minute = minute;
                                } else if (currentTag % 10 == 2) {
                                    fanOperatingTime.ManualMode_TurnOffTime = hourOfDay;
                                    fanOperatingTime.ManualMode_TurnOffTime_minute = minute;
                                }
                                break;
                        }

                        textViewLightTurnOnTime.setText(ampm(ProjectTools.getTimeString(lightOperatingTime.ManualMode_TurnOnTime, lightOperatingTime.ManualMode_TurnOnTime_minute)));
                        textViewLightTurnOffTime.setText(ampm(ProjectTools.getTimeString(lightOperatingTime.ManualMode_TurnOffTime, lightOperatingTime.ManualMode_TurnOffTime_minute)));
                        textViewAirTurnOnTime.setText(ampm(ProjectTools.getTimeString(airOperatingTime.ManualMode_TurnOnTime, airOperatingTime.ManualMode_TurnOnTime_minute)));
                        textViewAirTurnOffTime.setText(ampm(ProjectTools.getTimeString(airOperatingTime.ManualMode_TurnOffTime, airOperatingTime.ManualMode_TurnOffTime_minute)));
                        textViewFanTurnOnTime.setText(ampm(ProjectTools.getTimeString(fanOperatingTime.ManualMode_TurnOnTime, fanOperatingTime.ManualMode_TurnOnTime_minute)));
                        textViewFanTurnOffTime.setText(ampm(ProjectTools.getTimeString(fanOperatingTime.ManualMode_TurnOffTime, fanOperatingTime.ManualMode_TurnOffTime_minute)));


                        byte[] commandArray = ProjectTools.COMMAND_MANUAL;

                        commandArray[3] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOnTime, 16);
                        commandArray[4] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOnTime_minute, 16);
                        commandArray[5] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOffTime, 16);
                        commandArray[6] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOffTime_minute, 16);

                        commandArray[7] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOnTime, 16);
                        commandArray[8] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOnTime_minute, 16);
                        commandArray[9] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOffTime, 16);
                        commandArray[10] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOffTime_minute, 16);

                        commandArray[11] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOnTime, 16);
                        commandArray[12] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOnTime_minute, 16);
                        commandArray[13] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOffTime, 16);
                        commandArray[14] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOffTime_minute, 16);

                        // 計算 checksum
                        commandArray = ProjectTools.getChecksumArray(commandArray);

                        if (ipAddress != null && ipAddress.length() > 0) {
                            ecoproConnector.sendCommand(ipAddress, commandArray);
                        }
                    }
                }, hourTime, minuteTime, true);

        tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, "Save", tpd);
        tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancel", tpd);
        tpd.setTitle((onOff) ? "Turn on" : "Turn off");
        tpd.show();

    }

    /**
     * 改變四種模式的 狀態
     * .setSelected 畫面為明顯(true)或漸淡(false)
     *
     * @param statusValue
     */
    private void changeMode(int statusValue) {

        buttonShotDown.setSelected(false);
        buttonF1.setSelected(false);
        buttonF2.setSelected(false);
        buttonF3.setSelected(false);
        buttonManual.setSelected(false);

        currentModeValue = statusValue;

        switch (statusValue) {
            case 0:
                System.out.println("F0");
                buttonShotDown.setSelected(true);
                manual_layout.setVisibility(View.INVISIBLE);
                switchEnabled(false);
                break;
            case 1:
                buttonF1.setSelected(true);
                manual_layout.setVisibility(View.INVISIBLE);
//                textViewLightTurnOnTime.setText(ampm(ProjectTools.getTimeString(LightOperatingTime.F1Mode_DefaultTurnOnTime)));
//                textViewLightTurnOffTime.setText(ampm(ProjectTools.getTimeString(LightOperatingTime.F1Mode_DefaultTurnOffTime)));
//                textViewAirTurnOnTime.setText(ampm(ProjectTools.getTimeString(AirOperatingTime.F1Mode_DefaultTurnOnTime)));
//                textViewAirTurnOffTime.setText(ampm(ProjectTools.getTimeString(AirOperatingTime.F1Mode_DefaultTurnOffTime)));
//                textViewFanTurnOnTime.setText(ampm(ProjectTools.getTimeString(FanOperatingTime.F1Mode_DefaultTurnOnTime)));
//                textViewFanTurnOffTime.setText(ampm(ProjectTools.getTimeString(FanOperatingTime.F1Mode_DefaultTurnOffTime)));
                switchEnabled(false);
                break;
            case 2:
                buttonF2.setSelected(true);
                manual_layout.setVisibility(View.INVISIBLE);
//                textViewLightTurnOnTime.setText(ampm(ProjectTools.getTimeString(LightOperatingTime.F2Mode_DefaultTurnOnTime)));
//                textViewLightTurnOffTime.setText(ampm(ProjectTools.getTimeString(LightOperatingTime.F2Mode_DefaultTurnOffTime)));
//                textViewAirTurnOnTime.setText(ampm(ProjectTools.getTimeString(AirOperatingTime.F2Mode_DefaultTurnOnTime)));
//                textViewAirTurnOffTime.setText(ampm(ProjectTools.getTimeString(AirOperatingTime.F2Mode_DefaultTurnOffTime)));
//                textViewFanTurnOnTime.setText(ampm(ProjectTools.getTimeString(FanOperatingTime.F2Mode_DefaultTurnOnTime)));
//                textViewFanTurnOffTime.setText(ampm(ProjectTools.getTimeString(FanOperatingTime.F2Mode_DefaultTurnOffTime)));
                switchEnabled(false);
                break;
            case 3:
                buttonF3.setSelected(true);
                manual_layout.setVisibility(View.INVISIBLE);
//                textViewLightTurnOnTime.setText(ampm(ProjectTools.getTimeString(LightOperatingTime.F3Mode_DefaultTurnOnTime)));
//                textViewLightTurnOffTime.setText(ampm(ProjectTools.getTimeString(LightOperatingTime.F3Mode_DefaultTurnOffTime)));
//                textViewAirTurnOnTime.setText(ampm(ProjectTools.getTimeString(AirOperatingTime.F3Mode_DefaultTurnOnTime)));
//                textViewAirTurnOffTime.setText(ampm(ProjectTools.getTimeString(AirOperatingTime.F3Mode_DefaultTurnOffTime)));
//                textViewFanTurnOnTime.setText(ampm(ProjectTools.getTimeString(FanOperatingTime.F3Mode_DefaultTurnOnTime)));
//                textViewFanTurnOffTime.setText(ampm(ProjectTools.getTimeString(FanOperatingTime.F3Mode_DefaultTurnOffTime)));
                switchEnabled(false);
                break;
            case 4:
                buttonManual.setSelected(true);
                manual_layout.setVisibility(View.VISIBLE);
//                textViewLightTurnOnTime.setText(ProjectTools.getTimeString(lightOperatingTime.ManualMode_TurnOnTime));
//                textViewLightTurnOffTime.setText(ProjectTools.getTimeString(lightOperatingTime.ManualMode_TurnOffTime));
//                textViewAirTurnOnTime.setText(ProjectTools.getTimeString(airOperatingTime.ManualMode_TurnOnTime));
//                textViewAirTurnOffTime.setText(ProjectTools.getTimeString(airOperatingTime.ManualMode_TurnOffTime));
//                textViewFanTurnOnTime.setText(ProjectTools.getTimeString(fanOperatingTime.ManualMode_TurnOnTime));
//                textViewFanTurnOffTime.setText(ProjectTools.getTimeString(fanOperatingTime.ManualMode_TurnOffTime));
                switchEnabled(true);
                break;
        }
    }

    /**
     * 改變工作狀態
     * 主畫面 Status 地方 紅燈error 綠燈 ok
     **/
    private void changeWorkStatus(byte[] byteArray) {


        //燈光LIGHT
        if (ProjectTools.getEcoproWorkStatus(ProjectTools.ECOPRO_LIGHT, byteArray) == 0) {
            imageViewLightWorkStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.green_light));
        } else if (ProjectTools.getEcoproWorkStatus(ProjectTools.ECOPRO_LIGHT, byteArray) == 1) {
            imageViewLightWorkStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.red_light));
        } else {

        }

        //空氣AIR
        if (ProjectTools.getEcoproWorkStatus(ProjectTools.ECOPRO_AIR, byteArray) == 0) {
            imageViewAirWorkStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.green_light));
        } else if (ProjectTools.getEcoproWorkStatus(ProjectTools.ECOPRO_AIR, byteArray) == 1) {
            imageViewAirWorkStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.red_light));
        } else {

        }

        //風扇FAN
        if (ProjectTools.getEcoproWorkStatus(ProjectTools.ECOPRO_FAN, byteArray) == 0) {
            imageViewFanWorkStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.green_light));
        } else if (ProjectTools.getEcoproWorkStatus(ProjectTools.ECOPRO_FAN, byteArray) == 1) {
            imageViewFanWorkStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.red_light));
        } else {

        }

        //水WATER
        if (ProjectTools.getEcoproWaterStatus(byteArray) == 0) {
            imageViewWaterStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.green_light));
        } else if (ProjectTools.getEcoproWaterStatus(byteArray) == 1) {
            imageViewWaterStatus.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.red_light));
        } else {

        }

        //控制Light Air Fan Water 灰暗的地方
        if (ProjectTools.getEcoproOnOffStatus(ProjectTools.ECOPRO_LIGHT, byteArray) == 0) {
            textViewLight.setAlpha((float) 0.4);
        } else if (ProjectTools.getEcoproOnOffStatus(ProjectTools.ECOPRO_LIGHT, byteArray) == 1) {
            textViewLight.setAlpha((float) 1.0);
        }
        if (ProjectTools.getEcoproOnOffStatus(ProjectTools.ECOPRO_AIR, byteArray) == 0) {
            textViewAir.setAlpha((float) 0.4);
        } else if (ProjectTools.getEcoproOnOffStatus(ProjectTools.ECOPRO_AIR, byteArray) == 1) {
            textViewAir.setAlpha((float) 1.0);
        }
        if (ProjectTools.getEcoproOnOffStatus(ProjectTools.ECOPRO_FAN, byteArray) == 0) {
            textViewFan.setAlpha((float) 0.4);
        } else if (ProjectTools.getEcoproOnOffStatus(ProjectTools.ECOPRO_FAN, byteArray) == 1) {
            textViewFan.setAlpha((float) 1.0);
        }
    }

    /**
     * setEnabled 設定 textview 畫面顯示與漸淡
     **/
    private void switchEnabled(boolean enable) {

        textViewLightTurnOnTime.setEnabled(enable);
        textViewLightTurnOffTime.setEnabled(enable);
        textViewAirTurnOnTime.setEnabled(enable);
        textViewAirTurnOffTime.setEnabled(enable);
        textViewFanTurnOnTime.setEnabled(enable);
        textViewFanTurnOffTime.setEnabled(enable);

    }

    // endregion

    // region Timer

    private Timer myTimer;
    private EcoproTimerTask myTimerTask;
    /**
     * Timer 第一次延遲多久執行
     */
    private static int ECOPRO_TIMER_DELAY = 800;
    /**
     * Timer 週期 控制硬體傳輸傳輸速度
     */
    private static int ECOPRO_TIMER_PERIOD = 1000;

    private void startTimer() {
        myTimer = new Timer();
        myTimerTask = new EcoproTimerTask();
        myTimer.schedule(myTimerTask, ECOPRO_TIMER_DELAY, ECOPRO_TIMER_PERIOD);
    }

    private void stopTimer() {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
        }
    }



    /**
     * Timer 的 Task ， 每n秒發送一次指令
     **/
    private class EcoproTimerTask extends TimerTask {
        public void run() {
            if (ipAddress.length() > 0) {
                ecoproConnector.sendCommand(ipAddress, ProjectTools.COMMAND_POLLING);
            }
        }
    }

    // endregion

    // region EcoproConnector callback

    @Override
    public void onReceiveASIXUDPBroadcast(List list) {

    }

    @Override
    public void onReceiveASIXUDPUnicast(byte[] ackArray) {

    }

    /**
     * 收到硬體回傳資料
     * 使用interface技術
     * 利用 Handler 更新主UI執行序內容
     */
    @Override
    public void onReceiveAnteyaTCPCommandAck(byte[] ackArray) {
        Message message = new Message();
        message.what = MyHandler.RECEIVE_DATA;
        message.obj = ackArray;
        myHandler.sendMessage(message);
    }

    @Override
    public void onCheckLink(boolean isLinked) {

    }

    // endregion

    // region MyHandler

    /**
     * 添加12小時制AM/PM
     **/
    private String ampm(String time) {

        try {
           // System.out.println("ampm:"+time);
            String time1 = time;
            if(time !=null)
            {
                String[] temp = time1.split(":");
                int temp1 = Integer.parseInt(temp[0]);
                //   System.out.println("測試時間數值：" + temp1);

                if (temp1 < 12)
                    return "AM" + time1;
                else
                    return "PM" + String.format("%02d",temp1-12)+":"+temp[1];
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("錯誤MainActivity_ampm:"+e);
        }

        return time;
    }


    /**
     * 更新畫面
     **/
    public void updateView(byte[] byteArray) {
        ProjectTools.printByteArray(byteArray, "主執行緒收到 Ecopro 的 polling ack", 10);

        if (byteArray[1] == (byte) 0x81) { // 詢問狀態
            ProjectTools.printEcoproStatusArray(byteArray);

            // 更改畫面上所顯示的時間  收到資料傳到 ProjectTools.getEcoproOnTime 做轉換

//            System.out.println("測試1:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_ON_TIME, byteArray)));
//            System.out.println("測試2:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
//            System.out.println("測試3:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_ON_TIME, byteArray)));
//            System.out.println("測試4:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
//            System.out.println("測試5:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_ON_TIME, byteArray)));
//            System.out.println("測試6:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_OFF_TIME, byteArray)));


            textViewLightTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_ON_TIME, byteArray)));
            textViewLightTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
            textViewAirTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_ON_TIME, byteArray)));
            textViewAirTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
            textViewFanTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_ON_TIME, byteArray)));
            textViewFanTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_OFF_TIME, byteArray)));

            /**寫死的更改F1F2F3數值設定 此處修改會影響控制機控制手機變動**/
              changeMode(ProjectTools.getEcoproModeIndex(byteArray));

            // 更改畫面上的運作狀態
            changeWorkStatus(byteArray);

            // 更改畫面上開啟關閉狀態
        } else if (byteArray[1] == (byte) 0x82) { // 設定模式

        } else if (byteArray[1] == (byte) 0x83) { // 設定手動時間

        }

    }

    private MyHandler myHandler;

    /**
     * 從 背景執行緒返回 UI執行緒
     */
    private static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        /**
         * Android 在使用多執行緒搭配 UI 元件操作時
         * 我們必須使用 Handler 搭配 Thread 以便繞過 Android 不可在 UI Thread 以外執行 UI 操作的限制
         * 這個警告便表示 Handler 應該宣告為 static 以免程式發生無法預期的問題
         * 但宣告為 static 又無法呼叫 Activity 內的 function 該如何是好?
         * 這時就必須要用到 weakreference 這個類別的設計方法
         * 如此即可在 Handler 內存取 Activity 的成員
         **/
        private WeakReference<MainActivity> mOuter;

        public MyHandler(MainActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        public static final int RECEIVE_DATA = 1;

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mOuter.get();
            if (activity != null) {
                // Do something with outer as your wish.
                switch (msg.what) {
                    case RECEIVE_DATA:
                        //   System.out.println("MainActivity MyHandler.RECEIVE_DATA");
                        activity.updateView((byte[]) msg.obj);
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.manual_m1:
                System.out.println("記錄1");
                manual_m1.setAlpha((float) 1.0);
                manual_m2.setAlpha((float) 0.4);
                manual_m3.setAlpha((float) 0.4);
                manual_m4.setAlpha((float) 0.4);
                manual_m5.setAlpha((float) 0.4);
                break;
            case R.id.manual_m2:
                System.out.println("記錄2");
                manual_m1.setAlpha((float) 0.4);
                manual_m2.setAlpha((float) 1.0);
                manual_m3.setAlpha((float) 0.4);
                manual_m4.setAlpha((float) 0.4);
                manual_m5.setAlpha((float) 0.4);
                break;
            case R.id.manual_m3:
                System.out.println("記錄3");
                manual_m1.setAlpha((float) 0.4);
                manual_m2.setAlpha((float) 0.4);
                manual_m3.setAlpha((float) 1.0);
                manual_m4.setAlpha((float) 0.4);
                manual_m5.setAlpha((float) 0.4);
                break;
            case R.id.manual_m4:
                System.out.println("記錄4");
                manual_m1.setAlpha((float) 0.4);
                manual_m2.setAlpha((float) 0.4);
                manual_m3.setAlpha((float) 0.4);
                manual_m4.setAlpha((float) 1.0);
                manual_m5.setAlpha((float) 0.4);
            break;
            case R.id.manual_m5:
                System.out.println("記錄5");
                manual_m1.setAlpha((float) 0.4);
                manual_m2.setAlpha((float) 0.4);
                manual_m3.setAlpha((float) 0.4);
                manual_m4.setAlpha((float) 0.4);
                manual_m5.setAlpha((float) 1.0);
            break;





        }


    }


    // endregion
}
