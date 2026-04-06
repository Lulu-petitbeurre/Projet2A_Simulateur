package ensea.simulateurSTM32.ihm.Panel;

import ensea.simulateurSTM32.LCM3.EtatPipeline;
import ensea.simulateurSTM32.LCM3.Pipeline;
import ensea.simulateurSTM32.ihm.Ihm;
import ensea.simulateurSTM32.parseur.Instruction;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PanelAnim extends JPanel {

	// --- GÉOMÉTRIE RECALIBRÉE ---
	private static final int W_STAGE = 210;
	private static final int H_PANEL = 450;
	private static final int Y_SHIFT = 100;

	private static final Color COL_FETCH_BG   = new Color(150, 150, 255);
	private static final Color COL_DECODE_BG  = new Color(150, 255, 150);
	private static final Color COL_EXECUTE_BG = new Color(220, 150, 255);
	private static final Color COL_INACTIVE   = new Color(245, 245, 245);
	private static final Color COL_BLOCK      = Color.WHITE;
	private static final Color COL_VALUE      = new Color(0, 0, 150);

	private boolean activeF        = false;
	private int     pcVal          = 0;
	private String  fetchInstrHex  = "";
	private String  fetchInstrText = "";

	private boolean activeD         = false;
	private String  decodeInstrText = "";
	private String  decType = "-", decRegs = "-", decParams = "-";

	private boolean activeE = false;

	private String  aluInputA = "-", aluInputB = "-", aluOpText = "";
	private boolean flagN = false, flagZ = false, flagP = false;

	private String  writeTarget    = "", readTarget = "";
	private boolean writeHighlight = false, readHighlight = false;

	private String pipelineFetchText   = "";
	private String pipelineDecodeText  = "";
	private String pipelineExecuteText = "";

	private Color fetchBg   = COL_FETCH_BG;
	private Color decodeBg  = COL_DECODE_BG;
	private Color executeBg = COL_EXECUTE_BG;
	private final Map<Integer, Color> addrColorMap = new HashMap<>();

	private final Ihm ihm;

	// --- GESTION DU TEMPS ET DES FLUSHS ---
	private int currentTime = 0;
	private boolean pipelineFlushed = false;
	private final Set<Integer> flushCycles = new HashSet<>();

	public PanelAnim(Ihm ihm) {
		this.ihm = ihm;
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(W_STAGE * 3 + 20, H_PANEL));
		setFont(new Font("SansSerif", Font.PLAIN, 12));
	}

	public void disp(Pipeline p) {
		if (p == null) return;

		try {
			this.currentTime = p.getnStep();

			// On vide la mémoire des flushs si le simulateur redémarre
			if (this.currentTime == 0) flushCycles.clear();

			EtatPipeline[] etats = p.getEtat();
			if (etats.length > 0 && etats[0] != null && etats[0].getExecuteInstruction() != null) {
				pipelineFlushed = etats[0].getExecuteInstruction().contains("(FLUSH)");
				if (pipelineFlushed) {
					flushCycles.add(this.currentTime);
				}
			} else {
				pipelineFlushed = false;
			}
		} catch (Exception e) {}

		EtatPipeline[] etats = p.getEtat();
		mettreAJourFetch(p, etats);
		mettreAJourDecode(p, etats);
		mettreAJourExecute(p, etats);
		repaint();
	}

	private void mettreAJourFetch(Pipeline p, EtatPipeline[] etats) {
		if (etats.length == 0 || etats[0].getFetchPC() == 0 || etats[0].getFetchPC() == -1) {
			activeF = false;
			fetchBg = COL_INACTIVE;
			fetchInstrText = pipelineFetchText = "";
			return;
		}

		activeF = true;
		pcVal   = etats[0].getFetchPC();

		try {
			Color c = p.getColorForStep(etats[0].getNStep());
			addrColorMap.putIfAbsent(pcVal, c);
			fetchBg = addrColorMap.getOrDefault(pcVal, c);
		} catch (Exception ex) {
			if (etats[0].getCouleurF() != null) fetchBg = etats[0].getCouleurF();
		}

		fetchInstrText = "-";
		fetchInstrHex  = "0x....";
		try {
			int idx = indexDepuisAdresse(pcVal);
			if (indexValide(idx)) fetchInstrText = getNomComplet(idx);

			if (ihm.codeToBeDownloaded != null && indexValide(idx) && idx < ihm.codeToBeDownloaded.size()) {
				String bin = ihm.codeToBeDownloaded.get(idx);
				if (bin != null && !bin.isEmpty())
					fetchInstrHex = String.format("0x%04X", Integer.parseInt(bin, 2) & 0xFFFF);
			} else if (etats.length > 1) {
				int code = etats[1].getDecodeCode();
				if (code != 0 && code != -1) fetchInstrHex = String.format("0x%04X", code & 0xFFFF);
			}
		} catch (Exception ignored) {}

		pipelineFetchText = fetchInstrText.equals("-") ? "" : fetchInstrText;
	}

	private void mettreAJourDecode(Pipeline p, EtatPipeline[] etats) {
		// NOUVEAU : Si l'instruction actuellement en Decode a été flushée au cycle précédent, on vide la case !
		if (etats.length < 2 || etats[1].getFetchPC() == 0 || etats[1].getFetchPC() == -1 ||
				flushCycles.contains(currentTime - 1)) {
			activeD = false;
			decodeBg = COL_INACTIVE;
			decodeInstrText = pipelineDecodeText = "";
			decType = "-"; decRegs = "-"; decParams = "-";
			return;
		}

		activeD = true;
		int addrD = etats[1].getFetchPC();

		if (addrColorMap.containsKey(addrD)) {
			decodeBg = addrColorMap.get(addrD);
		} else {
			try {
				decodeBg = p.getColorForStep(etats[1].getNStep());
			} catch (Exception ex) {
				if (etats[1].getCouleurF() != null) decodeBg = etats[1].getCouleurF();
			}
		}

		String instr = null;
		try {
			int idx = indexDepuisAdresse(addrD);
			if (indexValide(idx)) instr = getNomComplet(idx);
		} catch (Exception ignored) {}

		if (instr == null || instr.toUpperCase().startsWith("NO ")) {
			decodeInstrText = "";
			decType = "-"; decRegs = "-"; decParams = "-";
		} else {
			decodeInstrText = instr;
			parseInstructionForDecoder(instr);
		}
		pipelineDecodeText = decodeInstrText;
	}

	private void mettreAJourExecute(Pipeline p, EtatPipeline[] etats) {
		String currentExe = etats[0].getExecuteInstruction();
		boolean isNoExec = (currentExe != null && currentExe.startsWith("NO EXEC"));

		// NOUVEAU : Si l'instruction a été annulée par un flush passé, ou si l'étage est vide, on désactive !
		if (etats.length < 3 || etats[2].getFetchPC() == 0 || etats[2].getFetchPC() == -1 ||
				flushCycles.contains(currentTime - 1) || flushCycles.contains(currentTime - 2) || isNoExec) {
			activeE = false;
			executeBg = COL_INACTIVE;
			pipelineExecuteText = "";
			updateALUDisplay(null);
			return;
		}

		activeE = true;
		int addrE = etats[2].getFetchPC();

		if (addrColorMap.containsKey(addrE)) {
			executeBg = addrColorMap.get(addrE);
		} else {
			try {
				executeBg = p.getColorForStep(etats[2].getNStep());
			} catch (Exception ex) {
				if (etats[2].getCouleurF() != null) executeBg = etats[2].getCouleurF();
			}
		}

		String texte = currentExe;
		if (texte == null || texte.toUpperCase().startsWith("NO ")) texte = "";
		try {
			int idx = indexDepuisAdresse(addrE);
			if (indexValide(idx)) {
				String nom = getNomComplet(idx);
				if (!nom.isEmpty()) texte = nom;
			}
		} catch (Exception ignored) {}

		if (texte.isEmpty()) {
			activeE = false;
			executeBg = COL_INACTIVE;
			pipelineExecuteText = "";
			updateALUDisplay(null);
			return;
		}

		pipelineExecuteText = texte;

		Instruction instrObj = null;
		try {
			int idx = indexDepuisAdresse(addrE);
			if (indexValide(idx)) instrObj = ihm.instructionsToBeDownloaded.get(idx);
		} catch (Exception ignored) {}

		updateALUDisplay(instrObj);
	}

	private int indexDepuisAdresse(int addr) { return (addr - 0x08000000) / 2; }
	private boolean indexValide(int idx) { return ihm != null && ihm.instructionsToBeDownloaded != null && idx >= 0 && idx < ihm.instructionsToBeDownloaded.size(); }
	private String getNomComplet(int idx) {
		try   { return ihm.instructionsToBeDownloaded.get(idx).getNomComplet(); }
		catch (Exception ex) { return ihm.instructionsToBeDownloaded.get(idx).getNom(); }
	}

	private void parseInstructionForDecoder(String instr) {
		decType = "-"; decRegs = "-"; decParams = "-";
		String[] parts = instr.replace(",", " ").trim().split("\\s+");

		if (parts.length > 0) decType = parts[0].toLowerCase();

		List<String> regs = new ArrayList<>();
		List<String> params = new ArrayList<>();

		for (int i = 1; i < parts.length; i++) {
			String p = parts[i];
			if (p.toUpperCase().matches("R\\d+") || p.startsWith("[")) {
				regs.add(p);
			} else {
				params.add(p);
			}
		}
		if (!regs.isEmpty()) decRegs = String.join(", ", regs);
		if (!params.isEmpty()) decParams = String.join(", ", params);
	}

	public void updateALUDisplay(Instruction instr) {
		aluInputA = "-"; aluInputB = "-"; aluOpText = "";
		flagN = flagZ = flagP = false;
		writeTarget = ""; readTarget = "";
		readHighlight = false; writeHighlight = false;

		if (instr == null) return;
		String op = instr.getNom().toUpperCase();
		List<String> regs = new ArrayList<>();
		List<String> all  = new ArrayList<>();
		for (int i = 1; i < instr.getListeParams().size(); i++) {
			Instruction.Mot m = instr.getListeParams().get(i);
			if (m instanceof Instruction.MotReg) {
				String r = "R" + ((Instruction.MotReg) m).getValeur();
				regs.add(r); all.add(r);
			} else if (m instanceof Instruction.MotImm) {
				all.add("#" + ((Instruction.MotImm) m).getValeur());
			}
		}

		switch (op) {
			case "MOV": {
				String rd  = regs.size() > 0 ? regs.get(0) : null;
				String rs  = regs.size() > 1 ? regs.get(1) : null;
				Integer imm = null;
				for (Instruction.Mot m : instr.getListeParams())
					if (m instanceof Instruction.MotImm) imm = ((Instruction.MotImm) m).getValeur();

				aluOpText = "PASS-\nTHROUGH";
				if (imm != null) { aluInputA = "#" + imm; aluInputB = "-"; }
				else if (rs != null) { aluInputA = rs; aluInputB = "-"; readTarget = rs; }
				writeTarget = rd != null ? rd : "";
				break;
			}
			case "ADD": case "SUB": case "AND": case "ORR": case "EOR": case "LSL": case "LSR": case "ASR": {
				String dest = regs.size() > 0 ? regs.get(0) : null;
				aluInputA = all.size() > 1 ? all.get(1) : "-";
				aluInputB = all.size() > 2 ? all.get(2) : "-";
				switch (op) {
					case "ADD": aluOpText = "A + B";   break; case "SUB": aluOpText = "A - B";   break;
					case "AND": aluOpText = "A & B";   break; case "ORR": aluOpText = "A | B";   break;
					case "EOR": aluOpText = "A ^ B";   break; case "LSL": aluOpText = "A << B";  break;
					case "LSR": aluOpText = "A >>> B"; break; case "ASR": aluOpText = "A >> B";  break;
				}
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < regs.size(); i++) {
					if (sb.length() > 0) sb.append(", ");
					sb.append(regs.get(i));
				}
				readTarget  = sb.toString();
				writeTarget = dest != null ? dest : "";
				break;
			}
			case "CMP": { aluInputA = all.size() > 0 ? all.get(0) : "-"; aluInputB = all.size() > 1 ? all.get(1) : "-"; aluOpText = "A - B"; readTarget = String.join(", ", regs); writeTarget = ""; break; }
			case "STR": { aluInputA = regs.size() > 0 ? regs.get(0) : "-"; aluInputB = regs.size() > 1 ? regs.get(1) : "-"; aluOpText = "→ RAM"; readTarget = String.join(", ", regs); writeTarget = ""; break; }
			case "LDR": { String rd = regs.size() > 0 ? regs.get(0) : null; String rn = regs.size() > 1 ? regs.get(1) : null; aluInputA = rn != null ? rn : "-"; aluInputB = "-"; aluOpText = "RAM →"; readTarget = rn != null ? rn : ""; writeTarget = rd != null ? rd : ""; break; }
			case "B": case "BL": case "BEQ": case "BNE": case "BLT": case "BGT": case "BLE": case "BGE": case "BX": aluOpText = "Branch"; break;
		}

		readHighlight  = !readTarget.isEmpty();
		writeHighlight = !writeTarget.isEmpty();
	}

	public void setAluInputs(String a, String b) { aluInputA = a; aluInputB = b; repaint(); }
	public void setAluOp(String op)              { aluOpText = op; repaint(); }
	public void setFlags(boolean n, boolean z, boolean p) { flagN = n; flagZ = z; flagP = p; repaint(); }

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawColumnBg(g2d, 0,           "FETCH",   fetchBg);
		drawColumnBg(g2d, W_STAGE,     "DECODE",  decodeBg);
		drawColumnBg(g2d, W_STAGE * 2, "EXECUTE", executeBg);

		if (activeE && pipelineFlushed) {
			drawLargeRedCross(g2d, 0);
			drawStageLabelFlushed(g2d, 0, "[ANNULÉE] " + pipelineFetchText);

			drawLargeRedCross(g2d, W_STAGE);
			drawStageLabelFlushed(g2d, W_STAGE, "[ANNULÉE] " + pipelineDecodeText);

			drawThickRedFeedbackArrow(g2d, W_STAGE*2, W_STAGE);
			drawThickRedFeedbackArrow(g2d, W_STAGE, 0);
		} else {
			drawStageLabel(g2d, 0,           pipelineFetchText);
			drawStageLabel(g2d, W_STAGE,     pipelineDecodeText);
		}

		drawStageLabel(g2d, W_STAGE * 2, pipelineExecuteText);

		int timeBoxW = 50, timeBoxH = 26;
		g2d.setColor(new Color(255, 255, 200));
		g2d.fillRoundRect(10, 10, timeBoxW, timeBoxH, 8, 8);
		g2d.setColor(Color.ORANGE.darker());
		g2d.setStroke(new BasicStroke(2f));
		g2d.drawRoundRect(10, 10, timeBoxW, timeBoxH, 8, 8);
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 15));
		String timeStr = "t" + currentTime;
		FontMetrics fmTime = g2d.getFontMetrics();
		g2d.drawString(timeStr, 10 + (timeBoxW - fmTime.stringWidth(timeStr)) / 2, 28);
		g2d.setStroke(new BasicStroke(1.5f));

		int romWidth = 140;
		int romX = (W_STAGE - romWidth) / 2;
		int romCenterX = romX + romWidth / 2;

		int pcBoxW = 110, pcBoxH = 48, pcBoxX = romCenterX - pcBoxW / 2, pcBoxY = 42;
		g2d.setColor(COL_BLOCK);
		g2d.fillRect(pcBoxX, pcBoxY, pcBoxW, pcBoxH);
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.drawRect(pcBoxX, pcBoxY, pcBoxW, pcBoxH);
		g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
		g2d.drawString("PC", pcBoxX + 4, pcBoxY + 14);
		g2d.setFont(new Font("Consolas", Font.BOLD, 11));
		g2d.setColor(COL_VALUE);
		String pcStr = activeF ? String.format("0x%08X", pcVal) : "0x00000000";
		FontMetrics fmPc = g2d.getFontMetrics();
		g2d.drawString(pcStr, pcBoxX + (pcBoxW - fmPc.stringWidth(pcStr)) / 2, pcBoxY + 35);
		g2d.setColor(Color.BLACK);

		int baseY = 100 + Y_SHIFT;
		drawRomBlock(g2d, romX, baseY);

		int decoderWidth = 150;
		int decoderX = W_STAGE + (W_STAGE - decoderWidth) / 2;
		drawDecoderBlock(g2d, decoderX, baseY);

		int aluX = W_STAGE * 2 + 50;
		int aluY = baseY;
		g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
		g2d.drawString("ALU", aluX + 35, aluY - 10);
		drawALU(g2d, aluX, aluY);

		g2d.setStroke(new BasicStroke(2f));
		drawArrow(g2d, romCenterX, pcBoxY + pcBoxH, romCenterX, baseY);

		int romRightX    = romX + romWidth;
		int romDecArrowY = baseY + 70;
		drawArrow(g2d, romRightX, romDecArrowY, decoderX, romDecArrowY);
		if (activeF && fetchInstrText != null && !fetchInstrText.equals("-")) {
			g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
			if (pipelineFlushed) g2d.setColor(Color.GRAY);
			else g2d.setColor(COL_VALUE);
			g2d.drawString(fetchInstrText, romRightX + 4, romDecArrowY - 5);
			g2d.setColor(Color.BLACK);
		}

		int decStartX  = decoderX + decoderWidth;
		int decAStartY = aluY + 20, decBStartY = aluY + 80;
		drawArrow(g2d, decStartX, decAStartY, aluX, aluY + 20);
		drawArrow(g2d, decStartX, decBStartY, aluX, aluY + 80);

		if (activeE) {
			int boxW = 55, boxH = 22, midX = (decStartX + aluX) / 2;
			dessinerBoiteOperande(g2d, midX - boxW / 2, decAStartY - boxH / 2, boxW, boxH, "A: " + aluInputA);
			dessinerBoiteOperande(g2d, midX - boxW / 2, decBStartY - boxH / 2, boxW, boxH, "B: " + aluInputB);
		}
	}

	private void drawColumnBg(Graphics2D g, int x, String titre, Color c) {
		g.setColor(c);
		g.fillRect(x, 0, W_STAGE, H_PANEL);
		g.setColor(Color.DARK_GRAY.darker());
		g.drawRect(x, 0, W_STAGE, H_PANEL);
		g.setColor(Color.DARK_GRAY);
		g.setFont(new Font("SansSerif", Font.BOLD, 14));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(titre, x + (W_STAGE - fm.stringWidth(titre)) / 2, 30);
	}

	private void drawStageLabel(Graphics2D g, int x, String text) {
		int w = W_STAGE - 20, h = 26, y = H_PANEL - h - 8;
		boolean actif = text != null && !text.isEmpty();
		g.setColor(actif ? new Color(255, 255, 180, 230) : new Color(240, 240, 240, 180));
		g.fillRoundRect(x + 10, y, w, h, 6, 6);
		g.setColor(Color.DARK_GRAY);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(x + 10, y, w, h, 6, 6);
		g.setStroke(new BasicStroke(1f));
		if (!actif) {
			g.setFont(new Font("SansSerif", Font.ITALIC, 11));
			g.setColor(Color.GRAY);
			g.drawString("—", x + 10 + w / 2 - 4, y + 17);
			return;
		}
		g.setFont(new Font("SansSerif", Font.BOLD, 12));
		FontMetrics fm = g.getFontMetrics();
		String display = text;
		while (fm.stringWidth(display) > w - 8 && display.length() > 1) display = display.substring(0, display.length() - 1);
		if (display.length() < text.length()) display = display.substring(0, Math.max(1, display.length() - 3)) + "...";
		g.setColor(new Color(0, 0, 160));
		g.drawString(display, x + 10 + (w - fm.stringWidth(display)) / 2, y + 18);
	}


	// Affiche un label "[ANNULÉE]" pour indiquer qu'un étage a été vidé par un flush.
	private void drawStageLabelFlushed(Graphics2D g, int x, String text) {
		int w = W_STAGE - 20, h = 26, y = H_PANEL - h - 8;
		// Remplissage de la boîte avec une couleur rose semi-transparente
		g.setColor(new Color(255, 200, 200, 230));
		g.fillRoundRect(x + 10, y, w, h, 6, 6);
		// Bordure rouge vif pour bien marquer l'annulation
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(x + 10, y, w, h, 6, 6);
		// Police en gras pour bien voir le message d'annulation
		g.setFont(new Font("SansSerif", Font.BOLD, 11));
		FontMetrics fm = g.getFontMetrics();
		// Adapter le texte à la largeur disponible si trop long
		String display = text;
		while (fm.stringWidth(display) > w - 8 && display.length() > 1) display = display.substring(0, display.length() - 1);
		g.drawString(display, x + 10 + (w - fm.stringWidth(display)) / 2, y + 18);
	}

	//Dessine une grande croix rouge (X) diagonale sur l'étage pour indiquer visuellement que l'étage a été annulé par un flush.
	private void drawLargeRedCross(Graphics2D g, int x) {
		// Couleur rouge semi-transparente pour ne pas voiler complètement le contenu
		g.setColor(new Color(255, 0, 0, 150));
		// Trait épais (6 pixels) avec bout arrondi pour une croix bien visible
		g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		// Première diagonale : de haut-gauche à bas-droit
		g.drawLine(x + 15, 60, x + W_STAGE - 15, H_PANEL - 60);
		// Deuxième diagonale : de haut-droit à bas-gauche (forme une croix complète)
		g.drawLine(x + W_STAGE - 15, 60, x + 15, H_PANEL - 60);
		// Restaurer le trait normal
		g.setStroke(new BasicStroke(1f));
	}


	private void drawThickRedFeedbackArrow(Graphics2D g, int xFrom, int xTo) {
		// Couleur rouge foncée pour bien contraster avec le blanc du fond
		g.setColor(Color.RED.darker());
		// Trait épais (5 pixels) avec bout arrondi
		g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		// Y constant au milieu de la zone pipeline
		int midY = 100 + Y_SHIFT + 60;
		// Ligne horizontale du feedback
		g.drawLine(xFrom - 10, midY, xTo + 10, midY);
		// Construction de la pointe de flèche (triangle pointant vers xTo)
		Path2D arrowHead = new Path2D.Double();
		arrowHead.moveTo(xTo + 10, midY);
		arrowHead.lineTo(xTo + 10 + 10, midY - 6);
		arrowHead.lineTo(xTo + 10 + 10, midY + 6);
		arrowHead.closePath();
		g.fill(arrowHead);
		// Restaurer le trait normal
		g.setStroke(new BasicStroke(1f));
	}

	private void drawRomBlock(Graphics2D g, int x, int y) {
		g.setColor(COL_BLOCK);
		g.fillRect(x, y, 140, 140);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 140, 140);
		g.setFont(new Font("SansSerif", Font.BOLD, 13));
		g.drawString("ROM", x + 58, y + 18);

		g.setFont(new Font("Consolas", Font.PLAIN, 12));
		if (pipelineFlushed) g.setColor(Color.GRAY); else g.setColor(COL_VALUE);
		g.drawString(activeF ? String.format("0x%08X", pcVal) : "0x00000000", x + 10, y + 40);
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.PLAIN, 12));
		g.drawString("Code (hex) :", x + 10, y + 64);
		g.setFont(new Font("Consolas", Font.BOLD, 12));
		if (pipelineFlushed) g.setColor(Color.GRAY); else g.setColor(COL_VALUE);
		g.drawString(fetchInstrHex.isEmpty() ? "0x...." : fetchInstrHex, x + 10, y + 84);
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.PLAIN, 12));
		g.drawString(fetchInstrText != null ? fetchInstrText : "", x + 10, y + 104);
	}

	private void drawDecoderBlock(Graphics2D g, int x, int y) {
		g.setColor(COL_BLOCK);
		g.fillRect(x, y, 150, 120);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 150, 120);
		g.setFont(new Font("SansSerif", Font.BOLD, 12));
		g.drawString("Decode", x + 50, y + 18);

		drawDecodeField(g, x, y + 36,      "Type",        decType);
		drawDecodeField(g, x, y + 36 + 26, "Registre(s)", decRegs);
		drawDecodeField(g, x, y + 36 + 52, "Param(s)",    decParams);
	}

	private void drawDecodeField(Graphics2D g, int x, int y, String label, String val) {
		g.setColor(new Color(230, 230, 230));
		g.fillRect(x + 5, y, 55, 20);
		g.setColor(Color.BLACK);
		g.drawRect(x + 5, y, 55, 20);
		g.setFont(new Font("SansSerif", Font.PLAIN, 9));
		g.drawString(label, x + 8, y + 14);

		g.setColor(new Color(245, 245, 245));
		g.fillRect(x + 65, y, 75, 20);
		g.setColor(Color.BLACK);
		g.drawRect(x + 65, y, 75, 20);
		if (!val.equals("-") && !val.isEmpty()) {
			if (pipelineFlushed) g.setColor(Color.GRAY); else g.setColor(COL_VALUE);
			g.setFont(new Font("SansSerif", Font.BOLD, 10));
			g.drawString(val, x + 70, y + 14);
			g.setColor(Color.BLACK);
		}
	}

	private void drawALU(Graphics2D g, int x, int y) {
		int fx = x + 85, fy = y + 20;
		g.setColor(new Color(230, 230, 250));
		g.fillRect(fx, fy, 45, 30);
		g.setColor(Color.GRAY);
		g.drawRect(fx, fy, 45, 30);
		g.setFont(new Font("SansSerif", Font.BOLD, 12));
		g.setColor(flagN ? Color.RED   : Color.LIGHT_GRAY); g.drawString("N", fx + 3,  fy + 16);
		g.setColor(flagZ ? Color.RED   : Color.LIGHT_GRAY); g.drawString("Z", fx + 17, fy + 16);
		g.setColor(flagP ? Color.GREEN : Color.LIGHT_GRAY); g.drawString("P", fx + 31, fy + 16);
		g.setColor(Color.BLACK);

		Path2D alu = new Path2D.Double();
		alu.moveTo(x,      y);
		alu.lineTo(x + 80, y + 20);
		alu.lineTo(x + 80, y + 80);
		alu.lineTo(x,      y + 100);
		alu.lineTo(x,      y + 60);
		alu.lineTo(x + 20, y + 50);
		alu.lineTo(x,      y + 40);
		alu.closePath();
		g.setColor(COL_BLOCK); g.fill(alu);
		g.setColor(Color.BLACK); g.draw(alu);

		if (activeE && !aluOpText.isEmpty()) {
			g.setColor(COL_VALUE);
			g.setFont(new Font("SansSerif", Font.BOLD, 11));
			if (aluOpText.contains("\n")) {
				String[] lines = aluOpText.split("\n");
				g.drawString(lines[0], x + 25, y + 48);
				g.drawString(lines[1], x + 25, y + 62);
			} else {
				g.drawString(aluOpText, x + 25, y + 55);
			}
			g.setColor(Color.BLACK);
		}

		int rectY = y + 130;
		dessinerIndicateur(g, x - 30, rectY, 75, 26, "WRITE", writeTarget, writeHighlight);
		dessinerIndicateur(g, x + 45, rectY, 75, 26, "READ",  readTarget,  readHighlight);
		g.setStroke(new BasicStroke(1.5f));
	}

	private void dessinerIndicateur(Graphics2D g, int x, int y, int w, int h,
									String label, String cible, boolean actif) {
		g.setColor(actif ? Color.YELLOW : new Color(220, 220, 220));
		g.fillRect(x, y, w, h);
		g.setColor(actif ? new Color(180, 140, 0) : Color.DARK_GRAY);
		g.setStroke(new BasicStroke(actif ? 2f : 1f));
		g.drawRect(x, y, w, h);
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif", Font.BOLD, 10));
		if (actif && !cible.isEmpty()) {
			g.drawString(label, x + 3, y + 11);
			g.setColor(COL_VALUE);
			g.setFont(new Font("SansSerif", Font.BOLD, 11));
			g.drawString(cible, x + 3, y + 22);
		} else {
			g.drawString(label, x + 3, y + 17);
		}
	}

	private void dessinerBoiteOperande(Graphics2D g, int x, int y, int w, int h, String texte) {
		g.setColor(COL_BLOCK);
		g.fillRect(x, y, w, h);
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRect(x, y, w, h);
		g.setFont(new Font("SansSerif", Font.BOLD, 11));
		g.setColor(COL_VALUE);
		g.drawString(texte, x + 4, y + 15);
		g.setColor(Color.BLACK);
	}

	private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
		Stroke    oldStroke = g.getStroke();
		Composite oldComp   = g.getComposite();
		Color     oldColor  = g.getColor();

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		g.setStroke(new BasicStroke(2f));
		g.setColor(Color.BLACK);
		g.drawLine(x1, y1, x2, y2);

		double angle = Math.atan2(y2 - y1, x2 - x1);
		int d = 7;
		int ax1 = (int)(x2 - d * Math.cos(angle - Math.PI / 6));
		int ay1 = (int)(y2 - d * Math.sin(angle - Math.PI / 6));
		int ax2 = (int)(x2 - d * Math.cos(angle + Math.PI / 6));
		int ay2 = (int)(y2 - d * Math.sin(angle + Math.PI / 6));
		g.fillPolygon(new int[]{x2, ax1, ax2}, new int[]{y2, ay1, ay2}, 3);

		g.setComposite(oldComp);
		g.setStroke(oldStroke);
		g.setColor(oldColor);
	}
}