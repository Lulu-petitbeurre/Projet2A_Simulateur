/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.parseur;

import ensea.simulateurSTM32.ihm.Panel.PanelCode;
import ensea.simulateurSTM32.ihm.Panel.PanelDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author louiroll
 */
public class CompilateurDonnees implements ActionListener
{
	//ZoneTexte src,dest,err;
        PanelCode src;
        PanelDialog err;
	private static final String[] DIRECTIVES_VALIDES = {"DCD", "SPACE", "DCB", "DCW", "ALIGN"};

	public CompilateurDonnees(PanelCode e, PanelCode s, PanelDialog err)
	{
		this.src=e;
		//this.dest=s;
		this.err=err;
	}

	/**
	 * Parse une valeur numérique (décimale ou hexadécimale)
	 */
	private long parseNumValue(String valStr) throws NumberFormatException {
		if (valStr.toLowerCase().startsWith("0x")) {
			return Long.parseLong(valStr.substring(2), 16);
		} else {
			return Long.parseLong(valStr);
		}
	}

	/**
	 * Vérifie si une chaîne est un nombre valide (décimal ou hexadécimal)
	 */
	private boolean isValidNumber(String str) {
		if (str.isEmpty()) return false;
		if (str.toLowerCase().startsWith("0x")) {
			String hex = str.substring(2);
			if (hex.isEmpty()) return false;
			return hex.matches("^[0-9A-Fa-f]+$");
		} else if (str.startsWith("-")) {
			return str.substring(1).matches("^[0-9]+$");
		} else {
			return str.matches("^[0-9]+$");
		}
	}

	/**
	 * Procède à l'assemblage du fichier assembleur vers le binaire.
	 */
	public void actionPerformed(ActionEvent event)
	{
		Parseur parsDir=new Parseur(src.getText(),err);
		String s1;
		err.setText("");

		int ligne=1;
		boolean erreurDetectee = false;

		while(parsDir.termine())
		{
			if (erreurDetectee) {
				break;
			}

			parsDir.obtenirMot(); // Lire et ignorer l'étiquette
			parsDir.passerEspace();
			s1=parsDir.obtenirMot();

			if (s1.isEmpty()) {
				// Ligne vide ou commentaire, passer
				parsDir.finLigne();
				ligne++;
				continue;
			}

			// Vérifier si la directive est valide
			if (!directiveValide(s1)) {
				ajouterErreur(ligne, "Directive inconnue",
						"La directive '" + s1 + "' n'est pas reconnue",
						"Directives valides: DCD, SPACE, DCB, DCW, ALIGN");
				erreurDetectee = true;
				parsDir.finLigne();
				ligne++;
				continue;
			}
			// Traiter la directive
			if (s1.equalsIgnoreCase("DCD")) {
				erreurDetectee |= traiterDCD(parsDir, ligne);
			}
			else if (s1.equalsIgnoreCase("SPACE")) {
				erreurDetectee |= traiterSPACE(parsDir, ligne);
			}
			else if (s1.equalsIgnoreCase("DCB")) {
				erreurDetectee |= traiterDCB(parsDir, ligne);
			}
			else if (s1.equalsIgnoreCase("DCW")) {
				erreurDetectee |= traiterDCW(parsDir, ligne);
			}
			else if (s1.equalsIgnoreCase("ALIGN")) {
				erreurDetectee |= traiterALIGN(parsDir, ligne);
			}
			else {
				ajouterErreur(ligne, "Directive non implémentée",
						"La directive '" + s1 + "' n'est pas encore implémentée",
						null);
				erreurDetectee = true;
			}




		parsDir.finLigne();
		ligne++;
	}

        if (erreurDetectee) {
	err.setText(err.getText() + "\n" +
			"RÉSUMÉ: Des erreurs ont été détectées dans les données\n" +
			"Le programme ne peut pas être assemblé correctement\n" + "\n");
} else {
	err.setText(err.getText() +
			"✓ Analyse des données terminée avec succès\n");
}
}

/**
 * Traite la directive DCD (Define Constant Data - 32 bits)
 */
private boolean traiterDCD(Parseur parsDir, int ligne) {
	parsDir.passerEspace();
	String s2 = parsDir.obtenirParam();

	if (!isValidNumber(s2)) {
		ajouterErreur(ligne, "Valeur DCD invalide",
				"'" + s2 + "' n'est pas une valeur numérique valide",
				"Formats acceptés: 42, -123, 0xFF, 0x0F");
		return true;
	}

	try {
		long p = parseNumValue(s2);

		// Vérifier les limites 32 bits signés
		if (p < Integer.MIN_VALUE || p > Integer.MAX_VALUE) {
			ajouterErreur(ligne, "Valeur hors limites",
					"La valeur " + p + " ne peut pas être codée sur 32 bits signés",
					"Plage valide: " + Integer.MIN_VALUE + " à " + Integer.MAX_VALUE);
			return true;
		}

		// Conversion DCD effectuée avec succès
		// (Traitement du binaire commenté pour le moment)

		return false; // Pas d'erreur

	} catch (NumberFormatException e) {
		ajouterErreur(ligne, "Erreur de conversion",
				"Impossible de convertir '" + s2 + "' en nombre",
				"Vérifiez que la valeur est un entier valide");
		return true;
	}
}

/**
 * Traite la directive SPACE (réserve de l'espace)
 */
private boolean traiterSPACE(Parseur parsDir, int ligne) {
	parsDir.passerEspace();
	String s2 = parsDir.obtenirParam();

	if (!isValidNumber(s2)) {
		ajouterErreur(ligne, "Valeur SPACE invalide",
				"'" + s2 + "' n'est pas une taille valide",
				"Formats acceptés: 16, 0x10, 0xFF (taille en octets, multiple de 4)");
		return true;
	}

	try {
		long pLong = parseNumValue(s2);

		if (pLong < 0 || pLong > Integer.MAX_VALUE) {
			ajouterErreur(ligne, "Taille invalide",
					"La taille " + pLong + " n'est pas valide",
					"Utilisez une valeur positive");
			return true;
		}

		int p = (int) pLong;

		if (p % 4 != 0) {
			ajouterErreur(ligne, "Alignement incorrect",
					"La taille " + p + " n'est pas un multiple de 4 octets",
					"Les espaces doivent être alignés sur 32 bits (4 octets)");
			return true;
		}

		// Réserver l'espace (écrire des zéros)
		for (int i = 0; i < p / 2; i++) {
			// dest.setText(dest.getText() + "0000000000000000\n");
		}

		return false; // Pas d'erreur

	} catch (NumberFormatException e) {
		ajouterErreur(ligne, "Erreur de conversion",
				"Impossible de convertir '" + s2 + "' en nombre",
				null);
		return true;
	}
}

/**
 * Traite la directive DCB (Define Constant Byte - 8 bits)
 */
private boolean traiterDCB(Parseur parsDir, int ligne) {
	parsDir.passerEspace();
	String s2 = parsDir.obtenirParam();

	if (!isValidNumber(s2)) {
		ajouterErreur(ligne, "Valeur DCB invalide",
				"'" + s2 + "' n'est pas un octet valide",
				"Formats acceptés: 255, 0xFF, 0x0F (valeur 0-255)");
		return true;
	}

	try {
		long valeur = parseNumValue(s2);

		if (valeur < 0 || valeur > 255) {
			ajouterErreur(ligne, "Valeur hors limites",
					"La valeur " + valeur + " ne peut pas être codée sur 8 bits",
					"Plage valide: 0 à 255");
			return true;
		}

		// Traitement DCB...
		return false;

	} catch (NumberFormatException e) {
		ajouterErreur(ligne, "Erreur de conversion",
				"Impossible de convertir '" + s2 + "' en nombre", null);
		return true;
	}
}

/**
 * Traite la directive DCW (Define Constant Word - 16 bits)
 */
private boolean traiterDCW(Parseur parsDir, int ligne) {
	parsDir.passerEspace();
	String s2 = parsDir.obtenirParam();

	if (!isValidNumber(s2)) {
		ajouterErreur(ligne, "Valeur DCW invalide",
				"'" + s2 + "' n'est pas un mot de 16 bits valide",
				"Formats acceptés: 65535, 0xFFFF, 0x0F (valeur 0-65535)");
		return true;
	}

	try {
		long valeur = parseNumValue(s2);

		if (valeur < 0 || valeur > 65535) {
			ajouterErreur(ligne, "Valeur hors limites",
					"La valeur " + valeur + " ne peut pas être codée sur 16 bits",
					"Plage valide: 0 à 65535");
			return true;
		}

		// Traitement DCW...
		return false;

	} catch (NumberFormatException e) {
		ajouterErreur(ligne, "Erreur de conversion",
				"Impossible de convertir '" + s2 + "' en nombre", null);
		return true;
	}
}

/**
 * Traite la directive ALIGN (alignement mémoire)
 */
private boolean traiterALIGN(Parseur parsDir, int ligne) {
	// La directive ALIGN peut avoir un paramètre optionnel
	parsDir.passerEspace();
	String s2 = parsDir.obtenirParam();

	if (!s2.isEmpty() && !s2.matches("[0-9]+")) {
		ajouterErreur(ligne, "Valeur ALIGN invalide",
				"'" + s2 + "' n'est pas une valeur d'alignement valide",
				"Format attendu: ALIGN  ou  ALIGN 4");
		return true;
	}

	return false;
}

/**
 * Vérifie si une directive est valide
 */
private boolean directiveValide(String directive) {
	String dirUpper = directive.toUpperCase();
	for (String dir : DIRECTIVES_VALIDES) {
		if (dir.equals(dirUpper)) {
			return true;
		}
	}
	return false;
}

/**
 * Ajoute un message d'erreur formaté
 */
private void ajouterErreur(int ligne, String type, String detail, String suggestion) {
		StringBuilder erreur = new StringBuilder();
		erreur.append("ERREUR\n");
		erreur.append(String.format("Ligne %d\n", ligne));
		erreur.append("Type: ").append(type).append("\n");
		erreur.append("\n");
		erreur.append("Détail: ").append(detail).append("\n");

		if (suggestion != null && !suggestion.isEmpty()) {
			erreur.append("Suggestion: ").append(suggestion).append("\n");
		}
		err.setText(err.getText() + erreur.toString());

}

/**
 * Classe interne pour représenter une variable
 */
public static class Variable {
	public int adresse;
	public String nom;
	public String type; // DCD, DCB, DCW, SPACE
	public int taille;

	public Variable(int adresse, String nom, String type, int taille) {
		this.adresse = adresse;
		this.nom = nom;
		this.type = type;
		this.taille = taille;
	}

	@Override
	public String toString() {
		return String.format("%s @ 0x%08X (%s, %d octets)",
				nom, adresse, type, taille);
	}
}

// Liste des variables déclarées (pour référence future)
private final List<Variable> variables = new ArrayList<>();

public List<Variable> getVariables() {
	return variables;
}
}