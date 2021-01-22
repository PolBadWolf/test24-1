package ru.yandex.fixcolor.tests.spc.runner.alarmmessage;

import ru.yandex.fixcolor.tests.spc.lib.MyLogger;

import java.util.logging.Level;

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

    public void setAlarmMessage(AlarmMessage alarmMessage) {
        alarmCode = alarmMessage.getAlarmCode();
    }

    public void setAlarmCode(int alarmCode) {
        this.alarmCode = alarmCode;
    }

    @Override
    public String toString() {
        String message;
        switch (alarmCode) {
            case AlarmMessageCode.NONE:
                message = "переключатель не в положении стоп";
                break;
            case AlarmMessageCode.ABORT:
                message = "прервано оператором";
                break;
            case AlarmMessageCode.SWITCH:
                message = "пропуск положения \"стоп\"";
                break;
            case AlarmMessageCode.SAFE_ENGINE_ON:
                message = "шток не выдвенулся";
                break;
            case AlarmMessageCode.SAFE_ENGINE_OFF:
                message = "шток не задвинулся";
                break;
            default:
                MyLogger.myLog.log(Level.SEVERE, "неизвестный код ошибки", new IllegalStateException("Unexpected value: " + alarmCode));
                message = "неизвестный код ошибки: " + alarmCode;
        }
        return message;
    }
}
