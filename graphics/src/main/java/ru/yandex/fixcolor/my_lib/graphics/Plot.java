package ru.yandex.fixcolor.my_lib.graphics;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Plot {
    private Canvas canvas = null;
    private GraphicsContext gc = null;
    // размер холста
    private double width = 0;
    private double height = 0;

    //          поля
    // ширина
    private double fieldWidth = 0;
    private double fieldHeight = 0;
    // цвет фона
    private Color fieldBackColor = Color.GRAY;
    // цвет рамки
    private Color fieldFrameLineColor = Color.LIGHTGREEN;
    // ширина рамки
    private double fieldFrameLineWidth = 3.0;

    //          окно
    // цвет фона
    private Color windowBackColor = Color.BLACK;

    //          сетка
    // цвет линий сетки
    private Color netLineColor = Color.DARKGREEN;
    // ширина линий сетки
    private double netLineWidth = 1.0;

    private double levelXbegin = 0.0;
    private double levelXlenght = 1000.0;

    private double levelYbegin = 0.0;
    private double levelYlenght = 500.0;
    private double levelYmax = 0;

    // масив графиков
    private ArrayList<Trend> trends = null;

    private boolean busy = false;

    private class Trend {
        private Color lineColor = null;
        private double lineWidth = 2.0;

        public Trend(Color lineColor, double lineWidth) {
            this.lineColor = lineColor;
            this.lineWidth = lineWidth;
        }

        public void rePaint(double[] x, double[] y) {
            gc.beginPath();
            gc.setStroke(lineColor);
            gc.setLineWidth(lineWidth);
            gc.strokePolyline(x, y, x.length);
            gc.stroke();
            gc.closePath();
        }
    }

    private class DatQueue {
        public int command;
        ArrayList<Short[]> datGraph;

        public DatQueue(int command, ArrayList<Short[]> datGraph) {
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
        private Object lock = new Object();
        private boolean onWork;

        DatQueue queueClearFields = new DatQueue(ClearFields, null);
        DatQueue queueClearWindow = new DatQueue(ClearWindow, null);
        DatQueue queuePaintNet = new DatQueue(PaintNet, null);

        @Override
        public void run() {
            onWork = true;
            DatQueue datQueue = null;

            while (onWork) {
                try {
                    datQueue = paintQueue.poll(1, TimeUnit.MILLISECONDS);
                    switch (datQueue.command) {
                        case ClearFields:
                            Platform.runLater( ()-> _clearFields() );
                            break;
                        case ClearWindow:
                            Platform.runLater( ()-> _clearWindow() );
                            break;
                        case PaintNet:
                            Platform.runLater( ()-> _paintNet() );
                            break;
                        case RePaint:
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
                }
            }
        }

        private void __rePaint(ArrayList<Short[]> datGraph) {
            // нахождение диапозона
            int indexBegin = 0;
            int indexEnd = datGraph.size();

            for (int i = indexBegin; i < indexEnd; i++) {
                if (datGraph.get(i)[0] >= levelXbegin) {
                    indexBegin = i;
                    break;
                }
            }

            // drops & selects
            int nItemsMass = datGraph.get(0).length;
            ArrayList<DatXindx> xIndxes = new ArrayList<>();
            Short[] tmpShort = null;
            double curX, oldX = -100;
            double kX = levelXlenght / (width - fieldWidth);
            double yLenght = (levelYmax > levelYlenght) ? levelYmax : levelYlenght;
            double vys = height - fieldHeight;
            double kY = yLenght / vys;

            for (int i = indexBegin; i < indexEnd; i++) {
                tmpShort = datGraph.get(i);
                if (tmpShort[0] >= (levelXbegin + levelXlenght)) break;
                curX = ((tmpShort[0].doubleValue() - levelXbegin) / kX) + fieldWidth;
                if ((curX - oldX) < 2)  continue;
                oldX = curX;
                xIndxes.add(new DatXindx(curX, i));
            }

            int dropLenght = xIndxes.size();
            double[][] massGraphcs = new double[nItemsMass][dropLenght];
            for (int i = 0; i < dropLenght; i++) {
                // x
                massGraphcs[0][i] = xIndxes.get(i).x;

                tmpShort = datGraph.get(xIndxes.get(i).indx);
                for (int j = 0; j < nItemsMass; j++) {
                    massGraphcs[j][i] = vys - ((tmpShort[j].doubleValue() - levelYbegin) / kY);
                }
            }

            Platform.runLater(()->{
                _clearFields();
                _clearWindow();
                _paintNet();
                for (int i = 1; i < nItemsMass; i++) {
                    trends.get(i - 1).rePaint(massGraphcs[0], massGraphcs[i]);
                }
            });
            busy = false;
        }
    }

    public Plot(Canvas canvas, double fieldWidth, double fieldHeight) {
        this.canvas = canvas;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }

    private void  _clearFields() {
        gc.beginPath();

        gc.setFill(fieldBackColor);
        gc.fillRect(0, 0, fieldWidth, height);
        gc.fillRect(0, height - fieldHeight, width, height);

        double polFrameLineWidth = fieldFrameLineWidth / 2;
        double[] x = {
                fieldWidth - polFrameLineWidth,
                fieldWidth - polFrameLineWidth,
                width - polFrameLineWidth
        };
        double[] y = {
                0,
                height - fieldHeight + polFrameLineWidth,
                height - fieldHeight + polFrameLineWidth
        };

        gc.setStroke(fieldFrameLineColor);
        gc.setLineWidth(fieldFrameLineWidth);
        gc.strokePolyline(x, y, x.length);

        gc.stroke();
        gc.closePath();
    }

    private void _clearWindow() {
        gc.beginPath();
        gc.setFill(windowBackColor);
        gc.fillRect(fieldWidth, 0, width, height - fieldHeight);
        gc.stroke();
        gc.closePath();
    }
    
    private void _paintNet() {
        double kNet;
        if (levelYlenght <= 100) kNet = 10;
        else if (levelYlenght <= 250) kNet = 25;
        else if (levelYlenght <= 500) kNet = 50;
        else if (levelYlenght <= 750) kNet = 75;
        else if (levelYlenght <= 1000) kNet = 100;
        else if (levelYlenght <= 1500) kNet = 150;
        else kNet = 200;
        
        double xSize = width - fieldWidth;
        double ySize = height - fieldHeight;
        int xN = 12 + 1;
        int yN = (int)(levelYlenght / kNet) + 0;
        double x, y, polLineWidth = netLineWidth / 2;
        
        gc.beginPath();
        gc.setStroke(netLineColor);
        gc.setLineWidth(netLineWidth);
        // x
        for (int i = 1; i < xN - 1; i++) {
            x = (i * xSize / (xN -1)) + fieldWidth;
            gc.moveTo(x, fieldHeight + polLineWidth);
            gc.lineTo(x, fieldHeight - polLineWidth + ySize);
        }
        // y
        double kp = ySize / levelYlenght;
        for (int i = 0; i < yN; i++) {
            y = height - (kp * i * kNet);
            gc.moveTo(fieldWidth + polLineWidth, y);
            gc.lineTo(width - polLineWidth, y);
        }

        gc.stroke();
        gc.closePath();
    }
}
