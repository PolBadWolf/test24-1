package ru.yandex.fixcolor.tests.spc.lib.plot2;

import javafx.scene.canvas.Canvas;
import ru.yandex.fixcolor.tests.spc.lib.swing.MPanel;

import java.awt.*;

public interface Plot {
    interface CallBack {
    }
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
        public static final int center = 2;
        public static final int right = 3;
    }
    int ZOOM_Y_OFF = 0;
    int ZOOM_Y_FROM_SCALE = 1;
    int ZOOM_Y_FROM_VISUAL_DATA = 2;
    int ZOOM_X_OFF = 0;
    int ZOOM_X_SHRINK  = 1;
    int ZOOM_X_SHIFT = 2;
    class Parameters {
        public double scale_img;
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
        public Color fieldFontColorBottom;
        // рамер шрифта на полях
        public double fieldFontSizeTop;
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
        public double scaleZero_maxX;
        public int scaleZero_zoomX;

        // линия указания обратного хода
        public double pointBackMove_time;
        public Color pointBackMove_color;
        public double pointBackMove_lineWidth;
        // линия указания начало полки
        public double pointBeginShelf_time;
        public Color pointBeginShelf_color;
        public double pointBeginShelf_lineWidth;
        // линия указания окончания возврата (стоп)
        public double pointStopBack_time;
        public Color  pointStopBack_color;
        public double pointStopBack_lineWidth;
        //
        //      тренд1
        // начальные значения минимума и максимума
        public double trend1_zeroY_min;
        public double trend1_zeroY_max;
        // 0 - off, 1 - zeroY+-, 2 - show trend +-
        public int trend1_AutoZoomY;
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
        // 0 - off, 1 - zeroY+-, 2 - show trend +-
        public int trend2_AutoZoomY;
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
            scale_img = 1.0;
//            // размер холста
//            this.width = width;
//            this.height = height;
            //          поля
            // размер полей
            fieldSizeTop = 10.0;
            fieldSizeLeft = 70.0;
            fieldSizeRight = 70.0;
            fieldSizeBottom = 40.0;
            // цвет шрифта на полях
            fieldFontColorTop = ColorName.YELLOWGREEN;
//            fieldFontColorLeft = ColorName.YELLOWGREEN;
//            fieldFontColorRight = ColorName.YELLOWGREEN;
            fieldFontColorBottom = ColorName.DARKGREEN;
            // рамер шрифта на полях
            fieldFontSizeTop = 16.0;
//            fieldFontSizeLeft = 16.0;
//            fieldFontSizeRight = 16.0;
            fieldFontSizeBottom = 16.0;
            // цвет фона полей
            fieldBackColor = new Color(220, 220, 220);
            // цвет рамки
            fieldFrameColor = ColorName.BLUE;
            // ширина рамки
            fieldFrameWidth = 3.0;
            //          окно
            // цвет фона окна
            windowBackColor = new Color(220, 220, 220);
            // цвет линий сетки
            netLineColor = new Color(50, 50, 50);
//            netTextColor = ColorName.RED;
//            netTextSize = 16;
            // ширина линий сетки
            netLineWidth = 2.0;
            // линия указания обратного хода
            pointBackMove_time = -1_000_000;
            pointBackMove_color = Color.blue;
            pointBackMove_lineWidth = 3;
            // линия указания начало полки
            pointBeginShelf_time = -1_000_000;
            pointBeginShelf_color = Color.blue;
            pointBeginShelf_lineWidth = 3;
            // линия указания окончания возврата (стоп)
            pointStopBack_time = -1_000_000;
            pointStopBack_color = Color.blue;
            pointStopBack_lineWidth = 3;
            // 5 секунд
            scaleZero_maxX = 1_400;
            scaleZero_zoomX = Plot.ZOOM_X_OFF;
            //
            //      тренд1
            // начальные значения минимума и максимума
            trend1_zeroY_min = 0.0;
            trend1_zeroY_max = 1000.0;
            trend1_AutoZoomY = ZOOM_Y_OFF;
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
            trend2_AutoZoomY = ZOOM_Y_OFF;
            // толщина линии
            trend2_lineWidth = 2.0;
            // цвет линии
            trend2_lineColor = ColorName.BLUE;
            // размер шрифта надписи
            trend2_textFontSize = 16.0;
            // цвет шрифта надписи
            trend2_textFontColor = ColorName.BLUE;
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
    CallBack getCallBack();
    void setCallBack(CallBack callBack);
    // -----------------
    void clearScreen();
    void newData(double ms);
    void addTrend(double zn);
    void setData();
    void paint();
    void reFresh();
    void allDataClear();
    void setZommXzero();
    void closeApp();
    boolean isAutoPaint();
    void setAutoPaint(boolean autoPaint);
    void setPointBackMove_time(double time);
    void setPointBeginShelf_time(double time);
    void setPointStopBack_time(double time);
}
