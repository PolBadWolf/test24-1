package org.example.test24.allinterface.commPort;

import org.example.test24.allinterface.runner.Runner_Interface;

public interface CommPort_Interface {
    String[] getListPortsName();
    int Open(Runner_Interface runner, String portName, BAUD baud);
    final int INITCODE_OK           = 0;
    final int INITCODE_NOTEXIST     = 1;
    final int INITCODE_ERROROPEN    = 2;
    void Close();
    boolean ReciveStart();
    void ReciveStop();
}
