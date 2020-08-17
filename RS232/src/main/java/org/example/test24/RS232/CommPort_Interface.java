package org.example.test24.RS232;

import org.example.lib.interfaces.RsCallBack;

public interface CommPort_Interface {
    String[] getListPortsName();
    int Open(RsCallBack rsCallBack, String portName, BAUD baud);
    int INITCODE_OK           = 0;
    int INITCODE_NOTEXIST     = 1;
    int INITCODE_ERROROPEN    = 2;
    void Close();
    boolean ReciveStart();
    void ReciveStop();
}
