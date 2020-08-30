package org.example.test24.loader;

import java.io.*;
import java.util.Properties;

public class ParametersConfig {
    final public static int MS_SQL = 0;
    final public static int MY_SQL = 1;
    final public static int ERROR = 99;
    public enum TypeBaseData {
        MS_SQL  (ParametersConfig.MS_SQL),
        MY_SQL  (ParametersConfig.MY_SQL),
        ERROR   (ParametersConfig.ERROR);
        private int typeBaseData;

        TypeBaseData(int typeBaseData) {
            this.typeBaseData = typeBaseData;
        }
        public int getTypeBaseData() {
            return typeBaseData;
        }
        public String getTypeBaseDataString() {
            return typeBaseDataString(typeBaseData);
        }
    }
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
    public static TypeBaseData typeBaseDataCode(String nameTypeBaseData) {
        TypeBaseData typeBaseData;
        switch (nameTypeBaseData.toUpperCase()) {
            case "MS_SQL":
                typeBaseData = TypeBaseData.MS_SQL;
                break;
            case "MY_SQL":
                typeBaseData = TypeBaseData.MY_SQL;
                break;
            default:
                typeBaseData = TypeBaseData.ERROR;
        }
        return typeBaseData;
    }
    public static String typeBaseDataString(TypeBaseData codeTypeBaseData) {
        String stroka;
        switch (codeTypeBaseData) {
            case MS_SQL:
                stroka = "MS_SQL";
                break;
            case MY_SQL:
                stroka = "MY_SQL";
                break;
            default:
                stroka = "ERROR";
        }
        return stroka;
    }
    private static String typeBaseDataString(int codeTypeBaseData) {
        String stroka;
        switch (codeTypeBaseData) {
            case MS_SQL:
                stroka = "MS_SQL";
                break;
            case MY_SQL:
                stroka = "MY_SQL";
                break;
            default:
                stroka = "ERROR";
        }
        return stroka;
    }

    private String fileNameConfig;
    private String portName;
    private TypeBaseData typeBaseData;

    public ParametersConfig(String fileNameConfig) {
        this.fileNameConfig = fileNameConfig;
        typeBaseData = TypeBaseData.ERROR;
        portName = null;
    }

    public String getPortName() {
        return portName;
    }
    public void setPortName(String portName) {
        this.portName = portName;
    }
    public TypeBaseData getTypeBaseData() {
        return typeBaseData;
    }
    public void setTypeBaseData(TypeBaseData typeBaseData) {
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
        typeBaseData = TypeBaseData.MY_SQL;
    }

    public Diagnostic save() {
        Diagnostic status;
        if (typeBaseData == TypeBaseData.ERROR || portName == null || portName == "") {
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
