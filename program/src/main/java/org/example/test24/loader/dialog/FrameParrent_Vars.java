package org.example.test24.loader.dialog;

import org.example.test24.bd.ParametersSql;
import org.example.test24.bd.UserClass;

import javax.swing.*;

class FrameParrent_Vars {
    // интерфейс обратного вызова
    protected FrameCallBack callBack;
    // статус: система в работе
    protected boolean statMainWork;

    // тип БД
    //protected BaseData.TypeBaseData typeBaseData;
    // парамеры подключения к БД
    protected ParametersSql parametersSql;
    // флаг структурной целостности БД
    protected boolean flCheckSql = false;
    // имя ком порта
    protected String commPortName;
    // флаг доступности ком портов
    protected boolean flCheckCommPort = false;


    protected JFrame frame;
    protected UserClass[] listUsers = null;
    protected UserClass user = null;
}
