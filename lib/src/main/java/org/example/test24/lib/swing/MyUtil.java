package org.example.test24.lib.swing;

import javax.swing.*;
import java.util.Arrays;

public class MyUtil {
    public static <T> void loadToComboBox(T[] list, JComboBox<T> comboBox, T def) throws Exception {
        if (list == null) throw new Exception("Указатель на список отсутствует");
        if (comboBox == null) throw new Exception("Указатель на combobox отсутствует");
        comboBox.removeAllItems();
        Arrays.stream(list).forEach(t -> comboBox.addItem(t));
        if (def != null) comboBox.setSelectedItem(def);
    }
}