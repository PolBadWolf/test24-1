package org.example.test24.loader;

import org.example.test24.RS232.CommPort;

public interface MainClassCallBackTuningFrame {
    CommPort getCommPort();
    void saveConfig(String[] parametrs);
    String[] getFilesNameSql();
    String getFileNameSql(String typeBd) throws Exception;
}
