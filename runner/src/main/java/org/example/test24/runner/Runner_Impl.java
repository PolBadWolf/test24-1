package org.example.test24.runner;

import org.example.test24.allinterface.runner.Runner_Interface;

public interface Runner_Impl extends Runner_Interface {
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
