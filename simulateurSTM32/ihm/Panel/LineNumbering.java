package ensea.simulateurSTM32.ihm.Panel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class LineNumbering extends JPanel {
    // Le composant texte pour lequel on affiche les numéros de ligne
    private final JTextComponent textComp;

    // Mémo du dernier nombre de lignes connu — utilisé pour déclencher
    // revalidation/repaint seulement quand il y a un changement.
    private int lastLineCount = 0;

    public LineNumbering(JTextComponent textComp) {
        this.textComp = textComp;
        setFont(textComp.getFont());
        setBackground(new Color(240,240,240));
        // On crée un petit Timer (100ms) qui vérifie si le nombre de lignes a changé
        // Solution utilisée car il n'y a pas d'événement spécifique pour le changement
        Timer timer = new Timer(100, _ -> {
            int currentLineCount = getLineCount();
            if (currentLineCount != lastLineCount) {
                lastLineCount = currentLineCount;
                revalidate();
                repaint();
            }
        });
        timer.start();
    }

    public void updateLineNumbers() {
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        int lineCount = getLineCount();
        int digits = Math.max(2, String.valueOf(lineCount).length());

        // Largeur approximative = largeur d'un chiffre * nb_chiffres + marge
        int width = digits * getFontMetrics(getFont()).charWidth('0') + 10;
        return new Dimension(width, textComp.getHeight());
    }

    private int getLineCount() {
        Element root = textComp.getDocument().getDefaultRootElement();
        return root.getElementCount();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // on cast pour plus de flexibilité
        FontMetrics fm = g2.getFontMetrics();

        Element root = textComp.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();

        for (int i = 0; i < lineCount; i++) {
            try {
                int startOffset = root.getElement(i).getStartOffset();

                // On obtient la position du début de la ligne dans le composant
                Rectangle2D r2d = textComp.modelToView2D(startOffset);

                if (r2d != null) {
                    int lineNumber = i + 1;
                    String num = String.valueOf(lineNumber);
                    // x = largeur du composant - largeur du texte - marge droite
                    int x = getWidth() - fm.stringWidth(num) - 5;

                    // y = position y de la ligne + ascent pour l'aligner correctement
                    int y = (int) (r2d.getY() + fm.getAscent());

                    g2.drawString(num, x, y);
                }

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}
