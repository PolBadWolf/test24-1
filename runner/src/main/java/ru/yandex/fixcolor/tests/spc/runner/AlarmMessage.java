package ru.yandex.fixcolor.tests.spc.runner;

import ru.yandex.fixcolor.tests.spc.lib.MyLogger;

import java.util.logging.Level;

class AlarmMessageCode {
    final static int NONE = 0;
    final static int ABORT = 1;
    final static int SWITCH = 2;
    final static int SAFE_ENGINE_ON = 3;
    final static int SAFE_ENGINE_OFF = 4;
}

public enum AlarmMessage {
    ALARM_CODE_NONE             (AlarmMessageCode.NONE),
    ALARM_CODE_ABORT            (AlarmMessageCode.ABORT),
    ALARM_CODE_SWITCH           (AlarmMessageCode.SWITCH),
    ALARM_CODE_SAFE_ENGINE_ON   (AlarmMessageCode.SAFE_ENGINE_ON),
    ALARM_CODE_SAFE_ENGINE_OFF  (AlarmMessageCode.SAFE_ENGINE_OFF);

    private int alarmCode;

    AlarmMessage(int alarmCode) {
        this.alarmCode = alarmCode;
    }

    public int getAlarmCode() {
        return alarmCode;
    }

    @Override
    public String toString() {
        String message;
        switch (alarmCode) {
            case AlarmMessageCode.NONE:
                message = "переключатель не в положении стоп";
                break;
            case AlarmMessageCode.ABORT:
                message = "отмена замера";
                break;
            case AlarmMessageCode.SWITCH:
                message = "переключатель неисправен";
                break;
            case AlarmMessageCode.SAFE_ENGINE_ON:
                message = "заклинивание при включенном двигателе";
                break;
            case AlarmMessageCode.SAFE_ENGINE_OFF:
                message = "заклинивание при отключенном двигателе";
                break;
            default:
                MyLogger.myLog.log(Level.SEVERE, "ошибка кода ALARM", new IllegalStateException("Unexpected value: " + alarmCode));
                message = "ошибка кода ALARM: " + alarmCode;
        }
        return message;
    }
}
