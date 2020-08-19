package org.example.test24.loader.editUsers;

import org.example.test24.allinterface.bd.UserClass;

import java.util.ArrayList;

public class EditUserLogic implements EditUserInterface, EditUserLogicInterface {
    private EditUserCallBackParent callBackParent;
    private EditUserGui frame;

    private ArrayList<UserClass> userList;


    public EditUserLogic(EditUserCallBackParent callBackParent) {
        this.callBackParent = callBackParent;
        new Thread(this::start).start();
    }

    private void start() {
        userList = new ArrayList<>();
        frame = EditUserGui.init((EditUserInterface) this);
        loadTable();
    }
    // ===========================
    // закрытие gui по инициативе gui
    @Override
    public void closeFromGui() {
        frame = null;
        callBackParent.messageCloseEditUsers();
    }
    // закрытие по инициативе родителя
    @Override
    public void closeFromParent() {
        frame.removeAll();
        frame.dispose();
        closeFromGui();
    }
    // загрузка таблицы
    private void loadTable() {

    }
}
