/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.test24.loader.dialog;

/**
 *
 * @author Gumerovmn
 */
public class EditTypePushers extends javax.swing.JFrame {

    /**
     * Creates new form EditTypePushers
     */
    public EditTypePushers() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableTypePushers = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        textForce = new javax.swing.JTextField();
        textMove = new javax.swing.JTextField();
        buttonAdd = new javax.swing.JButton();
        buttonEdit = new javax.swing.JButton();
        buttonClear = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        textUnclenching = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(640, 480));
        setResizable(false);
        getContentPane().setLayout(null);

        tableTypePushers.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        tableTypePushers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Тип толкателя", "Усилие на штоке (кг)", "Ход штока (мм)", "Время разжатия (сек)"
            }
        ));
        jScrollPane1.setViewportView(tableTypePushers);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(0, 0, 640, 220);

        jTextField1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jTextField1.setText("jTextField1");
        getContentPane().add(jTextField1);
        jTextField1.setBounds(30, 270, 400, 23);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel1.setText("Тип толкателя");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(120, 230, 210, 30);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setText("Усилие на штоке (кг)");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(30, 310, 190, 20);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel3.setText("Ход штока (мм)");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(40, 350, 160, 30);

        textForce.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        textForce.setText("jTextField2");
        getContentPane().add(textForce);
        textForce.setBounds(290, 310, 140, 23);

        textMove.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        textMove.setText("jTextField3");
        getContentPane().add(textMove);
        textMove.setBounds(290, 360, 140, 23);

        buttonAdd.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonAdd.setText("добавить");
        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddActionPerformed(evt);
            }
        });
        getContentPane().add(buttonAdd);
        buttonAdd.setBounds(470, 400, 120, 25);

        buttonEdit.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonEdit.setText("редактировать");
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });
        getContentPane().add(buttonEdit);
        buttonEdit.setBounds(470, 360, 120, 25);

        buttonClear.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonClear.setText("очистить");
        buttonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearActionPerformed(evt);
            }
        });
        getContentPane().add(buttonClear);
        buttonClear.setBounds(470, 320, 120, 25);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel4.setText("Время разжатия (сек)");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 390, 170, 30);

        textUnclenching.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        textUnclenching.setText("jTextField3");
        getContentPane().add(textUnclenching);
        textUnclenching.setBounds(290, 400, 140, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonAddActionPerformed

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonClearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EditTypePushers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditTypePushers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditTypePushers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditTypePushers.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditTypePushers().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAdd;
    private javax.swing.JButton buttonClear;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tableTypePushers;
    private javax.swing.JTextField textForce;
    private javax.swing.JTextField textMove;
    private javax.swing.JTextField textUnclenching;
    // End of variables declaration//GEN-END:variables
}
