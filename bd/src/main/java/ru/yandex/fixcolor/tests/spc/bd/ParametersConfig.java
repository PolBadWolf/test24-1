package ru.yandex.fixcolor.tests.spc.bd;

import ru.yandex.fixcolor.tests.spc.bd.usertypes.Point;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.PointK;
import ru.yandex.fixcolor.tests.spc.lib.MyLogger;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

class ParametersConfig implements BaseData.Config {
    private static final String fileNameConfig = "config.txt";
    // ==================================================================
    // distance point 1
    private static final String nameDistancePoint1_val = "Distance_vol1";
    private static final String nameDistancePoint1_adc = "Distance_adc1";
    // distance point 2
    private static final String nameDistancePoint2_val = "Distance_vol2";
    private static final String nameDistancePoint2_adc = "Distance_adc2";
    // force point 1
    private static final String nameForcePoint1_vol = "Force_vol1";
    private static final String nameForcePoint1_adc = "Force_adc1";
    // force point 2
    private static final String nameForcePoint2_vol = "Force_vol2";
    private static final String nameForcePoint2_adc = "Force_adc2";
    // ==================================================================
    private String portName;
    private TypeBaseDate typeBaseData;
    // *** distance ***
    // distance point 1
    private Point distance_point1;
    // distance point 2
    private Point distance_point2;
    //
    private PointK distancePointK;
    // *** force ***
    private Point force_point1;
    private Point force_point2;
    private PointK force_pointK;

    public ParametersConfig() {
        typeBaseData = TypeBaseDate.ERROR;
        portName = "";
        distancePointK = new PointK(1.0, 0);
        force_pointK = new PointK(1.0, 0);
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
        // чтение точки 1
        try {
            distance_point1 = new Point(
                    Double.parseDouble(properties.getProperty(nameDistancePoint1_val)),
                    Integer.parseInt(properties.getProperty(nameDistancePoint1_adc))
            );
        } catch (Exception e) {
            MyLogger.myLog.log(Level.WARNING, "ошибка чтения калибровочной точки 1 дистанции", e);
            distance_point1 = new Point(0.0, 0);
        }
        // чтение точки 2
        try {
            distance_point2 = new Point(
                    Double.parseDouble(properties.getProperty(nameDistancePoint2_val)),
                    Integer.parseInt(properties.getProperty(nameDistancePoint2_adc))
            );
        } catch (Exception e) {
            MyLogger.myLog.log(Level.WARNING, "ошибка чтения калибровочной точки 2 дистанции", e);
            distance_point2 = new Point(1000.0, 1000);
        }
        // рендер
        distancePointK = PointK.render(distance_point1, distance_point2);
        // force point 1
        try {
            force_point1 = new Point(
                    Double.parseDouble(properties.getProperty(nameForcePoint1_vol)),
                    Integer.parseInt(properties.getProperty(nameForcePoint1_adc))
            );
        } catch (Exception e) {
            MyLogger.myLog.log(Level.WARNING, "ошибка чтения калибровочной точки 1 усилия", e);
            force_point1 = new Point(0.0, 0);
        }
        // force point 2
        try {
            force_point2 = new Point(
                    Double.parseDouble(properties.getProperty(nameForcePoint2_vol)),
                    Integer.parseInt(properties.getProperty(nameForcePoint2_adc))
            );
        } catch (Exception e) {
            MyLogger.myLog.log(Level.WARNING, "ошибка чтения калибровочной точки 2 усилия", e);
            force_point2 = new Point(1000.0, 1000);
        }
        // рендер
        force_pointK = PointK.render(force_point1, force_point2);
        return status;
    }
    @Override
    public Status save() throws BaseDataException {
        Properties properties = new Properties();
        Status result;
        properties.setProperty("CommPort", portName);
        properties.setProperty("DataBase", typeBaseData.codeToString());
        // distance point 1
        properties.setProperty(nameDistancePoint1_val, String.valueOf(distance_point1.value));
        properties.setProperty(nameDistancePoint1_adc, String.valueOf(distance_point1.adc));
        // distance point 2
        properties.setProperty(nameDistancePoint2_val, String.valueOf(distance_point2.value));
        properties.setProperty(nameDistancePoint2_adc, String.valueOf(distance_point2.adc));

        // force point 1
        properties.setProperty(nameForcePoint1_vol, String.valueOf(force_point1.value));
        properties.setProperty(nameForcePoint1_adc, String.valueOf(force_point1.adc));
        // force point 2
        properties.setProperty(nameForcePoint2_vol, String.valueOf(force_point2.value));
        properties.setProperty(nameForcePoint2_adc, String.valueOf(force_point2.adc));

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
        // дистанция точка 1
        distance_point1 = new Point(0.0, 0);
        // дистанция точка 2
        distance_point2 = new Point(1000.0, 1000);
        // distance point k
        distancePointK = PointK.render(distance_point1, distance_point2);
        // force point 1
        force_point1 = new Point(0.0, 0);
        // force point 2
        force_point2 = new Point(1000.0, 1000);
        // force point k
        force_pointK = PointK.render(force_point1, force_point2);
    }

    // set calib distance
    @Override
    public void setDistanceCalib(Point point1, Point point2) {
        distance_point1 = point1;
        distance_point2 = point2;
        distancePointK = PointK.render(distance_point1, distance_point2);
    }

    @Override
    public double getDistance_k() {
        return distancePointK.k;
    }

    @Override
    public double getDistance_offset() {
        return distancePointK.offset;
    }

    // distance point 1
    @Override
    public double getDistancePoint1_vol() {
        return distance_point1.value;
    }
    @Override
    public int getDistancePoint1_adc() {
        return distance_point1.adc;
    }

    // distance point 2
    @Override
    public double getDistancePoint2_vol() {
        return distance_point2.value;
    }
    @Override
    public int getDistancePoint2_adc() {
        return distance_point2.adc;
    }
    // Set force calib
    @Override
    public void setForceCalib(Point point1, Point point2) {
        force_point1 = point1;
        force_point2 = point2;
        force_pointK = PointK.render(force_point1, force_point2);
    }
    // gets
    @Override
    public double getForce_k() {
        return force_pointK.k;
    }
    @Override
    public double getForce_offset() {
        return force_pointK.offset;
    }
    // get force point 1
    @Override
    public Point getForcePoint1() {
        return force_point1;
    }
    // get force point 2
    @Override
    public Point getForcePoint2() {
        return force_point2;
    }
}
