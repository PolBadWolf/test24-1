package org.example.test24.RS232;

import org.example.lib.interfaces.RsCallBack;

public interface CommPort_Interface {
    String[] getListPortsName();
    int Open(RsCallBack rsCallBack, String portName, BAUD baud);
    final int INITCODE_OK           = 0;
    final int INITCODE_NOTEXIST     = 1;
    final int INITCODE_ERROROPEN    = 2;
    void Close();
    boolean ReciveStart();
    void ReciveStop();
}
