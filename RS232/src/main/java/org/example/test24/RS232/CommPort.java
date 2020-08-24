package org.example.test24.RS232;

import org.example.test24.allinterface.Closer;

public interface CommPort {
    interface CallBack {
        void reciveRsPush(byte[] bytes, int lenght);
    }
    static CommPort main(Closer closer) {
        return new CommPortClass(closer);
    }
    String[] getListPortsName();
    int Open(CallBack callBack, String portName, BAUD baud);
    int INITCODE_OK           = 0;
    int INITCODE_NOTEXIST     = 1;
    int INITCODE_ERROROPEN    = 2;
    void Close();
    boolean ReciveStart();
    void ReciveStop();
}
