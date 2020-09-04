package org.example.test24.loader;

import org.example.test24.bd.*;
import org.example.test24.RS232.CommPort;
import org.example.test24.RS232.BAUD;
import org.example.test24.loader.dialog.FrameCallBack;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.runner.Runner;
import org.example.test24.screen.ScreenFx;

import java.util.function.Consumer;


public class MainClass {
    final public String fileNameConfig = "config.txt";
    final public String fileNameMsSql = "ms_sql.txt";
    final public String fileNameMySql = "my_sql.txt";
    final public String[] fileNameSql = {fileNameMsSql, fileNameMySql};
    // ===============================================
    // модули
    private ScreenFx screenFx;
    private Runner runner;
    private CommPort commPort;
    private BaseData1 bdSql;
    private StartFrame startFrame;
    // ===============================================
    private BaseData connBd;
    // ===============================================
    private void close() {
        if (screenFx != null) {
            screenFx.exitApp();
            screenFx = null;
        }
        if (commPort != null) {
            commPort.Close();
            commPort = null;
        }
        if (runner != null) {
            runner.Close();
            runner = null;
        }
        System.exit(0);
    }
    private void screenCloser() {
        close();
    }
    private void runnerCloser() {
        close();
    }
    private void commPortCloser() {
        close();
    }

    // ===============================================
    protected ParametersConfig parametersConfig;

    public static void main(String[] args) {
        new MainClass().start();
    }

    private void start() {
        // создание объекта для БД
        connBd = new BaseDataClass(new BaseData.CallBack() {
        });
        // создание основных объектов
        screenFx = ScreenFx.init(o->screenCloser());
        runner = Runner.main(o->runnerCloser());
        commPort = CommPort.main(o->commPortCloser());
        // загрузка начальной конфигурации
        switch (requestParametersConfig(fileNameConfig, parameters -> {
            parametersConfig = parameters;
        })) {
            case FILE_NOT_FOUND:
                System.out.println("Файл конфигурации не найден");
                // параметры по умолчанию
                parametersConfig.setDefault();
                break;
            case FILE_NOT_SPECIFIED:
                System.out.println("Файл конфигурации не указан");
                // параметры по умолчанию
                parametersConfig.setDefault();
                break;
            case ERROR_LOAD:
                System.out.println("Ошибка загрузки файла конфигурации");
                // параметры по умолчанию
                parametersConfig.setDefault();
                break;
            case ERROR_PARAMETERS:
                System.out.println("Ошибка параметров файла конфигурации");
                break;
        }

        startFrame = StartFrame.main(false, new StartFrameCallBack());


        //ParametersConfig.Diagnostic result = parametersConfig.load();
        /*
        int testStat1 = 99, testStat2 = 99, testStat3, testStat4;
        String[] listBd;
        UserClass[] listUsers;

        testStat1 = connBd.createTestConnect(org.example.test24.bd.BaseData.TypeBaseData.MY_SQL,
                new org.example.test24.bd.BaseData.Parameters("127.0.0.1", "3306", "root", "My!22360", "bas1")
        );
        if (testStat1 == org.example.test24.bd.BaseData.OK) {
            listBd = connBd.testConnectListBd();
        } else {
            listBd = null;
        }
        testStat2 = connBd.createWorkConnect(org.example.test24.bd.BaseData.TypeBaseData.MY_SQL,
                new org.example.test24.bd.BaseData.Parameters("127.0.0.1", "3306", "root", "My!22360", "bas2")
        );
        if (testStat2 == org.example.test24.bd.BaseData.OK) {
            try {
                listUsers = connBd.getListUsers(false);
            } catch (Exception e) {
                e.printStackTrace();
                listUsers = new UserClass[0];
            }
        } else {
            listUsers = new UserClass[0];
        }
        System.out.println("test conn: " + testStat1);
        Arrays.stream(listBd).iterator().forEachRemaining(s-> System.out.println(s));
        System.out.println("work conn: " + testStat2);
        Arrays.stream(listUsers).iterator().forEachRemaining(userClass -> System.out.println(userClass.name));
        */
        //
//        String portName;
        //parameters = getConfig();
        //portName = parameters[1];

        /*try {
            baseData = new org.example.test24.loader.BaseDataXXX(new BaseDataCallBack());
            baseData.initBaseData(parameters1.getTypeBaseData());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*try {
            while (startFrame != null) {
                Thread.yield();
                Thread.sleep(500);
            }
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }*/

        /*int checkComm = commPort.Open((bytes, lenght) -> runner.reciveRsPush(bytes, lenght), parametersConfig.getPortName(), BAUD.baud57600);
        if (checkComm != CommPort.INITCODE_OK) {
            errorCommMessage(checkComm, commPort);
            System.exit(0);
        }*/

        /*try {
            bdSql = BaseData1.init(parametersConfig.getTypeBaseData().getTypeBaseDataString(), fileNameSql);
            bdSql.getConnect();
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }*/

        /*screenFx.main();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }
        runner.init(bdSql, commPort, MainFrame.mainFrame);
        commPort.ReciveStart();*/
    }

    // загрузка конфигурации
    private ParametersConfig.Diagnostic requestParametersConfig(String fileNameConfig, Consumer<ParametersConfig> parametersConfig) {
        ParametersConfig parameters = new ParametersConfig(fileNameConfig);
        ParametersConfig.Diagnostic result = parameters.load();
        if (parametersConfig != null) parametersConfig.accept(parameters);
        return result;
    }

    private void errorCommMessage(int checkComm, CommPort commPort) {
        switch (checkComm) {
            case CommPort.INITCODE_ERROROPEN:
                System.out.println("ошибка открытия порта");
                break;
            case CommPort.INITCODE_NOTEXIST:
                System.out.println("указанный порт отсутствует");
                System.out.println("имеющиеся порты в системе:");
                for (String name : commPort.getListPortsName()) {
                    System.out.println(name);
                }
                break;
        }
    }

    private boolean checkCommPort(String portName) {
        int ch =  commPort.Open(null, portName, BAUD.baud57600);
        commPort.Close();
        return ch == CommPort.INITCODE_OK;
    }
    private class StartFrameCallBack implements FrameCallBack {
        // чтение параметров из конфига
        @Override
        public int requestParametersConfig(Consumer<ParametersConfig> configParameters) {
            if (parametersConfig == null) {
                synchronized (parametersConfig) {
                    if (parametersConfig == null) {
                        switch (requestParametersConfig(fileNameConfig, parameters -> {
                            parametersConfig = parameters;
                        })) {
                            case ParametersConfig.FILE_NOT_FOUND:
                                System.out.println("Файл конфигурации не найден");
                                // параметры по умолчанию
                                parametersConfig.setDefault();
                                break;
                            case ParametersConfig.FILE_NOT_SPECIFIED:
                                System.out.println("Файл конфигурации не указан");
                                // параметры по умолчанию
                                parametersConfig.setDefault();
                                break;
                            case ParametersConfig.ERROR_LOAD:
                                System.out.println("Ошибка загрузки файла конфигурации");
                                // параметры по умолчанию
                                parametersConfig.setDefault();
                                break;
                            case ParametersConfig.ERROR_PARAMETERS:
                                System.out.println("Ошибка параметров файла конфигурации");
                                break;
                        }
                    }
                }
            }
        }
        public ParametersConfig getParametersConfig() throws Exception {
            if (parametersConfig == null) throw new Exception("отсутствуют параметры конфигурации");
            return parametersConfig;
        }
        // ================================== работа с БД ====================================
        // чтение параметров
        @Override
        public int requestParametersSql(BaseData.TypeBaseData typeBaseData, Consumer<ParametersSql> sql) {
            if (typeBaseData == BaseData.TypeBaseData.ERROR) {
                return ParametersSql.UNKNOWN_ERROR;
            }
            ParametersSql parametersSql = new ParametersSql(fileNameSql[typeBaseData.getTypeBaseData()], typeBaseData);
            parametersSql.load();
            sql.accept(parametersSql);
            return ParametersSql.OK;
        }


        // установка тестого соединения
        @Override
        public int createTestConnectBd(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters) {
            if (connBd == null || typeBaseData == BaseData.TypeBaseData.ERROR) {
                return BaseData.CONNECT_ERROR;
            }
            return connBd.createTestConnect(typeBaseData, parameters);
        }
        // список доступных БД из тестового соединения
        @Override
        public boolean requestListBdFromTestConnect(Consumer<String[]> list) {
            return connBd.requestListBdFromTestConnect(list);
        }
        // проверка структуры БД
        @Override
        public int testConnectCheckStructure(String base) {
            return connBd.testConnectCheckStructure(base);
        }
        // создание рабочего соединения
        @Override
        public int createWorkConnect(BaseData.TypeBaseData typeBaseData, BaseData.Parameters parameters) {
            if (connBd == null || typeBaseData == BaseData.TypeBaseData.ERROR) {
                return BaseData.CONNECT_ERROR;
            }
            return connBd.createWorkConnect(typeBaseData, parameters);
        }
        // загрузка пользователей
        @Override
        public UserClass[] getListUsers(boolean actual) {
            // получение списка
            UserClass[] listUsers;
            try {
                listUsers = connBd.getListUsers(actual);
            } catch (Exception e) {
                listUsers = null;
            }
            return listUsers;
        }
        // установка нового пароля пользователя
        @Override
        public boolean setUserNewPassword(UserClass user, String newPassword) {
            if (connBd == null) return false;
            return connBd.setUserNewPassword(user, newPassword);
        }
        // ==================================== работа к ком портом ====================================
        // чтение comm port из конфига
        @Override
        public boolean requestCommPortNameFromConfig(Consumer<String> portName) {
            if (parametersConfig == null) return false;
            String name = parametersConfig.getPortName();
            if (name == null) return false;
            portName.accept(name);
            return true;
        }
        // проверка Comm Port на валидность
        @Override
        public boolean checkCommPort(String portName) {
            return MainClass.this.checkCommPort(portName);
        }
        // загрузка списка ком портов в системе
        @Override
        public String[] getComPortNameList() {
            return commPort.getListPortsName();
        }
        // --------------------------------------------------------
        @Override
        public void closeFrame() {
            MainClass.this.startFrame = null;
        }
    }

    /*private class TuningFrameCallBack implements TuningFrame.CallBackToMainClass {
        @Override
        public CommPort getCommPort() {
            return commPort;
        }

        @Override
        public void saveConfigCommPort(String portName) {
            parametersConfig.setPortName(portName);
            parametersConfig.save();
        }

        @Override
        public void saveConfigTypeBaseData(BaseData.TypeBaseData typeBaseData) {
            parametersConfig.setTypeBaseData(typeBaseData);
            parametersConfig.save();
        }

        @Override
        public String loadConfigCommPort() {
            return parametersConfig.getPortName();
        }

        @Override
        public BaseData.TypeBaseData loadConfigTypeBaseData() {
            return parametersConfig.getTypeBaseData();
        }

        @Override
        public String[] getFilesNameSql() {
            return fileNameSql;
        }

        @Override
        public String getFileNameSql(String typeBd) throws Exception {
            String fileName;
            switch (typeBd) {
                case "MS_SQL" :
                    fileName = fileNameMsSql;
                    break;
                case "MY_SQL" :
                    fileName = fileNameMySql;
                    break;
                default:
                    throw new Exception("неизвестный тип BD");
            }
            return fileName;
        }
    }*/
}
