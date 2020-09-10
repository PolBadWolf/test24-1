package org.example.test24.loader;

import org.example.test24.bd.BaseData;

import java.io.*;
import java.util.Properties;

import static org.example.test24.bd.BaseDataClass.*;

public class ParametersConfig {
    final public static int OK = 0;
    final public static int FILE_NOT_FOUND = 1;
    final public static int FILE_NOT_SPECIFIED = 2;
    final public static int ERROR_LOAD = 3;
    final public static int ERROR_PARAMETERS = 4;
    final public static int ERROR_SAVE = 5;
    final public static int ERROR = 99;
    public enum Diagnostic {
        OK  (ParametersConfig.OK),
        FILE_NOT_FOUND  (ParametersConfig.FILE_NOT_FOUND),
        FILE_NOT_SPECIFIED  (ParametersConfig.FILE_NOT_SPECIFIED),
        ERROR_LOAD  (ParametersConfig.ERROR_LOAD),
        ERROR_PARAMETERS    (ParametersConfig.ERROR_PARAMETERS),
        ERROR_SAVE  (ParametersConfig.ERROR_SAVE),
        ERROR  (ParametersConfig.ERROR);
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
    private Diagnostic status;

    public ParametersConfig(String fileNameConfig) {
        this.fileNameConfig = fileNameConfig;
        typeBaseData = BaseData.TypeBaseData.ERROR;
        portName = "";
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
    public Diagnostic getStatus() {
        return status;
    }

    public Diagnostic load() {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedReader(new FileReader(fileNameConfig)));
            typeBaseData = typeBaseDataCode(properties.getProperty("DataBase").toUpperCase());
            portName = properties.getProperty("CommPort").toUpperCase();
            status = Diagnostic.OK;
        } catch (FileNotFoundException e) {
            portName = "";
            status = Diagnostic.FILE_NOT_FOUND;
        } catch (IOException e) {
            portName = "";
            status = Diagnostic.ERROR_LOAD;
        }
        if (typeBaseData == BaseData.TypeBaseData.ERROR) {
            status = Diagnostic.ERROR_PARAMETERS;
        }
        return status;
    }
    public void setDefault() {
        portName = "com2";
        typeBaseData = BaseData.TypeBaseData.MY_SQL;
    }

    public Diagnostic save() {
        if (typeBaseData == BaseData.TypeBaseData.ERROR || portName == null || portName == "") {
            status = Diagnostic.ERROR_PARAMETERS;
        } else {
            Properties properties = new Properties();
            properties.setProperty("CommPort", portName);
            properties.setProperty("DataBase", typeBaseDataString(typeBaseData.getCodeTypeBaseData()));
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
