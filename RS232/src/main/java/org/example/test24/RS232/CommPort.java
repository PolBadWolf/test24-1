package org.example.test24.RS232;

import com.fazecast.jSerialComm.SerialPort;

import java.util.function.Consumer;

public interface CommPort {
    interface CallBack {
        void reciveRsPush(byte[] bytes, int lenght);
    }
    interface Closer {
        void close();
    }
    static CommPort main(Consumer closer) {
        return new CommPortClass(closer);
    }
    //String[] getListPortsName();
    static String[] getListPortsName() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] namePorts = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            namePorts[i] = ports[i].getSystemPortName().toUpperCase();
        }
        return namePorts;
    }
    PortStat Open(CallBack callBack, String portName, BAUD baud);
    int INITCODE_OK           = 0;
    int INITCODE_NOTEXIST     = 1;
    int INITCODE_ERROROPEN    = 2;
    int UNKNOWN_ERRORN        = 99;
    enum PortStat {
        INITCODE_OK         (CommPort.INITCODE_OK),
        INITCODE_NOTEXIST   (CommPort.INITCODE_NOTEXIST),
        INITCODE_ERROROPEN  (CommPort.INITCODE_ERROROPEN),
        UNKNOWN_ERRORN      (CommPort.UNKNOWN_ERRORN);
        int portStat;
        PortStat(int portStat) {
            this.portStat = portStat;
        }
        public int getCodePortStat() {
            return portStat;
        }
        @Override
        public String toString() {
            String stat = "";
            switch (portStat) {
                case CommPort.INITCODE_OK:
                    stat = "INITCODE_OK";
                    break;
                case CommPort.INITCODE_NOTEXIST:
                    stat = "INITCODE_NOTEXIST";
                    break;
                case CommPort.INITCODE_ERROROPEN:
                    stat = "INITCODE_ERROROPEN";
                    break;
                default:
                    stat = "UNKNOWN_ERRORN";
            }
            return stat;
        }
    }

    void Close();
    boolean ReciveStart();
    void ReciveStop();
}
