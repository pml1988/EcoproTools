package com.anteya.ecoprotools.operatingtype;

/**
 * Created by yenlungchen on 2016/2/17.
 */
public class LightOperatingTime {

    public static final int F1Mode_DefaultTurnOnTime = 7;
    public static final int F1Mode_DefaultTurnOffTime = 21;

    public static final int F2Mode_DefaultTurnOnTime = 6;
    public static final int F2Mode_DefaultTurnOffTime = 0;

    public static final int F3Mode_DefaultTurnOnTime = 0;
    public static final int F3Mode_DefaultTurnOffTime = 0;

    public int ManualMode_TurnOnTime = 6;
    public int ManualMode_TurnOffTime = 20;

    public int ManualMode_TurnOnTime_minute = 0;
    public int ManualMode_TurnOffTime_minute = 0;

    public int getTimeByModeOnOff(int mode, boolean onOff){
        switch(mode){
            case 1:
                return (onOff)?F1Mode_DefaultTurnOnTime:F1Mode_DefaultTurnOffTime;
            case 2:
                return (onOff)?F2Mode_DefaultTurnOnTime:F2Mode_DefaultTurnOffTime;
            case 3:
                return (onOff)?F3Mode_DefaultTurnOnTime:F3Mode_DefaultTurnOffTime;
            case 4:
                return (onOff)?ManualMode_TurnOnTime:ManualMode_TurnOffTime;
            default:
                return 0;
        }
    }
    public int getTimeByModeOnOff_minute(int mode, boolean onOff){
        switch(mode){
            case 1:
                return 0;
            case 2:
                return 0;
            case 3:
                return 0;
            case 4:
                return (onOff)?ManualMode_TurnOnTime_minute:ManualMode_TurnOffTime_minute;
            default:
                return 0;
        }
    }

    public LightOperatingTime(){

    }
}
