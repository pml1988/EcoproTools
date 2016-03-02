package com.anteyatec.anteyalibrary;

public class AirConState {

	/** 在這裡記錄冷氣控制的狀態 */
	
	
	/** 冷氣品牌 
	 *  1：日立	2：大金	3：三洋
	 *  4：東芝	5：國際	6：東元 
	 *  7：華凌	8：大同	9：0
	 * */
	private int iAirConBrand = 1;

	/** 現在 溫度 */
	private double iAirConTempCurrent = 27.0;
	/** 設定 溫度 */
	private double iAirConTempSet = 27.0;
	

	/** 建構子可以初始化一些數據但現在先不設定任何參數，直接用內建初始化的數值 */
	public AirConState() {
	}

	/*  模式設定  */
	private int iAirConMode = 32; 
	/** 自動模式 */
	public static final int AIRCON_MODE_Auto = 64;
	/** 冷氣模式 */
	public static final int AIRCON_MODE_COOL = 32;
	/** 送風模式 */
	public static final int AIRCON_MODE_WIND = 16;
	/** 暖氣模式 */
	public static final int AIRCON_MODE_WARM = 8;
	/** 除濕模式 */
	public static final int AIRCON_MODE_DRY  = 4;
	

	/** 設定冷氣模式 */
	public void setAirConMode(int mode){
		iAirConMode = mode;
	}

	/*  開關設定  */
	/** 1:開，0:關 */
	private int iAirConOnOff = 0; 
	/** 打開冷氣 */
	public void turnOn(){
		iAirConOnOff = 1;
	}
	/** 關閉冷氣 */
	public void turnOff(){
		iAirConOnOff = 0;
	}
	public int getOnOffState(){
		return iAirConOnOff + iAirConMode;
	} 
	public void setOnOff(){
		iAirConOnOff = (iAirConOnOff == 1)?0:1;
	}
	
	
	
	
	/*  冷氣品牌相關設定  */
	
	/** 冷氣品牌設定 */
	public void setAirConBrand(int ibrand){
		iAirConBrand = ibrand;
	}
	public int getAitConBrand(){
		return iAirConBrand;
	}
	
	
	
	

	/*  溫度相關設定  */
	
	/** 冷氣設定溫度上調 */
	public void setTempUp(){
		iAirConTempSet+=0.5;
		if(iAirConTempSet > 50.0){
			iAirConTempSet = 50.0;
		}
	}
	/** 冷氣溫度下調 */ 
	public void setTempDown(){
		iAirConTempSet-=0.5;
		if(iAirConTempSet < 0.0){
			iAirConTempSet = 0.0;
		}
	}
	/** 取得現在溫度 */
	public double getTempValue(){
		return iAirConTempSet;
	}
	/** 取得現在溫度 */
	public double getTempValueDouble(){
		return iAirConTempSet;
	}
	/** 取得現在溫度 */
	public String getTempValueString(){
		return "" + iAirConTempSet;
	}
	
	
	
	/*  風速、風向相關設定  */
	/** 風向 */
	private int iFanDirection = 16;
	/** 風速 */
	private int iFanSpeed = 1;
	
	/** 風向固定 */
	public static final int FAN_DIREC_HOLD = 0;
	/** 風向擺動 */
	public static final int FAN_DIREC_AUTO = 16;

	/** 風速自動 */
	public static final int FAN_SPEED_4 = 8;
	/** 風速強 */
	public static final int FAN_SPEED_3 = 4;
	/** 風速中 */
	public static final int FAN_SPEED_2 = 2;
	/** 風速弱 */
	public static final int FAN_SPEED_1 = 1;
	/* 風向補充說明 */
	

	/** 設定冷氣風速，風向 */
	public void setFan(int tFanDirection, int tFanSpeed){
		iFanDirection = tFanDirection;
		iFanSpeed = tFanSpeed;
	}
	public int getFanValue(){
		return iFanDirection + iFanSpeed;
	}
	public void setFanSpeed(){
		switch(iFanSpeed){
		case FAN_SPEED_1:
			iFanSpeed = FAN_SPEED_2;
			break;
		case FAN_SPEED_2:
			iFanSpeed = FAN_SPEED_3;
			break;
		case FAN_SPEED_3:
			iFanSpeed = FAN_SPEED_4;
			break;
		case FAN_SPEED_4:
			iFanSpeed = FAN_SPEED_1;
			break;
		}
	}
	public String getFanSpeedInString(){
		switch(iFanSpeed){
		case FAN_SPEED_1:
			return "弱";
		case FAN_SPEED_2:
			return "中";
		case FAN_SPEED_3:
			return "強";
		case FAN_SPEED_4:
			return "自動";
		default:
			return "";
		}
	}


	public void setFanDirection(int index){
		iFanDirection = FAN_DIREC_HOLD;
	}

	public int getFanSpeedInInt(){
		return iFanSpeed;
	}


	/**
	 * 取得 AirCon 的 Command
	 * @param tempData
	 * @return
	 */
	public static byte[] getAirConCommand(AirConState tempData){

		byte[] tempArray = new byte[10];

		tempArray[0] = (byte)0xfa;
		tempArray[1] = (byte)0x81;
		tempArray[2] = (byte)0x00; // ID (1-10 but 0等於廣播，所有AirCon都一起控制)
		tempArray[3] = (byte)tempData.getAitConBrand(); // 冷氣廠牌 1:日立、2：大金，後續請參閱說明書
		tempArray[4] = (byte)tempData.getOnOffState(); // 1:開，0:關
		tempArray[5] = (byte)tempData.getFanValue(); // 風向
		tempArray[6] = (byte)(tempData.getTempValue()*2); // 0~100，1格 = 0.5度 ， so, 25度等於50

		tempArray[7] = (byte)0x00; // 保留
		tempArray[8] = (byte)0x00; // 保留
		tempArray[9] = (byte)0x00; // CheckSum

		tempArray = FormatTool.getChecksumArray(tempArray);

		return tempArray;
	}
}
