package org.example.test24.loader;

import org.example.test24.bd.BaseData;

import java.io.*;
import java.util.Properties;

import static org.example.test24.bd.BaseData.typeBaseDataCode;
import static org.example.test24.bd.BaseData.typeBaseDataString;

public class ParametersConfig {
    final public static int OK = 0;
    final public static int FILE_NOT_FOUND = 1;
    final public static int ERROR_LOAD = 2;
    final public static int ERROR_PARAMETERS = 3;
    final public static int ERROR_SAVE = 9;
    public enum Diagnostic {
        OK  (ParametersConfig.OK),
        FILE_NOT_FOUND  (ParametersConfig.FILE_NOT_FOUND),
        ERROR_LOAD  (ParametersConfig.ERROR_LOAD),
        ERROR_PARAMETERS    (ParametersConfig.ERROR_PARAMETERS),
        ERROR_SAVE  (ParametersConfig.ERROR_SAVE);
        private int diagnos;

        Diagnostic(int diagnos) {
            this.diagnos = diagnos;
        }

        public int getDiagnos() {
            return diagnos;
        }
    }

    private String fileNameConfig;
    private String portName;
    private BaseData.TypeBaseData typeBaseData;

    public ParametersConfig(String fileNameConfig) {
        this.fileNameConfig = fileNameConfig;
        typeBaseData = BaseData.TypeBaseData.ERROR;
        portName = null;
    }

    public String getPortName() {
        return portName;
    }
    public void setPortName(String portName) {
        this.portName = portName;
    }
    public BaseData.TypeBaseData getTypeBaseData() {
        return typeBaseData;
    }
    public void setTypeBaseData(BaseData.TypeBaseData typeBaseData) {
        this.typeBaseData = typeBaseData;
    }

    public Diagnostic load() {
        Diagnostic status;
        try {
            Properties properties = new Properties();
            properties.load(new BufferedReader(new FileReader(fileNameConfig)));
            typeBaseData = typeBaseDataCode(properties.getProperty("DataBase").toUpperCase());
            portName = properties.getProperty("CommPort").toUpperCase();
            status = Diagnostic.OK;
        } catch (FileNotFoundException e) {
            status = Diagnostic.FILE_NOT_FOUND;
        } catch (IOException e) {
            status = Diagnostic.ERROR_LOAD;
        }
        return status;
    }
    public void setDefault() {
        portName = "com2";
        typeBaseData = BaseData.TypeBaseData.MY_SQL;
    }

    public Diagnostic save() {
        Diagnostic status;
        if (typeBaseData == BaseData.TypeBaseData.ERROR || portName == null || portName == "") {
            status = Diagnostic.ERROR_PARAMETERS;
        } else {
            Properties properties = new Properties();
            properties.setProperty("CommPort", portName);
            properties.setProperty("DataBase", typeBaseDataString(typeBaseData));
            try {
                properties.store(new BufferedWriter(new FileWriter(fileNameConfig)), "config");
                status = Diagnostic.OK;
            } catch (IOException e) {
                status = Diagnostic.ERROR_SAVE;
            }
        }
        return status;
    }
}
