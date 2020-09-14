package org.example.test24.bd;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

import static org.example.test24.bd.BaseData2Class.*;
import static org.example.test24.lib.MyLogger.myLog;

public class ParametersConfig implements BaseData.Config {
    final private String fileNameConfig = "config.txt";
    private BaseData.Status stat = BaseData.Status.OK;


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

    private String portName;
    private BaseData.TypeBaseDate typeBaseData;
    private Diagnostic status;

    public ParametersConfig(String fileNameConfig) {
        //this.fileNameConfig = fileNameConfig;
        typeBaseData = BaseData.TypeBaseDate.ERROR;
        portName = "";
    }

    @Override
    public BaseData.Status load1() {
        final BaseData.TypeBaseDate[] typeBaseDate = new BaseData.TypeBaseDate[1];
        BaseData.Status status;
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader(fileNameConfig)));
        } catch (IOException e) {
            myLog.log(Level.SEVERE, "ошибка чтения файла конфигурации", e);
            e.printStackTrace();
        }
        portName = properties.getProperty("CommPort", "COM2").toUpperCase();
        status = BaseData.TypeBaseDate.create(
                properties.getProperty("DataBase").toUpperCase(),
                t -> typeBaseDate[0] = t
        );
        this.typeBaseData = typeBaseDate[0];
        return status;
    }
    @Override
    public BaseData.Status save1() {
        return null;
    }
    @Override
    public String getPortName() {
        return portName;
    }
    @Override
    public BaseData.TypeBaseDate getTypeBaseData() {
        return typeBaseData;
    }
    @Override
    public void setPortName(String portName) {
        this.portName = portName;
    }
    @Override
    public void setTypeBaseData(BaseData.TypeBaseDate typeBaseData) {
        this.typeBaseData = typeBaseData;
    }

    public Diagnostic getStatus() {
        return status;
    }

    public Diagnostic load() {
        try {
            Properties properties = new Properties();
            properties.load(new BufferedReader(new FileReader(fileNameConfig)));
            //typeBaseData = typeBaseDataCode(properties.getProperty("DataBase").toUpperCase());
            portName = properties.getProperty("CommPort").toUpperCase();
            status = Diagnostic.OK;
        } catch (FileNotFoundException e) {
            portName = "";
            status = Diagnostic.FILE_NOT_FOUND;
        } catch (IOException e) {
            portName = "";
            status = Diagnostic.ERROR_LOAD;
        }
        if (typeBaseData == BaseData.TypeBaseDate.ERROR) {
            status = Diagnostic.ERROR_PARAMETERS;
        }
        return status;
    }


    public void setDefault() {
        portName = "com2";
        typeBaseData = BaseData.TypeBaseDate.MY_SQL;
    }

    public Diagnostic save() {
        if (typeBaseData == BaseData.TypeBaseDate.ERROR || portName == null || portName == "") {
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
