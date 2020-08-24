package org.example.test24.RS232;

import org.example.lib.interfaces.RsCallBack;

public interface CommPort {
    String[] getListPortsName();
    int Open(RsCallBack rsCallBack, String portName, BAUD baud);
    int INITCODE_OK           = 0;
    int INITCODE_NOTEXIST     = 1;
    int INITCODE_ERROROPEN    = 2;
    void Close();
    boolean ReciveStart();
    void ReciveStop();
    public static CommPort main() {
        return new CommPortClass();
    }
}
