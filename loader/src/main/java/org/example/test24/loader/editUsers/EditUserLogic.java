package org.example.test24.loader.editUsers;

public class EditUserLogic {
    private EditUserGui frame;

    private void start() {
        frame = EditUserGui.init(getEditUserInt());
    }

    private EditUserInt getEditUserInt() {
        return new EditUserInt() {
        };
    }
}
