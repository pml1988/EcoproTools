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
import com.anteya.ecoprotools.object.ProjectTools;
import com.anteya.ecoprotools.object.SQLiteControl;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class IpSettingsActivity extends Activity implements EcoproConnector.EcoproConnectorCallback
        , EditDialogFragment.EditDialogFragmentCallback {

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


    private int port_local = 8023;

    private int port_wan = 80;


    private Button btn;
    // endregion

    // region View

    private TextView text1;

    private TextView editTextIpAddress;

    private TextView editTextIpAddress_wan;

    private TextView activityIpSettings_editText_password;

    private ListView listView;

    private ImageButton buttonAddEcopro;

    private Button buttonLinkEcopro, buttonLinkEcopro_wan;

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
        if (tempIp.length() > 0) {
            //   editTextIpAddress.setText(tempIp);
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

    private void initData() {

        myHandler = new MyHandler(this);

        dataControl = (DataControl) getApplicationContext();

        ecoproConnector = new EcoproConnector();
        ecoproConnector.setEcoproConnectorCallback(this);

        sqLiteControl = new SQLiteControl(this);

        ipAddress = dataControl.getIpAddress();
    }

    private void initView() {

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Link");

        text1 = (TextView) findViewById(R.id.activityIpSettings_textView);


//        btn = (Button) findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("檢查密碼" + dataControl.getPd_one() + " " + dataControl.getPd_two() + " " + dataControl.getPd_three() + " " + dataControl.getPd_four());
//            }
//        });


        editTextIpAddress = (TextView) findViewById(R.id.activityIpSettings_editText);
        editTextIpAddress_wan = (TextView) findViewById(R.id.activityIpSettings_textView_wan);
        activityIpSettings_editText_password = (TextView) findViewById(R.id.activityIpSettings_editText_password);
        buttonAddEcopro = (ImageButton) findViewById(R.id.activityIpSettings_buttonAdd);
        buttonAddEcopro.setOnClickListener(buttonAddEcoproClickListener);

        buttonLinkEcopro = (Button) findViewById(R.id.activityIpSettings_buttonLink);
        buttonLinkEcopro_wan = (Button) findViewById(R.id.activityIpSettings_buttonLink_wan);
        buttonLinkEcopro.setOnClickListener(buttonLinkEcoproClickListener);
        buttonLinkEcopro_wan.setOnClickListener(buttonLinkEcoproClickListener_wan);


        listView = (ListView) findViewById(R.id.activityIpSettings_listView);
        listView.setOnItemClickListener(listViewClickListener);
        listView.setOnItemLongClickListener(listViewLongClickListener);


        if (ipAddress.length() > 0) {
            //   editTextIpAddress.setText(ipAddress);
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
            System.out.println("內網路：" + ipAddress + " PORT:" + port_wan);
            if (activityIpSettings_editText_password.getText().toString().length() != 4) {
                Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
            } else {


                // System.out.println("外網路："+editTextIpAddress.getText().toString());

                dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_local);

                dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
                dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
                dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
                dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));

                ipAddress = dataControl.getIpAddress();
                System.out.println("內網路：" + ipAddress + " PORT:" + port_local);
                if (ipAddress != null && ipAddress.length() > 0) {
                    System.out.println("內網路1：" + ipAddress + " PORT1:" + port_local);
                    ecoproConnector.checkLink(ipAddress, port_local);
                    dataControl.setPort_use(port_local);

                }
            }
        }
    };
    private View.OnClickListener buttonLinkEcoproClickListener_wan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (activityIpSettings_editText_password.getText().toString().length() != 4) {
                Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
            } else {


                dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_wan);
                dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
                dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
                dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
                dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));
                ipAddress = dataControl.getIpAddress();
                System.out.println("外網路：" + ipAddress + " PORT:" + port_wan);
                if (ipAddress != null && ipAddress.length() > 0) {

                    System.out.println("外網路1：" + ipAddress + " PORT1:" + port_local);
                    ecoproConnector.checkLink(ipAddress, port_local);
                    dataControl.setPort_use(port_local);

                }
            }


        }
    };


    public void link_check_fun() {
        link_check = true;
    }


    private boolean link_check = false;
    // 記憶 並連線
    private AdapterView.OnItemClickListener listViewClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            System.out.println("執行中");
//           if(link_check ==false)
//           {
            System.out.println("執行中");
            link_check = true;

            try {
                System.out.println("listView click");
                Toast.makeText(getApplication(), "Wait", Toast.LENGTH_SHORT).show();

                Ecopro ecopro = listEcopro.get(position);





                String[] tempIparray = ecopro.getIpAddress().split(":");
                String tempIp = tempIparray[0];

                if(tempIparray.length>1)
                {
                    dataControl.setPort_local(Integer.parseInt(tempIparray[1]));
                }



                int port = ProjectTools.getPort(ecopro.getIpAddress());


                String tempIp_wan = ecopro.getIpAddress_wan();
                String tempMac = ecopro.getMacAddress();
                String temppw = ecopro.getPassword();
                dataControl.saveIpAddress(tempIp, tempIp_wan, port);
                dataControl.saveMacAddress(tempMac);
                editTextIpAddress.setText(tempIp);
                editTextIpAddress_wan.setText(tempIp_wan);
                dataControl.setPd_one(Integer.parseInt(temppw.substring(0, 1)));
                dataControl.setPd_two(Integer.parseInt(temppw.substring(1, 2)));
                dataControl.setPd_three(Integer.parseInt(temppw.substring(2, 3)));
                dataControl.setPd_four(Integer.parseInt(temppw.substring(3, 4)));

                activityIpSettings_editText_password.setText(temppw);

                System.out.println("網路111111：" + tempIp + " >< " + port );
                System.out.println("外網路1：" + tempIp + " PORT1:" + port);
             //   ecoproConnector.sendUDPBroadcastToSpecifyIpAddress(tempIp);
                //   ecoproConnector.sendUDPBroadcastToSpecifyIpAddress(tempIp_wan);
                //  ipAddress = tempIp;

                   if (tempIp != null && tempIp.length() > 0) {
                       ecoproConnector.checkLink(tempIp , port);
                   }


            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("例外錯誤ipsettingsactivity："+e);
            }


         // }
//            else
//            {
//                System.out.println("執行中勿擾");
//            }


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

        System.out.println("收到晶片" + list.size());
        Message message = new Message();
        message.what = MyHandler.RECEIVE_BROADCAST_DATA;
        message.obj = list;
        myHandler.sendMessage(message);
    }

    @Override
    public void onReceiveBroadcastnoconnect(boolean flag) {

        if (activityIpSettings_editText_password.getText().toString().length() != 4) {
            Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
        } else {


            dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_wan);
            dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
            dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
            dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
            dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));
            ipAddress = dataControl.getIpAddress();
            System.out.println("外網路：" + ipAddress + " PORT:" + port_local);
            if (ipAddress != null && ipAddress.length() > 0) {

                System.out.println("外網路1：" + ipAddress + " PORT1:" + port_local);
                ecoproConnector.checkLink(ipAddress, port_local);
                dataControl.setPort_use(port_local);

                second_connect = true;
            }
        }


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
    public void deleteEcopro(Ecopro ecopro) {
        Log.d(TAG, "deleteEcopro 收到 event");
        sqLiteControl.deleteEcopro(ecopro);
        updateListView();
    }

    // endregion

    // region MyHandler
    private HashMap<String, Object> hashMap;

    public void updatedata(List list) {


        System.out.println("版本為=" + list.size());

        List<HashMap<String, Object>> listHashMap = list;

        System.out.println("網路updatedata:" + listHashMap.size());
        if (listHashMap.size() == 0) {
            System.out.println("跳出listhashmap：");


            System.out.println("網路111111：" + ipAddress + " " + ipAddress);
           // ecoproConnector.sendUDPBroadcastToSpecifyIpAddress(ipAddress);

//            if (activityIpSettings_editText_password.getText().toString().length() != 4) {
//                Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
//            } else {
//
//                second_connect =false;
//                dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_wan);
//                dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
//                dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
//                dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
//                dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));
//                ipAddress = dataControl.getIpaddress_wan();
//                System.out.println("外網路====：" + ipAddress + " PORT:" + port_local);
//                if (ipAddress != null && ipAddress.length() > 0) {
//
//                    System.out.println("外網路1：" + ipAddress + " PORT1:" + port_local);
//                    ecoproConnector.checkLink(ipAddress, port_local);
//                    dataControl.setPort_use(port_local);
//
//                    second_connect = false;
//                }
//            }


            return;
        }

        hashMap = listHashMap.get(0);

        // 將取回的資料更新到畫面中, 例如: Server Mode/Client Mode, Channel, Security Mode

        byte[] byteArray = (byte[]) hashMap.get("data");
        System.out.println("版本為==" + byteArray.length);
        //iTouch 初版為492長度
        if (byteArray.length == 492) {
            System.out.println("版本為492");

            port_local = 8023;
            port_wan = 80;

            if (activityIpSettings_editText_password.getText().toString().length() != 4) {
                Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
            } else {


                // System.out.println("外網路："+editTextIpAddress.getText().toString());

                dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_local);

                dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
                dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
                dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
                dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));


                ipAddress = dataControl.getIpAddress();
                System.out.println("內網路：" + ipAddress + " PORT:" + port_local);
                if (ipAddress != null && ipAddress.length() > 0) {
                    System.out.println("內網路1：" + ipAddress + " PORT1:" + port_local);
                    ecoproConnector.checkLink(ipAddress, port_local);
                    dataControl.setPort_use(port_local);

                }
            }


        }
        //iTouch 更新多了6 byte 前兩byte為 port1 後四byte 為port2
        else if (byteArray.length == 498) {
            System.out.println("版本為498");
            port_local = (int) hashMap.get("port1");
            port_wan = (int) hashMap.get("port2");

            dataControl.setPort_local(port_local);
            dataControl.setPort_wan(port_wan);

            System.out.println("版本為498" + port_local + " " + port_wan);


            if (activityIpSettings_editText_password.getText().toString().length() != 4) {
                Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
            } else {


                // System.out.println("外網路："+editTextIpAddress.getText().toString());

                dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_local);

                dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
                dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
                dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
                dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));


                ipAddress = dataControl.getIpAddress();
                System.out.println("內網路：" + ipAddress + " PORT:" + port_local);
                if (ipAddress != null && ipAddress.length() > 0) {
                    System.out.println("內網路1：" + ipAddress + " PORT1:" + port_local);
                    ecoproConnector.checkLink(ipAddress, port_local);
                    dataControl.setPort_use(port_local);

                }
            }


        } else {
            System.out.println("版本為未知");

        }

    }


    public void updateListView() {

        listEcopro = sqLiteControl.getEcoproArray();
        listMacData.clear();

        for (Ecopro ecopro : listEcopro) {

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put(EcoproString.HASH_MAP_KEY_ID, ecopro.getId());
            hashMap.put(EcoproString.HASH_MAP_KEY_NAME, ecopro.getName());
            hashMap.put(EcoproString.HASH_MAP_KEY_IP, ecopro.getIpAddress());
            hashMap.put(EcoproString.HASH_MAP_KEY_IP_WAN, ecopro.getIpAddress_wan());
            hashMap.put(EcoproString.HASH_MAP_KEY_MAC, ecopro.getMacAddress());

            listMacData.add(hashMap);
        }

        if (keepGoing) {
            listItemAdapter = new SimpleAdapter(this, listMacData, //套入動態資訊
                    R.layout.listview_ip_mac,//套用自訂的XML
                    new String[]{EcoproString.HASH_MAP_KEY_NAME, EcoproString.HASH_MAP_KEY_IP, EcoproString.HASH_MAP_KEY_IP_WAN}, //動態資訊取出順序
                    new int[]{R.id.layoutMac_ip, R.id.layoutMac_mac, R.id.layoutMac_mac1} //將動態資訊對應到元件ID

            );

            listView.setAdapter(listItemAdapter);
        }
    }

    private boolean second_connect = true;

    public void receiveLinkCheck(boolean isLinked) {

        if (isLinked) {
            Toast.makeText(IpSettingsActivity.this, "Connection Success "+ipAddress, Toast.LENGTH_SHORT).show();
            dataControl.saveIpAddress_now(ipAddress);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        link_check = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } else {

            System.out.println("");
            if (second_connect == true) {
                if (activityIpSettings_editText_password.getText().toString().length() != 4) {
                    Toast.makeText(getApplication(), "最多四碼", Toast.LENGTH_SHORT).show();
                } else {

                    second_connect =false;
                    dataControl.saveIpAddress(editTextIpAddress.getText().toString(), editTextIpAddress_wan.getText().toString(), port_local);
                    dataControl.setPd_one(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(0, 1)));
                    dataControl.setPd_two(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(1, 2)));
                    dataControl.setPd_three(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(2, 3)));
                    dataControl.setPd_four(Integer.parseInt(activityIpSettings_editText_password.getText().toString().substring(3, 4)));
                    ipAddress = dataControl.getIpaddress_wan();
                    if (ipAddress != null && ipAddress.length() > 0) {

                        System.out.println("外網路2re：" + ipAddress + " PORT1:" + dataControl.getPort_local());
                        ecoproConnector.checkLink(ipAddress, dataControl.getPort_local());
                        dataControl.setPort_use(dataControl.getPort_local());
                        second_connect = false;
                    }
                }

            } else {
                second_connect = true;
                Toast.makeText(IpSettingsActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            link_check = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }


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
        public void handleMessage(Message msg) {
            IpSettingsActivity activity = mOuter.get();
            if (activity != null) {
                // Do something with outer as your wish.
                switch (msg.what) {
                    case RECEIVE_BROADCAST_DATA:
                        System.out.println("IpSettingsActivity MyHandler.RECEIVE_BROADCAST_DATA");
                        activity.updatedata((List) msg.obj);
                        break;
                    case CHECK_LINK:
                        System.out.println("IpSettingsActivity MyHandler.CHECK_LINK");
                        activity.receiveLinkCheck((boolean) msg.obj);
                        break;
                }
            }
        }
    }

    // endregion
}
