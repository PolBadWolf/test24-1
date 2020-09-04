/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.yandex.fixcolor;

import javax.swing.*;
/**
 *
 * @author Gumerovmn
 */
public class StartFrame extends javax.swing.JFrame {

    /**
     * Creates new form StartFrame2
     */
    public StartFrame() {
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

        label1 = new javax.swing.JLabel();
        label2 = new javax.swing.JLabel();
        label3 = new javax.swing.JLabel();
        buttonEnter = new javax.swing.JButton();
        buttonWork = new javax.swing.JButton();
        buttonTuning = new javax.swing.JButton();
        buttonSetPassword = new javax.swing.JButton();
        fieldPassword = new javax.swing.JTextField();
        comboBoxUser = new javax.swing.JComboBox<>();
        comboBoxPusher = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        buttonEditUsers = new javax.swing.JButton();
        buttonEditPushers = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(640, 480));
        setResizable(false);
        getContentPane().setLayout(null);

        label1.setFont(new java.awt.Font("Times New Roman", 0, 57)); // NOI18N
        label1.setText("Стенд");
        getContentPane().add(label1);
        label1.setBounds(250, 20, 150, 66);

        label2.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label2.setText("испытания");
        getContentPane().add(label2);
        label2.setBounds(240, 70, 180, 42);

        label3.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        label3.setText("гидротолкателей");
        getContentPane().add(label3);
        label3.setBounds(200, 100, 260, 42);

        buttonEnter.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonEnter.setText("проверка");
        getContentPane().add(buttonEnter);
        buttonEnter.setBounds(320, 230, 90, 24);

        buttonWork.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonWork.setText("работа");
        getContentPane().add(buttonWork);
        buttonWork.setBounds(200, 330, 90, 24);

        buttonTuning.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonTuning.setText("настройка");
        getContentPane().add(buttonTuning);
        buttonTuning.setBounds(190, 370, 116, 24);

        buttonSetPassword.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonSetPassword.setText("новый пароль");
        getContentPane().add(buttonSetPassword);
        buttonSetPassword.setBounds(420, 230, 116, 24);

        fieldPassword.setText("Enter Password");
        getContentPane().add(fieldPassword);
        fieldPassword.setBounds(190, 230, 120, 24);

        comboBoxUser.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        comboBoxUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Users" }));
        getContentPane().add(comboBoxUser);
        comboBoxUser.setBounds(190, 190, 350, 24);

        comboBoxPusher.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        comboBoxPusher.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pushers" }));
        getContentPane().add(comboBoxPusher);
        comboBoxPusher.setBounds(190, 270, 350, 24);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel1.setText("Пользователь : ");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(100, 200, 90, 14);

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel2.setText("Пароль :");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(100, 240, 90, 16);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel3.setText("Толкатель :");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(100, 270, 90, 16);

        jPanel1.setBorder(BorderFactory.createTitledBorder("редактирование"));
        jPanel1.setToolTipText("");
        jPanel1.setLayout(null);

        buttonEditUsers.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonEditUsers.setText("Пользователей");
        buttonEditUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditUsersActionPerformed(evt);
            }
        });
        jPanel1.add(buttonEditUsers);
        buttonEditUsers.setBounds(20, 20, 120, 25);

        buttonEditPushers.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        buttonEditPushers.setText("Толкателей");
        buttonEditPushers.setToolTipText("123");
        jPanel1.add(buttonEditPushers);
        buttonEditPushers.setBounds(20, 55, 120, 25);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(380, 310, 160, 90);
        jPanel1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonEditUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditUsersActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonEditUsersActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        */
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonEditPushers;
    private javax.swing.JButton buttonEditUsers;
    private javax.swing.JButton buttonEnter;
    private javax.swing.JButton buttonSetPassword;
    private javax.swing.JButton buttonTuning;
    private javax.swing.JButton buttonWork;
    private javax.swing.JComboBox<String> comboBoxPusher;
    private javax.swing.JComboBox<String> comboBoxUser;
    private javax.swing.JTextField fieldPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel label1;
    private javax.swing.JLabel label2;
    private javax.swing.JLabel label3;
    // End of variables declaration//GEN-END:variables
}