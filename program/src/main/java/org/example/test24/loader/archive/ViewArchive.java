package org.example.test24.loader.archive;

import org.example.test24.bd.BaseData;
import org.example.test24.lib.swing.CreateComponents;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ViewArchive {
    private BaseData conn;
    public ViewArchive(BaseData conn) {
        this.conn = conn;
        new Thread(this::start, "thread view archive").start();
    }

    private String root = "Архив";
    private JFrame frame;
    private JTree tree;
    private JScrollPane scrollPane;
    private MyTreeModel myTreeModel;
    private void start() {
        initComponents();
    }
    private void initComponents() {
        frame = CreateComponents.getFrame("View Archive", 1024, 800, false, null, null);
        myTreeModel = new MyTreeModel();
        tree = new JTree(myTreeModel);
        tree.setEditable(false);
        tree.addTreeWillExpandListener(myTreeModel);
        scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(700, 0, 300, 760);
        frame.add(scrollPane);
        frame.setVisible(true);
    }
    class MyTreeModel implements TreeModel, TreeWillExpandListener {
        public MyTreeModel() {
        }

        @Override
        public Object getRoot() {
            return root;
        }

        @Override
        public Object getChild(Object parent, int index) {
            return null;
        }

        @Override
        public int getChildCount(Object parent) {
            return 0;
        }

        @Override
        public boolean isLeaf(Object node) {
            return false;
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

        }

        @Override
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

        }
    }
}
