/*
 * PanelRegistres.java
 *
 *
 */

package ensea.simulateurSTM32.ihm.Panel;


import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
/**
 *
 * Affichage des registres -Entier - Hexadécimal
 */

public class PanelRegistres extends javax.swing.JPanel {
    int formatAff=0;//0 entier et 1 hexa

    //  public Ram registres;
    int a=0;

    /** Creates new form PanelRegistres */
    public PanelRegistres() { //  public PanelRegistres(Ram ram, Fetch f) {

        initComponents();

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBarPC = new javax.swing.JToolBar();
        jLabelPC = new javax.swing.JLabel();
        jTextFieldPC = new javax.swing.JTextField();
        jToolBarFormat = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        jToolBarR0 = new javax.swing.JToolBar();
        jLabelR0 = new javax.swing.JLabel();
        jTextFieldR0 = new javax.swing.JTextField();
        jToolBarR1 = new javax.swing.JToolBar();
        jLabelR1 = new javax.swing.JLabel();
        jTextFieldR1 = new javax.swing.JTextField();
        jToolBarR2 = new javax.swing.JToolBar();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldR2 = new javax.swing.JTextField();
        jToolBarR3 = new javax.swing.JToolBar();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldR3 = new javax.swing.JTextField();
        jToolBarR4 = new javax.swing.JToolBar();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldR4 = new javax.swing.JTextField();
        jToolBarR5 = new javax.swing.JToolBar();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldR5 = new javax.swing.JTextField();
        jToolBarR6 = new javax.swing.JToolBar();
        jLabelR6 = new javax.swing.JLabel();
        jTextFieldR6 = new javax.swing.JTextField();
        jToolBarR7 = new javax.swing.JToolBar();
        jLabelR7 = new javax.swing.JLabel();
        jTextFieldR7 = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(200, 180));
        setMaximumSize(new java.awt.Dimension(200, 180));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.GridLayout(10, 1, 2, 2));

        jToolBarPC.setBorder(null);
        jToolBarPC.setRollover(true);
        jToolBarPC.setEnabled(false);

        jLabelPC.setText("PC");
        jLabelPC.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabelPC.setPreferredSize(new java.awt.Dimension(21, 14));
        jLabelPC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelPCMouseClicked(evt);
            }
        });
        jToolBarPC.add(jLabelPC);

        jTextFieldPC.setEditable(false);
        jTextFieldPC.setBackground(new java.awt.Color(120, 61, 13));
        jTextFieldPC.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldPC.setForeground(new java.awt.Color(255, 255, 51));
        jTextFieldPC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldPC.setText("0");
        jTextFieldPC.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldPC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPCActionPerformed(evt);
            }
        });
        jToolBarPC.add(jTextFieldPC);

        add(jToolBarPC);

        jToolBarFormat.setRollover(true);
        jToolBarFormat.setEnabled(false);
        jToolBarFormat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToolBarFormatMouseClicked(evt);
            }
        });

        jLabel1.setText("Hex/Dec");
        jToolBarFormat.add(jLabel1);

        add(jToolBarFormat);

        jToolBarR0.setBorder(null);
        jToolBarR0.setRollover(true);
        jToolBarR0.setEnabled(false);


        jLabelR0.setText("R0");
        jLabelR0.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabelR0.setPreferredSize(new java.awt.Dimension(21, 14));
        jLabelR0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelR0MouseClicked(evt);
            }
        });
        jToolBarR0.add(jLabelR0);

        jTextFieldR0.setEditable(false);
        jTextFieldR0.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR0.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR0.setText("0");
        jTextFieldR0.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR0ActionPerformed(evt);
            }
        });
        jTextFieldR0.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR0KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR0KeyReleased(evt);
            }
        });
        jToolBarR0.add(jTextFieldR0);

        add(jToolBarR0);

        jToolBarR1.setBorder(null);
        jToolBarR1.setRollover(true);
        jToolBarR1.setEnabled(false);

        jLabelR1.setText("R1");
        jLabelR1.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabelR1.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR1.add(jLabelR1);

        jTextFieldR1.setEditable(false);
        jTextFieldR1.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR1.setText("0");
        jTextFieldR1.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR1ActionPerformed(evt);
            }
        });
        jTextFieldR1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR1KeyReleased(evt);
            }
        });
        jToolBarR1.add(jTextFieldR1);

        add(jToolBarR1);

        jToolBarR2.setBorder(null);
        jToolBarR2.setRollover(true);
        jToolBarR2.setEnabled(false);

        jLabel5.setText("R2");
        jLabel5.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR2.add(jLabel5);

        jTextFieldR2.setEditable(false);
        jTextFieldR2.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR2.setText("0");
        jTextFieldR2.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR2ActionPerformed(evt);
            }
        });
        jTextFieldR2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR2KeyReleased(evt);
            }
        });
        jToolBarR2.add(jTextFieldR2);

        add(jToolBarR2);

        jToolBarR3.setBorder(null);
        jToolBarR3.setRollover(true);
        jToolBarR3.setEnabled(false);

        jLabel6.setText("R3");
        jLabel6.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabel6.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR3.add(jLabel6);

        jTextFieldR3.setEditable(false);
        jTextFieldR3.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR3.setText("0");
        jTextFieldR3.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR3ActionPerformed(evt);
            }
        });
        jTextFieldR3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR3KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR3KeyReleased(evt);
            }
        });
        jToolBarR3.add(jTextFieldR3);

        add(jToolBarR3);

        jToolBarR4.setBorder(null);
        jToolBarR4.setRollover(true);
        jToolBarR4.setEnabled(false);

        jLabel7.setText("R4");
        jLabel7.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabel7.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR4.add(jLabel7);

        jTextFieldR4.setEditable(false);
        jTextFieldR4.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR4.setText("0");
        jTextFieldR4.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR4ActionPerformed(evt);
            }
        });
        jTextFieldR4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR4KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR4KeyReleased(evt);
            }
        });
        jToolBarR4.add(jTextFieldR4);

        add(jToolBarR4);

        jToolBarR5.setBorder(null);
        jToolBarR5.setRollover(true);
        jToolBarR5.setEnabled(false);

        jLabel8.setText("R5");
        jLabel8.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabel8.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR5.add(jLabel8);

        jTextFieldR5.setEditable(false);
        jTextFieldR5.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR5.setText("0");
        jTextFieldR5.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR5ActionPerformed(evt);
            }
        });
        jTextFieldR5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR5KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR5KeyReleased(evt);
            }
        });
        jToolBarR5.add(jTextFieldR5);

        add(jToolBarR5);

        jToolBarR6.setBorder(null);
        jToolBarR6.setRollover(true);
        jToolBarR6.setEnabled(false);

        jLabelR6.setText("R6");
        jLabelR6.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabelR6.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR6.add(jLabelR6);

        jTextFieldR6.setEditable(false);
        jTextFieldR6.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR6.setText("0");
        jTextFieldR6.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR6ActionPerformed(evt);
            }
        });
        jTextFieldR6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR6KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR6KeyReleased(evt);
            }
        });
        jToolBarR6.add(jTextFieldR6);

        add(jToolBarR6);

        jToolBarR7.setBorder(null);
        jToolBarR7.setRollover(true);
        jToolBarR7.setEnabled(false);

        jLabelR7.setText("R7");
        jLabelR7.setMaximumSize(new java.awt.Dimension(25, 14));
        jLabelR7.setPreferredSize(new java.awt.Dimension(21, 14));
        jToolBarR7.add(jLabelR7);

        jTextFieldR7.setEditable(false);
        jTextFieldR7.setFont(new java.awt.Font("Courier New", Font.PLAIN, 11)); // NOI18N
        jTextFieldR7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldR7.setText("0");
        jTextFieldR7.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextFieldR7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldR7ActionPerformed(evt);
            }
        });
        jTextFieldR7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldR7KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldR7KeyReleased(evt);
            }
        });
        jToolBarR7.add(jTextFieldR7);

        add(jToolBarR7);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldR7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR7ActionPerformed

    }//GEN-LAST:event_jTextFieldR7ActionPerformed

    private void jTextFieldR2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR2ActionPerformed

    }//GEN-LAST:event_jTextFieldR2ActionPerformed


    private void jTextFieldR4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR4ActionPerformed


    }//GEN-LAST:event_jTextFieldR4ActionPerformed

    private void jTextFieldR6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR6ActionPerformed

    }//GEN-LAST:event_jTextFieldR6ActionPerformed

    private void jTextFieldPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPCActionPerformed

    }//GEN-LAST:event_jTextFieldPCActionPerformed

    private void jTextFieldR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR1ActionPerformed

        String contenu= jTextFieldR1.getText().substring(1);

        jTextFieldR1.setBackground(Color.white);


    }//GEN-LAST:event_jTextFieldR1ActionPerformed

    private void jTextFieldR3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR3ActionPerformed

        String contenu= jTextFieldR3.getText().substring(1);

        jTextFieldR3.setBackground(Color.white);


    }//GEN-LAST:event_jTextFieldR3ActionPerformed

    private void jTextFieldR5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR5ActionPerformed

        String contenu= jTextFieldR5.getText().substring(1);

        jTextFieldR5.setBackground(Color.white);

    }//GEN-LAST:event_jTextFieldR5ActionPerformed


    private void jTextFieldR0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldR0ActionPerformed

        String contenu= jTextFieldR0.getText().substring(1);

        jTextFieldR0.setBackground(Color.white);



    }//GEN-LAST:event_jTextFieldR0ActionPerformed

    private void jTextFieldR0KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR0KeyReleased
        //controleSyntaxe2(jTextFieldR0);
    }//GEN-LAST:event_jTextFieldR0KeyReleased

    private void jTextFieldR0KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR0KeyPressed
        //controleSyntaxe1(jTextFieldR0,evt);
        //jTextFieldR0.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR0KeyPressed

    private void jTextFieldR1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR1KeyPressed
        //controleSyntaxe1(jTextFieldR1,evt);
        //jTextFieldR1.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR1KeyPressed

    private void jTextFieldR1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR1KeyReleased
        //controleSyntaxe2(jTextFieldR1);
    }//GEN-LAST:event_jTextFieldR1KeyReleased

    private void jTextFieldR2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR2KeyPressed
        //controleSyntaxe1(jTextFieldR2,evt);
        //jTextFieldR2.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR2KeyPressed

    private void jTextFieldR2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR2KeyReleased
        //controleSyntaxe2(jTextFieldR2);
    }//GEN-LAST:event_jTextFieldR2KeyReleased

    private void jTextFieldR3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR3KeyPressed

        controleSyntaxe1(jTextFieldR3,evt);
        jTextFieldR3.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR3KeyPressed

    private void jTextFieldR3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR3KeyReleased
        controleSyntaxe2(jTextFieldR3);
    }//GEN-LAST:event_jTextFieldR3KeyReleased

    private void jTextFieldR4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR4KeyPressed

        controleSyntaxe1(jTextFieldR4,evt);
        jTextFieldR4.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR4KeyPressed

    private void jTextFieldR4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR4KeyReleased
        controleSyntaxe2(jTextFieldR4);
    }//GEN-LAST:event_jTextFieldR4KeyReleased

    private void jTextFieldR5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR5KeyPressed

        controleSyntaxe1(jTextFieldR5,evt);
        jTextFieldR5.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR5KeyPressed

    private void jTextFieldR5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR5KeyReleased
        controleSyntaxe2(jTextFieldR5);
    }//GEN-LAST:event_jTextFieldR5KeyReleased

    private void jTextFieldR6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR6KeyPressed

        controleSyntaxe1(jTextFieldR6,evt);
        jTextFieldR6.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR6KeyPressed

    private void jTextFieldR6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR6KeyReleased
        controleSyntaxe2(jTextFieldR6);
    }//GEN-LAST:event_jTextFieldR6KeyReleased

    private void jTextFieldR7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR7KeyPressed
        controleSyntaxe1(jTextFieldR7,evt);
        jTextFieldR7.setBackground(Color.green);
    }//GEN-LAST:event_jTextFieldR7KeyPressed

    private void jTextFieldR7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldR7KeyReleased
        controleSyntaxe2(jTextFieldR7);
    }//GEN-LAST:event_jTextFieldR7KeyReleased

    private void jLabelPCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPCMouseClicked

    }//GEN-LAST:event_jLabelPCMouseClicked

    private void jLabelR0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelR0MouseClicked
        int val;
        if (formatAff==0)
            formatAff=1;
        else
            formatAff=0;
        if(formatAff==1){
            val=Integer.parseInt(jTextFieldR0.getText(),10);
        }
        else{
            String buf;
            buf=jTextFieldR0.getText();
            buf=buf.substring(2);

            if(buf.length()==8){
                if(buf.charAt(0)>'7'){
                    String MSByte;
                    int n;
                    MSByte=buf.substring(0,1);
                    n=Integer.parseInt(MSByte,16);
                    n=n&0x7;
                    buf=buf.replaceFirst(MSByte,Integer.toHexString(n));
                    val=Integer.parseInt(buf,16);
                    val=val|0x80000000;
                }
                else
                    val=Integer.parseInt(buf,16);

            }else
                val=Integer.parseInt(buf,16);
        }

        affValReg(0,val,formatAff);
    }//GEN-LAST:event_jLabelR0MouseClicked

    private void jToolBarFormatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToolBarFormatMouseClicked

        JTextField[] tabReg = new JTextField[8];
        tabReg[0]=jTextFieldR0;
        tabReg[1]=jTextFieldR1;
        tabReg[2]=jTextFieldR2;
        tabReg[3]=jTextFieldR3;
        tabReg[4]=jTextFieldR4;
        tabReg[5]=jTextFieldR5;
        tabReg[6]=jTextFieldR6;
        tabReg[7]=jTextFieldR7;

        int val;
        int i;


        if (formatAff==0)
            formatAff=1;
        else
            formatAff=0;

        for( i=0;i<=7;i++){
            if(formatAff==1){
                val=Integer.parseInt(tabReg[i].getText(),10);
            }
            else{
                String buf;
                buf=tabReg[i].getText();
                buf=buf.substring(2);

                if(buf.length()==8){
                    if(buf.charAt(0)>'7'){
                        String MSByte;
                        int n;
                        MSByte=buf.substring(0,1);
                        n=Integer.parseInt(MSByte,16);
                        n=n&0x7;
                        buf=buf.replaceFirst(MSByte,Integer.toHexString(n));
                        val=Integer.parseInt(buf,16);
                        val=val|0x80000000;
                    }
                    else
                        val=Integer.parseInt(buf,16);

                }else
                    val=Integer.parseInt(buf,16);
            }

            affValReg(i,val,formatAff);

        }
    }//GEN-LAST:event_jToolBarFormatMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabelPC;
    private javax.swing.JLabel jLabelR0;
    private javax.swing.JLabel jLabelR1;
    private javax.swing.JLabel jLabelR6;
    private javax.swing.JLabel jLabelR7;
    private javax.swing.JTextField jTextFieldPC;
    private javax.swing.JTextField jTextFieldR0;
    private javax.swing.JTextField jTextFieldR1;
    private javax.swing.JTextField jTextFieldR2;
    private javax.swing.JTextField jTextFieldR3;
    private javax.swing.JTextField jTextFieldR4;
    private javax.swing.JTextField jTextFieldR5;
    private javax.swing.JTextField jTextFieldR6;
    private javax.swing.JTextField jTextFieldR7;
    private javax.swing.JToolBar jToolBarFormat;
    private javax.swing.JToolBar jToolBarPC;
    private javax.swing.JToolBar jToolBarR0;
    private javax.swing.JToolBar jToolBarR1;
    private javax.swing.JToolBar jToolBarR2;
    private javax.swing.JToolBar jToolBarR3;
    private javax.swing.JToolBar jToolBarR4;
    private javax.swing.JToolBar jToolBarR5;
    private javax.swing.JToolBar jToolBarR6;
    private javax.swing.JToolBar jToolBarR7;
    // End of variables declaration//GEN-END:variables



    public void controleSyntaxe2(JTextField j){

        String s= j.getText();

        if (s.isEmpty()){
            j.setText("$");
            a=0;}

        if (s.charAt(0)!='$'){
            j.setText("$");
            a=0;
        }

        if (a==1){
            s=s.substring(0,s.length()-1);
            j.setText(s);
            a=0;
        }

        while(s.length()>5){
            s=s.substring(0,s.length()-1);
            j.setText(s);
        }

    }

    public void controleSyntaxe1(JTextField j,  java.awt.event.KeyEvent evt){

        if (a==1){
            String s= j.getText();
            s=s.substring(0,s.length()-1);
            j.setText(s);
            a=0;
        }

        boolean cond= ((((int)evt.getKeyChar()>102) && ((int)evt.getKeyChar()<256))
                ||(((int)evt.getKeyChar()>70) && ((int)evt.getKeyChar()<97))
                ||(((int)evt.getKeyChar()>57) && ((int)evt.getKeyChar()<65))
                ||(((int)evt.getKeyChar()<48)))
                &&(evt.getKeyCode()!=10);

        if (cond) a=1;

        String s= j.getText();
        while(s.length()>5){
            s=s.substring(0,s.length()-1);
            j.setText(s);
        }
    }


    public void initAff() {
        formatAff=1;
        for(int i=0;i<8;i++){
            //setValReg(i,1);
            affValReg(i,0,formatAff);}
        setValPC();
    }
    void setModifReg(int loc, int val) {
        switch (loc)
        {
            case 0:
                jTextFieldR0.setForeground(Color.red);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR0.setText(String.valueOf(val));
                affValReg(0,val,formatAff);
                break;

            case 1:
                jTextFieldR1.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR1.setText(String.valueOf(val));
                affValReg(1,val,formatAff);
                break;

            case 2:
                jTextFieldR2.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR2.setText(String.valueOf(val));
                affValReg(2,val,formatAff);
                break;

            case 3:
                jTextFieldR3.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR3.setText(String.valueOf(val));
                affValReg(3,val,formatAff);
                break;

            case 4:
                jTextFieldR4.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR4.setText(String.valueOf(val));
                affValReg(4,val,formatAff);
                break;

            case 5:
                jTextFieldR5.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR5.setText(String.valueOf(val));
                affValReg(5,val,formatAff);
                break;

            case 6:
                jTextFieldR6.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR7.setForeground(Color.black);

                //jTextFieldR6.setText(String.valueOf(val));
                affValReg(6,val,formatAff);
                break;

            case 7:
                jTextFieldR7.setForeground(Color.red);
                jTextFieldR0.setForeground(Color.black);
                jTextFieldR1.setForeground(Color.black);
                jTextFieldR2.setForeground(Color.black);
                jTextFieldR3.setForeground(Color.black);
                jTextFieldR4.setForeground(Color.black);
                jTextFieldR5.setForeground(Color.black);
                jTextFieldR6.setForeground(Color.black);

                //jTextFieldR7.setText(String.valueOf(val));
                affValReg(7,val,formatAff);
                break;

            default:
        }




    }



    public void affValReg(int loc,int val,int format){
        String buf;
        if(format==1){
            buf="0x";
            buf=buf.concat(Integer.toHexString(val));
        }
        else{
            buf="";
            buf=buf.concat(String.valueOf(val));
        }

        switch (loc)
        {
            case 0:

                jTextFieldR0.setText(buf);
                break;

            case 1:

                jTextFieldR1.setText(buf);
                break;

            case 2:

                jTextFieldR2.setText(buf);
                break;

            case 3:

                jTextFieldR3.setText(buf);
                break;

            case 4:
                jTextFieldR4.setText(buf);
                break;

            case 5:

                jTextFieldR5.setText(buf);
                break;

            case 6:

                jTextFieldR6.setText(buf);
                break;

            case 7:

                jTextFieldR7.setText(buf);
                break;



            default:

        }
    }
    void setModifPC(int val){
        jTextFieldPC.setForeground(Color.yellow);
        //jTextFieldPC.setText(String.valueOf(val));
        String buf= "0x";
        buf=buf.concat(Integer.toHexString(val));
        jTextFieldPC.setText(buf);
    }
    void setValPC(){
        jTextFieldPC.setForeground(Color.green);
        String buf= "0x";
        buf=buf.concat(Integer.toHexString(134217728));
        jTextFieldPC.setText(buf);
    }
}