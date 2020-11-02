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
    private double scale;
    private int sizeX;
    private int sizeY;

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
    public void createImage(int dpi) {
        scale = (double) dpi / (double) 72;
        int w = (int) Math.ceil((double) width * scale);
        int h = (int) Math.ceil((double) height * scale);
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        setSize();
    }
    public void closeImage() {
        image = null;
    }
    private void setSize() {
        sizeX = image.getWidth() - (int) Math.ceil(fieldWidth * scale);
        sizeY = image.getHeight() - (int) Math.ceil(fieldHeight * scale);
    }
    public void clear() {
        Graphics2D g2d = image.createGraphics();
        try {
            // очистка окна
            g2d.setColor(windowBackGround);
            g2d.fillRect(fieldWidth, 0, width, height - fieldHeight);
            // очистка поля
            g2d.setColor(fieldBackGround);
            g2d.fillRect(0, 0, fieldWidth, height);
            g2d.fillRect(fieldWidth, height - fieldHeight, width, height);
            // сетка
            int cx0, cx1, cy0, cy1;
            String string;
            float stringWidth, tmp;
            final int nX = 10;
            // x
            cy0 = sizeY;
            cy1 = 0;
            for (int i = 1; i < nX; i++) {
                cx0 = (i * sizeX / (nX - 1));
                g2d.setStroke(new BasicStroke(netLineWidth));
                g2d.setColor(netLineColor);
                g2d.drawLine(cx0, cy0, cx0, cy1);
                //
                tmp = (float) Math.round(i * 10 * 1_000) / 1_000;
                string = String.valueOf(tmp);
                stringWidth = g2d.getFontMetrics().stringWidth(string);
                cx1 = cx0 - Math.round(stringWidth / 2);
                g2d.setColor(fieldFontColor);
                g2d.setFont(fieldFont);
                g2d.drawString(string, cx1, (int) Math.ceil(sizeY + 20 * scale));
            }
        } finally {
            g2d.dispose();
        }
    }
}
