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
    private int pos;
    private boolean block;

    public MLabel(String text) {
        super(text);
        pos = POS_LEADS;
        block = false;
    }

    public MLabel() {
        pos = POS_LEADS;
        block = false;
    }
    public MLabel(int pos) {
        this.pos = normPos(pos);
        block = false;
    }

    private void setBoundsInt(int x, int y, int width, int height, String text) {
        int strWidth = getParent().getFontMetrics(getFont()).stringWidth(text);
        super.setBounds(corHor(x, strWidth, pos), y, width, height);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (getParent() != null) {
            if (!block) this.beginX = x;
            setBoundsInt(x, y, width, height, getText());
        }
        else super.setBounds(x, y, width, height);
    }

    @Override
    public void setBounds(Rectangle r) {
        if (getParent() != null) {
            if (!block) this.beginX = r.x;
            setBoundsInt(r.x, r.y, r.width, r.height, getText());
        }
        else super.setBounds(r);
    }

    @Override
    public void setText(String text) {
        if (getParent() != null) {
            Rectangle2D r = getParent().getFontMetrics(getFont()).getStringBounds(text, getParent().getGraphics());
            setSize((int) r.getWidth() + 1, (int) r.getHeight() + 1);
            setBoundsInt(beginX, getY(), getWidth(), getHeight(), text);
        }
        super.setText(text);
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (getParent() != null) {
            Rectangle2D r = getParent().getFontMetrics(font).getStringBounds(getText(), getParent().getGraphics());
            setSize((int) r.getWidth() + 1, (int) r.getHeight() + 1);
            setBoundsInt(beginX, getY(), getWidth(), getHeight(), getText());
        }
    }

    @Override
    public void setSize(int width, int height) {
        block = true;
        super.setSize(width, height);
        block = false;
    }

    @Override
    public void setSize(Dimension d) {
        block = true;
        super.setSize(d);
        block = false;
    }

    public void setBeginX(int beginX) {
        this.beginX = beginX;
        if (getParent() != null) {
            block = false;
            setBoundsInt(beginX, getY(), getWidth(), getHeight(), getText());
        }
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = normPos(pos);
        if (getParent() != null) {
            block = false;
            setBoundsInt(beginX, getY(), getWidth(), getHeight(), getText());
        }
    }
    private int normPos(int pos) {
        if (pos < POS_LEADS || pos > POS_RIGHT) pos = POS_LEADS;
        return pos;
    }
    public int corHor(int x, int strWidth, int pos) {
        switch (pos) {
            case POS_LEFT:
                x = x + strWidth;
                break;
            case POS_CENTER:
                x = x - (strWidth / 2);
                break;
            case POS_RIGHT:
                x = x - strWidth;
                break;
        }
        return x;
    }
}
