package com.tutk.sample.AVAPI;

import com.anteya.ecoprotools.object.ProjectTools;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by anteya on 15/10/23.
 */
public class SMsgAVIoctrlSetStreamCtrlReq {

    public static byte[] parseContent(int paramInt, byte paramByte)
    {
        byte[] arrayOfByte = new byte[8];
        System.arraycopy(SMsgAVIoctrlSetStreamCtrlReq.intToByteArray_Little(paramInt), 0, arrayOfByte, 0, 4);
        arrayOfByte[4] = paramByte;
        return arrayOfByte;
    }
    public static int[] parseContent2(int paramInt)
    {
        int[] arrayOfByte = new int[8];
        System.arraycopy(SMsgAVIoctrlSetStreamCtrlReq.intToByteArray_Little_Int(paramInt), 0, arrayOfByte, 0, 4);
        return arrayOfByte;
    }
    public static byte[] parseContent(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4, byte paramByte5, byte paramByte6)
    {
        byte[] arrayOfByte = new byte[8];
        arrayOfByte[0] = paramByte1; //ptz control command , refer to ENUM_PTZCMD
        arrayOfByte[1] = paramByte2; //ptz control speed
        arrayOfByte[2] = paramByte3; // no use in app
        arrayOfByte[3] = paramByte4; // no use in app
        arrayOfByte[4] = paramByte5; // no use in app
        arrayOfByte[5] = paramByte6; // camera index
        //arrayOfByte[6] arrayOfByte[7] 保留

        return arrayOfByte;
    }


    public static byte[] SET_WIFI_COMMAND()
    {

        byte[] byteArray = new byte[76];


        // 設定 SSID byte
        String ssid;
        ssid = "ANTEYA_Guest1";
        byte[] ssidArray = convertStringToByteArray(ssid);// (byte)0x00, (byte)0x74, (byte)0x31, (byte)0x00


        // 設定 password byte
        String password = "";
        password = "";
        byte[] passwordArray = convertStringToByteArray(password);



        System.arraycopy(ssidArray, 0, byteArray, 0, ssidArray.length);
        System.arraycopy(passwordArray, 0, byteArray, 32, passwordArray.length);


        byteArray[65] = (byte)0x01;
        byteArray[66] = (byte)0x02;
        return byteArray;
    }
    public static byte[] convertStringToByteArray(String ssid){

        byte[] ssidArray = new byte[32];

        // 初始化預設放 0xff
        Arrays.fill(ssidArray, (byte) 0xff);

        if(ssid.length() > 0){
            try {
                byte[] bytes = ssid.getBytes("UTF-8");
                System.arraycopy(bytes, 0, ssidArray, 0, bytes.length);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ssidArray;
    }

    public static final byte[] intToByteArray_Little(int paramInt)
    {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = ((byte)paramInt);
        arrayOfByte[1] = ((byte)(paramInt >>> 8));
        arrayOfByte[2] = ((byte)(paramInt >>> 16));
        arrayOfByte[3] = ((byte)(paramInt >>> 24));
        return arrayOfByte;
    }
    public static final int[] intToByteArray_Little_Int(int paramInt)
    {
        int[] arrayOfByte = new int[4];

        byte tempByte = (byte)paramInt;

        arrayOfByte[0] = (int)tempByte;
        arrayOfByte[1] = ((paramInt >>> 8));
        arrayOfByte[2] = ((paramInt >>> 16));
        arrayOfByte[3] = ((paramInt >>> 24));
        return arrayOfByte;
    }

}
