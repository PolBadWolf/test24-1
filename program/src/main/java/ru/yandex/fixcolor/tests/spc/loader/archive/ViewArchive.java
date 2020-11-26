package ru.yandex.fixcolor.tests.spc.loader.archive;

import ru.yandex.fixcolor.tests.spc.bd.*;
import ru.yandex.fixcolor.tests.spc.bd.usertypes.*;
import ru.yandex.fixcolor.tests.spc.lib.*;
import ru.yandex.fixcolor.tests.spc.lib.swing.*;
import ru.yandex.fixcolor.tests.spc.allinterface.bd.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

public class ViewArchive {
    public interface CallBack {
        void closeArchive();
    }
    // =====================
    private static final float PG_WIDTH  = Math.round(210.0f * Scale.MM2UNIT_SCR);
    private static final float PG_HEIGHT = Math.round(297.0f * Scale.MM2UNIT_SCR);
    private static final float PG_LEFT   = (int) Math.ceil(30.0F * Scale.MM2UNIT_SCR);
    private static final float PG_RIGHT  = (int) Math.ceil(15.0F * Scale.MM2UNIT_SCR);
    private static final float PG_TOP    = (int) Math.ceil(20.0F * Scale.MM2UNIT_SCR);
    private static final float PG_BOTTOM = (int) Math.ceil(20.0F * Scale.MM2UNIT_SCR);
    // =====================
    private final BaseData conn;
    private final CallBack callBack;
    public ViewArchive(CallBack callBack, BaseData conn) {
        this.callBack = callBack;
        this.conn = conn;
        new Thread(this::start, "thread view archive").start();
    }

    private final String root = "Архив";
    private JFrame frame;
    private MPanelPrintableCap panelMain;
    private MPanel panelPlot;
    private ru.yandex.fixcolor.tests.spc.lib.plot2.Plot plot;
//    private Plot plot;
    private JTree tree;
    private JScrollPane scrollPane;
    // ======
    private JButton buttonPrint;
    private MLabel labelDate;
    private MLabel labelUser;
    private MLabel labelPusherSampleTitle;
    private MLabel labelPusherSampleForce;
    private MLabel labelPusherSampleMove;
    private MLabel labelPusherSampleUnClenchingTime;
    private MLabel labelPusherMeasuredForce;
    private MLabel labelPusherMeasuredMove;
    private MLabel labelPusherMeasuredUnClenchingTime;
    private MLabel labelGraphTitle;
    private MLabel labelPusherForceTitle;
    private MLabel labelPusherMoveTitle;
    private MLabel labelPusherUnClenchingTimeTitle;
    private MLabel labelTitleSample;
    private MLabel labelTitleMeasured;
    // ======
    private MyTreeModel myTreeModel;
    private void start() {
        initComponents();
    }
    private void initComponents() {
        frame = CreateComponents.getFrame("View Archive", 1024, 800, false, null, null);
        frame.setBackground(Color.white);
        panelMain = CreateComponents.getPanelPrintableCap(
                null,
                null,
                null,
                0, 0, 700, 760,
                true, true
        );
        panelMain.setBackground(Color.white);
        frame.add(panelMain);
        panelPlot = CreateComponents.getMPanel(
                null,
                null,
                null,
                1, 140, 698, 460,
                true, true
        );
        panelMain.add(panelPlot);
        myTreeModel = new MyTreeModel();
        tree = new JTree(myTreeModel);
        tree.setEditable(false);
        tree.addTreeWillExpandListener(myTreeModel);
        tree.addTreeSelectionListener(myTreeModel);
        scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(700, 50, 300, 710); // max 760 (800 - 40)
        frame.add(scrollPane);
        //
        buttonPrint = CreateComponents.getButton("Печать", new Font("Dialog", Font.PLAIN,12),
                800, 10, 80, 30, this::callButtonPrinterPush, true, false);
        frame.add(buttonPrint);
        //
        CreateComponents.getLabel(panelMain, "Измеритель СПЦ участок ла-ла-ла", new Font("Times New Roman", Font.PLAIN, 32),
                350, 10, true, true, MLabel.POS_CENTER);
        labelDate = CreateComponents.getLabel(panelMain, "Время измерения", new Font("Times New Roman", Font.PLAIN, 16),
                350, 40, false, true, MLabel.POS_CENTER);
        labelUser = CreateComponents.getLabel(panelMain, "user", new Font("Times New Roman", Font.PLAIN, 16),
                350, 70, false, true, MLabel.POS_CENTER);
        labelPusherSampleTitle = CreateComponents.getLabel(panelMain, "pusher", new Font("Times New Roman", Font.PLAIN, 16),
                350, 100, false, true, MLabel.POS_CENTER);
        labelGraphTitle = CreateComponents.getLabel(panelMain, "Динамические  характеристики: ", new Font("Times New Roman", Font.PLAIN, 16),
                350, 120, false, true, MLabel.POS_CENTER);
        // =============
        int showY_Title    = 620;
        int showY_Sample   = 650;
        int showY_Measured = 680;
        int showX_Title    = 150;
        int showX_Force    = 200;
        int showX_Move     = 350;
        int showX_Time     = 500;
        //
        labelTitleSample = CreateComponents.getLabel(panelMain, "Образец?", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Title, showY_Sample, false, true, MLabel.POS_RIGHT);
        labelTitleMeasured = CreateComponents.getLabel(panelMain, "Измерение?", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Title, showY_Measured, false, true, MLabel.POS_RIGHT);
        //
        labelPusherForceTitle = CreateComponents.getLabel(panelMain, "Номинальное усилие", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Force, showY_Title, false, true, MLabel.POS_CENTER);
        labelPusherMoveTitle = CreateComponents.getLabel(panelMain, "Номинальный ход", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Move, showY_Title, false, true, MLabel.POS_CENTER);
        labelPusherUnClenchingTimeTitle = CreateComponents.getLabel(panelMain, "Время разжатия", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Time, showY_Title, false, true, MLabel.POS_CENTER);
        labelPusherSampleForce = CreateComponents.getLabel(panelMain, "Force", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Force, showY_Sample, false, true, MLabel.POS_CENTER);
        labelPusherSampleMove = CreateComponents.getLabel(panelMain, "Move", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Move, showY_Sample, false, true, MLabel.POS_CENTER);
        labelPusherSampleUnClenchingTime = CreateComponents.getLabel(panelMain, "Time", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Time, showY_Sample, false, true, MLabel.POS_CENTER);
        //
        labelPusherMeasuredForce = CreateComponents.getLabel(panelMain, "Force-M", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Force, showY_Measured, false, true, MLabel.POS_CENTER);
        labelPusherMeasuredMove = CreateComponents.getLabel(panelMain, "Move-M", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Move, showY_Measured, false, true, MLabel.POS_CENTER);
        labelPusherMeasuredUnClenchingTime = CreateComponents.getLabel(panelMain, "Time-M", new Font("Times New Roman", Font.PLAIN, 16),
                showX_Time, showY_Measured, false, true, MLabel.POS_CENTER);
        //
        frame.setVisible(true);
        frame.pack();
        //
        ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.Parameters plotParameters = new ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.Parameters();
        plotParameters.scale_img = Scale.scaleUp;
        // ************ ПОЛЯ ************
        // размер полей
        plotParameters.fieldSizeTop = 10;
        plotParameters.fieldSizeLeft = 70;
        plotParameters.fieldSizeRight = 70;
        plotParameters.fieldSizeBottom = 50;
        // цвет фона
        plotParameters.fieldBackColor = new Color(220, 220, 220);
        // цвет шрифта надписей по полям
        plotParameters.fieldFontColorTop = new Color(100, 100, 255);
        plotParameters.fieldFontColorBottom = new Color(80, 80, 80);
        // размер шрифта надписей по полям
        plotParameters.fieldFontSizeTop = 16;
        plotParameters.fieldFontSizeBottom = 16;
        // цвет рамки
        plotParameters.fieldFrameColor = new Color(120, 120, 120);
        // размер рамки
        plotParameters.fieldFrameWidth = 4;
        // ************ ОКНО ************
        // размер окна опреляется входным компонентом
        // цвет фона окна
        plotParameters.windowBackColor = new Color(255, 255, 255);
        // размер окна в мсек
        plotParameters.scaleZero_maxX = 5_000;
        // тип зумирования
        plotParameters.scaleZero_zoomX = ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.ZOOM_X_SHIFT;
        // ************ СЕТКА ************
        // цвет линии сетки
        plotParameters.netLineColor = new Color(50, 50, 50);
        // толщина линии сетки
        plotParameters.netLineWidth = 1;
        // ************ ТРЕНД1  ************
        // позитция подписи тренда относительно окна
        plotParameters.trend1_positionFromWindow = ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.TrendPosition.left;
        // попись условновной еденицы тренда
        plotParameters.trend1_text = "мм";
        // цвет шрифта подписи
        plotParameters.trend1_textFontColor = new Color(255, 0, 0);
        // размер шрифта подписи
        plotParameters.trend1_textFontSize = 16;
        // цвет линии тренда
        plotParameters.trend1_lineColor = new Color(255, 0, 0);
        // размер линии тренда
        plotParameters.trend1_lineWidth = 2;
        // начальное значение шкалы тренда
        plotParameters.trend1_zeroY_min = 0;
        // конечное значение шкалы тренда
        plotParameters.trend1_zeroY_max = 50;
        // режим автомасштабирования шкалы тренда
        plotParameters.trend1_AutoZoomY = ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.ZOOM_Y_FROM_SCALE;
        // ************ ТРЕНД2  ************
        // позитция подписи тренда относительно окна
        plotParameters.trend2_positionFromWindow = ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.TrendPosition.right;
        // попись условновной еденицы тренда
        plotParameters.trend2_text = "кг";
        // цвет шрифта подписи
        plotParameters.trend2_textFontColor = new Color(0, 200, 0);
        // размер шрифта подписи
        plotParameters.trend1_textFontSize = 16;
        // цвет линии тренда
        plotParameters.trend1_lineColor = new Color(0, 200, 0);
        // размер линии тренда
        plotParameters.trend1_lineWidth = 2;
        // начальное значение шкалы тренда
        plotParameters.trend1_zeroY_min = 0;
        // конечное значение шкалы тренда
        plotParameters.trend1_zeroY_max = 300;
        // режим автомасштабирования шкалы тренда
        plotParameters.trend1_AutoZoomY = ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.ZOOM_Y_FROM_SCALE;

        plot = ru.yandex.fixcolor.tests.spc.lib.plot2.Plot.createSwing(plotParameters, panelPlot);

        plot.clearScreen();
        plot.reFresh();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                callBack.closeArchive();
            }
        });
    }

    class MyTreeModel implements TreeModel, TreeWillExpandListener, TreeSelectionListener {
        private ArrayList<Shablon> nodes;

        public MyTreeModel() {
            try {
                nodes = ShYear.getListYear(conn);
            } catch (BaseDataException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object getRoot() {
            return root;
        }

        @Override
        public Object getChild(Object node, int index) {
            if (node == root)
                return nodes.get(index);
            return ((Shablon) node).children.get(index);
        }

        @Override
        public int getChildCount(Object node) {
            if (node == root)
                return nodes.size();
            return ((Shablon) node).children.size();
        }

        @Override
        public boolean isLeaf(Object node) {
            if (node == root) return false;
            boolean leaf = false;
            if (((Shablon) node).getLevel() == 3) {
                leaf = true;
            }
            return leaf;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {

        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            return 0;
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {

        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {

        }

        @Override
        public void treeWillExpand(TreeExpansionEvent event) {
            Object o = event.getPath().getLastPathComponent();
            if (o == root) return;
            Shablon comp = (Shablon) o;
            ArrayList<Shablon> x;
            if (comp.getLevel() > 2) return;
            if (comp.children.size() > 0) return;
            switch (comp.getLevel()) {
                case 0:
                    x = ShMounth.getListMounth(conn, comp.getName());
                    comp.children = x;
                    break;
                case 1:
                    x = ShDate.getListDates(conn, comp.getName());
                    comp.children = x;
                    break;
                case 2:
                    x = ShCheck.getListChecks(conn, comp.getName());
                    comp.children = x;
                    break;
            }
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) { }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getNewLeadSelectionPath();
            if (path == null) return;
            Object node = path.getLastPathComponent();
            if (node == root) return;
            int level = ((Shablon) node).getLevel();
            if (level < 3) return;
            showPusher(((Shablon) node).idx);
        }
    }
    private void showPusher(long id) {
        DataUnitMeasured measured;
        User user;
        Pusher pusherSample;
        try {
            measured = conn.getDataMeasured(id);
            user = conn.getUser(measured.id_user);
            pusherSample = conn.getPusher(measured.id_pusher);
        } catch (BaseDataException e) {
            MyLogger.myLog.log(Level.SEVERE, "визуализация архива", e);
            return;
        }
        showComponentsForVisual();
        // декодер графика
        MeasuredBlobDecoder blobDecoder;
        DistClass distClass;
        int tik0;
        try {
            blobDecoder = new MeasuredBlobDecoder(measured.dataMeasured);
            distClass = blobDecoder.get(0);
            tik0 = distClass.tik;
            plot.allDataClear();
            plot.clearScreen();
            for (int i = 0; i < blobDecoder.lenght(); i++) {
                distClass = blobDecoder.get(i);
                plot.newData(distClass.tik - tik0);
                plot.addTrend(distClass.distance);
                plot.addTrend(distClass.ves);
                plot.setData();
            }
            plot.paint();
            //Thread.sleep(10);
            plot.reFresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            // заголовок
            labelDate.setText("Дата измерения " +
                    (new SimpleDateFormat("dd-MM-yyyy в HH:mm:ss")).format(measured.dateTime));
            // здесь вывод данных о пользователе
            labelUser.setText("Оператор: " + user.surName);
            // здесь вывод данных о толкателе
            labelPusherSampleTitle.setText("Регистрационный номер толкателя: \"" + pusherSample.loggerPusher.namePusher +
                    "\", тип толкателя: \"" + pusherSample.loggerPusher.typePusher.loggerTypePusher.nameType + "\"");
            //
            labelPusherSampleForce.setText(String.valueOf(pusherSample.loggerPusher.typePusher.loggerTypePusher.forceNominal));
            labelPusherSampleMove.setText(String.valueOf(pusherSample.loggerPusher.typePusher.loggerTypePusher.moveNominal));
            labelPusherSampleUnClenchingTime.setText(String.valueOf(pusherSample.loggerPusher.typePusher.loggerTypePusher.unclenchingTime));
            // здесь вывод данных замера
            labelPusherMeasuredForce.setText(String.valueOf(measured.forceNominal));
            labelPusherMeasuredMove.setText(String.valueOf(measured.moveNominal));
            labelPusherMeasuredUnClenchingTime.setText(String.valueOf(measured.unclenchingTime));
        });
    }
    private void showComponentsForVisual() {
        if (buttonPrint.isEnabled()) return;
        SwingUtilities.invokeLater(() -> {
            buttonPrint.setEnabled(true);
            // декодер графика
            labelDate.setVisible(true);
            labelUser.setVisible(true);
            labelPusherSampleTitle.setVisible(true);
            labelGraphTitle.setVisible(true);
            labelPusherSampleForce.setVisible(true);
            labelPusherSampleMove.setVisible(true);
            labelPusherSampleUnClenchingTime.setVisible(true);
            labelPusherMeasuredForce.setVisible(true);
            labelPusherMeasuredMove.setVisible(true);
            labelPusherMeasuredUnClenchingTime.setVisible(true);
            labelPusherForceTitle.setVisible(true);
            labelPusherMoveTitle.setVisible(true);
            labelPusherUnClenchingTimeTitle.setVisible(true);
            labelTitleSample.setVisible(true);
            labelTitleMeasured.setVisible(true);
        });
    }
    private void callButtonPrinterPush(ActionEvent actionEvent) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pf = printerJob.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT);
        Paper paper = pf.getPaper();
        paper.setSize(PG_WIDTH, PG_HEIGHT);
        paper.setImageableArea(PG_LEFT, PG_TOP,
                PG_WIDTH - (PG_LEFT + PG_RIGHT),
                PG_HEIGHT - (PG_TOP + PG_BOTTOM));
        pf.setPaper(paper);
        Book book = new Book();
        book.append(panelMain, pf);
        printerJob.setPageable(book);
        if (! printerJob.printDialog()) {
            return;
        }
        try {
            printerJob.print();
        } catch (PrinterException e) {
            MyLogger.myLog.log(Level.SEVERE, "ошибка при печати", e);
        }
    }
}
