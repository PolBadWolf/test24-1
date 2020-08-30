package org.example.test24.runner;

import org.example.test24.bd.BaseData;
import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.screen.MainFrame_interface;

import java.util.function.Consumer;

public interface Runner {
    interface Closer {
        void close();
    }
    static Runner main(Consumer closer) {
        return new RunningClass(closer);
    }

    void init(BaseData bdSql, CommPort commPort, MainFrame_interface mainFrame);
    void reciveRsPush(byte[] bytes, int lenght);

    void Suspended();
    void Close();

    class TypePack {
        final static int MANUAL_ALARM       = 0;
        final static int MANUAL_BACK        = 1;
        final static int MANUAL_STOP        = 2;
        final static int MANUAL_FORWARD     = 3;
        final static int MANUAL_SHELF       = 4;
        final static int CYCLE_ALARM        = 5;
        final static int CYCLE_BACK         = 6;
        final static int CYCLE_DELAY        = 7;
        final static int CYCLE_FORWARD      = 8;
        final static int CYCLE_SHELF        = 9;
        final static int CURENT_DATA        = 11;
        final static int VES                = 12;
    }

}
