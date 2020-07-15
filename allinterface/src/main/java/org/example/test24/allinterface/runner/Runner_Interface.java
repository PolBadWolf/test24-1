package org.example.test24.allinterface.runner;

import org.example.test24.allinterface.commPort.CommPort_Interface;

public interface Runner_Interface {
    void init(CommPort_Interface commPort, MainFrame_interface mainFrame);
    void reciveRsPush(byte[] bytes, int lenght);

    void Suspended();
    void Close();
}
