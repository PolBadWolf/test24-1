package org.example.test24.loader;

import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.bd.UserClass;

public interface MainClassCallBackTuningFrame {
    CommPort getCommPort();
    void saveConfig(String[] parametrs);
    String[] getFileNameSql();
}
