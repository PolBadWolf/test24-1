package org.example.test24.loader;

import org.example.test24.lib.MyLogger;
import org.example.test24.bd.*;
import org.example.test24.RS232.CommPort;
import org.example.test24.RS232.BAUD;
import org.example.test24.loader.dialog.FrameCallBack;
import org.example.test24.loader.dialog.StartFrame;
import org.example.test24.runner.Runner;
import org.example.test24.screen.ScreenFx;

import java.util.logging.Level;


public class MainClass extends MainClassRequest {
    public static void main(String[] args) {
        new MyLogger(Level.ALL, Level.OFF);
        Thread.currentThread().setName("Main class thread");
        new MainClass().start();
    }
    private void start() {
        //myLog.log(Level.INFO, "я в Main.start()");
//        myLog.log(Level.WARNING, "ww", new Exception("123"));
        //BaseData.Parameters parameters = BaseData.Parameters.create(BaseData.TypeBaseDate.MY_SQL);
        //BaseData.Status result = parameters.load();

        // создание объекта для БД
        //connBd = new BaseData2Class(/*new BaseData2.CallBack() {}*/);
        //connBd = BaseData.
        // создание основных объектов
        screenFx = ScreenFx.init(o->screenCloser());
        runner = Runner.main(o->runnerCloser());
        commPort = CommPort.main();

        // пуск
        try {
            startFrame = StartFrame.main(false, new StartFrameCallBack());
        } catch (Exception exception) {
            startFrame = null;
        }
        //myLog.log(Level.WARNING, "sdsd", new Exception("bla-bla-bla"));

        /*
        int testStat1 = 99, testStat2 = 99, testStat3, testStat4;
        String[] listBd;
        User[] listUsers;

        testStat1 = connBd.createTestConnect(org.example.test24.bd.BaseData2.TypeBaseData.MY_SQL,
                new org.example.test24.bd.BaseData2.Parameters("127.0.0.1", "3306", "root", "My!22360", "bas1")
        );
        if (testStat1 == org.example.test24.bd.BaseData2.OK) {
            listBd = connBd.testConnectListBd();
        } else {
            listBd = null;
        }
        testStat2 = connBd.createWorkConnect(org.example.test24.bd.BaseData2.TypeBaseData.MY_SQL,
                new org.example.test24.bd.BaseData2.Parameters("127.0.0.1", "3306", "root", "My!22360", "bas2")
        );
        if (testStat2 == org.example.test24.bd.BaseData2.OK) {
            try {
                listUsers = connBd.getListUsers(false);
            } catch (Exception e) {
                e.printStackTrace();
                listUsers = new User[0];
            }
        } else {
            listUsers = new User[0];
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










    // ===============================================
    private void close() {
        if (screenFx != null) {
            screenFx.exitApp();
            screenFx = null;
        }
        if (commPort != null) {
            commPort.close();
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



    // загрузка конфигурации
    /*private ParametersConfig.Diagnostic requestParametersConfig(String fileNameConfig, Consumer<ParametersConfig> parametersConfig) {
        ParametersConfig parameters = new ParametersConfig(fileNameConfig);
        ParametersConfig.Diagnostic result = parameters.load();
        if (parametersConfig != null) parametersConfig.accept(parameters);
        return result;
    }*/

    private void errorCommMessage(int checkComm, CommPort commPort) {
        switch (checkComm) {
            case CommPort.INITCODE_ERROROPEN:
                System.out.println("ошибка открытия порта");
                break;
            case CommPort.INITCODE_NOTEXIST:
                System.out.println("указанный порт отсутствует");
                System.out.println("имеющиеся порты в системе:");
                for (String name : CommPort.getListPortsName()) {
                    System.out.println(name);
                }
                break;
        }
    }

    private boolean checkCommPort(String portName) {
        CommPort.PortStat ch =  commPort.open(null, portName, BAUD.baud57600);
        commPort.close();
        return ch == CommPort.PortStat.INITCODE_OK;
    }
    private class StartFrameCallBack implements FrameCallBack {
        // чтение параметров из конфига
        @Override
        public ParametersConfig getParametersConfig(){
            return MainClass.this.getParametersConfig();
        }
        // создание объекта параметров соединения с БД
        @Override
        public ParametersSql2 createParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception {
            return MainClass.this.createParametersSql(typeBaseData);
        }

        // запрос параметров соединения с БД
        @Override
        public ParametersSql2 requestParametersSql(BaseData2.TypeBaseData typeBaseData) throws Exception {
            return MainClass.this.requestParametersSql(typeBaseData);
        }
        // -----------------------------------------------------------
        // создание тестого соединения
        @Override
        public BaseData2.Status createTestConnectBd(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
            return MainClass.this.createTestConnectBd(typeBaseData, parameters);
        }
        // тестовое соединение проверка структуры БД
        @Override
        public BaseData2.Status checkCheckStructureBd(String base) {
            return MainClass.this.checkCheckStructureBd(base);
        }
        // -----------------------------------------------------------
        // создание рабочего соединения
        @Override
        public BaseData2.Status createWorkConnect(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
            return MainClass.this.createWorkConnect(typeBaseData, parameters);
        }
        // чтение списка пользователей
        @Override
        public User[] getListUsers(boolean actual) throws Exception {
            return MainClass.this.getListUsers(actual);
        }

        @Override
        public boolean isCheckCommPort(boolean statMainWork, String portName) throws Exception {
            return MainClass.this.isCheckCommPort(statMainWork, portName);
        }

        @Override
        public String[] getListBd() throws Exception {
            return null;  //connBd.getListBd();
        }


        /*
        // ================================== работа с БД ====================================
        // чтение параметров
        @Override
        public int requestParametersSql(BaseData2.TypeBaseData typeBaseData, Consumer<ParametersSql2> sql) {
            if (typeBaseData == BaseData2.TypeBaseData.ERROR) {
                return ParametersSql2.UNKNOWN_ERROR;
            }
            ParametersSql2 parametersSql = new ParametersSql2(fileNameSql[typeBaseData.getTypeBaseData()], typeBaseData);
            parametersSql.load();
            sql.accept(parametersSql);
            return ParametersSql2.OK;
        }


        // установка тестого соединения
        @Override
        public int createTestConnectBd(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
            if (connBd == null || typeBaseData == BaseData2.TypeBaseData.ERROR) {
                return BaseData2.CONNECT_ERROR;
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
        public int createWorkConnect(BaseData2.TypeBaseData typeBaseData, BaseData2.Parameters parameters) {
            if (connBd == null || typeBaseData == BaseData2.TypeBaseData.ERROR) {
                return BaseData2.CONNECT_ERROR;
            }
            return connBd.createWorkConnect(typeBaseData, parameters);
        }
        // загрузка пользователей
        @Override
        public User[] getListUsers(boolean actual) {
            // получение списка
            User[] listUsers;
            try {
                listUsers = connBd.getListUsers(actual);
            } catch (Exception e) {
                listUsers = null;
            }
            return listUsers;
        }
        // установка нового пароля пользователя
        @Override
        public boolean setUserNewPassword(User user, String newPassword) {
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
        */
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
        public void saveConfigTypeBaseData(BaseData2.TypeBaseData typeBaseData) {
            parametersConfig.setTypeBaseData(typeBaseData);
            parametersConfig.save();
        }

        @Override
        public String loadConfigCommPort() {
            return parametersConfig.getPortName();
        }

        @Override
        public BaseData2.TypeBaseData loadConfigTypeBaseData() {
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
