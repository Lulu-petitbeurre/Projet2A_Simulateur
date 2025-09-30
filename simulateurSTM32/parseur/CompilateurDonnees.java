/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.parseur;

//import compilateur.ZoneTexte;
import ensea.simulateurSTM32.ihm.Panel.PanelCode;
import ensea.simulateurSTM32.ihm.Panel.PanelDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author louiroll
 */
public class CompilateurDonnees implements ActionListener
{
	//ZoneTexte src,dest,err;
        PanelCode src;
        PanelDialog err;

	public CompilateurDonnees(PanelCode e, PanelCode s, PanelDialog err)
	{
		this.src=e;
		//this.dest=s;
		this.err=err;
	}
	
	/**
	 * Procède à l'assemblage du fichier assembleur vers le binaire.
	 * @param event l'évenement déclenchant l'assemblage.
	 */
	public void actionPerformed(ActionEvent event)
	{
		Parseur parsDir=new Parseur(src.getText(),err);
		String s1,s2,s3,s4;
		int p;
		//dest.setText("");
		err.setText("");

		int ligne=1;
		while(!parsDir.termine())
		{
			parsDir.obtenirMot();
			parsDir.passerEspace();
			s1=parsDir.obtenirMot();
			if(s1.equalsIgnoreCase("DCD"))
			{
				parsDir.passerEspace();
				if((s2=parsDir.obtenirParam()).matches("[0-9]{1,9}"))
				{
					p=Integer.parseInt(s2);
					s2=Integer.toBinaryString(p);
					if(s2.length()>32)
						err.setText(err.getText()+"ligne "+ligne+" :La valeur ne peut pas être codée sur 32bits.\n");
					else
					{
						while(s2.length()<32)
							s2="0"+s2;
					}
					s3=s2.substring(16,32);
					s4=s2.substring(0,16);

					//dest.setText(dest.getText()+s4+"\n"+s3+"\n");
				}
				else
					err.setText(err.getText()+"ligne "+ligne+" :Valeur initiale incorrecte.\n");
			}
			else if(s1.equalsIgnoreCase("SPACE"))
			{
				parsDir.passerEspace();
				if((s2=parsDir.obtenirParam()).matches("[0-9]{1,9}"))
				{
					p=Integer.parseInt(s2);
					if(p%4==0)
					{
						for(int i=0;i<p/2;i++)
						{
							//dest.setText(dest.getText()+"0000000000000000\n");
						}
					}
					else
						err.setText(err.getText()+"ligne "+ligne+" : La variable doit prendre comme espace un multiple de 16bit.\n");
				}
			}
			else
			{
				err.setText(err.getText()+"ligne "+ligne+" : Directive non identifiée\n");
			}
		parsDir.finLigne();
		ligne++;
		}
	}

	public class Variable
	{
		public int adresse;
		public String nom;

		public Variable(int adresse, String nom)
		{
			this.adresse=adresse;
			this.nom=nom;
		}
	}
}

