package com.anteya.ecoprotools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anteya.ecoprotools.object.DataControl;

/**
 * Created by yenlungchen on 2016/2/25.
 */
public class IpCameraSettingsActivity extends Activity {

    private static final String TAG = "IpCameraSettingsActivity";

    private TextView textViewUIDSettings, textViewWifiSettings;

    private DataControl dataControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ip_camera_settings);

        initData();

        initView();

    }

    // region initial

    private void initData(){
        dataControl = (DataControl) getApplicationContext();
    }

    private void initView(){

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("IP Camera Settings");

        textViewUIDSettings = (TextView) findViewById(R.id.activityIpCameraSettings_textView1);
        textViewWifiSettings = (TextView) findViewById(R.id.activityIpCameraSettings_textView2);

        textViewUIDSettings.setOnClickListener(uidSettingsListener);
        textViewWifiSettings.setOnClickListener(wifiSettingsListener);
    }


    private View.OnClickListener uidSettingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            Intent intent = new Intent(IpCameraSettingsActivity.this, IpCameraUidSettingsActivity.class);
//            startActivity(intent);

            dataControl.saveIpCameraUid("");

            Toast.makeText(IpCameraSettingsActivity.this, "remove IP Camera UID", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener wifiSettingsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(IpCameraSettingsActivity.this, IpCameraWifiSettingsActivity.class);
            startActivity(intent);
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // ActionBar Menu OnclickListener
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
