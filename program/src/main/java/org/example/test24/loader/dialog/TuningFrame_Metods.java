package org.example.test24.loader.dialog;

import org.example.test24.lib.MyUtil;
import org.example.test24.RS232.CommPort;
import org.example.test24.bd.BaseData2;
import org.example.test24.bd.ParametersSql2;
import org.example.test24.bd.UserClass;
import org.example.test24.loader.ParametersConfig;

import javax.swing.*;
import java.util.Arrays;

class TuningFrame_Metods extends TuningFrame_Vars {
    // =============================================================================================================
    protected void onOffButtonOk() { System.out.println("bbbbbbb"); }
    protected void onButtonOk() { System.out.println("bbbbbbb"); }
    protected void offButtonOk() { System.out.println("bbbbbbb"); }
    //
    protected void onOffButtonTest() { System.out.println("bbbbbbb"); }
    protected void onButtonTest() { System.out.println("bbbbbbb"); }
    protected void offButtonTest() { System.out.println("bbbbbbb"); }
    //
    protected void onOffButtonSave() { System.out.println("bbbbbbb"); }
    protected void onButtonSave() { System.out.println("bbbbbbb"); }
    protected void offButtonSave() { System.out.println("bbbbbbb"); }
    //
    protected void onOffButtonEditUsers() { System.out.println("bbbbbbb"); }
    protected void onButtonEditUsers() { System.out.println("bbbbbbb"); }
    protected void offButtonEditUsers() { System.out.println("bbbbbbb"); }
    //
    protected void onOffButtonEditPushers() { System.out.println("bbbbbbb"); }
    protected void onButtonEditPushers() { System.out.println("bbbbbbb"); }
    protected void offButtonEditPushers() { System.out.println("bbbbbbb"); }
    // =============================================================================================================
    // загрузка начальных параметров
    protected void loadBeginerParameters() {
        ParametersConfig config;
        ParametersSql2 parametersSql = null;
        BaseData2.Status resultBaseData;
        int parametersSqlError;
        //
        // запрос конфигурации
        config = callBack.getParametersConfig();
        // тип БД
        if (config.getTypeBaseData() == BaseData2.TypeBaseData.ERROR) {
            System.out.println("ошибка типа базы данных: " + config.getTypeBaseData().toString());
            config.setTypeBaseData(BaseData2.TypeBaseData.MY_SQL);
        }
        // загрузка параметров соединения с БД
        try {
            parametersSql = callBack.createParametersSql(config.getTypeBaseData());
            ParametersSql2.Status status = parametersSql.load();
            if (status == ParametersSql2.Status.OK) {
                parametersSqlError = 0;
            } else {
                parametersSql.setDefault();
                parametersSqlError = 1;
            }
        } catch (Exception e) { // ошибка типа базы данных:
            e.printStackTrace();
            parametersSqlError = 1;
        }
        if (parametersSqlError == 0) {
            // установка тестового соединения
            resultBaseData = callBack.createTestConnectBd(
                    parametersSql.typeBaseData,
                    new BaseData2.Parameters(
                            parametersSql.urlServer,
                            parametersSql.portServer,
                            parametersSql.user,
                            parametersSql.password,
                            parametersSql.dataBase
                    )
            );
            if (resultBaseData == BaseData2.Status.OK) {
                // создание рабочего соединения
                resultBaseData = callBack.createWorkConnect(
                        parametersSql.typeBaseData,
                        new BaseData2.Parameters(
                                parametersSql.urlServer,
                                parametersSql.portServer,
                                parametersSql.user,
                                parametersSql.password,
                                parametersSql.dataBase
                        )
                );
                if (resultBaseData == BaseData2.Status.OK) {
                    // чтение списка пользователей
                    try {
                        listUsers = callBack.getListUsers(true);
                    } catch (Exception e) {
                        System.out.println("Ошибка чтения списка пользователей: " + e.getMessage());
                        listUsers = new UserClass[0];
                    }
                } else {
                    System.out.println("ошибка установки рабочего соединения: " + resultBaseData.toString());
                    listUsers = new UserClass[0];
                }
            } else { // ошибка установки тестового соединения
                System.out.println("ошибка установки тестового соединения: " + resultBaseData.toString());
                // пустой список пользователей
                listUsers = new UserClass[0];
                // ошибка структкры БД
                flCheckSql = false;
                // список доступных БД
                listBaseData = new String[0];
            }
            // проверка структуры БД
            resultBaseData = callBack.checkCheckStructureBd(parametersSql.dataBase);
            if (resultBaseData == BaseData2.Status.OK) {
                flCheckSql = true;
            } else {
                System.out.println("нарушена целостность структуры БД: " + resultBaseData.toString());
                flCheckSql = false;
            }
            // список доступных БД
            try {
                listBaseData = callBack.getListBd();
            } catch (Exception exception) {
                exception.printStackTrace();
                listBaseData = new String[0];
            }
        } else {
            // =============== ошибка параметров соединения с БД
            // нарушена целостность структуры БД
            flCheckSql = false;
            // создать пустой список пользователей
            listUsers = new UserClass[0];
            // пустой список дотупных БД
            listBaseData = new String[0];
        }
        // чтение списка пользователей из нового соединения
        this.parametersSql = parametersSql;
        // ****************************************************************************************
        // проверка ком порта
        try {
            flCheckCommPort = callBack.isCheckCommPort(statMainWork, config.getPortName());
            commPortName = config.getPortName();
            commPortNameList = CommPort.getListPortsName();
        } catch (Exception e) {
            System.out.println("Ошибка поверки ком порта: " + e.getMessage());
            flCheckCommPort = false;
            commPortName = "";
            commPortNameList = new String[0];
        }
        // ---
    }
    // проверка CommPort
    protected boolean isValidCommPort(String portName) {
        /*CommPort commPort = CommPort.main(
                (o)->{},
                portName
                );*/
        return false;
    }
    // =============================================================================================================
    protected void setComponentCommPort(String[] listCommPort, String defaultCommPort) {
        comboBoxCommPort.removeAllItems();
        Arrays.stream(listCommPort).sorted((a, b) -> a.compareTo(b)).forEach(s -> comboBoxCommPort.addItem(s));
        comboBoxCommPort.setSelectedItem(defaultCommPort);
    }
    protected void setComponentBaseData(ParametersSql2 parametersSql) {
        // тип БД
        comboBoxTypeBd.setSelectedItem(parametersSql.typeBaseData.toString());
        // параметры подключения
        fieldParamServerIP.setText(parametersSql.urlServer);
        fieldParamServerPort.setText(parametersSql.portServer);
        fieldParamServerLogin.setText(parametersSql.user);
        fieldParamServerPassword.setText(parametersSql.password);
    }
    // установка компонентов в начальное положение
    protected void setComponentsBegin() {
        // ком порт
        setComponentCommPort(commPortNameList, commPortName);
        labelPortCurrent.setText(commPortName);
        textCommPortStatus.setText("");
        // БД
        setComponentBaseData(parametersSql);
        textTypeBdStatus.setText(parametersSql.typeBaseData.toString());
        // список БД
        try { MyUtil.<String>loadToComboBox(listBaseData, comboBoxListBd); } catch (Exception e) {
            System.out.println("Ошибка загрузки списка БД в comboBoxListBd: " + e.getMessage());
        }
        //
        // установка начального состояния кнопок по основным параметрам
        setButtonBegin();
    }
    // установка начального состояния кнопок по основным параметрам
    private void setButtonBegin() {
        // если БД в порядке
        if (flCheckSql && listBaseData.length > 0) {
            onButtonEditUsers();
            onButtonEditPushers();
        } else {
            offButtonEditUsers();
            offButtonEditPushers();
        }
        // разрешение кнопки тест
        if (listBaseData.length > 0) onButtonTest();
        // запрет кнопки save
        offButtonSave();
        // разрешение кнопки ок
        onOffButtonOk();
    }
    // =============================================================================================================
    // выбран comm port
    protected void selectCommPort(JComboBox comboBox) {
        if (lockBegin)  return;
//        threadSkeepOn = false;
        //checkStatusComp();
        //outStatus();
        // разрешение кнопки ок
        onOffButtonOk();
        // сохранить
        //if (chCheckCommPort == CommPort.PortStat.INITCODE_OK) {
            // callBack MC.saveConfigCommPort((String) comboBoxCommPort.getSelectedItem());
        //}
    }
    // =============================================================================================================
}
