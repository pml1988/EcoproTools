package com.anteya.ecoprotools;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anteya.ecoprotools.fragment.EditDialogFragment;
import com.anteya.ecoprotools.object.DataControl;
import com.anteya.ecoprotools.object.Ecopro;
import com.anteya.ecoprotools.object.EcoproConnector;
import com.anteya.ecoprotools.object.EcoproString;
import com.anteya.ecoprotools.object.SQLiteControl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IpSettingsActivity extends Activity implements EcoproConnector.EcoproConnectorCallback
, EditDialogFragment.EditDialogFragmentCallback{

    private final String TAG = "IpSettingsActivity";

    // region Data

    private List<Ecopro> listEcopro = new ArrayList<>();
    private List<HashMap<String, Object>> listMacData = new ArrayList<>();

    private SimpleAdapter listItemAdapter;

    private DataControl dataControl;

    private SQLiteControl sqLiteControl;

    private EcoproConnector ecoproConnector;

    private boolean keepGoing = true;

    private String ipAddress = "";

    // endregion

    // region View

    private TextView text1;

    private EditText editTextIpAddress;

    private ListView listView;

    private ImageButton buttonAddEcopro;

    private Button buttonLinkEcopro;

    // endregion

    // region service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ip_settings);

        initData();

        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        String tempIp = dataControl.getIpAddress();
        if(tempIp.length() > 0){
            editTextIpAddress.setText(tempIp);
        }
        updateListView();
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
        ecoproConnector.setEcoproConnectorCallback(this);

        sqLiteControl = new SQLiteControl(this);

        ipAddress = dataControl.getIpAddress();
    }

    private void initView(){

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Link");

        text1 = (TextView) findViewById(R.id.activityIpSettings_textView);

        editTextIpAddress = (EditText) findViewById(R.id.activityIpSettings_editText);

        buttonAddEcopro = (ImageButton) findViewById(R.id.activityIpSettings_buttonAdd);
        buttonAddEcopro.setOnClickListener(buttonAddEcoproClickListener);

        buttonLinkEcopro = (Button) findViewById(R.id.activityIpSettings_buttonLink);
        buttonLinkEcopro.setOnClickListener(buttonLinkEcoproClickListener);

        listView = (ListView) findViewById(R.id.activityIpSettings_listView);
        listView.setOnItemClickListener(listViewClickListener);
        listView.setOnItemLongClickListener(listViewLongClickListener);


        if(ipAddress.length() > 0){
            editTextIpAddress.setText(ipAddress);
        }

        initActionBar();
    }

    private void initActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_ip_settings_activity, null);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        TextView textView = (TextView) actionBarLayout.findViewById(R.id.actionBar_ipSettingsActivity_textLanScan);
        textView.setOnClickListener(buttonLanScanClickListener);
    }

    // endregion

    // region view click listener
    private View.OnClickListener buttonLanScanClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d(TAG, "Lan Scan textView was clicked");

            Intent intent = new Intent(IpSettingsActivity.this, LanScanActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener buttonAddEcoproClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            EditDialogFragment editDialogFragment = EditDialogFragment.newInstance(IpSettingsActivity.this);

            editDialogFragment.show(getFragmentManager(), "addDialog");


        }
    };

    private View.OnClickListener buttonLinkEcoproClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataControl.saveIpAddress(editTextIpAddress.getText().toString());
            ipAddress = dataControl.getIpAddress();
            if(ipAddress != null && ipAddress.length() > 0){
                ecoproConnector.checkLink(ipAddress);
            }
        }
    };

    // 記憶 並連線
    private AdapterView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            System.out.println("listView click");

            Ecopro ecopro = listEcopro.get(position);

            String tempIp = ecopro.getIpAddress();
            String tempMac = ecopro.getMacAddress();

            dataControl.saveIpAddress(tempIp);
            dataControl.saveMacAddress(tempMac);

            editTextIpAddress.setText(tempIp);

            ipAddress = tempIp;

            if(ipAddress != null && ipAddress.length() > 0){
                ecoproConnector.checkLink(ipAddress);
            }

            // 點擊完直接連線並顯示連線成功
            // 不跳頁
        }
    };

    // 跳出修改視窗
    private AdapterView.OnItemLongClickListener listViewLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Ecopro ecopro = listEcopro.get(position);

            EditDialogFragment editDialogFragment = EditDialogFragment.newInstance(ecopro, IpSettingsActivity.this);

            editDialogFragment.show(getFragmentManager(), "updateDialog");

            return false;
        }
    };

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
        // 這個 Activity 只會用到 onReceiveASIXUDPBroadcast
        Message message = new Message();
        message.what = MyHandler.CHECK_LINK;
        message.obj = isLinked;
        myHandler.sendMessage(message);
    }

    // endregion

    // region EDitDialogFragment callback

    @Override
    public void addNewEcopro(Ecopro ecopro) {
        Log.d(TAG, "addNewEcopro 收到 event");
        sqLiteControl.addEcopro(ecopro);
        updateListView();
    }

    @Override
    public void updateEcopro(Ecopro ecopro) {
        Log.d(TAG, "updateEcopro 收到 event");
        sqLiteControl.updateEcopro(ecopro);
        updateListView();
    }

    @Override
    public void deleteEcopro(Ecopro ecopro){
        Log.d(TAG, "deleteEcopro 收到 event");
        sqLiteControl.deleteEcopro(ecopro);
        updateListView();
    }

    // endregion

    // region MyHandler

    public void updateListView(){

        listEcopro = sqLiteControl.getEcoproArray();
        listMacData.clear();

        for (Ecopro ecopro : listEcopro){

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put(EcoproString.HASH_MAP_KEY_ID, ecopro.getId());
            hashMap.put(EcoproString.HASH_MAP_KEY_NAME, ecopro.getName());
            hashMap.put(EcoproString.HASH_MAP_KEY_IP, ecopro.getIpAddress());
            hashMap.put(EcoproString.HASH_MAP_KEY_MAC, ecopro.getMacAddress());

            listMacData.add(hashMap);
        }

        if(keepGoing){
            listItemAdapter = new SimpleAdapter(this,listMacData, //套入動態資訊
                    R.layout.listview_ip_mac,//套用自訂的XML
                    new String[] {EcoproString.HASH_MAP_KEY_NAME,EcoproString.HASH_MAP_KEY_IP}, //動態資訊取出順序
                    new int[] {R.id.layoutMac_ip,R.id.layoutMac_mac} //將動態資訊對應到元件ID
            );

            listView.setAdapter(listItemAdapter);
        }
    }

    public void receiveLinkCheck(boolean isLinked){

        if(isLinked){
            Toast.makeText(IpSettingsActivity.this, "連線成功", Toast.LENGTH_SHORT).show();
        }
        else
        {

            Toast.makeText(IpSettingsActivity.this, "連線失敗，請確認IP是否正確", Toast.LENGTH_SHORT).show();
        }
    }

    private MyHandler myHandler;

    /**
     * 從 背景執行緒返回 UI執行緒
     */
    private static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<IpSettingsActivity> mOuter;
        public MyHandler(IpSettingsActivity activity) {
            mOuter = new WeakReference<>(activity);
        }
        public static final int RECEIVE_BROADCAST_DATA = 1;
        public static final int CHECK_LINK = 2;
        @Override
        public void handleMessage(Message msg){
            IpSettingsActivity activity = mOuter.get();
            if (activity != null) {
                // Do something with outer as your wish.
                switch(msg.what){
                    case RECEIVE_BROADCAST_DATA :
                        System.out.println("IpSettingsActivity MyHandler.RECEIVE_BROADCAST_DATA");
//                        activity.updateListView((List)msg.obj);
                        break;
                    case CHECK_LINK :
                        System.out.println("IpSettingsActivity MyHandler.CHECK_LINK");
                        activity.receiveLinkCheck((boolean) msg.obj);
                        break;
                }
            }
        }
    }

    // endregion
}
