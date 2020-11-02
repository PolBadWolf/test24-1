package ru.yandex.fixcolor.tests.spc.lib.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Plot2 extends JComponent {
    public static final Color LIGHTGREEN = new Color(144, 238, 144);
    public static final Color DARKGREEN = new Color(0, 100, 0);
    // ====================
    private int x;
    private int y;
    private int width;
    private int height;
    // ======
    // fields
    private int fieldWidth;
    private static final int _fieldWidth = 50;
    private int fieldHeight;
    private static final int _fieldHeight = 50;
    private Color fieldBackGround;
    private static final Color _fieldBackGround = Color.GRAY;
    private Color fieldLineColor;
    private static final Color _fieldLineColor = Plot2.LIGHTGREEN;
    private float fieldLineWidth;
    private static final float _fieldLineWidth = 3.0f;
    private Color fieldFontColor;
    private static final Color _fieldFontColor = Color.YELLOW;
    private Font fieldFont;
    private static final Font _fieldFont = new Font("Dialog", Font.PLAIN, 12);
    // ======
    // window
    private Color windowBackGround;
    private static final Color _windowBackGround = Color.BLACK;
    private Color netLineColor;
    private static final Color _netLineColor = Plot2.DARKGREEN;
    private float netLineWidth;
    private static final float _netLineWidth = 1.0f;
    // ======
    private BufferedImage image;

    public Plot2(int x, int y, int width, int height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        fieldWidth = _fieldWidth;
        fieldHeight = _fieldHeight;
        fieldBackGround = _fieldBackGround;
        fieldLineColor = _fieldLineColor;
        fieldLineWidth = _fieldLineWidth;
        fieldFontColor = _fieldFontColor;
        fieldFont = _fieldFont;
        windowBackGround = _windowBackGround;
        netLineColor = _netLineColor;
        netLineWidth = _netLineWidth;
        setBounds(x, y, width, height);
    }

}
