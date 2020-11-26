package ru.yandex.fixcolor.tests.spc.bd;

import java.io.*;
import java.util.Properties;

class ParametersConfig implements BaseData.Config {
    private static final String fileNameConfig = "config.txt";

    private String portName;
    private TypeBaseDate typeBaseData;
    private double distance_k;
    private double distance_offset;
    private double weight_k;
    private double weight_offset;

    public ParametersConfig() {
        typeBaseData = TypeBaseDate.ERROR;
        portName = "";
        distance_k = weight_k = 1.0;
        distance_offset = weight_offset = 0;
    }

    @Override
    public Status load() throws BaseDataException {
        Status status;
        Properties properties = new Properties();
        try { properties.load(new BufferedReader(new FileReader(fileNameConfig)));
        } catch (IOException e) { throw new BaseDataException(e, Status.PARAMETERS_ERROR);
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
        try { distance_k = Double.parseDouble(properties.getProperty("Distance_K"));
        } catch (Exception e) { distance_k = 1.0; }
        try { distance_offset = Double.parseDouble(properties.getProperty("Distance_Offset"));
        } catch (Exception e) { distance_offset = 0; }
        try { weight_k = Double.parseDouble(properties.getProperty("Weight_K"));
        } catch (Exception e) { weight_k = 1.0; }
        try { weight_offset = Double.parseDouble(properties.getProperty("Weight_Offset"));
        } catch (Exception e) { weight_offset = 0; }
        return status;
    }
    @Override
    public Status save() throws BaseDataException {
        Properties properties = new Properties();
        Status result;
        properties.setProperty("CommPort", portName);
        properties.setProperty("DataBase", typeBaseData.codeToString());
        properties.setProperty("Distance_K", String.valueOf(distance_k));
        properties.setProperty("Distance_Offset", String.valueOf(distance_offset));
        properties.setProperty("Weight_K", String.valueOf(weight_k));
        properties.setProperty("Weight_Offset", String.valueOf(weight_offset));
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
        distance_k = weight_k = 1.0;
        distance_offset = weight_offset = 0;
    }

    @Override
    public double getDistance_k() {
        return distance_k;
    }

    @Override
    public void setDistance_k(double distance_k) {
        this.distance_k = distance_k;
    }

    @Override
    public double getDistance_offset() {
        return distance_offset;
    }

    @Override
    public void setDistance_offset(double distance_offset) {
        this.distance_offset = distance_offset;
    }

    @Override
    public double getWeight_k() {
        return weight_k;
    }

    @Override
    public void setWeight_k(double weight_k) {
        this.weight_k = weight_k;
    }

    @Override
    public double getWeight_offset() {
        return weight_offset;
    }

    @Override
    public void setWeight_offset(double weight_offset) {
        this.weight_offset = weight_offset;
    }
}
