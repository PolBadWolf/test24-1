package org.example.test24.loader;

import org.example.test24.allinterface.bd.UserClass;

public interface MainClassCallBackStartFrame {
    boolean checkCommPort();
    boolean checkSql();
    void closeFrame();
    // ---------------
    TuningFrame getTuningFrame();
    String[] getParameters();
    String[] getFilesNameSql();
    String getFileNameSql(String typeBd) throws Exception;
}
