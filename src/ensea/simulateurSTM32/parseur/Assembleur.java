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
import javax.swing.text.JTextComponent;


/**
 * Déclare les fonctions générale d'un Assembleur.
 * @author louiroll
 */
public abstract class Assembleur {
	protected ArrayList<Instruction> listeCommandes;
	styleAsm styleTexte;
        
	
       //ZoneTexte src,dest,err;
        PanelCode src;
        PanelDialog err; 
        
        
	ActionCompilation assembler;

	public Assembleur()
	{
		listeCommandes=new ArrayList();
	}
        
        public ArrayList<Instruction> getListeCommandes() {
            return listeCommandes;
        }

	public void preparerES()
	{
		initCommandes();
		//styleTexte = new styleAsm(Main.fen.getCodeAss());
                styleTexte = new styleAsm(MainFrame.ihm.panelcode);
		assembler = new ActionCompilation(MainFrame.ihm.panelcode, MainFrame.ihm.panelcode,MainFrame.ihm.panelDialog); //new ActionCompilation(MainFrame.ihm.panelcode,Main.fen.getCodeBin(),Main.fen.getErreur());
                MainFrame.ihm.panelcode.getjTextPane1().setDocument(styleTexte);
                //Main.fen.getCodeAss().getPanneau().setDocument(styleTexte);
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
			String retour="";
			for(Instruction istr : listeCommandes)
			{
				retour+="\\W"+istr.getNom().toUpperCase()+"\\W|"
						+"\\W"+istr.getNom().toLowerCase()+"\\W|"
						+"\\W"+istr.getNom().toUpperCase()+"$|"
						+"\\W"+istr.getNom().toLowerCase()+"$|";
			}
			retour+="R[0-9]+";
			return retour;
		}

		@Override
		public void initStyle()
		{
                        int index=0;
			words = new String[]
			{
				"[a-zA-Z]\\w*\\W|[a-zA-Z]\\w*$",
				"#-?[0-9]+",
				"^[a-zA-Z]\\w*\\W|^[a-zA-Z]\\w*$",
				listeNomComm(),
				"\".*\"",
				"^[\\t ]*#.*",
				";.*\\n|;.*$"
			};

			styles = new Style[7];
			normal = sc.addStyle("normal", null);
			normal.addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			normal.addAttribute(StyleConstants.FontSize, new Integer(14));
			normal.addAttribute(StyleConstants.FontFamily, "Courier New");
			normal.addAttribute(StyleConstants.Bold, false);

			styles[index]= sc.addStyle("parametre", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,128));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("chiffre", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,128,0));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("etiquette", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,128));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("controle", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,0,192));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index].addAttribute(StyleConstants.FontFamily, "Courier New");
			styles[index++].addAttribute(StyleConstants.Bold, true);

			styles[index]= sc.addStyle("texte", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(128,0,0));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");

			styles[index]= sc.addStyle("preprocesseur", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,192,0));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index].addAttribute(StyleConstants.FontFamily, "Courier New");
			styles[index++].addAttribute(StyleConstants.Italic, true);

			styles[index]= sc.addStyle("comentaire", null);
			styles[index].addAttribute(StyleConstants.Foreground, new Color(0,0,0));
			styles[index].addAttribute(StyleConstants.FontSize, new Integer(14));
			styles[index].addAttribute(StyleConstants.Background, new Color(192,192,192));
			styles[index++].addAttribute(StyleConstants.FontFamily, "Courier New");
		}
	}

	/**
	 *
	 * @return le style du texte.
	 */
	public styleAsm getStyle()
	{
		return styleTexte;
	}

	public ActionCompilation getAction()
	{
		return assembler;
	}

	public class ActionCompilation implements ActionListener{
		//public ZoneTexte e,s,err;
                public PanelCode e;
                public PanelDialog err;
		
		public ActionCompilation(PanelCode e, PanelCode s, PanelDialog err){
			this.e=e;
			//this.s=s;
			this.err=err;
		}
		/**
		 * Procède à l'assemblage du fichier assembleur vers le binaire.
		 * @param event l'évenement déclenchant l'assemblage.
		 */
                 @Override
		public void actionPerformed(ActionEvent event) {
			/* Variables nécessaire pour l'assemblage */
			int ligne=0;
			ArrayList<Etiquette> listeEtiquette=new ArrayList();
			err.setText("");
                       // s.setText("");
			Parseur parsEtiq=new Parseur(e.getText(),err);
                        System.out.println("Assemblage GO");
                        System.out.println(e.getText());


			/* Premier parsage de la zone de texte pour récupérer les etiquettes */
			while(!parsEtiq.termine()){
				String a1=parsEtiq.obtenirMot();
				if(!a1.equals("")){
					listeEtiquette.add(new Etiquette(ligne, a1));
				}
				parsEtiq.passerEspace();
				if(parsEtiq.TypeParam()!=null)
					ligne++;
				parsEtiq.finLigne();
			}
			if(err.getText().equals(""))
			{
				ligne=0;
				Parseur parsInstr=new Parseur(e.getText(),err);
				Instruction reconnue,identifiee=null;
                                String binaire="";
				boolean bool=true;

				/* Second parsage de la zone de texte pour récupérer les instructions */
				while(!parsInstr.termine()){
					/* Sauter l'étiquette */
					parsInstr.obtenirMot();
					parsInstr.passerEspace();

					

					/* Prendre l'instruction */
					if((reconnue=parsInstr.TypeParam())!=null)
					{
						ligne++;
                                                identifiee=null;
						for(Instruction i : listeCommandes)
						{
							if(i.compInstr(reconnue))
                                                        {
							identifiee=i;
                                                        }
                                                }
						if(identifiee!=null)
						{
							reconnue.charger(identifiee);
							binaire=binaire+reconnue.genererBin(listeEtiquette,ligne)+"\n";
						}
						else
                                                {
							err.setText(err.getText()+"ligne "+ligne+" : Instruction non identifiée\n");
							bool=false;
						}
					}
					parsInstr.finLigne();
				}
				//if(bool) s.setText(binaire);
			}
		}
	}
}
