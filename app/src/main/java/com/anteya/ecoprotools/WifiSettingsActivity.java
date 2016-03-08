package com.anteya.ecoprotools;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anteya.ecoprotools.object.DataControl;
import com.anteya.ecoprotools.object.EcoproConnector;
import com.anteya.ecoprotools.object.ProjectTools;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tim Chen on 2016/2/23.
 *
 * 因設定 Wifi UDP Unicast 的 資料眾多且複雜 (byte array length 492), 有太多參數需要設定
 * 單獨建立可能會遺漏資料導致Wi-Fi設定失敗
 *
 * 所以我的做法是進到此頁面後先以儲存好的IP, 發送獨立的 Broadcast 一次到指定的IP
 * 取回該IP裝置的相關網路資訊, 再修改資料當作Wi-fi設定的command, 發送回去完成設定
 *
 */
public class WifiSettingsActivity extends Activity implements EcoproConnector.EcoproConnectorCallback{

    private final String TAG = "WifiSettingsActivity";


    private EditText editTextSSID, editTextPassword;

    private Spinner spinnerNetworkMode, spinnerSecurityMode, spinnerChannel;

    private TextView textViewNetworkMode;
    private TextView textViewChannel;
    private TextView textViewSSID;
    private TextView textViewSecurityMode;
    private TextView textViewPassword;

    private ArrayAdapter<String> adapterNetworkMode;
    private ArrayAdapter<String> adapterChannel;
    private ArrayAdapter<String> adapterSecurityMode;
    private String[] stringArrayNetworkMode = {"Client(Infrastructure)", "Server(AP Mode)"};
    private String[] stringArrayChannel = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
    private String[] stringArraySecurityMode = {"No Security", "TKIP", "AES"};

    private HashMap<String, Object> hashMap;

    private EcoproConnector ecoproConnector;

    private DataControl dataControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wifi_settings);

        initData();

        initView();

        ecoproConnector.sendUDPBroadcastToSpecifyIpAddress(dataControl.getIpAddress());
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

    private void initData(){

        myHandler = new MyHandler(this);

        dataControl = (DataControl) getApplicationContext();

        ecoproConnector = new EcoproConnector();
        ecoproConnector.setEcoproConnectorCallback(this);

        System.out.println("WifiSettingsActivity get IpAddress = " + dataControl.getIpAddress());
    }

    private void initView(){

        editTextSSID = (EditText) findViewById(R.id.activityWifiSettings_editTextSSID);
        editTextPassword = (EditText) findViewById(R.id.activityWifiSettings_editTextPassword);


        spinnerNetworkMode = (Spinner) findViewById(R.id.activityWifiSettings_spinnerNetworkMode);
        spinnerChannel = (Spinner) findViewById(R.id.activityWifiSettings_spinnerChannel);
        spinnerSecurityMode = (Spinner) findViewById(R.id.activityWifiSettings_spinnerSecurityMode);


        adapterNetworkMode = new ArrayAdapter<>(WifiSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, stringArrayNetworkMode);
        spinnerNetworkMode.setAdapter(adapterNetworkMode);
        spinnerNetworkMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterChannel = new ArrayAdapter<>(WifiSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, stringArrayChannel);
        spinnerChannel.setAdapter(adapterChannel);

        adapterSecurityMode = new ArrayAdapter<>(WifiSettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, stringArraySecurityMode);
        spinnerSecurityMode.setAdapter(adapterSecurityMode);

        initActionBar();
    }

    private void initActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_wifi_settings_activity, null);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        Button buttonSetting = (Button)actionBarLayout.findViewById(R.id.actionBar_wifiSettingsActivity_buttonSave);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("1", "Save Button was clicked");

                prepareASIXUDPUnicast();

            }
        });

    }

    /**
     * 準備 Wi-Fi設定 所需的 Data
     */
    public void prepareASIXUDPUnicast(){

        if(hashMap == null){
            Toast.makeText(WifiSettingsActivity.this, "尚未連線", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] byteArray = (byte[])hashMap.get("data");

        // 將要設定的相關參數放入 byteArray

        // 設定 SSID byte
        String ssid;
        ssid = editTextSSID.getText().toString();
        byte[] ssidArray = ProjectTools.convertSsidToByteArray(ssid);// (byte)0x00, (byte)0x74, (byte)0x31, (byte)0x00

        // 設定 password byte
        String password = "";
        password = editTextPassword.getText().toString();
        byte[] passwordArray = ProjectTools.convertPasswordToByteArray(password);


        System.arraycopy(ssidArray, 0, byteArray, 56, ssidArray.length);

        System.arraycopy(passwordArray, 0, byteArray, 168, passwordArray.length);

        byteArray[8] = (byte) 0x02;// op-code 0x02 = set_req
        byteArray[9] = (byte) 0x01;// enable reboot device server, 該裝置收到此設定後會進行重開機
        byteArray[53] = (byte) spinnerNetworkMode.getSelectedItemPosition();// Network mode 0:Client, 1:server
        byteArray[54] = (byte) (spinnerChannel.getSelectedItemPosition() + 1);// channel number
        byteArray[55] = (byte) ssid.length(); // SSID length
        byteArray[92] = (byte) ProjectTools.convertLocalEncryptionToASIX(spinnerSecurityMode.getSelectedItemPosition());// Encryption Mode 0:No Security 3:TKIP 4:AES
        byteArray[95] = (byte) password.length();// password length


        // 將重製後的 byte array 放回 hashMap
        hashMap.put("data", byteArray);

        ecoproConnector.sendUDPUnicast(hashMap);
        Toast.makeText(WifiSettingsActivity.this, "設定中請稍候", Toast.LENGTH_LONG).show();
    }

    /**
     * 準備 Wi-Fi設定 所需的 Data
     */
    public void prepareASIXUDPUnicastForReset(View v){

        if(hashMap == null){
            Toast.makeText(WifiSettingsActivity.this, "尚未連線", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] byteArray = (byte[])hashMap.get("data");

        // 將要設定的相關參數放入 byteArray
        byteArray[8] = (byte) 0x08;// op-code 0x08 = reset_req
        byteArray[9] = (byte) 0x01;// enable reboot device server, 該裝置收到此設定後會進行重開機

        // 將重製後的 byte array 放回 hashMap
        hashMap.put("data", byteArray);

        ecoproConnector.sendUDPUnicast(hashMap);
        Toast.makeText(WifiSettingsActivity.this, "設定中請稍候", Toast.LENGTH_LONG).show();
    }

    public void updateView(List list){

        List<HashMap<String, Object>> listHashMap = list;

        if(list.size() == 0){
            Toast.makeText(WifiSettingsActivity.this, "尚未連線", Toast.LENGTH_SHORT).show();
            return;
        }

        hashMap = listHashMap.get(0);

        // 將取回的資料更新到畫面中, 例如: Server Mode/Client Mode, Channel, Security Mode

        byte[] byteArray = (byte[])hashMap.get("data");

        String ip = (String)hashMap.get("ip");

        String ssid = ProjectTools.getSSIDFromAck(byteArray);

        String password = ProjectTools.getPasswordFromAck(byteArray);

        int networkMode = ProjectTools.getNetworkModeFromAck(byteArray);

        int encryptionMode = ProjectTools.getEncryptionModeFromAck(byteArray);

        int channel = ProjectTools.getChannelFromAck(byteArray);

        Log.d(TAG, "IP = " + ip + ", SSID = " + ssid + ", networkMode = " + networkMode + ", encryption = " + encryptionMode);

        spinnerNetworkMode.setSelection(networkMode);
        spinnerSecurityMode.setSelection(encryptionMode);
        spinnerChannel.setSelection(channel - 1);

        editTextSSID.setText(ssid);
        editTextPassword.setText(password);

    }

    public void updateView(byte[] byteArray){

    }

    // region EcoproConnector callback
    @Override
    public void onReceiveASIXUDPBroadcast(List list) {
    // 這個 Activity 只會用到 onReceiveASIXUDPBroadcast & onReceiveASIXUDPUnicast
        Message message = new Message();
        message.what = MyHandler.RECEIVE_BROADCAST_DATA;
        message.obj = list;
        myHandler.sendMessage(message);
    }

    @Override
    public void onReceiveASIXUDPUnicast(byte[] ackArray) {
    // 這個 Activity 只會用到 onReceiveASIXUDPBroadcast & onReceiveASIXUDPUnicast
        Message message = new Message();
        message.what = MyHandler.RECEIVE_UNICAST_DATA;
        message.obj = ackArray;
        myHandler.sendMessage(message);
    }

    @Override
    public void onReceiveAnteyaTCPCommandAck(byte[] ackArray) {
        ProjectTools.printByteArray(ackArray, "WifiSettingsActivity onReceiveAnteyaTCPCommandAck", 4);
    }

    @Override
    public void onCheckLink(boolean isLinked) {

    }

    // endregion

    private MyHandler myHandler;
    /**
     * 從 背景執行緒返回 UI執行緒
     */
    private static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<WifiSettingsActivity> mOuter;
        public MyHandler(WifiSettingsActivity activity) {
            mOuter = new WeakReference<>(activity);
        }
        public static final int RECEIVE_BROADCAST_DATA = 1;
        public static final int RECEIVE_UNICAST_DATA = 2;
        @Override
        public void handleMessage(Message msg){
            WifiSettingsActivity activity = mOuter.get();
            if (activity != null) {
                // Do something with outer as your wish.
                switch(msg.what){
                    case RECEIVE_BROADCAST_DATA :
                        System.out.println("WifiSettingsActivity MyHandler.RECEIVE_BROADCAST_DATA");
                        activity.updateView((List)msg.obj);
                        break;
                    case RECEIVE_UNICAST_DATA:
                        System.out.println("WifiSettingsActivity MyHandler.RECEIVE_UNICAST_DATA");
                        activity.updateView((byte[])msg.obj);
                        break;
                }
            }
        }
    }
}
