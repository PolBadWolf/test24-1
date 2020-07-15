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

    private SerialPort port = null;
    private Thread threadRS = null;

    @Override
    public int Open(Runner_Interface runner, String portName, BAUD baud) {
        if (port != null) {
            Close();
        }

        boolean flagTmp = false;
        String[] portsName = getListPortsName();
        String portNameCase = portName.toUpperCase();
        for (int i = 0; i < portsName.length; i++) {
            if (portsName[i].equals(portNameCase))  {
                flagTmp = true;
                break;
            }
        }

        if (!flagTmp)   return INITCODE_NOTEXIST;

        port = SerialPort.getCommPort(portNameCase);
        port.setComPortParameters(baud.getBaud(), 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 1000);

        if (port.openPort())    return INITCODE_OK;

        return INITCODE_ERROROPEN;
    }

    @Override
    public void Close() {
        if (port == null)   return;

        ReciveStop();

        port.closePort();
        port = null;
    }

    private boolean onCycle;

    @Override
    public boolean ReciveStart() {
        if (port == null)   return false;
        if (!port.isOpen()) return false;

        threadRS = new Thread( ()->runner() );
        threadRS.start();
        return false;
    }

    @Override
    public void ReciveStop() {
        onCycle = false;

        try {
            if (threadRS != null) {
                while (!threadRS.isAlive()) {
                    Thread.yield();
                }
            }
        }
        catch (java.lang.Throwable th) {
            th.printStackTrace();
        }
    }

    private void runner() {
        // flush
        int num = 1;
        byte[] bytes = new byte[1000];
        while (num > 0) {
            num = port.readBytes(bytes, bytes.length);
        }

        onCycle = true;
        while (onCycle) {
            
        }
    }
}
