package org.example.test24.lib.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class MLabel extends JLabel {
    public static final int POS_LEADS = 0;
    public static final int POS_LEFT = 1;
    public static final int POS_CENTER = 2;
    public static final int POS_RIGHT = 3;
    private int beginX;
    private int beginY;
    private int pos;

    public MLabel(String text) {
        super(text);
        pos = POS_LEADS;
    }

    public MLabel() {
        pos = POS_LEADS;
    }
    public MLabel(int pos) {
        this.pos = normPos(pos);
    }

    private void setBoundsInt(int x, int y, int width, int height, String text) {
        int sh = getParent().getFontMetrics(getFont()).stringWidth(text);
        switch (pos) {
            case POS_LEFT:
                x = x + sh;
                break;
            case POS_CENTER:
                x = x - (sh / 2);
                break;
            case POS_RIGHT:
                x = x - sh;
                break;
        }
        super.setBounds(x, y, width, height);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        this.beginX = x;
        this.beginY = y;
        if (getParent() != null) setBoundsInt(x, y, width, height, getText());
        else super.setBounds(x, y, width, height);
    }

    @Override
    public void setBounds(Rectangle r) {
        this.beginX = r.x;
        this.beginY = r.y;
        if (getParent() != null) setBoundsInt(r.x, r.y, r.width, r.height, getText());
        else super.setBounds(r);
    }

    @Override
    public void setText(String text) {
        if (getParent() != null) setBoundsInt(beginX, beginY, getWidth(), getHeight(), text);
        super.setText(text);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (getParent() != null) {
            Rectangle2D r = getParent().getFontMetrics(font).getStringBounds(getText(), getParent().getGraphics());
            setSize((int) r.getWidth() + 1, (int) r.getHeight() + 1);
            setBoundsInt(beginX, beginY, getWidth(), getHeight(), getText());
        }
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = normPos(pos);
        if (getParent() != null) setBoundsInt(beginX, beginY, getWidth(), getHeight(), getText());
    }
    private int normPos(int pos) {
        if (pos < POS_LEADS || pos > POS_RIGHT) pos = POS_LEADS;
        return pos;
    }
}
