/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.parseur;

import ensea.simulateurSTM32.ihm.Panel.PanelDialog;
import ensea.simulateurSTM32.parseur.Instruction.MotConst;
import ensea.simulateurSTM32.parseur.Instruction.MotEtiq;
import ensea.simulateurSTM32.parseur.Instruction.MotImm;
import ensea.simulateurSTM32.parseur.Instruction.MotReg;
import ensea.simulateurSTM32.parseur.Instruction.NotBinaryException;
//import compilateur.ZoneTexte;
import java.util.ArrayList;


/**
 * Permet d'analyser la structure d'une chaine de caractères.
 * @author louiroll
 */
public class Parseur {

	public int indice;
	public String texte;
	//public ZoneTexte zoneErreur;
        public PanelDialog zoneErreur;
	public int ligne;

	/**
	 *
	 * @param texte le texte à parser.
	 * @param zoneErreur zone texte d'erreur.
	 */
	public Parseur(String texte, PanelDialog zoneErreur)
	{
		indice=0;
		ligne=1;
		this.texte=texte;
		this.zoneErreur=zoneErreur;
                System.out.println("Parseur");
	}

	/**
	 * Permet de sauter les éventuels espaces et tabulations.
	 */
	public void passerEspace(){
		while(indice<texte.length() && (texte.charAt(indice)==' ' || texte.charAt(indice)=='\t'))
			indice++;
	}

	/**
	 * Permet d'obtenir le prochain mot de la chaine de caractères.
	 * @return le mot.
	 */
	public String obtenirMot(){
		String s="";
		while(indice<texte.length() && (s+texte.charAt(indice)).matches("[A-Za-z_][A-Za-z0-9_]*"))
		{
			s+=texte.charAt(indice);
			indice++;
		}
		return s;
	}

	/**
	 * Permet d'obtenir le prochain paramètre de la chaine de caractères.
	 * @return le paramètre
	 */
	public String obtenirParam(){
		String s="";
		passerEspace();
		while(indice<texte.length() && (s+texte.charAt(indice)).matches("#?-?[A-Za-z0-9_]*"))
		{
			s+=texte.charAt(indice);
			indice++;
		}
		return s;
	}

	/**
	 * Permet d'obtenir la prochaine valeur immédiate.
	 * @return
	 */
	public int obtenirImm(){
		String s="";

		if(texte.charAt(indice)=='#')
		{
			indice++;
			while((s+texte.charAt(indice)).matches("-?[0-9]+"))
			{
				s+=texte.charAt(indice);
				indice++;
			}
		}
		else
			zoneErreur.setText(zoneErreur.getText()+"ligne "+ligne+" : Erreur, valeur immédiate attendue.\n");
		return Integer.parseInt(s);
	}

	/**
	 * Permet d'obtenir la valeur immédiate d'une étiquette
	 * @param listeEtiquette liste des étiquettes détecté dans la chaine de caractère.
	 * @return la valeur immédiate de l'étiquette ou 0 si l'étiquette est introuvable.
	 */
	public int obtenirImmEtiq(ArrayList<Etiquette> listeEtiquette){
		String s="";

		if(texte.charAt(indice)=='#')
		{
			indice++;
			while((s+texte.charAt(indice)).matches("-?[0-9]+"))
			{
				s+=texte.charAt(indice);
				indice++;
			}
			return Integer.parseInt(s);
		}
		else
		{
			String nomEtiq = obtenirMot();
			for(Etiquette etiq : listeEtiquette)
			{
				if(etiq.nom.equals(nomEtiq))
					return etiq.adresse-ligne;
			}
			zoneErreur.setText(zoneErreur.getText()+"ligne "+ligne+" : Erreur, etiquette \""+nomEtiq+"\" introuvable.\n");
			return 0;
		}
	}

	/**
	 * Boolean qui permet de savoir si il s'agit d'une valeur immédiate(donc précédée de #).
	 * @return 1 si le caractère est #, 0 sinon.
	 */
	public boolean estImmediat(){
		return texte.charAt(indice)=='#';
	}

	/**
	 * Analyse la ligne en cours pour déterminer l'instruction correspondante.
	 * @return l'instruction résultant de l'analyse de la ligne en cours du parseur.
	 */
	public Instruction TypeParam()
	{
		/* Instancie une nouvelle instruction portant le nom du premier mot trouvé */
		String titre=obtenirMot();
		if(titre.equals(""))
			return null;
		Instruction instru=new Instruction(titre);

		/* Rajoute un mot constant vide correspondant au titre de l'instruction */
		try {
			instru.ajouterMot(new MotConst(0, ""));
		} catch (NotBinaryException ex) {
			ex.printStackTrace();
		}

		/* Passer du côté des paramètres */
		passerEspace();
		String p1;
		boolean resteParams=true;

		/* Analyser les paramètres jusqu'à la fin de la ligne */
		while(resteParams)
		{
			p1=obtenirParam();
			if(p1.matches("[rR][0-9]+"))
			{
				MotReg m=new MotReg(0);
				m.setVal(Integer.parseInt(p1.substring(1)));
				instru.ajouterMot(m);
			}
			else if(p1.matches("[A-Za-z][A-Za-z0-9]*"))
			{
				MotEtiq m=new MotEtiq(0);
				m.setVal(p1);
				instru.ajouterMot(m);
			}
			else if(p1.matches("#-?[0-9]+"))
			{
				MotImm m=new MotImm(0,false);
				m.setVal(Integer.parseInt(p1.substring(1)));
				instru.ajouterMot(m);
			}
			else
				zoneErreur.setText(zoneErreur.getText()+"ligne "+ligne+" : Paramètre non identifiee:"+p1+"\n");
			resteParams = paramSuivOpt();
		}
		return instru;
	}

	/**
	 * Permet d'arriver au paramètre suivant, et vérifie qu'une virgule est bien présente avant.
	 */
	public void paramSuiv()
	{
		passerEspace();
		if(indice<texte.length() && texte.charAt(indice)!=',')
		{
			zoneErreur.setText(zoneErreur.getText()+"ligne "+ligne+" : Erreur, virgule attendue.\n");
		}
		else
			indice++;
		passerEspace();
	}

	/**
	 * Permet d'arriver au paramètre suivant, et vérifie qu'une virgule est bien présente avant.
	 * @return true si il reste des paramètres
	 */
	public boolean paramSuivOpt()
	{
		boolean ret=false;
		passerEspace();
		if(indice<texte.length() && texte.charAt(indice)==',')
		{
				ret=true;
				indice++;
		}
		
		passerEspace();
		return ret;
	}

	/**
	 * Permet d'avancer jusqu'à la fin de la ligne et de passer à la ligne suivante.
	 */
	public void finLigne()
	{
		boolean surplusParam=false;
		passerEspace();
		while(indice<texte.length() && texte.charAt(indice)!='\n' && texte.charAt(indice)!='\r' && texte.charAt(indice)!=';')
		{
			if(!surplusParam)
				zoneErreur.setText(zoneErreur.getText()+"ligne "+ligne+" : Paramètres en trop sur la ligne : ");
			surplusParam=true;
			zoneErreur.setText(zoneErreur.getText()+texte.charAt(indice));
			indice++;
		}
		while(indice<texte.length() && texte.charAt(indice)!='\n' && texte.charAt(indice)!='\r')
				indice++;
		while(indice<texte.length() && (texte.charAt(indice)=='\n' || texte.charAt(indice)=='\r'))
				indice++;
		if(surplusParam)
			zoneErreur.setText(zoneErreur.getText()+"\n");
		ligne++;
	}

	/**
	 *  Permet de savoir si le parsage est terminé.
	 * @return 1 si le parsage est terminé, 0 sinon.
	 */
	public boolean termine()
	{
		return indice>=texte.length()-1;
	}
}
