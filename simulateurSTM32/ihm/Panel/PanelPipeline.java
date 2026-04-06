package ensea.simulateurSTM32.ihm.Panel;

import ensea.simulateurSTM32.LCM3.EtatPipeline;
import ensea.simulateurSTM32.LCM3.Pipeline;
import ensea.simulateurSTM32.ihm.Ihm;
import ensea.simulateurSTM32.parseur.Instruction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class PanelPipeline extends JPanel {

    public Ihm ihm;
    private static final int CELL_WIDTH = 40;
    private static final int ROW_HEIGHT = 25;
    private static final int HEADER_WIDTH = 175;
    private static final int HEADER_HEIGHT = 30;

    private final List<Integer> rowAddresses = new ArrayList<>();
    private final Map<Integer, String> addressToText = new LinkedHashMap<>();

    private final Map<Integer, Color> addressToColor = new LinkedHashMap<>();
    private final Map<Integer, Integer> fetchHistory = new LinkedHashMap<>();

    private final Set<Integer> flushCycles = new HashSet<>();

    private int currentTime = 0;

    private final RowHeader rowHeader;
    private final ColHeader colHeader;
    private final CornerHeader cornerHeader;

    public PanelPipeline(Ihm ihm) {
        this.ihm = ihm;
        this.setBackground(Color.WHITE);

        this.rowHeader = new RowHeader();
        this.colHeader = new ColHeader();
        this.cornerHeader = new CornerHeader();
    }

    public void attachToScrollPane(JScrollPane scrollPane) {
        scrollPane.setViewportView(this);
        scrollPane.setRowHeaderView(rowHeader);
        scrollPane.setColumnHeaderView(colHeader);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, cornerHeader);
    }

    public void disp(Pipeline p) {
        if (p == null) return;

        int t = p.getnStep();
        this.currentTime = t;

        int currentFetchPC = 0;
        Color originalColor = Color.GRAY;

        try {
            if (p.getEtat() != null && p.getEtat().length > 0) {
                EtatPipeline e = p.getEtat()[0];
                currentFetchPC = e.getFetchPC();

                if (e.getCouleurF() != null) {
                    originalColor = e.getCouleurF();
                }

                // Enregistrement des moments où un FLUSH s'est produit
                String exe = e.getExecuteInstruction();
                if (exe != null && (exe.contains("HARDFAULT") || exe.contains("(FLUSH)"))) {
                    flushCycles.add(t);
                }
            }
        } catch (Exception e) {}

        if (currentFetchPC != 0 && currentFetchPC != -1) {
            fetchHistory.put(t, currentFetchPC);

            if (!rowAddresses.contains(currentFetchPC)) {
                rowAddresses.add(currentFetchPC);
                String instrText = findInstructionText(currentFetchPC);
                addressToText.put(currentFetchPC, instrText);
                addressToColor.put(currentFetchPC, originalColor);
            }
        }

        int gridWidth = (t + 5) * CELL_WIDTH;
        int gridHeight = (rowAddresses.size() + 2) * ROW_HEIGHT;

        this.setPreferredSize(new Dimension(gridWidth, gridHeight));
        rowHeader.setPreferredSize(new Dimension(HEADER_WIDTH, gridHeight));
        colHeader.setPreferredSize(new Dimension(gridWidth, HEADER_HEIGHT));

        this.revalidate();
        rowHeader.revalidate();
        colHeader.revalidate();

        this.repaint();
        rowHeader.repaint();
        colHeader.repaint();
        cornerHeader.repaint();

        int targetX = t * CELL_WIDTH;
        int targetY = rowAddresses.size() * ROW_HEIGHT;
        this.scrollRectToVisible(new Rectangle(targetX, targetY, CELL_WIDTH * 2, ROW_HEIGHT));
    }


    private String findInstructionText(int pc) {
        int baseAddr = 0x08000000;
        int index = (pc - baseAddr) / 2;

        if (ihm.instructionsToBeDownloaded != null &&
                index >= 0 &&
                index < ihm.instructionsToBeDownloaded.size()) {

            Instruction ins = ihm.instructionsToBeDownloaded.get(index);
            return ins.getNomComplet();
        }
        return "???";
    }

    public void reset() {
        rowAddresses.clear();
        addressToText.clear();
        addressToColor.clear();
        fetchHistory.clear();
        flushCycles.clear();
        currentTime = 0;
        repaint();
        rowHeader.repaint();
        colHeader.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Surligne verticalement la colonne du cycle actuel
        g2d.setColor(new Color(230, 230, 230));
        g2d.fillRect(currentTime * CELL_WIDTH, 0, CELL_WIDTH, getHeight());

        // Grille : lignes verticales (cycles) et horizontales (instructions)
        g2d.setColor(new Color(245, 245, 245));
        for (int t = 0; t <= currentTime + 5; t++) g2d.drawLine(t * CELL_WIDTH, 0, t * CELL_WIDTH, getHeight());
        for (int i = 0; i < rowAddresses.size(); i++) g2d.drawLine(0, (i + 1) * ROW_HEIGHT, getWidth(), (i + 1) * ROW_HEIGHT);

        for (Map.Entry<Integer, Integer> entry : fetchHistory.entrySet()) {
            int t_start = entry.getKey();
            int pc = entry.getValue();

            int rowIndex = rowAddresses.indexOf(pc);
            if (rowIndex == -1) continue;

            int y = rowIndex * ROW_HEIGHT;
            Color baseColor = addressToColor.getOrDefault(pc, Color.GRAY);

            boolean isFlushedF = flushCycles.contains(t_start);
            boolean isFlushedD = flushCycles.contains(t_start + 1);

            // FETCH
            if (isFlushedF && currentTime >= t_start) {
                drawFlushedCell(g2d, t_start, y, "F");
                continue;
            } else {
                drawGradientCell(g2d, "F", t_start, y, baseColor, 60);
            }

            // DECODE
            if (currentTime >= t_start + 1) {
                if (isFlushedD) {
                    drawFlushedCell(g2d, t_start + 1, y, "D");
                    continue;
                } else {
                    drawGradientCell(g2d, "D", t_start + 1, y, baseColor, 150);
                }
            }

            // EXECUTE (Ne peut pas être annulé)
            if (currentTime >= t_start + 2) {
                drawGradientCell(g2d, "E", t_start + 2, y, baseColor, 255);
            }
        }
    }

    private void drawGradientCell(Graphics2D g2d, String letter, int t, int y, Color base, int alpha) {
        int x = t * CELL_WIDTH;
        Color cellColor = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
        g2d.setColor(cellColor);
        g2d.fillRect(x + 2, y + 2, CELL_WIDTH - 4, ROW_HEIGHT - 4);
        g2d.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue()));
        g2d.drawRect(x + 2, y + 2, CELL_WIDTH - 4, ROW_HEIGHT - 4);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2d.drawString(letter, x + 15, y + 18);
    }

    // Dessine la case avec sa lettre (F ou D) et une croix par dessus
    private void drawFlushedCell(Graphics2D g2d, int t, int y, String letter) {
        int x = t * CELL_WIDTH;
        // Fond rouge clair pour marquer l'annulation
        g2d.setColor(new Color(255, 230, 230));
        g2d.fillRect(x + 2, y + 2, CELL_WIDTH - 4, ROW_HEIGHT - 4);
        // Bordure rouge
        g2d.setColor(Color.RED);
        g2d.drawRect(x + 2, y + 2, CELL_WIDTH - 4, ROW_HEIGHT - 4);
        // On dessine la lettre F ou D
        g2d.setColor(Color.RED.darker());
        g2d.setFont(new Font("SansSerif", Font.BOLD, 13));
        g2d.drawString(letter, x + 15, y + 18);
        // On trace une belle croix rouge sur la case
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(x + 4, y + 4, x + CELL_WIDTH - 4, y + ROW_HEIGHT - 4);
        g2d.drawLine(x + CELL_WIDTH - 4, y + 4, x + 4, y + ROW_HEIGHT - 4);
        g2d.setStroke(new BasicStroke(1f)); // Remise à zero
    }

    private class RowHeader extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.GRAY);
            g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
            g2d.setFont(new Font("Consolas", Font.PLAIN, 12));

            for (int i = 0; i < rowAddresses.size(); i++) {
                int y = i * ROW_HEIGHT;
                int pc = rowAddresses.get(i);

                String addrStr = String.format("0x%X", pc);
                String instr = addressToText.getOrDefault(pc, "???");

                // Retour à l'ancien affichage épuré
                String headerStr = addrStr + " : " + instr;

                Color bg = addressToColor.getOrDefault(pc, Color.GRAY);
                g2d.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 40));
                g2d.fillRect(0, y, getWidth()-1, ROW_HEIGHT);

                g2d.setColor(Color.BLACK);
                g2d.drawString(headerStr, 5, y + 18);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawLine(0, y + ROW_HEIGHT, getWidth(), y + ROW_HEIGHT);
            }
        }
    }

    private class ColHeader extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, getWidth(), getHeight());
            // Surligne la colonne du cycle actuel dans l'en-tête
            g.setColor(new Color(210, 210, 210));
            g.fillRect(currentTime * CELL_WIDTH, 0, CELL_WIDTH, getHeight());
            g.setColor(Color.GRAY);
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

            // Affiche les étiquettes des cycles (t0, t1, ...) avec mise en évidence du cycle actuel
            for (int t = 0; t <= currentTime + 5; t++) {
                int x = t * CELL_WIDTH;
                if (t == currentTime) {
                    // Cycle actuel : ROUGE et gras
                    g.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g.setColor(Color.RED);
                } else {
                    // Autres cycles : noir normal
                    g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g.setColor(Color.BLACK);
                }
                g.drawString("t" + t, x + 10, 20);
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(x + CELL_WIDTH, 0, x + CELL_WIDTH, getHeight());
            }
        }
    }

    private class CornerHeader extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(new Color(220, 220, 220));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            // Retour à l'ancien affichage
            g.drawString("Addr : Instruction", 10, 20);
            g.setColor(Color.GRAY);
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
    }
}