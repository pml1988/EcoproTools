package com.anteya.ecoprotools;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
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
import java.security.interfaces.ECKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements EcoproConnectorCallback, View.OnClickListener {

    // region Data variables

    private String TAG = "MainActivity";

    private String title = "EcoproTools";

    private DataControl dataControl;

    public int one;

    private int two;

    private int three;

    private int four;


    private EcoproConnector ecoproConnector;

    private String ipAddress;

    private LightOperatingTime lightOperatingTime;
    private AirOperatingTime airOperatingTime;
    private FanOperatingTime fanOperatingTime;

    private String golbe_password;

    private byte manual_byte = 0x04;

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

    private ImageButton lb_stop;
    private ImageButton lb_F1;
    private ImageButton lb_F2;
    private ImageButton lb_F3;
    private ImageButton lb_Manual;

    private ImageButton mm1;
    private ImageButton mm2;
    private ImageButton mm3;
    private ImageButton mm4;
    private ImageButton mm5;


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
    private LinearLayout manual_layout;

    private TextView manual_m1, manual_m2, manual_m3, manual_m4, manual_m5;

    private ActionBar actionBar;

    private byte[] temp_time;


    private TextView actionBar_mainActivity_textTitle;
    // endregion

    // region service
    private String strVersion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        try {
            strVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM 月 dd 日 HH 時 mm 分 ss 秒");
        System.out.println("======================(校正)" + sdf.format(new Date()) + "(校正)======================");
        DisplayMetrics monitorsize = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(monitorsize);
        float d = getResources().getDimension(R.dimen.activity_horizontal_margin);
        float mDpi = getResources().getDisplayMetrics().densityDpi;

        System.out.println("解析度：" + mDpi + " 手機螢幕解析度為：" + monitorsize.widthPixels + "x" + monitorsize.heightPixels);
        initData();
        initView();


        actionBar_mainActivity_textTitle.setText(title + " Ver." + strVersion + " (Guest)");
        golbe_password = readData();

//        if (golbe_password == "") {
//
//            first_input_password();
//        } else {
//
//            //  startTimer();
//        }


        System.out.print("數字：" + golbe_password);

    }

    public void first_input_password() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.activity_main_first_passwrod, null);
        final EditText activity_main_password_editText = (EditText) view.findViewById(R.id.activity_main_password_editText);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("INPUT Password").setView(view).setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("確定" + activity_main_password_editText.getText().toString());
                saveData(activity_main_password_editText.getText().toString());
                golbe_password = readData();

                stopTimer();
                startTimer();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("取消");
                startTimer();
            }
        }).show();
    }


    @Override
    protected void onStart() {
        super.onStart();


        try {
            ipAddress = dataControl.getIpAddress();
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    // endregion

    // region initial

    private void initData() {


        try {
            lightOperatingTime = new LightOperatingTime();
            airOperatingTime = new AirOperatingTime();
            fanOperatingTime = new FanOperatingTime();

            //IP cam
            dataControl = (DataControl) getApplicationContext();

            myHandler = new MyHandler(this);

            ecoproConnector = new EcoproConnector();
            ecoproConnector.setEcoproConnectorCallback(this);  //interface
        } catch (Exception e) {
            e.printStackTrace();
        }


//        dataControl.saveIpCameraUid("");
    }

    private void initView() {


        lb_stop = (ImageButton) findViewById(R.id.lb_stop);
        lb_F1 = (ImageButton) findViewById(R.id.lb_germinate);
        lb_F2 = (ImageButton) findViewById(R.id.lb_growth);
        lb_F3 = (ImageButton) findViewById(R.id.lb_flower);
        lb_Manual = (ImageButton) findViewById(R.id.lb_manual);
        lb_stop.setOnClickListener(buttonClickListener);
        lb_F1.setOnClickListener(buttonClickListener);
        lb_F2.setOnClickListener(buttonClickListener);
        lb_F3.setOnClickListener(buttonClickListener);
        lb_Manual.setOnClickListener(buttonClickListener);
        lb_stop.setTag(0);
        lb_F1.setTag(1);
        lb_F2.setTag(2);
        lb_F3.setTag(3);
        lb_Manual.setTag(4);
        mm1 = (ImageButton) findViewById(R.id.mm1);
        mm2 = (ImageButton) findViewById(R.id.mm2);
        mm3 = (ImageButton) findViewById(R.id.mm3);
        mm4 = (ImageButton) findViewById(R.id.mm4);
        mm5 = (ImageButton) findViewById(R.id.mm5);
        mm1.setOnClickListener(this);
        mm2.setOnClickListener(this);
        mm3.setOnClickListener(this);
        mm4.setOnClickListener(this);
        mm5.setOnClickListener(this);


        buttonShotDown = (Button) findViewById(R.id.button0);
        buttonF1 = (Button) findViewById(R.id.button1);
        buttonF2 = (Button) findViewById(R.id.button2);
        buttonF3 = (Button) findViewById(R.id.button3);
        buttonManual = (Button) findViewById(R.id.button4);


        //控制手動模式 設定組別
        manual_layout = (LinearLayout) findViewById(R.id.activityMain_Manual_control);
        manual_m1 = (TextView) findViewById(R.id.manual_m1);
        manual_m2 = (TextView) findViewById(R.id.manual_m2);
        manual_m3 = (TextView) findViewById(R.id.manual_m3);
        manual_m4 = (TextView) findViewById(R.id.manual_m4);
        manual_m5 = (TextView) findViewById(R.id.manual_m5);
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
        actionBar = getActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        Button buttonSetting = (Button) actionBarLayout.findViewById(R.id.actionBar_mainActivity_buttonSetting);
        actionBar_mainActivity_textTitle = (TextView) actionBarLayout.findViewById(R.id.actionBar_mainActivity_textTitle);
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "Setting Button was clicked");

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });
        actionBar.setTitle("測試");
    }

    // endregion

    // region view click listener

    private View.OnClickListener textViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String type = "";

            switch (v.getId()) {
                case R.id.activityMain_textLightTurnOnTime:
                    type = "Light Turn On";
                    break;
                case R.id.activityMain_textLightTurnOffTime:
                    type = "Light Turn Off";
                    break;
                case R.id.activityMain_textAirTurnOnTime:
                    type = "Pump Turn On";
                    break;
                case R.id.activityMain_textAirTurnOffTime:
                    type = "Pump Turn Off";
                    break;
                case R.id.activityMain_textFanTurnOnTime:
                    type = "Fan Turn On";
                    break;
                case R.id.activityMain_textFanTurnOffTime:
                    type = "Fan Turn Off";
                    break;
            }
            TextView tempTextView = (TextView) v;
            int textViewTag = (int) tempTextView.getTag();
            currentTag = textViewTag;
            boolean isOnOff = (textViewTag % 10 == 1);
            switch (textViewTag / 10) {
                case 1:
                    showTimePickerDialog(lightOperatingTime.getTimeByModeOnOff(currentModeValue, isOnOff), lightOperatingTime.getTimeByModeOnOff_minute(currentModeValue, isOnOff), isOnOff, type);
                    break;
                case 2:
                    showTimePickerDialog(airOperatingTime.getTimeByModeOnOff(currentModeValue, isOnOff), airOperatingTime.getTimeByModeOnOff_minute(currentModeValue, isOnOff), isOnOff, type);
                    break;
                case 3:
                    showTimePickerDialog(fanOperatingTime.getTimeByModeOnOff(currentModeValue, isOnOff), fanOperatingTime.getTimeByModeOnOff_minute(currentModeValue, isOnOff), isOnOff, type);
                    break;
            }
        }
    };

    // private boolean manual_flag = true;
    /**
     * 按下主畫面控制的選項 變更 F1 F2 F3 F4
     **/
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //依照button tag 選擇不同功能 0=停止 1=育苗模式 2=生長模式 3= 開花模式 4=手動模式
            if ((int) v.getTag() == 4) {
                //   if (manual_flag == true) {
                manual_layout.setVisibility(View.VISIBLE);
            }
            changeMode((int) v.getTag());
            if (ipAddress != null && ipAddress.length() > 0 && (int) v.getTag() < 4) {
                try {
                    System.out.println("按下主控鍵傳遞的訊息：" + ProjectTools.getCommandChangeMode((int) v.getTag()));
                    byte[] temp_commandchangmode = ProjectTools.getCommandChangeMode((int) v.getTag());
                    temp_commandchangmode[3] = (byte) dataControl.getPd_one();
                    temp_commandchangmode[4] = (byte) dataControl.getPd_two();
                    temp_commandchangmode[5] = (byte) dataControl.getPd_three();
                    temp_commandchangmode[6] = (byte) dataControl.getPd_four();
                    temp_commandchangmode = ProjectTools.getChecksumArray(temp_commandchangmode);
                    System.out.println("傳送訊息1 " + temp_commandchangmode.length);
                    ecoproConnector.sendCommand(ipAddress, temp_commandchangmode, dataControl.getPort_use());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ((int) v.getTag() == 4) {
                try {
                    System.out.println("按下主控鍵傳遞的訊息：::::::::");
                    byte[] temp_commandchangmode = ProjectTools.getCommandChangeMode((int) v.getTag());
                    temp_commandchangmode[2] = manual_byte;
                    temp_commandchangmode[3] = (byte) dataControl.getPd_one();
                    temp_commandchangmode[4] = (byte) dataControl.getPd_two();
                    temp_commandchangmode[5] = (byte) dataControl.getPd_three();
                    temp_commandchangmode[6] = (byte) dataControl.getPd_four();
                    temp_commandchangmode = ProjectTools.getChecksumArray(temp_commandchangmode);
                    System.out.println("傳送訊息2 " + temp_commandchangmode.length);
                    ecoproConnector.sendCommand(ipAddress, temp_commandchangmode, dataControl.getPort_use());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("按下主控鍵傳遞的訊息：============");
            }


        }
    };

    // endregion

    // region function

    /**
     * 手動模式 修改時間
     **/
    private void showTimePickerDialog(int hourTime, int minuteTime, boolean onOff, String type) {

        edit_time = false;
        System.out.println("確認時間");
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {


                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Log.d(TAG, "hourOfDay = " + hourOfDay);
                        Log.d(TAG, "minute = " + minute);


                        System.out.println("確認");


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

//                        textViewLightTurnOnTime.setText(ampm(ProjectTools.getTimeString(lightOperatingTime.ManualMode_TurnOnTime, lightOperatingTime.ManualMode_TurnOnTime_minute)));
//                        textViewLightTurnOffTime.setText(ampm(ProjectTools.getTimeString(lightOperatingTime.ManualMode_TurnOffTime, lightOperatingTime.ManualMode_TurnOffTime_minute)));
//                        textViewAirTurnOnTime.setText(ampm(ProjectTools.getTimeString(airOperatingTime.ManualMode_TurnOnTime, airOperatingTime.ManualMode_TurnOnTime_minute)));
//                        textViewAirTurnOffTime.setText(ampm(ProjectTools.getTimeString(airOperatingTime.ManualMode_TurnOffTime, airOperatingTime.ManualMode_TurnOffTime_minute)));
//                        textViewFanTurnOnTime.setText(ampm(ProjectTools.getTimeString(fanOperatingTime.ManualMode_TurnOnTime, fanOperatingTime.ManualMode_TurnOnTime_minute)));
//                        textViewFanTurnOffTime.setText(ampm(ProjectTools.getTimeString(fanOperatingTime.ManualMode_TurnOffTime, fanOperatingTime.ManualMode_TurnOffTime_minute)));

                        System.out.println("Test byte:" + manual_byte);
                        byte[] commandArray = ProjectTools.COMMAND_MANUAL;


                        commandArray[2] = manual_byte;
                        commandArray[3] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOnTime, 16);
                        commandArray[4] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOnTime_minute, 16);
                        commandArray[5] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOffTime, 16);
                        commandArray[6] = Byte.parseByte("" + lightOperatingTime.ManualMode_TurnOffTime_minute, 16);

                        commandArray[7] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOnTime, 16);
                        commandArray[8] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOnTime_minute, 16);
                        commandArray[9] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOffTime, 16);
                        commandArray[10] = Byte.parseByte("" + airOperatingTime.ManualMode_TurnOffTime_minute, 16);
//
                        commandArray[11] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOnTime, 16);
                        commandArray[12] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOnTime_minute, 16);
                        commandArray[13] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOffTime, 16);
                        commandArray[14] = Byte.parseByte("" + fanOperatingTime.ManualMode_TurnOffTime_minute, 16);
                        /**
                         * 密碼
                         * **/
                        commandArray[15] = (byte) dataControl.getPd_one();
                        commandArray[16] = (byte) dataControl.getPd_two();
                        commandArray[17] = (byte) dataControl.getPd_three();
                        commandArray[18] = (byte) dataControl.getPd_four();

                        // 計算 checksum
                        commandArray = ProjectTools.getChecksumArray(commandArray);
                        for (int i = 0; i < commandArray.length; i++) {
                            System.out.println(i + "項 丟出修改時間資料：" + commandArray[i]);
                        }
                        if (ipAddress != null && ipAddress.length() > 0) {
                            System.out.println("傳送訊息4 " + commandArray.length);
                            ecoproConnector.sendCommand(ipAddress, commandArray , dataControl.getPort_use());
                            edit_time = true;
                        }
                    }
                }, hourTime, minuteTime, true);

        tpd.setButton(TimePickerDialog.BUTTON_POSITIVE, "Save", tpd);
        tpd.setButton(TimePickerDialog.BUTTON_NEGATIVE, "Cancel", tpd);


      //  tpd.setTitle((onOff) ? "Turn on" : "Turn off");
        tpd.setTitle(type);
        tpd.show();

        tpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                edit_time = true;
            }
        });

    }

    /**
     * 改變四種模式的 狀態
     * .setSelected 畫面為明顯(true)或漸淡(false)
     *
     * @param statusValue
     */
    private void changeMode(int statusValue) {

        lb_stop.setImageResource(R.drawable.activity_main_stop_off);
        lb_F1.setImageResource(R.drawable.activity_main_germinate_off);
        lb_F2.setImageResource(R.drawable.activity_main_growth_off);
        lb_F3.setImageResource(R.drawable.activity_main_flower_off);
        // lb_Manual.setImageResource(R.drawable.activity_main_manual_m1_off);

        buttonShotDown.setSelected(false);
        buttonF1.setSelected(false);
        buttonF2.setSelected(false);
        buttonF3.setSelected(false);
        buttonManual.setSelected(false);

        currentModeValue = statusValue;

        switch (statusValue) {
            case 0:
                System.out.println("F0");
                lb_stop.setImageResource(R.drawable.activity_main_stop_on);
                change_manual_main_states();
                buttonShotDown.setSelected(true);
                manual_layout.setVisibility(View.INVISIBLE);
                switchEnabled(false);
                break;
            case 1:
                buttonF1.setSelected(true);
                change_manual_main_states();
                lb_F1.setImageResource(R.drawable.activity_main_germinate_on);
                manual_layout.setVisibility(View.INVISIBLE);
                switchEnabled(false);
                break;
            case 2:
                buttonF2.setSelected(true);
                change_manual_main_states();
                lb_F2.setImageResource(R.drawable.activity_main_growth_on);
                manual_layout.setVisibility(View.INVISIBLE);
                switchEnabled(false);
                break;
            case 3:
                buttonF3.setSelected(true);
                change_manual_main_states();
                lb_F3.setImageResource(R.drawable.activity_main_flower_on);

                manual_layout.setVisibility(View.INVISIBLE);
                switchEnabled(false);
                break;
            case 4:
                buttonManual.setSelected(true);
                switch (manual_byte) {
                    case 0x04:
                        lb_Manual.setImageResource(R.drawable.activity_main_manual_m1_on);
                        mm1.setImageResource(R.drawable.activity_main_manual_m1_on);
                        break;
                    case 0x05:
                        lb_Manual.setImageResource(R.drawable.activity_main_manual_m2_on);
                        mm2.setImageResource(R.drawable.activity_main_manual_m2_on);
                        break;
                    case 0x06:
                        lb_Manual.setImageResource(R.drawable.activity_main_manual_m3_on);
                        mm3.setImageResource(R.drawable.activity_main_manual_m3_on);
                        break;
                    case 0x07:
                        lb_Manual.setImageResource(R.drawable.activity_main_manual_m4_on);
                        mm4.setImageResource(R.drawable.activity_main_manual_m4_on);
                        break;
                    case 0x08:
                        lb_Manual.setImageResource(R.drawable.activity_main_manual_m5_on);
                        mm5.setImageResource(R.drawable.activity_main_manual_m5_on);
                        break;
                }
//                lb_Manual.setImageResource(R.drawable.activity_main_manual_on);


//                AnimationSet animationSet = new AnimationSet(true);  //動畫
//                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
//                alphaAnimation.setDuration(1000);
//                animationSet.addAnimation(alphaAnimation);
//                manual_layout.startAnimation(animationSet);

                switchEnabled(true);
                break;
        }
    }

    private void change_manual_main_states() {


        switch (manual_byte) {
            case 0x04:
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m1_off);

                break;
            case 0x05:
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m2_off);

                break;
            case 0x06:
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m3_off);

                break;
            case 0x07:
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m4_off);

                break;
            case 0x08:
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m5_off);

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
                System.out.println(dataControl.getPd_one() + " " + dataControl.getPd_two() + " " + dataControl.getPd_three() + " " + dataControl.getPd_four());
                byte[] COMMAND_POLLING = new byte[]{(byte) 0xf0, (byte) 0x01, (byte) dataControl.getPd_one(), (byte) dataControl.getPd_two(), (byte) dataControl.getPd_three(), (byte) dataControl.getPd_four(), (byte) 0x00};
                //  byte[] COMMAND_POLLING = new byte[]{(byte) 0xf0, (byte) 0x01,(byte) 4, (byte) 4, (byte)9, (byte) 3,(byte) 0x00};

                COMMAND_POLLING = ProjectTools.getChecksumArray(COMMAND_POLLING);

                System.out.println("傳送訊息5 " + COMMAND_POLLING.length);
                ecoproConnector.sendCommand(ipAddress, COMMAND_POLLING, dataControl.getPort_use());
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

        try {
            if (ackArray.length > 3)
                if (ackArray[2] > 3) {
                    manual_byte = ackArray[2];
                }
            Message message = new Message();
            message.what = MyHandler.RECEIVE_DATA;
            message.obj = ackArray;
            myHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("MainActivity_onReceiveAnteyaTCPCommandAck try catch");
        }
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
            if (time != null) {
                String[] temp = time1.split(":");
                int temp1 = Integer.parseInt(temp[0]);
                //   System.out.println("測試時間數值：" + temp1);

                if (temp1 < 12) {
                    return "AM" + time1;
                } else if (temp1 == 12) {
                    return "PM" + String.format("%02d", temp1) + ":" + temp[1];

                } else {

                    // System.out.println("夠了："+temp1);
                    return "PM" + String.format("%02d", temp1 - 12) + ":" + temp[1];

                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("錯誤MainActivity_ampm:" + e);
        }

        return time;
    }

    /**
     * 改變activity bar 使用者狀態
     **/
    public void change_user(int user) {
        System.out.println(user);

        switch (user) {
            case -125:
            case -126:
            case -127:
                actionBar_mainActivity_textTitle.setText(title + " Ver." + strVersion + " (User)");
                break;

            case 1:
            case 2:
            case 3:
                actionBar_mainActivity_textTitle.setText(title + " Ver." + strVersion + " (Guest)");
                break;

        }
    }


    private boolean edit_time = true;

    /**
     * 更新畫面
     **/
    public void updateView(byte[] byteArray) {
        try {
            ProjectTools.printByteArray(byteArray, "主執行緒收到 Ecopro 的 polling ack", 10);
//            temp_time = byteArray;
//
//            for(int i = 3 ; i<19; i++)
//            {
//                System.out.println("正巧："+i+" :"+temp_time[i]);
//
//
//            }

            if (byteArray.length > 18) {


                if (edit_time == true) {

//                    System.out.println("正合適03：" + convertByteToHexString(byteArray[3]));
//                    System.out.println("正合適04：" + convertByteToHexString(byteArray[4]));
//                    System.out.println("正合適05：" + convertByteToHexString(byteArray[5]));
//                    System.out.println("正合適06：" + convertByteToHexString(byteArray[6]));
//                    System.out.println("正合適09：" + convertByteToHexString(byteArray[9]));
//                    System.out.println("正合適10：" + convertByteToHexString(byteArray[10]));
//                    System.out.println("正合適11：" + convertByteToHexString(byteArray[11]));
//                    System.out.println("正合適12：" + convertByteToHexString(byteArray[12])); //10禁衛
//                    System.out.println("正合適15：" + convertByteToHexString(byteArray[15]));
//                    System.out.println("正合適16：" + convertByteToHexString(byteArray[16]));
//                    System.out.println("正合適17：" + convertByteToHexString(byteArray[17]));
//                    System.out.println("正合適18：" + convertByteToHexString(byteArray[18]));


                    lightOperatingTime.ManualMode_TurnOnTime = Integer.parseInt(convertByteToHexString(byteArray[3]));
                    lightOperatingTime.ManualMode_TurnOnTime_minute = Integer.parseInt(convertByteToHexString(byteArray[4]));
                    lightOperatingTime.ManualMode_TurnOffTime = Integer.parseInt(convertByteToHexString(byteArray[5]));
                    lightOperatingTime.ManualMode_TurnOffTime_minute = Integer.parseInt(convertByteToHexString(byteArray[6]));

                    airOperatingTime.ManualMode_TurnOnTime = Integer.parseInt(convertByteToHexString(byteArray[9]));
                    airOperatingTime.ManualMode_TurnOnTime_minute = Integer.parseInt(convertByteToHexString(byteArray[10]));
                    airOperatingTime.ManualMode_TurnOffTime = Integer.parseInt(convertByteToHexString(byteArray[11]));
                    airOperatingTime.ManualMode_TurnOffTime_minute = Integer.parseInt(convertByteToHexString(byteArray[12]));

                    fanOperatingTime.ManualMode_TurnOnTime = Integer.parseInt(convertByteToHexString(byteArray[15]));
                    fanOperatingTime.ManualMode_TurnOnTime_minute = Integer.parseInt(convertByteToHexString(byteArray[16]));
                    fanOperatingTime.ManualMode_TurnOffTime = Integer.parseInt(convertByteToHexString(byteArray[17]));
                    fanOperatingTime.ManualMode_TurnOffTime_minute = Integer.parseInt(convertByteToHexString(byteArray[18]));

//                    System.out.println("揪合適03：" + lightOperatingTime.ManualMode_TurnOnTime);
//                    System.out.println("揪合適04：" + lightOperatingTime.ManualMode_TurnOnTime_minute);
//                    System.out.println("揪合適05：" + lightOperatingTime.ManualMode_TurnOffTime);
//                    System.out.println("揪合適06：" + lightOperatingTime.ManualMode_TurnOffTime_minute);
//                    System.out.println("揪合適09：" + airOperatingTime.ManualMode_TurnOnTime);
//                    System.out.println("揪合適10：" + airOperatingTime.ManualMode_TurnOnTime_minute);
//                    System.out.println("揪合適11：" + airOperatingTime.ManualMode_TurnOffTime);
//                    System.out.println("揪合適12：" + airOperatingTime.ManualMode_TurnOffTime_minute); //10禁衛
//                    System.out.println("揪合適15：" + fanOperatingTime.ManualMode_TurnOnTime);
//                    System.out.println("揪合適16：" + fanOperatingTime.ManualMode_TurnOnTime_minute);
//                    System.out.println("揪合適17：" + fanOperatingTime.ManualMode_TurnOffTime);
//                    System.out.println("揪合適18：" + fanOperatingTime.ManualMode_TurnOffTime_minute);

                }


            }


            change_user(byteArray[1]);

//            for (int i = 0; i < byteArray.length; i++) {
//                System.out.println("第 " + i + " 解析：" + byteArray[i]);
//            }
            if (byteArray.length > 3)
                if (byteArray[2] > 3) {
                    manual_byte = byteArray[2];
                }


            if (byteArray[1] == (byte) 0x81) { // 詢問狀態

                ProjectTools.printEcoproStatusArray(byteArray);

                // 更改畫面上所顯示的時間  收到資料傳到 ProjectTools.getEcoproOnTime 做轉換
                // 更改畫面上所顯示的時間  收到資料傳到 ProjectTools.getEcoproOnTime 做轉換

                System.out.println("測試1:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                System.out.println("測試2:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                System.out.println("測試3:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                System.out.println("測試4:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                System.out.println("測試5:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                System.out.println("測試6:" + ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_OFF_TIME, byteArray)));


                textViewLightTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                textViewLightTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                textViewAirTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                textViewAirTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                textViewFanTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                textViewFanTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_OFF_TIME, byteArray)));

                change_manual_main_states();

                change_manual_status(byteArray);

                /**寫死的更改F1F2F3數值設定 此處修改會影響控制機控制手機變動**/
                changeMode(ProjectTools.getEcoproModeIndex(byteArray));

                // 更改畫面上的運作狀態
                changeWorkStatus(byteArray);

                // 更改畫面上開啟關閉狀態
            } else if (byteArray[1] == (byte) 0x82) { // 設定模式
                System.out.println("進入82");
            } else if (byteArray[1] == (byte) 0x83) { // 設定手動時間
                System.out.println("進入83");
            } else if (byteArray[1] == (byte) 0x01) { // 設定手動時間
                System.out.println("進入guest狀態");


                ProjectTools.printEcoproStatusArray(byteArray);
                textViewLightTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                textViewLightTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_LIGHT, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                textViewAirTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                textViewAirTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_AIR, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                textViewFanTurnOnTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_ON_TIME, byteArray)));
                textViewFanTurnOffTime.setText(ampm(ProjectTools.getEcoproOnTime(ProjectTools.ECOPRO_FAN, ProjectTools.ECOPRO_OFF_TIME, byteArray)));
                change_manual_main_states();
                change_manual_status(byteArray);

                /**寫死的更改F1F2F3數值設定 此處修改會影響控制機控制手機變動**/
                changeMode(ProjectTools.getEcoproModeIndex(byteArray));

                // 更改畫面上的運作狀態
                changeWorkStatus(byteArray);
                // switchEnabled(false);
                // 更改畫面上開啟關閉狀態

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("MainActivity_updateView try catch");
        }

    }

    private void change_manual_status(byte[] byte_manual) {

        mm1.setImageResource(R.drawable.activity_main_manual_m1_off);
        mm2.setImageResource(R.drawable.activity_main_manual_m2_off);
        mm3.setImageResource(R.drawable.activity_main_manual_m3_off);
        mm4.setImageResource(R.drawable.activity_main_manual_m4_off);
        mm5.setImageResource(R.drawable.activity_main_manual_m5_off);
        // manual_layout.setVisibility(View.VISIBLE);
        switch (byte_manual[2]) {
            case 0x04:
                manual_byte = 0x04;
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m1_on);
                mm1.setImageResource(R.drawable.activity_main_manual_m1_on);
                break;

            case 0x05:
                manual_byte = 0x05;
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m2_on);
                mm2.setImageResource(R.drawable.activity_main_manual_m2_on);

                break;

            case 0x06:
                manual_byte = 0x06;
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m3_on);
                mm3.setImageResource(R.drawable.activity_main_manual_m3_on);

                break;

            case 0x07:
                manual_byte = 0x07;
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m4_on);
                mm4.setImageResource(R.drawable.activity_main_manual_m4_on);

                break;

            case 0x08:
                manual_byte = 0x08;
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m5_on);
                mm5.setImageResource(R.drawable.activity_main_manual_m5_on);

                break;

        }
        switchEnabled(true);

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
                        System.out.println("收到控制台原始資料：" + msg.obj);
                        activity.updateView((byte[]) msg.obj);
                        break;
                }
            }
        }
    }

    /**
     * 更改手動模式狀態 M1(0x04) M2(0x05) M3(0x06) M4(0x07) M5(0x08)
     **/
    private void send_manual_state(byte m_byte) {
        byte[] commandArray = ProjectTools.COMMAND_CHANGE_MODE_4;
        System.out.println("奇怪勒：" + m_byte);
        commandArray[2] = m_byte;
        commandArray = ProjectTools.getChecksumArray(commandArray);
        if (ipAddress != null && ipAddress.length() > 0) {
            System.out.println("傳送訊息6 " + commandArray.length);
            ecoproConnector.sendCommand(ipAddress, commandArray, dataControl.getPort_use());
        }
    }

    @Override
    public void onClick(View v) {

//        manual_m1.setAlpha((float) 0.4);
//        manual_m2.setAlpha((float) 0.4);
//        manual_m3.setAlpha((float) 0.4);
//        manual_m4.setAlpha((float) 0.4);
//        manual_m5.setAlpha((float) 0.4);

        mm1.setImageResource(R.drawable.activity_main_manual_m1_off);
        mm2.setImageResource(R.drawable.activity_main_manual_m2_off);
        mm3.setImageResource(R.drawable.activity_main_manual_m3_off);
        mm4.setImageResource(R.drawable.activity_main_manual_m4_off);
        mm5.setImageResource(R.drawable.activity_main_manual_m5_off);


        switch (v.getId()) {
            case R.id.mm1:
                System.out.println("記錄1");
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m1_on);
                mm1.setImageResource(R.drawable.activity_main_manual_m1_on);
                manual_byte = 0x04;
                send_manual_state(manual_byte);
                // manual_m1.setAlpha((float) 1.0);
                break;
            case R.id.mm2:
                System.out.println("記錄2");
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m2_on);
                mm2.setImageResource(R.drawable.activity_main_manual_m2_on);
                manual_byte = 0x05;
                send_manual_state(manual_byte);
                //manual_m2.setAlpha((float) 1.0);
                break;
            case R.id.mm3:
                System.out.println("記錄3");
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m3_on);
                mm3.setImageResource(R.drawable.activity_main_manual_m3_on);
                manual_byte = 0x06;
                send_manual_state(manual_byte);
                //  manual_m3.setAlpha((float) 1.0);
                break;
            case R.id.mm4:
                System.out.println("記錄4");
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m4_on);
                mm4.setImageResource(R.drawable.activity_main_manual_m4_on);
                manual_byte = 0x07;
                send_manual_state(manual_byte);
                //   manual_m4.setAlpha((float) 1.0);
                break;
            case R.id.mm5:
                System.out.println("記錄5");
                lb_Manual.setImageResource(R.drawable.activity_main_manual_m5_on);
                mm5.setImageResource(R.drawable.activity_main_manual_m5_on);
                manual_byte = 0x08;
                send_manual_state(manual_byte);
                //manual_m5.setAlpha((float) 1.0);
                break;
        }
    }

    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String password = "PASSWORD";


    public String readData() {
        settings = getSharedPreferences(data, 0);
        String temp_pw = settings.getString(password, "");

        if (temp_pw.length() == 4) {

            System.out.println("怒1：" + Integer.parseInt(temp_pw.substring(0, 1)));
            System.out.println("怒2：" + Integer.parseInt(temp_pw.substring(1, 2)));
            System.out.println("怒3：" + Integer.parseInt(temp_pw.substring(2, 3)));
            System.out.println("怒4：" + Integer.parseInt(temp_pw.substring(3, 4)));


            one = Integer.parseInt(temp_pw.substring(0, 1));
            two = Integer.parseInt(temp_pw.substring(1, 2));
            three = Integer.parseInt(temp_pw.substring(2, 3));
            four = Integer.parseInt(temp_pw.substring(3, 4));


            dataControl.setPd_one(Integer.parseInt(temp_pw.substring(0, 1)));
            dataControl.setPd_two(Integer.parseInt(temp_pw.substring(1, 2)));
            dataControl.setPd_three(Integer.parseInt(temp_pw.substring(2, 3)));
            dataControl.setPd_four(Integer.parseInt(temp_pw.substring(3, 4)));
        } else {
            System.out.println("怒 四位密碼");
        }

        return temp_pw;
    }

    public void saveData(String number) {

        settings = getSharedPreferences(data, 0);
        settings.edit()
                .putString(password, number)
                .commit();

    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String convertByteToHexString(byte b) {
        byte[] temp = new byte[1];
        temp[0] = b;
        char[] hexChars = new char[temp.length * 2];
        for (int j = 0; j < temp.length; j++) {
            int v = temp[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        //    int intValue = Integer.parseInt(new String(hexChars));



        return new String(hexChars);
    }

}
