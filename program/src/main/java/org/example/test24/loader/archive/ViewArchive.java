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
import ru.yandex.fixcolor.my_lib.graphics.swing.Plot;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

public class ViewArchive {
    private final BaseData conn;
    public ViewArchive(BaseData conn) {
        this.conn = conn;
        new Thread(this::start, "thread view archive").start();
    }

    private final String root = "Архив";
    private JFrame frame;
    private JPanel panelMain;
    private Plot plot;
    private JTree tree;
    private JScrollPane scrollPane;
    // ======
    private JButton buttonPrint;
    private MLabel labelDate;
    // ======
    private MyTreeModel myTreeModel;
    private void start() {
        initComponents();
    }
    private void initComponents() {
        frame = CreateComponents.getFrame("View Archive", 1024, 800, false, null, null);
        panelMain = CreateComponents.getPanel(null, null, null, 0, 0, 700, 760,true, true);
        frame.add(panelMain);
        myTreeModel = new MyTreeModel();
        tree = new JTree(myTreeModel);
        tree.setEditable(false);
        tree.addTreeWillExpandListener(myTreeModel);
        tree.addTreeSelectionListener(myTreeModel);
        scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(700, 0, 300, 660); // max 760 (800 - 40)
        frame.add(scrollPane);
        //
        buttonPrint = CreateComponents.getButton("Печать", new Font("Dialog", Font.PLAIN,12),
                800, 700, 80, 30, null, true, false);
        frame.add(buttonPrint);
        //
//        CreateComponents.getLabel(panelMain, "Измеритель СПЦ участок ла-ла-ла", new Font("Times New Roman", Font.PLAIN, 32),
//                350, 10, true, true, MLabel.POS_CENTER);
        labelDate = CreateComponents.getLabel(panelMain, "Время измерения", new Font("Times New Roman", Font.PLAIN, 16),
                350, 40, true, true, MLabel.POS_CENTER);
        //
        frame.setVisible(true);
        frame.pack();
        //
        plot = new Plot(panelMain, 0, 200, 700, 460, 50, 50);
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
        buttonPrint.setEnabled(true);
        // загаловок
        labelDate.setText("Дата измерения " +
                (new SimpleDateFormat("dd-MM-yyyy в HH:mm:ss")).format(measured.dateTime));
        // здесь вывод данных о пользователе
        // здесь вывод данных о толкателе
        // здесь вывод данных замера
        int a = 5;
    }
}
