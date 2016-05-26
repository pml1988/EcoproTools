package com.anteya.ecoprotools.object;

import android.util.Log;


import com.anteyatec.anteyalibrary.AnteyaString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.SocketFactory;

/**
 * Created by yenlungchen on 2016/2/22.
 * <p/>
 * 連線工具
 * <p/>
 * 包含了 UDP 及 TCP 的 Socket 連線函式
 * <p/>
 * 以及回呼的 Callback, 必須 implements Callback
 */
public class EcoproConnector {

    private static final String TAG = "EcoproConnector";

    public static final int BROADCAST_PORT = 25122; // 詢問狀態使用 Broadcast port
    public static final int UNICAST_PORT = 25124; // 設定一定要用 Unicast port
    public static final int RECEIVE_PORT = 25999;

    /**
     * UDP 走區域網路, 速度較快, 很難有取不到 Ack 的情況
     * 故 timeout 較短
     */
    public static final int UDP_SOCKET_TIMEOUT_FOR_SET = 2000;
    public static final int UDP_SOCKET_TIMEOUT_FOR_SEARCH = 1200;

    /**
     * TCP 可以走外網, 也可能從行動網路連進來, 所以連線需要比較長的時間
     * 如果 timeout 設太短, 可能會造成資料尚未傳回來,就一直出現 SocketTimeoutException
     * 導致資料都一直無法更新, 所以 TCP 設定的 timeout 會比 UDP 要久一些
     */
    public static final int TCP_SOCKET_TIMEOUT = 2000;

    // region callback settings

    private EcoproConnectorCallback ecoproConnectorCallback;

    public void setEcoproConnectorCallback(EcoproConnectorCallback projectToolCallback) {
        this.ecoproConnectorCallback = projectToolCallback;
    }

    /**
     * Ecopro Connector 的 Callback, 資料傳輸完成會從這裡將資料傳回
     */
    public interface EcoproConnectorCallback {

        /**
         * ASIX 的 UDP Command, 使用 Broadcast 去做詢問
         * 詢問網內有哪些是 ASIX 的 Wi-Fi 晶片, 每個 ASIX Wi-Fi 晶片皆會回傳各自的資料
         * 再存成List<HashMap>回傳
         *
         * @param list
         */
        void onReceiveASIXUDPBroadcast(List list);


        void onReceiveBroadcastnoconnect(boolean flag);

        /**
         * ASIX 的 UDP Command, 使用 Unicast 去做 Wi-Fi 設定回傳的 byte array
         *
         * @param ackArray
         */
        void onReceiveASIXUDPUnicast(byte[] ackArray);

        /**
         * 安提亞科技 的 TCP Command, 走 TCP/IP Socket 回傳的 byte array
         *
         * @param ackArray
         */
        void onReceiveAnteyaTCPCommandAck(byte[] ackArray);

        /**
         * 單純測試是否能連線成功
         *
         * @param isLinked
         */
        void onCheckLink(boolean isLinked);
    }

    // endregion

    /**
     * socket 連線硬體部分192.168.2.3
     * 首先wifi要連結硬體AP
     **/
    public void checkLink(final String ipAddress , final int port) {
        new Thread() {
            @Override
            public void run() {

                Socket socket = null;


                int tempPort = port;

             //   int tempPort = AnteyaString.getPort(ipAddress);
                String tempIpAddress = AnteyaString.getIpAddress(ipAddress);

                // connect and send
                try {
                    socket = SocketFactory.getDefault().createSocket();
                    System.out.println("傳送訊息5555:"+tempIpAddress+"   "+ tempPort);
                    SocketAddress sa = new InetSocketAddress(tempIpAddress, tempPort);
                    socket.connect(sa, TCP_SOCKET_TIMEOUT);
                    System.out.println("checkLink 開啟 socket");
                    socket.setSoTimeout(TCP_SOCKET_TIMEOUT);
                    if (socket.isConnected()) {
                        Log.i(TAG, "連線 成功：" + socket.toString());
                        if (ecoproConnectorCallback != null) {
                            ecoproConnectorCallback.onCheckLink(true);
                        }
                    } else {
                        if (ecoproConnectorCallback != null) {
                            ecoproConnectorCallback.onCheckLink(false);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (ecoproConnectorCallback != null) {
                        ecoproConnectorCallback.onCheckLink(false);
                    }
                }

                // 執行完畢，關閉連線。
                try {
                    System.out.println("checkLink socket 關閉");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * send command with new thread, return by listener.
     *
     * @param ipAddress
     * @param commandArray
     */
    public void sendCommand(final String ipAddress, final byte[] commandArray , final int port) {
        new Thread() {
            @Override
            public void run() {

                System.out.println("傳送訊息主地方");
                if (commandArray != null) {
                    byte[] tempArray;
                    //tempArray回傳訊息
                    tempArray = executeCommand(ipAddress, commandArray,port);

                    // 務必檢查 data 是否為 null, 有可能就版本不支援此command,tempArray 會 return null
                    if (tempArray != null) {
                        // 這裡就必須使用 listener 了, 因為這裡無法 return 東西回去, 因為還在 Thread裡面
                        if (ecoproConnectorCallback == null) {
                            Log.d(TAG, "ecoproConnectorCallback == null");
                        } else {
                            ecoproConnectorCallback.onReceiveAnteyaTCPCommandAck(tempArray);

//                            for(int i = 0  ; i<tempArray.length;i++)
//                            {
//
//                                System.out.println("大數據："+tempArray[i]);
//
//                            }

                        }
                    }
                }
            }
        }.start();
    }


    /**
     * step 1
     * 傳送訊息時 socket 連線 中斷  不保持連線
     **/
    public byte[] executeCommand(String ipAddress, byte[] commandArray , int port) {
        System.out.println("傳送訊息主地方2:"+ ipAddress+" >< "+port);
        Log.i(TAG, "sendCommand, " + ipAddress);
        //   System.out.println("追蹤："+commandArray);
        Socket socket = null;

        //int port = AnteyaString.getPort(ipAddress);

        ipAddress = AnteyaString.getIpAddress(ipAddress);

        // connect and send
        try {
            socket = SocketFactory.getDefault().createSocket();
            SocketAddress sa = new InetSocketAddress(ipAddress, port);
            socket.connect(sa, TCP_SOCKET_TIMEOUT);
            //  System.out.println("executeCommand 開啟 socket");
            socket.setSoTimeout(TCP_SOCKET_TIMEOUT);
            if (socket.isConnected()) {
                Log.i(TAG, "連線 成功：" + socket.toString());
            } else {
                // 執行完畢，關閉連線。
                try {
                    ecoproConnectorCallback.onCheckLink(false);
                    System.out.println("executeCommand socket 不成功就關閉");
                    socket.close();
                } catch (IOException e) {
                    ecoproConnectorCallback.onCheckLink(false);
                    System.out.println("executeCommand socket 不成功就關閉＝＝");
                    e.printStackTrace();
                }
                return null;
            }
        } catch (Exception e) {
            ecoproConnectorCallback.onCheckLink(false);
            System.out.println("executeCommand socket 不成功就關閉＝＝");
            e.printStackTrace();
            return null;
        }

        byte[] arrayAckData;
        //發出訊息，並且接收回傳訊息。
        arrayAckData = sendCommandGetArray(commandArray, socket);

        // 執行完畢，關閉連線。
        try {
            //  System.out.println("executeCommand socket 關閉");
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return arrayAckData;
    }

    /**
     * step:2
     * 訊息傳輸
     * 把指令陣列丟進去給 outputStream 發送出去
     * 用 inputStream 接收回來的訊息, 並放到byteArray return回去
     **/
    protected byte[] sendCommandGetArray(byte[] tempArray, Socket socket) {
        byte[] array_getEchoFromServer = new byte[1024];
        System.out.println("傳送訊息主地方3====");
        try {
            if (socket.getOutputStream() != null) {
                socket.getOutputStream().write(tempArray);
                socket.getOutputStream().flush();

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                int bytesCount;
                InputStream inputStream = socket.getInputStream();
                if ((bytesCount = inputStream.read(array_getEchoFromServer)) != -1) {
                    byteArrayOutputStream.write(array_getEchoFromServer, 0, bytesCount);
                }
                array_getEchoFromServer = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
            } else {
                Log.d("", "bufferOutput == null");
            }
            return array_getEchoFromServer;
        } catch (IOException e) {
            e.printStackTrace();
            try {

                System.out.println("sendCommandGetArray socket 關閉");
                socket.close();
            } catch (IOException ee) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // region ASIX UDP protocol

    /**
     * 使用 ASIX 的 protocol 走 UDP Broadcast 到指定的 IpAddress
     * 用途為 Wi-Fi設定頁面需要該Ip 的其他設定資料，所以要先從 UDP Broadcast API 指定該Ip 發出詢問
     * 接收後另外儲存, 設定Wi-Fi的參數就由這筆資料來做修改
     */
    public void sendUDPBroadcastToSpecifyIpAddress(final String ipAddress) {
        new Thread() {
            @Override
            public void run() {

                List<HashMap<String, Object>> tempListMacData = new ArrayList<>();

                Log.d(TAG, "EcoproConnector Start UDP Broadcast.");
                // set 0x02
                byte[] command = new byte[]{(byte) 0x41, (byte) 0x53, (byte) 0x49, (byte) 0x58, (byte) 0x58, (byte) 0x49, (byte) 0x53, (byte) 0x41, (byte) 0x80, (byte) 0x00};

                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    // 設定 local port 為 25999
                    socket.bind(new InetSocketAddress("0.0.0.0", RECEIVE_PORT));
                    socket.setSoTimeout(UDP_SOCKET_TIMEOUT_FOR_SEARCH);
                    socket.setBroadcast(true);


                    System.out.println("修改封包ＩＤ:"+ipAddress);
                    DatagramPacket packet = new DatagramPacket(command, command.length, InetAddress.getByName(ipAddress), BROADCAST_PORT);
                    // 發送三次 Broadcast
                    socket.send(packet);
                    socket.send(packet);
                    socket.send(packet);


                    byte[] receiveBuffer = new byte[1000];
                    DatagramPacket packet2 = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    System.out.println("packet1:"+packet2);
                   socket.receive(packet2);
                    System.out.println("packet2:"+packet2);


                    byte[] tempArray = new byte[packet2.getLength()];

                    System.out.println("temparray:"+tempArray.length);

                    System.arraycopy(packet2.getData(), 0, tempArray, 0, packet2.getLength());

                    HashMap map = new HashMap();
                    map.put("ip", packet2.getAddress().getHostAddress());
                    map.put("length", packet2.getLength());
                    map.put("data", tempArray);
                    map.put("mac", ProjectTools.getMacFromAck(packet2.getData()));

                    if (tempArray.length >= 498) {
                        String temp = ProjectTools.convertByteToHexString(tempArray[492]) + ProjectTools.convertByteToHexString(tempArray[493]);

                        int D1 = Integer.parseInt(temp, 16);
                        System.out.println("16進位:" + D1);

                        String temp1 = ProjectTools.convertByteToHexString(tempArray[494]) + ProjectTools.convertByteToHexString(tempArray[495]) + ProjectTools.convertByteToHexString(tempArray[496]) + ProjectTools.convertByteToHexString(tempArray[497]);

                        int D2 = Integer.parseInt(temp1, 16);
                        System.out.println("16進位:" + D2);
                        map.put("port1", D1);
                        map.put("port2", D2);
                    }


                    tempListMacData.add(map);

                    if (ecoproConnectorCallback != null) {
                        ecoproConnectorCallback.onReceiveASIXUDPBroadcast(tempListMacData);
                    }
                    socket.close();
                    System.out.println("EcoproConnector SocketException, sendUDPBroadcast is finish");

                } catch (SocketException e) {
                    System.out.println("EcoproConnector SocketException, sendUDPBroadcastToSpecifyIpAddress is over");
                    if (ecoproConnectorCallback != null) {
                        ecoproConnectorCallback.onReceiveASIXUDPBroadcast(tempListMacData);
                    }
                    socket.close();
                } catch (IOException e) {
                    System.out.println("EcoproConnector IOException, sendUDPBroadcastToSpecifyIpAddress loop is over");
                    if (ecoproConnectorCallback != null) {
                        System.out.println("未連結");
                        ecoproConnectorCallback.onReceiveBroadcastnoconnect(true);
                        ecoproConnectorCallback.onReceiveASIXUDPBroadcast(tempListMacData);
                    }
                    socket.close();
                }
            }
        }.start();
    }

    /**
     * 使用 ASIX 的 protocol 走 UDP Broadcast 向區域網路內的所有 IP 做詢問
     * ASIX 的 Wi-Fi 晶片認得此 protocol 即馬上回傳該裝置的 Wi-Fi狀態
     */
    public void sendUDPBroadcast() {
        new Thread() {
            @Override
            public void run() {

                List<HashMap<String, Object>> tempListMacData = new ArrayList<>();

                Log.d(TAG, "EcoproConnector Start UDP Broadcast.");
                // set 0x02
                byte[] command = new byte[]{(byte) 0x41, (byte) 0x53, (byte) 0x49, (byte) 0x58, (byte) 0x58, (byte) 0x49, (byte) 0x53, (byte) 0x41, (byte) 0x80, (byte) 0x00};

                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    // 設定 local port 為 25999
                    socket.bind(new InetSocketAddress("0.0.0.0", RECEIVE_PORT));
                    socket.setSoTimeout(UDP_SOCKET_TIMEOUT_FOR_SEARCH);
                    socket.setBroadcast(true);

                    DatagramPacket packet = new DatagramPacket(command, command.length, InetAddress.getByName("255.255.255.255"), BROADCAST_PORT);
                    // 發送三次 Broadcast
                    socket.send(packet);
                    socket.send(packet);
                    socket.send(packet);

                    // 收到 Broadcast 的 亞信Wifi晶片 會回傳相關資訊, 開一個 while loop 不斷地接收
                    // 直到觸發 socket timeout, 代表沒有訊號再傳回來了, 即跳出 while loop
                    while (true) {

                        byte[] receiveBuffer = new byte[1000];
                        DatagramPacket packet2 = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        socket.receive(packet2);

                        byte[] tempArray = new byte[packet2.getLength()];
                        System.arraycopy(packet2.getData(), 0, tempArray, 0, packet2.getLength());

                        HashMap map = new HashMap();
                        map.put(EcoproString.HASH_MAP_KEY_IP, packet2.getAddress().getHostAddress());
                        map.put(EcoproString.HASH_MAP_KEY_DATA, tempArray);
                        map.put(EcoproString.HASH_MAP_KEY_MAC, ProjectTools.getMacFromAck(packet2.getData()));

                        String ip = packet2.getAddress().getHostAddress();

                        for (int i = tempListMacData.size() - 1; i >= 0; i--) {
                            HashMap hm = tempListMacData.get(i);
                            String tempIp = (String) hm.get(EcoproString.HASH_MAP_KEY_IP);
                            if (tempIp.equals(ip)) {
                                tempListMacData.remove(i);
                                break;
                            }
                        }
                        tempListMacData.add(map);
                    }
                } catch (SocketException e) {
                    System.out.println("EcoproConnector SocketException, sendUDPBroadcast is over");
                    if (ecoproConnectorCallback != null) {
                        ecoproConnectorCallback.onReceiveASIXUDPBroadcast(tempListMacData);
                    }
                    socket.close();
                } catch (IOException e) {
                    System.out.println("EcoproConnector IOException, sendUDPBroadcast is over");
                    if (ecoproConnectorCallback != null) {
                        ecoproConnectorCallback.onReceiveASIXUDPBroadcast(tempListMacData);
                    }
                    socket.close();
                }
            }
        }.start();
    }

    /**
     * 使用 ASIX 的 protocol 走 UDP 去設定 iTouch/ipHost Wi-Fi狀態
     *
     * @param hashMap 由 Broadcast 回傳回來的 data 建立的 HashMap
     */
    public void sendUDPUnicast(final HashMap hashMap) {
        new Thread() {
            @Override
            public void run() {

                String ipAddress = (String) hashMap.get("ip");

                byte[] ackArray = (byte[]) hashMap.get("data");

                System.out.println("測試修改長度："+ackArray.length);
                for(int i =  0    ; i<ackArray.length;i++ )
                {

                    System.out.println("測試修改"+i+":"+ackArray[i]);

                }

                byte[] tempArrayForAck;

                ProjectTools.printByteArray(ackArray, "sendUDPUnicast", 4);

                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);

                    // 設定 local port 為 25999
                    socket.bind(new InetSocketAddress("0.0.0.0", RECEIVE_PORT));
                    socket.setSoTimeout(UDP_SOCKET_TIMEOUT_FOR_SET);
                    socket.setBroadcast(true);

                    DatagramPacket packet = new DatagramPacket(ackArray, ackArray.length, InetAddress.getByName(ipAddress), UNICAST_PORT);
                    // 發送 Unicast
                    socket.send(packet);
                    socket.send(packet);

                    byte[] receiveBuffer = new byte[1000];
                    DatagramPacket packet2 = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(packet2);
                    System.out.println("EcoproConnector SocketException, socket.receive(packet2);");

                    tempArrayForAck = new byte[packet2.getLength()];
                    System.arraycopy(packet2.getData(), 0, tempArrayForAck, 0, packet2.getLength());

                    HashMap map = new HashMap();
                    map.put(EcoproString.HASH_MAP_KEY_IP, packet2.getAddress().getHostAddress());
                    map.put(EcoproString.HASH_MAP_KEY_DATA, tempArrayForAck);
                    map.put(EcoproString.HASH_MAP_KEY_MAC, ProjectTools.getMacFromAck(packet2.getData()));

                    ProjectTools.printByteArray(ackArray, "sendUDPUnicast receive ack", 4);

                    if (ecoproConnectorCallback != null) {
                        ecoproConnectorCallback.onReceiveASIXUDPUnicast(tempArrayForAck);
                    }
                    socket.close();
                    System.out.println("EcoproConnector SocketException, sendUDPBroadcast is finish");


                } catch (SocketException e) {
                    System.out.println("EcoproConnector SocketException, sendUDPBroadcast is over");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("EcoproConnector IOException, sendUDPBroadcast is over");
                    socket.close();
                }
            }
        }.start();
    }

    // endregion
}
