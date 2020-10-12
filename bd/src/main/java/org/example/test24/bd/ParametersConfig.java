package org.example.test24.bd;

import java.io.*;
import java.util.Properties;

public class ParametersConfig implements BaseData.Config {
    final private String fileNameConfig = "config.txt";

    private String portName;
    private TypeBaseDate typeBaseData;

    public ParametersConfig() {
        typeBaseData = TypeBaseDate.ERROR;
        portName = "";
    }

    @Override
    public Status load() throws Exception {
        Status status;
        Properties properties = new Properties();
        try { properties.load(new BufferedReader(new FileReader(fileNameConfig)));
        } catch (IOException e) { throw new Exception(e);
        }
        portName = properties.getProperty("CommPort", "COM2").toUpperCase();
        try {
            this.typeBaseData = TypeBaseDate.create(
                    properties.getProperty("DataBase").toUpperCase()
            );
            status = Status.OK;
        } catch (Exception exception) {
            status = Status.BASE_TYPE_ERROR;
            // set default
            this.typeBaseData = TypeBaseDate.MY_SQL;
        }
        return status;
    }
    @Override
    public Status save() throws BaseDataException {
        Properties properties = new Properties();
        Status result;
        properties.setProperty("CommPort", portName);
        properties.setProperty("DataBase", typeBaseData.codeToString());
        try {
            properties.store(new BufferedWriter(new FileWriter(fileNameConfig)), "config");
            result = Status.OK;
        } catch (IOException e) {
            result = Status.PARAMETERS_PASSWORD_ERROR;
            throw new BaseDataException("сохранение сонфигурации", e, result);
        }
        return result;
    }
    @Override
    public String getPortName() {
        return portName;
    }
    @Override
    public TypeBaseDate getTypeBaseData() {
        return typeBaseData;
    }
    @Override
    public void setPortName(String portName) {
        this.portName = portName;
    }
    @Override
    public void setTypeBaseData(TypeBaseDate typeBaseData) {
        this.typeBaseData = typeBaseData;
    }
    @Override
    public void setDefault() {
        portName = "com2";
        typeBaseData = TypeBaseDate.MY_SQL;
    }
}
