package ensea.simulateurSTM32.parseur;

import ensea.simulateurSTM32.ihm.Panel.PanelDialog;
import ensea.simulateurSTM32.parseur.Instruction.MotConst;
import ensea.simulateurSTM32.parseur.Instruction.MotEtiq;
import ensea.simulateurSTM32.parseur.Instruction.MotImm;
import ensea.simulateurSTM32.parseur.Instruction.MotReg;
import ensea.simulateurSTM32.parseur.Instruction.NotBinaryException;
import ensea.simulateurSTM32.ihm.Panel.PanelAnim;

/**
 * Permet d'analyser la structure d'une chaine de caractères.
 * @author louiroll
 */
public class Parseur {

	public int indice;
	// reference to UI component for ALU display (can be null if not used)
	private PanelAnim panelAnim;
	// local copy of flags for conditional branches
	private boolean flagN, flagZ, flagP;
	public String texte;
	public PanelDialog zoneErreur;
	public int ligne;
	public int colonne;
	private int debutLigne;

	private static final String[] INSTRUCTIONS_VALIDES = {
			"add", "sub", "mov", "ldr", "str", "ldrh", "strh",
			"b", "beq", "bne", "blt", "bge", "ble",
			"cmp", "and", "orr", "eor", "lsl", "lsr", "asr",
			"nop"
	};
	// Indique si une erreur a déjà été signalée sur la ligne courante

	/**
	 * @param texte      le texte a parser.
	 * @param zoneErreur zone texte d'erreur.
	 */
	public Parseur(String texte, PanelDialog zoneErreur) {
		indice = 0;
		ligne = 1;
		colonne = 1;
		debutLigne = 0;
		this.texte = texte;
		this.zoneErreur = zoneErreur;
	}

	public void passerEspace() {
		while (indice < texte.length() && (texte.charAt(indice) == ' ' || texte.charAt(indice) == '\t')) {
			indice++;
			colonne++;
		}
	}

	public String obtenirMot() {
		StringBuilder s = new StringBuilder();
		while (indice < texte.length() && (s.toString() + texte.charAt(indice)).matches("[A-Za-z_][A-Za-z0-9_]*")) {
			s.append(texte.charAt(indice));
			indice++;
			colonne++;
		}

		return s.toString();
	}

	public String obtenirParam() {
		StringBuilder s = new StringBuilder();
		passerEspace();
		while (indice < texte.length() && (s.toString() + texte.charAt(indice)).matches("#?-?[A-Za-z0-9_\\[\\]]*")) {
			s.append(texte.charAt(indice));
			indice++;
			colonne++;
		}
		return s.toString();
	}

	private void ajouterErreur(String type, String detail, String suggestion) {

		StringBuilder erreur = new StringBuilder();
		erreur.append("\n");
		erreur.append("╔═══ ERREUR ═════════════════════════════════════════\n");
		erreur.append(String.format("║ Ligne %d, colonne %d\n", ligne, colonne));
		erreur.append("║ Type : ").append(type).append("\n");
		erreur.append("╠══════════════════════════════════════════════════════\n");

		// Afficher la ligne de code avec le problème
		String ligneCode = extraireLigneCourante();
		erreur.append("║ ").append(ligneCode).append("\n");

		// Pointer vers l'erreur avec ^
		erreur.append("║ ");
		for (int i = 0; i < colonne - 1; i++) {
			erreur.append(" ");
		}
		erreur.append("^\n");

		erreur.append("║ Détail : ").append(detail).append("\n");

		if (suggestion != null && !suggestion.isEmpty()) {
			erreur.append("║ Suggestion : ").append(suggestion).append("\n");
		}

		erreur.append("╚══════════════════════════════════════════════════════\n");

		String errStr = erreur.toString();
		// Éviter d'ajouter plusieurs fois le même message d'erreur
		try {
			zoneErreur.appendErrorForLine(ligne, errStr);
		} catch (Exception ex) {
			if (!zoneErreur.getText().contains(errStr)) {
				zoneErreur.setText(zoneErreur.getText() + errStr);
			}
		}
	}


	private String extraireLigneCourante() {
		int debut = debutLigne;
		int fin = indice;

		// Trouver la fin de la ligne
		while (fin < texte.length() && texte.charAt(fin) != '\n' && texte.charAt(fin) != '\r') {
			fin++;
		}
		return texte.substring(debut, fin).trim();
	}

	public void setPanelAnim(PanelAnim panel) {
		this.panelAnim = panel;
	}

	/**
	 * helper called from simulateALULogic to update flags in UI
	 */
	private void updateFlags(long result) {
		flagN = (result < 0);
		flagZ = (result == 0);
		flagP = (result > 0);
		if (panelAnim != null) panelAnim.setFlags(flagN, flagZ, flagP);
	}

	private boolean conditionTaken(String mnemonic) {
		switch (mnemonic.toLowerCase()) {
			case "beq":
				return flagZ;
			case "bne":
				return !flagZ;
			case "blt":
				return flagN ^ flagP;
			case "bge":
				return !(flagN ^ flagP);
			case "ble":
				return flagZ || (flagN ^ flagP);
			default:
				return true; // unconditional b
		}
	}

	/** Simule le comportement de l'ALU et met à jour l'affichage.
	 *  valA/valB sont les opérandes numériques. bImmediate indique si valB est un immédiat.
	 */
	public void simulateALULogic(String opcode, long valA, long valB, boolean bImmediate) {
		String aText = Long.toString(valA);
		String bText = bImmediate ? "#" + valB : Long.toString(valB);
		if (panelAnim != null) panelAnim.setAluInputs(aText, bText);

		switch (opcode.toLowerCase()) {
			case "add" -> {
				if (panelAnim != null) panelAnim.setAluOp(bImmediate ? "A + #B" : "A + B");
				updateFlags(valA + valB);
			}
			case "sub" -> {
				if (panelAnim != null) panelAnim.setAluOp("A - B");
				updateFlags(valA - valB);
			}
			case "cmp" -> {
				if (panelAnim != null) panelAnim.setAluOp("A - B");
				updateFlags(valA - valB);
			}
			case "and" -> {
				if (panelAnim != null) panelAnim.setAluOp("A & B");
				updateFlags(valA & valB);
			}
			case "orr" -> {
				if (panelAnim != null) panelAnim.setAluOp("A | B");
				updateFlags(valA | valB);
			}
			case "eor" -> {
				if (panelAnim != null) panelAnim.setAluOp("A ^ B");
				updateFlags(valA ^ valB);
			}
			case "lsl" -> {
				if (panelAnim != null) panelAnim.setAluOp("A << B");
				updateFlags(valA << valB);
			}
			case "lsr" -> {
				if (panelAnim != null) panelAnim.setAluOp("A >>> B");
				updateFlags(valA >>> valB);
			}
			case "asr" -> {
				if (panelAnim != null) panelAnim.setAluOp("A >> B");
				updateFlags(valA >> valB);
			}
			case "mov" -> {
				if (panelAnim != null) panelAnim.setAluOp("PASS-THROUGH");
				updateFlags(valA);
			}
			case "ldr", "ldrh" -> {
				if (panelAnim != null) panelAnim.setAluOp("RAM →");
			}
			case "str", "strh" -> {
				if (panelAnim != null) panelAnim.setAluOp("→ RAM");
			}
			case "b", "beq", "bne", "blt", "bge", "ble" -> {
				if (panelAnim != null) panelAnim.setAluOp("Branch " + opcode.toUpperCase());
			}
			case "nop" -> {
				if (panelAnim != null) {
					panelAnim.setAluOp("Inactive");
					panelAnim.setAluInputs("", "");
				}
			}
		}
	}

	private boolean instructionValide(String instruction) {
		String instrLower = instruction.toLowerCase();
		for (String instr : INSTRUCTIONS_VALIDES) {
			if (instr.equals(instrLower)) {
				return true;
			}
		}
		return false;
	}

	/* Trouve l'instruction la plus proche
	 */
	private String trouverInstructionProche(String instruction) {
		String instrLower = instruction.toLowerCase();
		int distanceMin = Integer.MAX_VALUE;
		String plusProche = null;

		for (String instr : INSTRUCTIONS_VALIDES) {
			int distance = calculerDistanceLevenshtein(instrLower, instr);
			if (distance < distanceMin && distance <= 2) {
				distanceMin = distance;
				plusProche = instr;
			}
		}

		return plusProche;
	}

	/* Calcule la distance de Levenshtein entre deux chaînes
	 */
	private int calculerDistanceLevenshtein(String s1, String s2) {
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			dp[i][0] = i;
		}
		for (int j = 0; j <= s2.length(); j++) {
			dp[0][j] = j;
		}

		for (int i = 1; i <= s1.length(); i++) {
			for (int j = 1; j <= s2.length(); j++) {
				int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
				dp[i][j] = Math.min(Math.min(
								dp[i - 1][j] + 1,      // suppression
								dp[i][j - 1] + 1),     // insertion
						dp[i - 1][j - 1] + cost // substitution
				);
			}
		}

		return dp[s1.length()][s2.length()];
	}

	/* Essaie de deviner le type de paramètre attendu
	 */
	private String devinerTypeParametre(String param) {
		if (param.matches("[0-9]+")) {
			return "Valeur numérique détectée. Ajoutez # devant (ex: #" + param + ")";
		} else if (param.toLowerCase().startsWith("r") && param.length() > 1) {
			return "Possible registre mal formé. Format attendu : R0, R1, R2, etc.";
		} else if (param.contains("#") && !param.startsWith("#")) {
			return "Le symbole # doit être au début de la valeur immédiate";
		} else if (param.matches(".*[^A-Za-z0-9_#\\-].*")) {
			return "Caractères spéciaux non autorisés. Utilisez uniquement lettres, chiffres et _";
		}
		return "Formats valides: R0-R15 (registre), #nombre (immédiat), ou étiquette";
	}

	public Instruction TypeParam() {

		String titre = obtenirMot();
		if (titre.isEmpty())
			return null;

		// Validation de l'instruction
		if (!instructionValide(titre)) {
			String suggestion = trouverInstructionProche(titre);
			String msgSuggestion;

			if (suggestion != null) {
				msgSuggestion = "Vouliez-vous dire '" + suggestion.toUpperCase() + "' ?";
			} else {
				msgSuggestion = "Instructions valides : ADD, SUB, MOV, LDR, STR, B, BEQ, BNE, CMP, etc.";
			}

			ajouterErreur("Instruction inconnue",
					"L'instruction '" + titre.toUpperCase() + "' n'existe pas dans le jeu d'instructions",
					msgSuggestion);

			return null;
		}

		Instruction instru = new Instruction(titre);

		try {
			instru.ajouterMot(new MotConst(0, ""));
		} catch (NotBinaryException ex) {
			ajouterErreur("Erreur interne",
					"Impossible de créer l'instruction '" + titre + "'",
					"Vérifiez que l'instruction existe dans le jeu d'instructions");
			ex.printStackTrace();
			return null;
		}

		passerEspace();
		String p1;
		boolean resteParams = true;
		int numParam = 0;

		while (resteParams) {
			numParam++;
			p1 = obtenirParam();

			if (p1.isEmpty()) {
				ajouterErreur("Paramètre manquant",
						"Paramètre " + numParam + " vide ou manquant pour l'instruction '" + titre + "'",
						"Vérifiez la syntaxe de l'instruction");
				break;
			}

			// Vérification spéciale pour STR, STRH, LDR, LDRH
			String titreLower = titre.toLowerCase();
			if ((titreLower.equals("str") || titreLower.equals("strh") || titreLower.equals("ldr") || titreLower.equals("ldrh")) && numParam == 2) {
				System.out.println("DEBUG Parser STR/LDR: numParam=2, p1='" + p1 + "'");

				// More flexible bracket matching - allow spaces around R and digits
				boolean validBracket = false;
				String regNum = null;

				// Try to extract register number from bracketed parameter
				if (p1.startsWith("[") && p1.endsWith("]")) {
					String inside = p1.substring(1, p1.length() - 1).trim();  // Remove brackets and trim
					System.out.println("DEBUG Parser STR/LDR: inside brackets='" + inside + "'");

					if (inside.toUpperCase().matches("R[0-9]+")) {
						validBracket = true;
						regNum = inside;
					}
				}

				if (!validBracket) {
					ajouterErreur("Format incorrect",
						"Pour l'instruction '" + titre.toUpperCase() + "', le deuxième paramètre doit être entre crochets",
						"Format attendu : " + titre.toUpperCase() + " R0, [R1]");
					System.out.println("DEBUG Parser STR/LDR: Bracket validation failed for '" + p1 + "'");
					break;
				}

				// Extract register number and create MotReg
				try {
					int regNumVal = Integer.parseInt(regNum.substring(1));
					if (regNumVal < 0 || regNumVal > 15) {
						ajouterErreur("Registre hors limites",
							"Le registre R" + regNumVal + " est invalide (R0-R15 acceptés)",
							"Utilisez un registre valide R0 à R15");
						break;
					}
					System.out.println("DEBUG Parser STR/LDR: Creating MotReg for R" + regNumVal);
					MotReg m = new MotReg(0);
					m.setVal(regNumVal);
					instru.ajouterMot(m);
				} catch (NumberFormatException nfe) {
					ajouterErreur("Erreur de parsing",
						"Impossible de parser le numéro de registre '" + regNum + "'",
						"Utilisez R0 à R15");
					break;
				}
			} else if (p1.matches("[rR][0-9][(][rR][0-9][rR][0-9][)]")) {
			
				MotReg m = new MotReg(0);
				m.setVal(Integer.parseInt(p1.substring(3)));
				instru.ajouterMot(m);
			} else if (p1.matches("[rR][0-9]+")) {
				MotReg m = new MotReg(0);
				m.setVal(Integer.parseInt(p1.substring(1)));
				instru.ajouterMot(m);
			} else if (p1.matches("[A-Za-z][A-Za-z0-9_]*")) {
				MotEtiq m = new MotEtiq(0);
				m.setVal(p1);
				instru.ajouterMot(m);
			} else if (p1.matches("#-?([0-9]+|0[xX][0-9A-Fa-f]+)")) {
				MotImm m = new MotImm(0, false);
				String valStr = p1.substring(1);
				long val;
				if (valStr.toLowerCase().startsWith("0x")) {
					val = Long.parseLong(valStr.substring(2), 16);
				} else {
					val = Long.parseLong(valStr);
				}

				// Validation de la plage pour les valeurs immédates
				if (val < 0 || val > 255) {
					ajouterErreur("Valeur immédiate hors limites",
							"La valeur " + val + " dépasse la limite de 8 bits (0-255)",
							"Utilisez une valeur entre 0 et 255 (ou 0x00 à 0xFF en hexadécimal)");
					break;
				}

				m.setVal((int) val);
				instru.ajouterMot(m);
			} else {
				String suggestion = devinerTypeParametre(p1);
				ajouterErreur("Paramètre non reconnu",
						"Le paramètre #" + numParam + " '" + p1 + "' n'est pas valide pour l'instruction '" + titre + "'",
						suggestion);
				break;
			}

			resteParams = paramSuivOpt();
		}

		String instrLower = titre.toLowerCase();

		// Préfixe binaire pour STR / LDR / STRH / LDRH
		// Forme : Rt, [Rn]  →  2 MotReg dans listeParams (index 1 et 2)
		if (instrLower.equals("str") || instrLower.equals("ldr")
				|| instrLower.equals("strh") || instrLower.equals("ldrh")) {
			java.util.ArrayList<Instruction.Mot> params = instru.listeParams;

			// Validation: ensure proper structure
			System.out.println("DEBUG Parser: Processing " + instrLower.toUpperCase() + " - params size = " + params.size());
			for (int i = 0; i < params.size(); i++) {
				Instruction.Mot m = params.get(i);
				System.out.println("  Param[" + i + "] type = " + m.getClass().getSimpleName());
			}

			if (params.size() < 3) {
				ajouterErreur("Erreur de structure",
						"Instruction " + instrLower.toUpperCase() + " invalide: parametres manquants",
						"Attendu: " + instrLower.toUpperCase() + " Rt,[Rn]");
			}

			try {
				// préfixes Thumb : STR=01100, LDR=01101, STRH=10000, LDRH=10001
				// ajout des bits d'offset à 0 pour atteindre 10 bits pour STR/LDR
				String prefix;
				int taille;
				switch (instrLower) {
					case "str":  prefix = "0110000000"; taille = 10; break;
					case "ldr":  prefix = "0110100000"; taille = 10; break;
					case "strh": prefix = "10000";      taille = 5;  break;
					case "ldrh": prefix = "10001";      taille = 5;  break;
					default:     prefix = "0110000000"; taille = 10; break;
				}
				params.set(0, new MotConst(taille, prefix));
				System.out.println("DEBUG Parser: Set prefix '" + prefix + "' (" + taille + " bits) for " + instrLower.toUpperCase());
			System.out.println("DEBUG Parser FINAL params count=" + params.size());
			for (int _fi = 0; _fi < params.size(); _fi++) {
				Instruction.Mot _fm = params.get(_fi);
				System.out.println("  FinalParam[" + _fi + "] type=" + _fm.getClass().getSimpleName() + " ordre=" + _fm.getOrdre() + " valBin='" + _fm.getValBin() + "'");
			}
			} catch (NotBinaryException nbe) {
				nbe.printStackTrace();
				ajouterErreur("Erreur de codage",
						"Impossible de créer le code binaire pour " + instrLower.toUpperCase(),
						"Erreur interne du parseur");
			} catch (IndexOutOfBoundsException ibe) {
				ajouterErreur("Erreur de structure",
						"Impossible d'accéder aux parametres de " + instrLower.toUpperCase(),
						"Vérifiez le format: " + instrLower.toUpperCase() + " Rt,[Rn]");
				ibe.printStackTrace();
			}
		}

		if (instrLower.equals("add") || instrLower.equals("sub")) {
			// access the list directly since we're in same package
			java.util.ArrayList<Instruction.Mot> params = instru.listeParams;
			try {
				if (params.size() == 4) {
					// three operands present
					Instruction.Mot third = params.get(3);
					if (third instanceof MotImm) {
						// Rd, Rn, #imm3
						MotImm imm = (MotImm) third;
						if (imm.getVal() > 7) {
							ajouterErreur("Valeur immédiate hors limites",
									"Immédiate trop grande pour ADD/SUB #imm3 (0-7)",
									"Utilisez un immédiat plus petit ou une autre forme d'instruction");
						}
						String prefix = instrLower.equals("add") ? "0001110" : "0001111";
						params.set(0, new MotConst(7, prefix, 1));
					} else if (third instanceof MotReg) {
						// Rd, Rn, Rm
						String prefix = instrLower.equals("add") ? "0001100" : "0001101";
						params.set(0, new MotConst(7, prefix, 1));
					}
				} else if (params.size() == 3) {
					Instruction.Mot second = params.get(2);
					if (second instanceof MotImm) {
						// Rd, #imm8
						String prefix = instrLower.equals("add") ? "00110" : "00111";
						params.set(0, new MotConst(5, prefix));
					}
				}
			} catch (NotBinaryException nbe) {
				// should never happen with hardcoded constants
				nbe.printStackTrace();
			}
		}
		return instru;
	}


	public boolean paramSuivOpt() {
		boolean ret = false;
		passerEspace();
		if (indice < texte.length() && texte.charAt(indice) == ',') {
			ret = true;
			indice++;
			colonne++;
		}

		passerEspace();
		return ret;
	}

	public void finLigne() {
		boolean surplusParam = false;
		StringBuilder paramsEnTrop = new StringBuilder();
		passerEspace();

		while (indice < texte.length()) {
			// stop at end of line or at start of comment '//'
			if (texte.charAt(indice) == '\n' || texte.charAt(indice) == '\r')
				break;
			if (texte.charAt(indice) == '/' && indice + 1 < texte.length() && texte.charAt(indice + 1) == '/')
				break;
			surplusParam = true;
			paramsEnTrop.append(texte.charAt(indice));
			indice++;
			colonne++;
		}

		if (surplusParam) {
			ajouterErreur("Paramètres en trop",
					"Paramètres excédentaires détectés : '" + paramsEnTrop.toString().trim() + "'",
					"Vérifiez le nombre de paramètres requis pour cette instruction");
		}

		// Avancer jusqu'à la fin de la ligne (inclut commentaires commençant par //)
		while (indice < texte.length() && texte.charAt(indice) != '\n' && texte.charAt(indice) != '\r') {
			indice++;
			colonne++;
		}

		while (indice < texte.length() && (texte.charAt(indice) == '\n' ||
				texte.charAt(indice) == '\r')) {
			indice++;
		}

		ligne++;
		colonne = 1;
		debutLigne = indice;
	}

	public boolean termine() {
		return indice < texte.length();
	}

}