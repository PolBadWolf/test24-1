package ru.yandex.fixcolor.tests.spc.lib;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class Reflection {
    public static int getInt(Object o, String nameField) {
        int zn = 0;
        try {
            Field field = o.getClass().getDeclaredField(nameField);
            field.setAccessible(true);
            zn = field.getInt(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка доступа", e);
        }
        return zn;
    }
    public static void setInt(Object o, String nameField, int zn) {
        try {
            Field field = o.getClass().getDeclaredField(nameField);
            field.setAccessible(true);
            field.setInt(o, zn);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка доступа", e);
        }
    }
    //
    @SuppressWarnings("unchecked")
    public static <T> T getField(Object o, String nameField) {
        T zn = null;
        try {
            Field field = o.getClass().getDeclaredField(nameField);
            field.setAccessible(true);
            zn = (T) field.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка доступа", e);
        }
        return zn;
    }
    public static <T> void setField(Object o, String nameField, T zn) {
        try {
            Field field = o.getClass().getDeclaredField(nameField);
            field.setAccessible(true);
            field.set(o, zn);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка доступа", e);
        }
    }
}
