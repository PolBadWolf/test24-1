package org.example.test24.RS232;

public interface CommPort {
    interface CallBack {
        void reciveRsPush(byte[] bytes, int lenght);
    }
    static CommPort main() {
        return new CommPortClass();
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
