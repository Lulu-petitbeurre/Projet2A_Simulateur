package ensea.simulateurSTM32.ihm.Panel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class LineNumbering extends JPanel {
    private final JTextComponent textComp;
    private int lastLineCount = 0;

    public LineNumbering(JTextComponent textComp) {
        this.textComp = textComp;
        setFont(textComp.getFont());
        setBackground(new Color(240,240,240));

        Timer timer = new Timer(100, e -> {
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
        FontMetrics fm = g.getFontMetrics();

        Element root = textComp.getDocument().getDefaultRootElement();
        int lineCount = root.getElementCount();

        for (int i = 0; i < lineCount; i++) {
            try {
                int startOffset = root.getElement(i).getStartOffset();
                Rectangle r = textComp.modelToView(startOffset);
                if (r != null) {
                    String num = String.valueOf(i + 1);
                    int x = getWidth() - fm.stringWidth(num) - 5;
                    g.drawString(num, x, r.y + fm.getAscent());
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}

