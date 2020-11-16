package ru.yandex.fixcolor.tests.spc.lib.plot2;

import javafx.scene.canvas.Canvas;
import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import java.awt.*;

public interface Plot {
    class ColorName {
        public static final Color WHITE = new Color(255, 255, 255);
        public static final Color LIGHT_GRAY = new Color(192, 192, 192);
        public static final Color GRAY = new Color(128, 128, 128);
        public static final Color DARK_GRAY = new Color(64, 64, 64);
        public static final Color BLACK = new Color(0, 0, 0);
        public static final Color RED = new Color(255, 0, 0);
        public static final Color PINK = new Color(255, 175, 175);
        public static final Color ORANGE = new Color(255, 200, 0);
        public static final Color YELLOW = new Color(255, 255, 0);
        public static final Color YELLOWGREEN = new Color(154, 205, 50);
        public static final Color LIGHTGREEN = new Color(144, 238, 144);
        public static final Color GREEN = new Color(0, 255, 0);
        public static final Color DARKGREEN = new Color(0, 100, 0);
        public static final Color MAGENTA = new Color(255, 0, 255);
        public static final Color CYAN = new Color(0, 255, 255);
        public static final Color BLUE = new Color(0, 0, 255);
    }
    class TrendPosition {
        public static final int left = 1;
        public static final int right = 2;
    }
    class Parameters {
        // размер холста ( задается в основном конструкторе )
//        public double width;
//        public double height;
        //          поля
        // размер полей
        public double fieldSizeTop;
        public double fieldSizeLeft;
        public double fieldSizeRight;
        public double fieldSizeBottom;
        // цвет шрифта на полях
        public Color fieldFontColorTop;
        public Color fieldFontColorLeft;
        public Color fieldFontColorRight;
        public Color fieldFontColorBottom;
        // рамер шрифта на полях
        public double fieldFontSizeTop;
        public double fieldFontSizeLeft;
        public double fieldFontSizeRight;
        public double fieldFontSizeBottom;
        // цвет фона полей
        public Color fieldBackColor;
        // цвет рамки
        public Color fieldFrameColor;
        // ширина рамки
        public double fieldFrameWidth;
        //          окно
        // цвет фона
        public Color windowBackColor;
        // цвет линий сетки
        public Color netLineColor;
        // ширина линий сетки
        public double netLineWidth;
        //
        //      тренд1
        // начальные значения минимума и максимума
        public double trend1_zeroY_min;
        public double trend1_zeroY_max;
        public boolean trend1_AutoZoomY;
        // толщина линии
        public double trend1_lineWidth;
        // цвет линии
        public Color trend1_lineColor;
        // размер шрифта надписи
        public double trend1_textFontSize;
        // цвет шрифта надписи
        public Color trend1_textFontColor;
        // текст надписи
        public String trend1_text;
        // позитция тренда относительно окна
        public int trend1_positionFromWindow;
        //
        //      тренд2
        // начальные значения минимума и максимума
        public double trend2_zeroY_min;
        public double trend2_zeroY_max;
        public boolean trend2_AutoZoomY;
        // толщина линии
        public double trend2_lineWidth;
        // цвет линии
        public Color trend2_lineColor;
        // размер шрифта надписи
        public double trend2_textFontSize;
        // цвет шрифта надписи
        public Color trend2_textFontColor;
        // текст надписи
        public String trend2_text;
        // позитция тренда относительно окна
        public int trend2_positionFromWindow;
        //

        public Parameters() {
//            // размер холста
//            this.width = width;
//            this.height = height;
            //          поля
            // размер полей
            fieldSizeTop = 40.0;
            fieldSizeLeft = 70.0;
            fieldSizeRight = 70.0;
            fieldSizeBottom = 40.0;
            // цвет шрифта на полях
            fieldFontColorTop = ColorName.YELLOWGREEN;
            fieldFontColorLeft = ColorName.YELLOWGREEN;
            fieldFontColorRight = ColorName.YELLOWGREEN;
            fieldFontColorBottom = ColorName.YELLOWGREEN;
            // рамер шрифта на полях
            fieldFontSizeTop = 20.0;
            fieldFontSizeLeft = 20.0;
            fieldFontSizeRight = 20.0;
            fieldFontSizeBottom = 20.0;
            // цвет фона полей
            fieldBackColor = ColorName.GREEN;
            // цвет рамки
            fieldFrameColor = ColorName.BLUE;
            // ширина рамки
            fieldFrameWidth = 3.0;
            //          окно
            // цвет фона
            windowBackColor = Color.CYAN;
            // цвет линий сетки
            netLineColor = ColorName.DARKGREEN;
            // ширина линий сетки
            netLineWidth = 5.0;
            //
            //      тренд1
            // начальные значения минимума и максимума
            trend1_zeroY_min = 0.0;
            trend1_zeroY_max = 1000.0;
            trend1_AutoZoomY = false;
            // толщина линии
            trend1_lineWidth = 2.0;
            // цвет линии
            trend1_lineColor = ColorName.RED;
            // размер шрифта надписи
            trend1_textFontSize = 16.0;
            // цвет шрифта надписи
            trend1_textFontColor = ColorName.RED;
            // текст надписи
            trend1_text = "мм";
            // позитция тренда относительно окна
            trend1_positionFromWindow = TrendPosition.left;
            //
            //      тренд2
            // начальные значения минимума и максимума
            trend2_zeroY_min = 0.0;
            trend2_zeroY_max = 500.0;
            trend2_AutoZoomY = false;
            // толщина линии
            trend2_lineWidth = 2.0;
            // цвет линии
            trend2_lineColor = ColorName.GREEN;
            // размер шрифта надписи
            trend2_textFontSize = 16.0;
            // цвет шрифта надписи
            trend2_textFontColor = ColorName.PINK;
            // текст надписи
            trend2_text = "кг";
            // позитция тренда относительно окна
            trend2_positionFromWindow = TrendPosition.right;
            //
            //
        }
    }
    // -----------------
    static Plot createSwing(Parameters parameters, MPanel panel) { return new PlotSwing(parameters, panel); }
    static Plot createFx(Parameters parameters, Canvas canvas) { return new PlotFx(parameters, canvas); }
    // -----------------
    void clear();
}
