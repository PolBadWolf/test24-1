package org.example.test24.lib.swing;

import java.awt.*;

public class SaveEnableComponents {
    private Component[] components;
    private boolean[] stats;

    public SaveEnableComponents(Component[] components) {
        this.components = components;
        stats = new boolean[components.length];
        save();
    }
    public void save() {
        for (int i = 0; i < components.length; i++) {
            stats[i] = components[i].isEnabled();
        }
    }
    public void restore() {
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(stats[i]);
        }
    }
    public void offline() {
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(false);
        }
    }
    public void online() {
        for (int i = 0; i < components.length; i++) {
            components[i].setEnabled(true);
        }
    }
}
