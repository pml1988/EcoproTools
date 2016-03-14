package com.anteya.ecoprotools;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.anteya.ecoprotools.object.DataControl;
import com.anteya.ecoprotools.object.ProjectTools;
import com.tutk.sample.AVAPI.IpCamThread;

/**
 * Created by yenlungchen on 2016/2/25.
 */
public class IpCameraWifiSettingsActivity extends Activity implements IpCamThread.DataReceiveListener{

    private static final String TAG = "IpCameraWifiSettingsActivity";

    private IpCamThread ipCamThread;

    private DataControl dataControl;

    private String uid = "";

    private ArrayAdapter<String> adapterSecurityMode;

    private String[] stringArraySecurityMode = {"No Security", "TKIP", "AES"};

    private Spinner spinnerSecurity;

    private EditText editTextSSID, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ip_camera_wifi_settings);

        initData();

        initView();
    }

    // region inioial

    private void initData(){

        dataControl = (DataControl) getApplicationContext();

        uid = dataControl.getIpCameraUid();

        ipCamThread = new IpCamThread(uid, IpCameraWifiSettingsActivity.this);
    }

    private void initView(){

        editTextSSID = (EditText) findViewById(R.id.activityIpCameraWifiSettings_editTextPassword);
        editTextPassword = (EditText) findViewById(R.id.activityIpCameraWifiSettings_editTextSSID);

        spinnerSecurity = (Spinner) findViewById(R.id.activityIpCameraWifiSettings_spinnerSecurityMode);
        adapterSecurityMode = new ArrayAdapter<>(IpCameraWifiSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, stringArraySecurityMode);
        spinnerSecurity.setAdapter(adapterSecurityMode);

        initActionBar();


    }

    private void initActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("IP Camera Settings");

        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_ip_camera_wifi_settings_activity, null);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        Button buttonSetting = (Button)actionBarLayout.findViewById(R.id.actionBar_ipCameraWifiSettingsActivity_buttonSave);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "Save Button was clicked");

//                ipCamThread.sendIOCtrl_1();

                ipCamThread.setSsid(editTextSSID.getText().toString());

                ipCamThread.setPassword(editTextPassword.getText().toString());

                ipCamThread.setSecurityMode(convertSecurityModeToIOTCProtocol());

                ipCamThread.startSetWifi();
                System.out.println("ipcam wifi 啟動");
            }
        });

    }

    //endregion

    private int convertSecurityModeToIOTCProtocol(){
        switch(spinnerSecurity.getSelectedItemPosition()){
            case 0:
                return ProjectTools.AVIOTC_WIFI_APENC_WEP; // no password
            case 1:
                return ProjectTools.AVIOTC_WIFI_APENC_WPA_TKIP;
            case 2:
                return ProjectTools.AVIOTC_WIFI_APENC_WPA_AES;
        }
        return ProjectTools.AVIOTC_WIFI_APENC_WEP; // no password
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ActionBar Menu OnclickListener
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // region IPCamThread callback

    @Override
    public void onVideoDataReceive(byte[] data) {

    }

    @Override
    public void onProgressbarReceive(boolean close) {

    }

    // endregion
}
