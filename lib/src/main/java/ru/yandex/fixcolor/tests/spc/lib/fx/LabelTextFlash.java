package ru.yandex.fixcolor.tests.spc.lib.fx;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class LabelTextFlash implements TextControl  {
    private double x0;
    private Label label;

    public LabelTextFlash(Label label) {
        this.label = label;
        x0 = label.getLayoutX();
    }

    @Override
    public void setText(String text) {
        double lenght = TextUtils.computeTextWidth(label.getFont(), text, 0.0);
        Platform.runLater(()->{
            label.setLayoutX(x0 - lenght / 2);
            label.setText(text);
            label.setVisible(true);
        });
    }

    @Override
    public void setVisible(boolean visible) {
        Platform.runLater(()->{
            label.setVisible(visible);
        });
    }

    @Override
    public boolean isVisible() {
        return label.isVisible();
    }

    @Override
    public void setFlash(int time) {

    }

    @Override
    public int getFlash() {
        return 0;
    }
}
