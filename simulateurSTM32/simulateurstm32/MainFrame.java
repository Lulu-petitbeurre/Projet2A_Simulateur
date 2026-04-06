package ensea.simulateurSTM32.simulateurstm32;

/*
 * MainFrame.java
 *
 *@author  Betty && Samira
 *
 */

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import ensea.simulateurSTM32.ihm.Ihm;
import ensea.simulateurSTM32.parseur.AssLvl_1;
import ensea.simulateurSTM32.parseur.Assembleur;

public class MainFrame extends javax.swing.JFrame implements WindowListener {
    public static Ihm ihm;
    public static Assembleur assPrincipal;
    
    public MainFrame() {
        initComponents();
        assPrincipal = new AssLvl_1();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    // GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        // Variables declaration - do not modify//GEN-BEGIN:variables
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JButton jButton1 = new javax.swing.JButton();
        javax.swing.JButton jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LCM3 : Le simulateur made in ENSEA");
        setBounds(new java.awt.Rectangle(400, 200, 0, 0));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jButton1.setText("J'adoooore les microprocesseurs");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jButton2.setText("J'aime bien aussi mais...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         ihm = new Ihm();
         ihm.setVisible(true);
         ihm.addWindowListener(this);
         assPrincipal.preparerES(); 
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton2ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // End of variables declaration//GEN-END:variables

    public void windowOpened(WindowEvent e) {
        this.setVisible(false);
    }

    public void windowClosing(WindowEvent e) {
        
    }

    public void windowClosed(WindowEvent e) {
        this.setVisible(true);
    }

    public void windowIconified(WindowEvent e) {
        
    }

    public void windowDeiconified(WindowEvent e) {
        
    }

    public void windowActivated(WindowEvent e) {
        
    }

    public void windowDeactivated(WindowEvent e) {
        
    }
    
}
