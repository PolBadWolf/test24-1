package ru.yandex.fixcolor.my_lib.graphics.swing;

import org.example.test24.lib.MyLogger;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Plot {
    public static final Color LIGHTGREEN = new Color(144, 238, 144);
    public static final Color DARKGREEN = new Color(0, 100, 0);
    // ====================
    private JMyPane panelGraph;
    private Graphics2D gc;

    // размер холста
    private int width;
    private int height;

    //          поля
    // ширина
    private int fieldWidth;
    private int fieldHeight;
    // цвет фона
    private Color fieldBackColor = Color.GRAY;
    // цвет рамки
    private Color fieldFrameLineColor = LIGHTGREEN;
    // ширина рамки
    private float fieldFrameLineWidth = 3.0f;

    //          окно
    // цвет фона
    private Color windowBackColor = Color.BLACK;

    //          сетка
    // цвет линий сетки
    private Color netLineColor = DARKGREEN;
    // ширина линий сетки
    private double netLineWidth = 1.0;

    private int     indexBegin = 0;
    private double  levelXbegin = 0.0;
    private double  levelXbeginSave = levelXbegin;
    private double  levelXlenght = 1000.0;
    private double  levelXlenghtMax = 0;
    private boolean levelXlenghtAuto = false;
    private boolean levelXbeginAuto = false;
    private double  kX;
    private int     xStep = 10;

    private double levelYbegin = 0.0;
    private double levelYlenght = 300.0;
    private boolean levelYauto = false;
    private double levelYmin;
    private double levelYlenghtMax = 0;
    private int    yStep;

    // масив графиков
    private ArrayList<Trend> trends;
    private ArrayList<NewDataClass> dataGraphics;
    private MyPaint myPaint;
    private NewDataClass newData;
    private CirkMassive cirkMassive;

    private boolean busy = false;

    private class Trend {
        private Color lineColor;
        private double lineWidth = 2.0;

        public Trend(Color lineColor, double lineWidth) {
            this.lineColor = lineColor;
            this.lineWidth = lineWidth;
        }

        public void rePaint(double[] x, double[] y, int lenght) {
            /*gc.beginPath();
            gc.setStroke(lineColor);
            gc.setLineWidth(lineWidth);
            gc.strokePolyline(x, y, lenght);
            gc.stroke();
            gc.closePath();*/
            MyLogger.myLog.log(Level.ALL, "рисование - реализовать !", new Exception());
        }
    }

    private class DatQueue {
        public int command;
        ArrayList<NewDataClass> datGraph;

        public DatQueue(int command, ArrayList<NewDataClass> datGraph) {
            this.command = command;
            this.datGraph = datGraph;
        }
    }

    private class DatXindx {
        public double x;
        public int indx;

        public DatXindx(double x, int indx) {
            this.x = x;
            this.indx = indx;
        }
    }

    private class MyPaint extends Thread {
        public static final int ClearFields = 0;
        public static final int ClearWindow = 1;
        public static final int PaintNet = 2;
        public static final int RePaint = 3;

        private final BlockingQueue<DatQueue> paintQueue = new ArrayBlockingQueue<>(25);
        private boolean onWork;
        private MyPaint thisThread = null;

        DatQueue queueClearFields = new DatQueue(ClearFields, null);
        DatQueue queueClearWindow = new DatQueue(ClearWindow, null);
        DatQueue queuePaintNet = new DatQueue(PaintNet, null);

        @Override
        public void run() {
            thisThread = this;
            onWork = true;
            DatQueue datQueue;

            while (onWork) {
                try {
                    datQueue = paintQueue.poll(1, TimeUnit.SECONDS);
                    if (datQueue == null)   {
                        Thread.sleep(1);
                        continue;
                    }
                    switch (datQueue.command) {
                        case ClearFields:
                            __clearFields();
                            break;
                        case ClearWindow:
                            __clearWindow();
                            break;
                        case PaintNet:
                            //Platform.runLater(this::__paintNet);
                            break;
                        case RePaint:
                            __rePaint(datQueue.datGraph);
                            break;
                        default:
                            System.out.println("o!@# Plot.java MyPaint swith\r what command: " + datQueue.command);
                    }
                } catch (IllegalStateException ex) {
                    System.out.println("o!@# Plot.java MyPaint run()\r" + ex.toString());
                    break;
                } catch (InterruptedException e) {
                    System.out.println("o!@# Plot.java MyPaint run() poll()\r" + e.toString());
                    break;
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }

        public void clearFields() {
            paintQueue.add(queueClearFields);
        }

        public void clearWindow() {
            paintQueue.add(queueClearWindow);
        }

        public void paintNet() {
            paintQueue.add(queuePaintNet);
        }

        public void rePaint(ArrayList<NewDataClass> dat) {
            paintQueue.add(new DatQueue(RePaint, dat));
        }

        @Override
        protected void finalize() throws Throwable {
            onWork = false;
            while (!thisThread.isAlive()) {
                Thread.yield();
            }
            super.finalize();
        }
        public void close() {
            onWork = false;
            while (!thisThread.isAlive()) {
                Thread.yield();
            }
        }

        private void __rePaint(ArrayList<NewDataClass> datGraph) {
            // нахождение диапозона
            int indexBeginInteger = -1;
            int indexEnd = datGraph.size();

            // zoom X
            levelXlenghtMax = levelXlenght;
            if (levelXlenghtAuto) {
                if (levelXlenghtMax < datGraph.get(indexEnd -1).getxPos()) {
                    levelXlenghtMax = datGraph.get(indexEnd -1).getxPos();
                }
            }
            double xMaxSample = datGraph.get(indexEnd - 1).getxPos();
            double levelXlenghtMaxSpl = levelXlenghtMax;
            if (levelXbeginAuto) {
                indexBeginInteger = -1;
                for (int i = indexBegin; i < indexEnd; i++) {
                    if (i < 0)  continue;
                    if ( (xMaxSample - datGraph.get(i).getxPos()) < levelXlenghtMaxSpl ) {
                        indexBeginInteger = i;
                        levelXbegin = datGraph.get(i).getxPos();
                        break;
                    }
                }
                if (indexBeginInteger < 0) {
                    busy = false;
                    return;
                }
                indexBegin = indexBeginInteger;
            }
            else {
                for (int i = indexBegin; i < indexEnd; i++) {
                    if (i < 0)  continue;
                    if (datGraph.get(i).getxPos() >= levelXbegin) {   // shift X
                        indexBeginInteger = i;
//                        levelXbeginCurent = datGraph.get(i)[0];
                        break;
                    }
                }
                if (indexBeginInteger < 0) {
                    busy = false;
                    return;
                }
                indexBegin = indexBeginInteger;
            }

            // drops & selects
            //int nItemsMass = datGraph.get(0).length;
            int nItemsMass = datGraph.get(0).getZnTrends().length;
            ArrayList<DatXindx> xIndxes = new ArrayList<>();
            NewDataClass tmpShort = null;
            double curX, oldX = -100;
            double vys = height - fieldHeight;
            double kY = levelYlenghtMax / vys;

            for (int i = indexBegin; i < indexEnd; i++) {
                try {
                    tmpShort = datGraph.get(i);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (tmpShort.getxPos() >= (levelXbegin + levelXlenghtMax)) break;
                curX = Math.round(((tmpShort.getxPos().doubleValue() - levelXbegin) / kX) + fieldWidth);
                if ((curX - oldX) < 2)  continue;
                oldX = curX;
                xIndxes.add(new DatXindx(curX, i));
            }

            double y;
            double yMin = levelYbegin, yMax = 0;

            int dropLenght = xIndxes.size();
            double[][] massGraphcs = cirkMassive.next();
            for (int i = 0; i < dropLenght; i++) {
                // x
                massGraphcs[0][i] = xIndxes.get(i).x;

                tmpShort = datGraph.get(xIndxes.get(i).indx);
                for (int j = 0; j < nItemsMass; j++) {
                    Short[] trendsLocal = tmpShort.getZnTrends();
                    y = trendsLocal[j].doubleValue() - levelYmin;
                    if (y < 0)  y = 0;
                    massGraphcs[j + 1][i] = vys - y / kY;
                    if (!levelYauto)    continue;
                    if (yMin > trendsLocal[j].doubleValue())   yMin = trendsLocal[j].doubleValue();
                    if (yMax < trendsLocal[j].doubleValue())   yMax = trendsLocal[j].doubleValue();
                }
            }

            levelYmin = levelYbegin;
            levelYlenghtMax = levelYlenght;
            if (levelYauto) {
                if (levelYmin > yMin)   levelYmin = yMin;
                if ((levelYlenghtMax + levelYmin) < yMax)   levelYlenghtMax = yMax - levelYmin;
            }
            if (levelYlenghtMax < 100)  levelYlenghtMax = 100;

            /*Platform.runLater(()->{
                __clearFields();
                __clearWindow();
                __paintNet();
                for (int i = 1; i < nItemsMass + 1; i++) {
                    trends.get(i - 1).rePaint(massGraphcs[0], massGraphcs[i], dropLenght);
                }
            });*/
            busy = false;
        }

        private void __clearFields() {
            gc.setColor(fieldBackColor);
            gc.fillRect(0, 0, fieldWidth, height);
            gc.fillRect(0, height - fieldHeight, width, height);

            int polFrameLineWidth = (int) (fieldFrameLineWidth / 2);
            int[] x = {
                    fieldWidth - polFrameLineWidth,
                    fieldWidth - polFrameLineWidth,
                    width - polFrameLineWidth
            };
            int[] y = {
                    0,
                    height - fieldHeight + polFrameLineWidth,
                    height - fieldHeight + polFrameLineWidth
            };

            gc.setColor(fieldFrameLineColor);
            gc.setStroke(new BasicStroke(fieldFrameLineWidth));
            gc.drawPolyline(x, y, x.length);
            panelGraph.repaint();
        }

        private void __clearWindow() {
            gc.setColor(windowBackColor);
            gc.fillRect(fieldWidth, 0, width, height - fieldHeight);
            panelGraph.repaint();
        }

        private void __paintNet() {
            /*
            double xSize = width - fieldWidth;
            double ySize = height - fieldHeight;

            double y_level;
            int yN;
            int yNk;
            if (levelYlenghtMax == 0) {
                levelYmin = levelYbegin;
                levelYlenghtMax = levelYlenght;
            }
            y_level = levelYlenghtMax;
            yStep = Math.floorDiv((int) y_level, 100) * 10;
            yN = (int) Math.ceil(y_level / yStep);

            //yN = (int) (y_level / kNet + 1);
            yNk = (int) (levelYmin / yStep);
            if ((levelYmin % yStep) > 0) yNk++;

            double  xCena;
            int xN;
            if (levelXlenghtMax == 0)   levelXlenghtMax = levelXlenght;
            if (levelXlenghtMax < 200) {
                xStep = Math.floorDiv((int)levelXlenghtMax, 100) * 10;
            }
            else {
                int div = Math.floorDiv((int) levelXlenghtMax, 200) * 20;
                xStep = Math.floorDiv((int) levelXlenghtMax, (div * 10)) * div;
            }

            xN = (int) Math.ceil(levelXlenghtMax / xStep) + 1;
            xCena = (double) xStep / 200;

            double x, y, polLineWidth = netLineWidth / 2;

            gc.beginPath();
            gc.setStroke(netLineColor);
            gc.setLineWidth(netLineWidth);
            gc.setFill(Color.YELLOW);
            gc.setTextAlign(TextAlignment.CENTER);

            // x
            kX = (Math.ceil(levelXlenghtMax / xStep) * xStep) / (width - fieldWidth);
            int xNd = (int) Math.ceil(levelXbegin / xStep);
            for (int i = 1; i < xN - 1 + xNd ; i++) {
                x = (i * xSize / (xN -1)) + fieldWidth - (levelXbegin / kX);
                if (x < fieldWidth)     continue;
                if (x > width) {
                    continue;
                }
                gc.moveTo(x,  polLineWidth);
                gc.lineTo(x, ySize - polLineWidth);
                double tmp = (double) Math.round(i * xCena * 1000) / 1000;
                gc.fillText(String.valueOf(tmp), x, ySize + 20 );
            }

            // y
            gc.setTextAlign(TextAlignment.RIGHT);
            double kp = ySize / y_level;
            int iK;
            for (int i = 0; i < yN; i++) {
                iK = (i + yNk) * yStep;
                y = kp * (iK - levelYmin);
                if (y < 0)  {
                    continue;
                }
                if (y > ySize)  {
                    break;
                }
                y = ySize - y;
                gc.moveTo(fieldWidth + polLineWidth, y);
                gc.lineTo(width - polLineWidth, y);
                gc.fillText(String.valueOf(iK), fieldWidth - 10, y + 5);
            }

            gc.closePath();
            gc.stroke();*/
            MyLogger.myLog.log(Level.ALL, "рисование сетки - реализовать", new Exception());
        }

    }

    public Plot(JComponent parent, int x, int y, int width, int height, int fieldWidth, int fieldHeight) {
        panelGraph = new JMyPane();
        panelGraph.setLayout(null);
        panelGraph.setBounds(x, y, width, height);
        parent.add(panelGraph);
        panelGraph.createBI();
        gc = panelGraph.getGraphics2D();
        //
        this.width = width;
        this.height = height;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        //
        trends = new ArrayList<>();
        dataGraphics = new ArrayList<>();
        newData = new NewDataClass(0);
        cirkMassive = new CirkMassive();

        myPaint = new MyPaint();
        myPaint.start();
    }

    public void close() {
        myPaint.close();
    }
    private class JMyPane extends JPanel {
        private BufferedImage bufferedImage = null;
        private Graphics2D graphics2D = null;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bufferedImage != null) {
                Graphics2D g2 = (Graphics2D) g;
                g2.drawRenderedImage(bufferedImage, null);
            }
        }
        public void createBI() {
            bufferedImage = new BufferedImage(super.getWidth(), super.getHeight(), BufferedImage.TYPE_INT_ARGB);
            graphics2D = bufferedImage.createGraphics();
        }
        public void closeBI() {
            if (graphics2D != null) {
                graphics2D.dispose();
                graphics2D = null;
            }
            if (bufferedImage != null) {
                bufferedImage.flush();
                bufferedImage = null;
            }
        }

        public Graphics2D getGraphics2D() {
            if (graphics2D == null) createBI();
            return graphics2D;
        }
    }

    public void setFieldBackColor(Color fieldBackColor) {
        this.fieldBackColor = fieldBackColor;
    }

    public Color getFieldBackColor() {
        return fieldBackColor;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldFrameLineColor(Color fieldFrameLineColor) {
        this.fieldFrameLineColor = fieldFrameLineColor;
    }

    public Color getFieldFrameLineColor() {
        return fieldFrameLineColor;
    }

    public void setFieldFrameLineWidth(float fieldFrameLineWidth) {
        this.fieldFrameLineWidth = fieldFrameLineWidth;
    }

    public double getFieldFrameLineWidth() {
        return fieldFrameLineWidth;
    }

    public void setNetLineColor(Color netLineColor) {
        this.netLineColor = netLineColor;
    }

    public Color getNetLineColor() {
        return netLineColor;
    }

    public void setNetLineWidth(double netLineWidth) {
        this.netLineWidth = netLineWidth;
    }

    public double getNetLineWidth() {
        return netLineWidth;
    }

    public void setWindowBackColor(Color windowBackColor) {
        this.windowBackColor = windowBackColor;
    }

    public Color getWindowBackColor() {
        return windowBackColor;
    }

    public void setZoomXbegin(double levelXbegin) {
        this.levelXbegin = levelXbegin;
        levelXbeginSave = levelXbegin;
        indexBegin = 0;
    }

    public void setZoomXlenght(double levelXlenght) {
        this.levelXlenght = levelXlenght;
    }

    public void setZoomX(double levelXbegin, double levelXlenght) {
        this.levelXbegin = levelXbegin;
        levelXbeginSave = levelXbegin;
        this.levelXlenght = levelXlenght;
        indexBegin = 0;
    }

    public double getZoomXbegin() {
        return levelXbegin;
    }

    public double getZoomXlenght() {
        return levelXlenght;
    }

    public void setZoomXlenghtAuto(boolean levelXauto) {
        this.levelXlenghtAuto = levelXauto;
    }

    public boolean getZoomXlenghtAuto() {
        return levelXlenghtAuto;
    }

    public void setZoomXbeginAuto(boolean levelXbeginAuto) {
        this.levelXbeginAuto = levelXbeginAuto;
    }

    public boolean getZoomXbeginAuto() {
        return levelXbeginAuto;
    }

    public double getLevelXlenghtMax() {
        return levelXlenghtMax;
    }

    public void setZoomYbegin(double levelYbegin) {
        this.levelYbegin = levelYbegin;
    }

    public void setZoomYlenght(double levelYlenght) {
        this.levelYlenght = levelYlenght;
    }

    public void setZoomYauto(boolean levelYauto) {
        this.levelYauto = levelYauto;
    }

    public void setZoomY(double levelYbegin, double levelYlenght) {
        this.levelYbegin = levelYbegin;
        this.levelYlenght = levelYlenght;
    }

    public double getZoomYbegin() {
        return levelYbegin;
    }

    public double getZoomYlenght() {
        return levelYlenght;
    }

    public boolean getZoomYauto() {
        return levelYauto;
    }
    // ---
    public void clearFields() {
        myPaint.clearFields();
    }

    public void clearWindow() {
        myPaint.clearWindow();
    }

    public void paintNet() {
        myPaint.paintNet();
    }

    public void clearScreen() {
        myPaint.clearFields();
        myPaint.clearWindow();
        myPaint.paintNet();
    }

    public void rePaint() {
        if (busy)   return;
        busy = true;
        myPaint.rePaint(dataGraphics);
    }
    // ----
    public void addTrend(Color lineColor, double lineWidth) {
        trends.add(new Trend(lineColor, lineWidth));
        //newData = new Short[trends.size() + 1];
        newData = new NewDataClass(trends.size());
        cirkMassive.init(trends.size() + 1, (int) (width * 1.5));
    }

    public void removeAllTrends() {
        trends.clear();
    }
    // ----
    public void newDataX(int dataX) {
        //newData[0] = dataX;
        newData.setxPos(dataX);
    }

    public void newDataTrend(int n, short data) {
        //newData[n + 1] = data;
        newData.setZnTrends(n, data);
    }

    public void newDataPush() {
        dataGraphics.add(newData);
        //newData = new Short[trends.size() + 1];
        newData = new NewDataClass(trends.size());
    }

    public void allDataClear() {
        while (busy) {
            Thread.yield();
        }
        dataGraphics.clear();
        indexBegin = 0;
        levelXbegin = levelXbeginSave;
        //newData = new Short[trends.size() + 1];
        newData = new NewDataClass(trends.size());
    }
    // ----

    @Override
    protected void finalize() throws Throwable {
        myPaint.finalize();
        while (!myPaint.isAlive()) {
            Thread.yield();
        }
        super.finalize();
    }

    private class CirkMassive {
        private double[][][] massInt;
        private int indx;

        public CirkMassive() {
            massInt = new double[2][][];
            indx = 0;
        }

        public void init(int ch, int n) {
            massInt[0] = new double[ch][n];
            massInt[1] = new double[ch][n];
        }

        public double[][] next() {
            indx++;
            if (indx > 1)   indx = 0;
            return massInt[indx];
        }
    }

}

class NewDataClass {
    private Integer     xPos;
    private Short[]     znTrends;

    public NewDataClass(int nTrends) {
        znTrends = new Short[nTrends];
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public void setZnTrends(int n, Short zn) {
        znTrends[n] = zn;
    }

    public Integer getxPos() {
        return xPos;
    }

    public Short[] getZnTrends() {
        return znTrends;
    }
}
