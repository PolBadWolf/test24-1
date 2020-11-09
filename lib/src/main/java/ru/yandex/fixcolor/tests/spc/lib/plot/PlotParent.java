package ru.yandex.fixcolor.tests.spc.lib.plot;

import java.awt.*;

class PlotParent implements Plot {
    // размер холста
    protected double width;
    protected double height;
    //          поля
    // размер полей
    protected double fieldSizeTop;
    protected double fieldSizeLeft;
    protected double fieldSizeRight;
    protected double fieldSizeBottom;
    // цвет шрифта на полях
    protected Color fieldFontColorTop;
    protected Color fieldFontColorLeft;
    protected Color fieldFontColorRight;
    protected Color fieldFontColorBottom;
    // рамер шрифта на полях
    protected double fieldFontSizeTop;
    protected double fieldFontSizeLeft;
    protected double fieldFontSizeRight;
    protected double fieldFontSizeBottom;
    // цвет фона полей
    protected Color fieldBackColor;
    // цвет рамки
    protected Color fieldFrameColor;
    // ширина рамки
    protected double fieldFrameWidth;
    //          окно
    // цвет фона
    protected Color windowBackColor;
    // цвет линий сетки
    protected Color netLineColor;
    // ширина линий сетки
    protected double netLineWidth;

    protected PlotParent(Parameters parameters) {
        // размер холста
        width = parameters.width;
        height = parameters.height;
        //          поля
        // размер полей
        fieldSizeTop = parameters.fieldSizeTop;
        fieldSizeLeft = parameters.fieldSizeLeft;
        fieldSizeRight = parameters.fieldSizeRight;
        fieldSizeBottom = parameters.fieldSizeBottom;
        // цвет шрифта на полях
        fieldFontColorTop = parameters.fieldFontColorTop;
        fieldFontColorLeft = parameters.fieldFontColorLeft;
        fieldFontColorRight = parameters.fieldFontColorRight;
        fieldFontColorBottom = parameters.fieldFontColorBottom;
        // рамер шрифта на полях
        fieldFontSizeTop = parameters.fieldFontSizeTop;
        fieldFontSizeLeft = parameters.fieldFontSizeLeft;
        fieldFontSizeRight = parameters.fieldFontSizeRight;
        fieldFontSizeBottom = parameters.fieldFontSizeBottom;
        // цвет фона полей
        fieldBackColor = parameters.fieldBackColor;
        // цвет рамки
        fieldFrameColor = parameters.fieldFrameColor;
        // ширина рамки
        fieldFrameWidth = parameters.fieldFrameWidth;
        //          окно
        // цвет фона
        windowBackColor = parameters.windowBackColor;
        // цвет линий сетки
        netLineColor = parameters.netLineColor;
        // ширина линий сетки
        netLineWidth = parameters.netLineWidth;
    }
}
