package org.example.test24.loader.archive;

import org.example.test24.bd.BaseData;
import org.example.test24.bd.BaseDataException;
import org.example.test24.lib.swing.CreateComponents;
import ru.yandex.fixcolor.my_lib.graphics.swing.Plot;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;

public class ViewArchive {
    private BaseData conn;
    public ViewArchive(BaseData conn) {
        this.conn = conn;
        new Thread(this::start, "thread view archive").start();
    }

    private String root = "Архив";
    private JFrame frame;
    private JPanel panelMain;
    private Plot plot;
    private JTree tree;
    private JScrollPane scrollPane;
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
        scrollPane.setBounds(700, 0, 300, 760);
        frame.add(scrollPane);
        frame.setVisible(true);
        frame.pack();
        //
        plot = new Plot(panelMain, 0, 200, 700, 460, 50, 50);
        plot.addTrend(Color.WHITE, 2);
        plot.setNetLineColor(Plot.DARKGREEN);
        plot.setNetLineWidth(1.0);

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
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            Object o = event.getPath().getLastPathComponent();
            if (o == root) return;
            Shablon comp = (Shablon) o;
            ArrayList<Shablon> x;
            if (comp.getLevel() > 3) return;
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
                case 3:
                    int ind = comp.idx;
                    String n = comp.name;

                    break;
            }
        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            Object node = e.getNewLeadSelectionPath().getLastPathComponent();
            if (node == root) return;
            int level = ((Shablon) node).getLevel();
            if (level < 3) return;
        }
    }
}
