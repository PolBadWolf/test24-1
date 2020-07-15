package org.example.test24.RS232;

import com.fazecast.jSerialComm.SerialPort;
import org.example.test24.allinterface.commPort.BAUD;
import org.example.test24.allinterface.runner.Runner_Interface;

public class CommPort implements CommPort_Impl {
    @Override
    public String[] getListPortsName() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] namePorts = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            namePorts[i] = ports[i].getSystemPortName().toUpperCase();
        }
        return namePorts;
    }

    @Override
    public int Open(Runner_Interface runner, String portName, BAUD baud) {
        return 0;
    }

    @Override
    public void Close() {

    }

    @Override
    public boolean ReciveStart() {
        return false;
    }

    @Override
    public void ReciveStop() {

    }
}
