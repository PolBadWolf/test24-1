package org.example.test24.loader.editUsers;

public class EditUserLogic implements EditUserInterface {
    private EditUserCallBackParent callBackParent;
    private EditUserGui frame;


    public EditUserLogic(EditUserCallBackParent callBackParent) {
        this.callBackParent = callBackParent;
        new Thread(this::start).start();
    }

    private void start() {
        frame = EditUserGui.init((EditUserInterface) this);
    }
    // ===========================
    // закрытие gui по инициативе gui
    @Override
    public void closeGui() {
        frame = null;
        callBackParent.messageCloseEditUsers();
    }
}
