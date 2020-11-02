package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.*;
import java.util.Arrays;

public class MyUtil {
    public static <T> void loadToComboBox(T[] list, JComboBox<T> comboBox, boolean space, T def) throws Exception {
        if (list == null) throw new Exception("Указатель на список отсутствует");
        if (comboBox == null) throw new Exception("Указатель на combobox отсутствует");
        comboBox.removeAllItems();
        if (space) {
            comboBox.addItem(null);
        }
        Arrays.stream(list).forEach(comboBox::addItem);
        if (def != null) comboBox.setSelectedItem(def);
    }
}
