package org.example.test24.loader.archive;

import org.example.test24.allinterface.bd.DistClass;
import org.example.test24.allinterface.bd.MeasuredBlobDecoder;
import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.bd.usertypes.DataUnitMeasured;
import org.example.test24.bd.usertypes.Pusher;
import org.example.test24.bd.usertypes.User;
import org.example.test24.lib.MyLogger;
import org.example.test24.lib.swing.CreateComponents;
import org.example.test24.lib.swing.MLabel;
import org.example.test24.lib.swing.MPanelPrintableCap;
import org.example.test24.lib.swing.Scale;
import ru.yandex.fixcolor.my_lib.graphics.swing.Plot;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

public class ViewArchive {
    // =====================
    private static final float PG_WIDTH  = Math.round(210.0f * Scale.MM2UNIT_SCR);
    private static final float PG_HEIGHT = Math.round(297.0f * Scale.MM2UNIT_SCR);
    private static final float PG_LEFT   = (int) Math.ceil(30.0F * Scale.MM2UNIT_SCR);
    private static final float PG_RIGHT  = (int) Math.ceil(15.0F * Scale.MM2UNIT_SCR);
    private static final float PG_TOP    = (int) Math.ceil(20.0F * Scale.MM2UNIT_SCR);
    private static final float PG_BOTTOM = (int) Math.ceil(20.0F * Scale.MM2UNIT_SCR);
    // =====================
    private final BaseData conn;
    public ViewArchive(BaseData conn) {
        this.conn = conn;
        new Thread(this::start, "thread view archive").start();
    }

    private final String root = "Архив";
    private JFrame frame;
    private MPanelPrintableCap panelMain;
    private Plot plot;
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
    // ======
    private MyTreeModel myTreeModel;
    private void start() {
        initComponents();
    }
    private void initComponents() {
        frame = CreateComponents.getFrame("View Archive", 1024, 800, false, null, null);
        frame.setBackground(Color.white);
        panelMain = CreateComponents.getPanelPrintableCap(null, null, null, 0, 0, 700, 760,true, true);
        panelMain.setBackground(Color.white);
        frame.add(panelMain);
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
        labelGraphTitle = CreateComponents.getLabel(panelMain, "Динамические характеристики:", new Font("Times New Roman", Font.PLAIN, 16),
                350, 120, false, true, MLabel.POS_CENTER);
        //
        labelPusherSampleForce = CreateComponents.getLabel(panelMain, "Force", new Font("Times New Roman", Font.PLAIN, 16),
                200, 640, false, true, MLabel.POS_CENTER);
        labelPusherSampleMove = CreateComponents.getLabel(panelMain, "Move", new Font("Times New Roman", Font.PLAIN, 16),
                350, 640, false, true, MLabel.POS_CENTER);
        labelPusherSampleUnClenchingTime = CreateComponents.getLabel(panelMain, "Time", new Font("Times New Roman", Font.PLAIN, 16),
                500, 640, false, true, MLabel.POS_CENTER);
        //
        labelPusherMeasuredForce = CreateComponents.getLabel(panelMain, "Force-M", new Font("Times New Roman", Font.PLAIN, 16),
                200, 670, false, true, MLabel.POS_CENTER);
        labelPusherMeasuredMove = CreateComponents.getLabel(panelMain, "Move-M", new Font("Times New Roman", Font.PLAIN, 16),
                350, 670, false, true, MLabel.POS_CENTER);
        labelPusherMeasuredUnClenchingTime = CreateComponents.getLabel(panelMain, "Time-M", new Font("Times New Roman", Font.PLAIN, 16),
                500, 670, false, true, MLabel.POS_CENTER);
        //
        frame.setVisible(true);
        frame.pack();
        //
        plot = new Plot(panelMain, 1, 140, 698, 460, 50, 50);
        plot.addTrend(Color.WHITE, 2);
        plot.setNetLineColor(Plot.DARKGREEN);
        plot.setNetLineWidth(1.0f);

        plot.clearScreen();

        plot.setZoomY(0, 1024);
        plot.setZoomYauto(false);

        plot.setZoomX(0, 5_000 / 5);
        plot.setZoomXlenghtAuto(true);
        plot.setZoomXbeginAuto(false);
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
            Object node = e.getNewLeadSelectionPath().getLastPathComponent();
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
        int tik0, x;
        try {
            blobDecoder = new MeasuredBlobDecoder(measured.dataMeasured);
            distClass = blobDecoder.get(0);
            tik0 = distClass.tik;
            plot.allDataClear();
            plot.clearScreen();
            for (int i = 0; i < blobDecoder.lenght(); i++) {
                distClass = blobDecoder.get(i);
                x = (short)((distClass.tik - tik0) / 5);
                plot.newDataX(x);
                plot.newDataTrend(0, (short) distClass.distance);
                //plot.newDataTrend(1, ves);
                plot.newDataPush();
            }
            plot.rePaint();
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
