package ru.yandex.fixcolor.tests.spc.runner;

import ru.yandex.fixcolor.tests.spc.bd.BaseData;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;
import ru.yandex.fixcolor.tests.spc.screen.MainFrame_interface;

import java.util.logging.Level;

public interface Runner {
    public interface CallBack {
        void sendStopNcycleMax(int nCycleMax);
        void sendMessageStop();
    }
    static Runner main(CallBack callBack) {
        return new RunningClass(callBack);
    }

    void init(BaseData bdSql, MainFrame_interface mainFrame);
    void fillFields();
    void loadConfigK();
    void reciveRsPush(byte[] bytes, int lenght);

    void Suspended();
    void Close();

    class TypePack {
        final static int MANUAL_ALARM       = 0;
        final static int MANUAL_BACK        = 1;
        final static int MANUAL_STOP        = 2;
        final static int MANUAL_FORWARD     = 3;
        final static int MANUAL_SHELF       = 4;
        //final static int CYCLE_ALARM        = 5;
        final static int CYCLE_BACK         = 6;
        final static int CYCLE_DELAY        = 7;
        final static int CYCLE_FORWARD      = 8;
        final static int CYCLE_SHELF        = 9;
        final static int CURENT_DATA        = 11;
        final static int FORCE              = 12;
        final static int RESET              = 13;
        final static int CALIBR_DATA        = 17;
        final static int ERROR              = 255;
        public static String toString(int typePack) {
            String stroka = "";
            switch (typePack) {
                case MANUAL_ALARM:
                    stroka = "Ошибка";
                    break;
                case MANUAL_BACK:
                    stroka = "Ручной режим: Шток назад";
                    break;
                case CYCLE_BACK:
                    stroka = "Авто режим: Шток назад";
                    break;
                case MANUAL_STOP:
                    stroka = "Стенд остановлен";
                    break;
                case CYCLE_DELAY:
                    stroka = "Авто режим: пауза";
                    break;
                case MANUAL_FORWARD:
                    stroka = "Ручной режим: Шток вперед";
                    break;
                case CYCLE_FORWARD:
                    stroka = "Авто режим: Шток вперед";
                    break;
                case MANUAL_SHELF:
                case CYCLE_SHELF:
                    stroka = "гидротолкатели под нагрузкой";
                    break;
                default:
                    stroka = "что это было ? " +typePack;
                    MyLogger.myLog.log(Level.SEVERE,"TypePack toString", new IllegalStateException(stroka));
            }
            return stroka;
        }
    }
}
