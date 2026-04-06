/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.parseur;

import ensea.simulateurSTM32.ihm.ColorationSyntaxique;
import ensea.simulateurSTM32.simulateurstm32.MainFrame;
import ensea.simulateurSTM32.ihm.Panel.PanelCode;
import ensea.simulateurSTM32.ihm.Panel.PanelDialog;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;


/**
 * Déclare les fonctions générale d'un Assembleur.
 * @author louiroll
 */
public abstract class Assembleur {
	protected ArrayList<Instruction> listeCommandes;
	styleAsm styleTexte;
        
	ActionCompilation assembler;

	public Assembleur()
	{
		listeCommandes=new ArrayList<>();
	}
        
	public ArrayList<Instruction> getListeCommandes() {
		return listeCommandes;
	}

	public void preparerES()
	{
		initCommandes();
		styleTexte = new styleAsm(MainFrame.ihm.panelcode);
		assembler = new ActionCompilation(MainFrame.ihm.panelcode, MainFrame.ihm.panelcode,MainFrame.ihm.panelDialog);
		MainFrame.ihm.panelcode.getjTextPane1().setDocument(styleTexte);
	}
	
	protected abstract void initCommandes();

	public class styleAsm extends ColorationSyntaxique
	{
		public styleAsm(PanelCode comp)
		{
			super(comp.getjTextPane1());
			System.out.println("Constructeur styleAsm");
		}

		public String listeNomComm()
		{
			StringBuilder retour= new StringBuilder();
			// Construire une alternation qui matche une instruction en début de mot
			for(Instruction istr : listeCommandes)
			{
				// matcher l'instruction comme mot séparé (début de ligne ou non-mot avant, et fin de ligne ou non-mot après)
				retour.append("(?:^|\\W)")
					.append(istr.getNom())
					.append("(?:$|\\W)")
					.append("|");
			}
			// ajouter la reconnaissance des registres
			retour.append("R[0-9]+");
			// rendre l'expression insensible à la casse
			return "(?i)" + retour.toString();
		}

		@Override
		public void initStyle()
		{
			int index=0;
			words = new String[]
			{
				"#(?:-?[0-9]+|-?0[xX][0-9A-Fa-f]+)",
				"[a-zA-Z]\\w*\\W|[a-zA-Z]\\w*$",
				"^[a-zA-Z]\\w*(?=\\s*:)",
				listeNomComm(),
				"\".*\"",
				"^[\\t ]*#.*",
				"//.*\\n|//.*$"
			};

			styles = new Style[7];
			normal = sc.addStyle("normal", null);
			normal.addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			normal.addAttribute(StyleConstants.FontSize, 14);
			normal.addAttribute(StyleConstants.FontFamily, "Courier New");
			normal.addAttribute(StyleConstants.Bold, false);

			styles[index]= sc.addStyle("chiffre", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,128,0));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("parametre", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,128));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("etiquette", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,128));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("controle", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,0,192));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index].addAttribute(StyleConstants.FontFamily, "Courier New");
			styles[index++].addAttribute(StyleConstants.Bold, true);

			styles[index]= sc.addStyle("texte", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,0));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("preprocesseur", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,192,0));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index].addAttribute(StyleConstants.FontFamily, "Courier New");
			styles[index++].addAttribute(StyleConstants.Italic, true);

			styles[index]= sc.addStyle("comentaire", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			styles[index].addAttribute(StyleConstants.FontSize, 14);
			styles[index].addAttribute(StyleConstants.Background, new Color(192,192,192));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");
		}
	}


	public class ActionCompilation implements ActionListener {
		public PanelCode e;
		public PanelCode s;
		public PanelDialog err;

		public ActionCompilation(PanelCode e, PanelCode s, PanelDialog err) {
			this.e = e;
			this.s = s;
			this.err = err;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			// 1. Initialisation
			err.setText("");
			ArrayList<Etiquette> listeEtiquette = new ArrayList<>();
			int adresseCourante = 0; // Compteur d'octets pour calculer les adresses des étiquettes

			System.out.println("--- Début Assemblage ---");

			// ==== PASSE 1 : Repérage des étiquettes ====
			Parseur pars1 = new Parseur(e.getText(), err);

			while (pars1.termine()) {
				String mot = pars1.obtenirMot();

				if (!mot.isEmpty()) {
					// Détecter une étiquette uniquement si le mot est suivi d'un ':'
					int tempIdx = pars1.indice;
					int spaces = 0;
					while (tempIdx < pars1.texte.length() && 
						   (pars1.texte.charAt(tempIdx) == ' ' || 
							pars1.texte.charAt(tempIdx) == '\t')) {
						tempIdx++;
						spaces++;
					}
					if (tempIdx < pars1.texte.length() && 
						pars1.texte.charAt(tempIdx) == ':') {
						// C'est une étiquette : on l'ajoute et on avance après ':'
						listeEtiquette.add(new Etiquette(adresseCourante, mot));
						pars1.indice = tempIdx + 1;
						pars1.colonne += mot.length() + spaces + 1;
					} else {
						// Ce n'est pas une étiquette : remettre l'indice en arrière pour traiter une instruction
						pars1.indice -= mot.length();
						pars1.colonne -= mot.length();
					}
				}

				Instruction instr = pars1.TypeParam();
				
				// Si le parseur a ajouté des erreurs, on continue la passe
				if (!err.getText().isEmpty()) {
					System.out.println("Erreur détectée en Passe 1 : continuation pour collecter autres erreurs");
				}
				
				if (instr != null) {
					adresseCourante += 4;
				}
				
				pars1.finLigne();
				
				// Vérifier à nouveau après finLigne() car elle peut aussi détecter des erreurs
				if (!err.getText().isEmpty()) {
					System.out.println("Erreur détectée en Passe 1 (après finLigne) : continuation pour collecter autres erreurs");
				}
			}

			// ==== PASSE 2 : Génération du code binaire ====
			Parseur pars2 = new Parseur(e.getText(), err);
			StringBuilder binaire = new StringBuilder();
			int ligne = 0;

			while (pars2.termine()) {
				ligne++;

				String mot = pars2.obtenirMot();
				if (!mot.isEmpty()) {
					// Détecter une étiquette uniquement si le mot est suivi d'un ':'
					int tempIdx2 = pars2.indice;
					int spaces2 = 0;
					while (tempIdx2 < pars2.texte.length() && 
						   (pars2.texte.charAt(tempIdx2) == ' ' || 
							pars2.texte.charAt(tempIdx2) == '\t')) {
						tempIdx2++;
						spaces2++;
					}
					if (tempIdx2 < pars2.texte.length() && 
						pars2.texte.charAt(tempIdx2) == ':') {
						// C'est une étiquette : on avance après ':' et on ignore pour cette passe
						pars2.indice = tempIdx2 + 1;
						pars2.colonne += mot.length() + spaces2 + 1;
					} else {
						// Ce n'est pas une étiquette : remettre l'indice en arrière pour traiter l'instruction
						pars2.indice -= mot.length();
						pars2.colonne -= mot.length();
					}
				}

				Instruction reconnue = pars2.TypeParam();
				
				// Si le parseur a ajouté des erreurs, on ne fait pas d'arrêt immédiat
				if (!err.getText().isEmpty()) {
					System.out.println("Erreur détectée en Passe 2 (ligne " + ligne + ") : continuation pour collecter autres erreurs");
				}

				if (reconnue != null) {
					Instruction identifiee = null;
					for (Instruction i : listeCommandes) {
						if (i.getNom().equalsIgnoreCase(reconnue.getNom())) {
							if (i.compInstr(reconnue)) {
								identifiee = i;
								break;
							}
						}
					}

					if (identifiee != null) {
						reconnue.charger(identifiee);

						try {
							String codeHexa = reconnue.genererBin(listeEtiquette, ligne);
							binaire.append(codeHexa).append("\n");
						} catch (Exception ex) {
							err.setText(err.getText() + "Ligne " + ligne + ": Erreur de génération (" + ex.getMessage() + ")\n");
							s.setText("");
							// avancer à la fin de la ligne pour ne pas rester bloqué
							pars2.finLigne();
							continue;
						}
					} else {
						// Instruction non reconnue dans la liste des commandes
						err.setText(err.getText() + "Ligne " + ligne + " : Instruction ou syntaxe non reconnue : " + reconnue.getNom() + "\n");
						s.setText("");
						// avancer à la fin de la ligne pour ne pas rester bloqué
						pars2.finLigne();
						// continuer la passe pour accumuler d'autres erreurs
						continue;
					}
				}
				
				pars2.finLigne();
				
				// *** VÉRIFICATION après finLigne ***
				if (!err.getText().isEmpty()) {
					System.out.println("Erreur détectée en Passe 2 (après finLigne, ligne " + ligne + ") : continuation pour collecter autres erreurs");
				}
			}

			// Si on arrive ici, afficher le résultat uniquement s'il n'y a pas d'erreurs
			if (err.getText().isEmpty()) {
				s.setText(binaire.toString());
				err.setText("Assemblage réussi !");
			} else {
				// garder les erreurs affichées et ne pas produire de binaire
				s.setText("");
			}
			System.out.println("Code assembleur correct: SIMULATION POSSIBLE");
		}

		// Petite méthode utilitaire pour distinguer Label vs Instruction
		private boolean estInstruction(String mot) {
			for (Instruction i : listeCommandes) {
				if (i.getNom().equalsIgnoreCase(mot))
					return true;
			}
			return false;
		}
	}

}