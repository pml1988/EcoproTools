package com.anteya.ecoprotools;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.anteya.ecoprotools.object.DataControl;
import com.anteya.ecoprotools.object.EcoproConnector;
import com.anteya.ecoprotools.object.EcoproString;
import com.anteya.ecoprotools.object.ProjectTools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LanScanActivity extends Activity implements EcoproConnector.EcoproConnectorCallback {

    private static final String TAG = "LanScanActivity";

    // region View

    private ListView listView;

    private SimpleAdapter listItemAdapter;

    private ImageView imageView;

    private Button buttonRefresh;

    // endregion

    // region Data

    private List<HashMap<String, Object>> listMacData = new ArrayList<>();

    private DataControl dataControl;

    private EcoproConnector ecoproConnector;

    private boolean keepGoing = true;

    // endregion

    // region service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lan_scan);

        initData();

        initView();

        // 發出Broadcast 詢問區域網路中有哪些是 ASIX的Wi-Fi晶片
        ecoproConnector.sendUDPBroadcast();
        imageView.setVisibility(View.VISIBLE);
        buttonRefresh.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        keepGoing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 不再接收背景執行緒傳回來的資料
        keepGoing = false;
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

    // endregion

    // region initial

    private void initData(){

        myHandler = new MyHandler(this);

        dataControl = (DataControl)getApplicationContext();

        ecoproConnector = new EcoproConnector();
        // 將 ecoproConnector 的 callback 對象指回 IpSettingsActivity, 收到資料後會直接回傳到這裡實現的三個 onReceive
        ecoproConnector.setEcoproConnectorCallback(this);
    }

    private void initView(){

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("LAN Scan");

        imageView = (ImageView) findViewById(R.id.activityLanScan_imageViewProgress);
        ((AnimationDrawable) imageView.getBackground()).start(); // 啟動動畫

        buttonRefresh = (Button) findViewById(R.id.activityLanScan_buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ecoproConnector.sendUDPBroadcast();
                imageView.setVisibility(View.VISIBLE);
                buttonRefresh.setVisibility(View.INVISIBLE);
            }
        });

        listView = (ListView) findViewById(R.id.activityLanScan_listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HashMap hm = listMacData.get(position);

                String tempIp = (String)hm.get("ip");

                dataControl.saveIpAddress(tempIp);

                finish();
            }
        });
    }

    // endregion

    // region EcoproConnector callback

    @Override
    public void onReceiveASIXUDPBroadcast(List list) {
        // 這個 Activity 只會用到 onReceiveASIXUDPBroadcast
        Message message = new Message();
        message.what = MyHandler.RECEIVE_BROADCAST_DATA;
        message.obj = list;
        myHandler.sendMessage(message);
    }

    @Override
    public void onReceiveASIXUDPUnicast(byte[] ackArray) {

    }

    @Override
    public void onReceiveAnteyaTCPCommandAck(byte[] ackArray) {

    }

    @Override
    public void onCheckLink(boolean isLinked) {

    }

    // endregion

    // region MyHandler

    public void updateView(List list){

        imageView.setVisibility(View.INVISIBLE);

        if(list == null || list.size() == 0){
            buttonRefresh.setVisibility(View.VISIBLE);
        }

        listMacData = list;
        for (HashMap hm : listMacData){
            String tempIp = (String)hm.get(EcoproString.HASH_MAP_KEY_IP);
            byte[] tempArray = (byte[])hm.get(EcoproString.HASH_MAP_KEY_DATA);
            String tempSSID = ProjectTools.getSSIDFromAck(tempArray);
        }

        if(keepGoing){
            listItemAdapter = new SimpleAdapter(this,listMacData, //套入動態資訊
                    R.layout.listview_ip_mac,//套用自訂的XML
                    new String[] {"ip","mac"}, //動態資訊取出順序
                    new int[] {R.id.layoutMac_ip,R.id.layoutMac_mac} //將動態資訊對應到元件ID
            );

            listView.setAdapter(listItemAdapter);
        }
    }
    private MyHandler myHandler;
    /**
     * 從 背景執行緒返回 UI執行緒
     */
    private static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<LanScanActivity> mOuter;
        public MyHandler(LanScanActivity activity) {
            mOuter = new WeakReference<>(activity);
        }
        public static final int RECEIVE_BROADCAST_DATA = 1;
        @Override
        public void handleMessage(Message msg){
            LanScanActivity activity = mOuter.get();
            if (activity != null) {
                // Do something with outer as your wish.
                switch(msg.what){
                    case RECEIVE_BROADCAST_DATA :
                        System.out.println("LanScanActivity MyHandler.RECEIVE_BROADCAST_DATA");
                        activity.updateView((List)msg.obj);
                        break;
                }
            }
        }
    }

    // endregion
}
