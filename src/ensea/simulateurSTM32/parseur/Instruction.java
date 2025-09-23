/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.parseur;


import java.util.ArrayList;

/**
 * La brique de base des jeux d'instructions Assembleur
 * @author louiroll
 */
public class Instruction {
	protected String nom;
	protected ArrayList<Mot> listeParams;
        //added by LM BEGIN
        protected Limits limit;
        protected int line;
        //added by LM BEGIN
        
        
	/**
	 * Créé une nouvelle instruction pour Assembleur.
	 * @param n le nom de l'instruction
	 */
	public Instruction(String n)
	{
		listeParams = new ArrayList();
                //added by LM BEGIN
                line=0;
                //added by LM BEGIN
		nom=n;
	}
        
        
        public ArrayList<Mot> getListeParams() {
            return listeParams;
        }

        public void setListeParams(ArrayList<Mot> listeParams) {
            this.listeParams = listeParams;
        }

        public int getLine() {
            return line;
        }
        public void setLine(int line) {
            this.line = line;
        }

	/**
	 * Créé une nouvelle instruction pour Assembleur.
	 * @param n le nom de l'instruction
	 * @param tabM le tableau de mots binaires de l'instruction ordonné dans le
	 * même ordre que la commande assembleur.
	 */
	public Instruction(String n, Mot[] tabM)
	{
		listeParams = new ArrayList();
		nom=n;
		ajouter(tabM);
	}

	public Instruction(String n, Mot m1)
	{
		listeParams = new ArrayList();
		nom=n;
		Mot[] tabM={m1};
		ajouter(tabM);
	}

	public Instruction(String n, Mot m1, Mot m2)
	{
		listeParams = new ArrayList();
		nom=n;
		Mot[] tabM={m1,m2};
		ajouter(tabM);
	}

	public Instruction(String n, Mot m1, Mot m2, Mot m3)
	{
		listeParams = new ArrayList();
		nom=n;
		Mot[] tabM={m1,m2,m3};
		ajouter(tabM);
	}

	public Instruction(String n, Mot m1, Mot m2, Mot m3, Mot m4)
	{
		listeParams = new ArrayList();
		nom=n;
		Mot[] tabM={m1,m2,m3,m4};
		ajouter(tabM);
	}

	/**
	 * Permet de mettre les Mot dans le bon ordre.
	 * @param tabM le tableau de mots binaires de l'instruction ordonné dans le
	 * même ordre que la commande assembleur.
	 */
	public final void ajouter(Mot[] tabM)
	{
		int t=0;
		boolean tous_ordonnes = true;
		for(Mot m : tabM)
		{
			if(m.getOrdre()<0)
				tous_ordonnes=false;
			t+=m.getTaille();
		}
		if(!tous_ordonnes)
			for(int i=0;i<tabM.length;i++)
				tabM[i].setOrdre(i+1);
                for(int i=0; i<tabM.length;i++)
                    listeParams.add(tabM[i]);
		if(t!=16)
			System.out.println("Avertissement : mot binaire de "+nom+" de taille "+t+"bits.");
	}
	
	public final void ajouterMot(Mot m){
		listeParams.add(m);
	}

	public boolean compInstr(Instruction instr){
		if(this.nom.equalsIgnoreCase(instr.nom) && this.listeParams.size()==instr.listeParams.size())
		{
			for(int i=0;i<instr.listeParams.size();i++)
			{
				if(this.listeParams.get(i).getType()!=instr.listeParams.get(i).getType())
					return false;
			}
			return true;
		}
		return false;
	}
	
	public void charger(Instruction instr)
	{
		for(int i=0; i<listeParams.size();i++)
		{
			listeParams.get(i).charger(instr.listeParams.get(i));
		}
	}
	
	public String genererBin(ArrayList<Etiquette> listeEtiquette, int ligne){
		ArrayList<Mot> listeMot=new ArrayList();
		boolean ordonnee=false;
		int min=0,max=-1;
		Mot prems;
		
		while(!ordonnee)
		{
			max=-1;
			prems=null;
			for(Mot m : listeParams)
			{
				if(m.getOrdre()>min && (m.getOrdre()<max || max==-1))
				{
					max=m.getOrdre();
					prems=m;
				}
			}
			if(prems!=null)
			{
				listeMot.add(prems);
				min=prems.getOrdre();
			}
			else
				ordonnee=true;
		}

		String motBin="";

		for(Mot m : listeMot)
		{
			if(m instanceof MotEtiq)
				((MotEtiq)m).regler(listeEtiquette, ligne);
			motBin+=m.genererBin();
		}

		return motBin;
	}

	/**
	 *
	 * @return le nom de l'instruction (ADD ...)
	 */
	public String getNom()
	{
		return nom;
	}

        public String getNomComplet(){
            String res=new String("");
            int val;
            int j=0;
            res=res.concat(nom);
            res=res.concat(" ");
            for(int i=1; i<listeParams.size();i++){
                if(listeParams.get(i).getType()==2){
                    //Imm
                    MotImm motImm;
                    motImm=(MotImm)listeParams.get(i);
                    val=motImm.getVal();
                    res=res.concat("#");
                    res=res.concat(String.valueOf(val));

                }
                else
                    if(listeParams.get(i).getType()==3){
                        //Reg
                        MotReg motReg;
                        motReg=(MotReg)listeParams.get(i);
                        val=motReg.getVal();
                        res=res.concat("R");
                        res=res.concat(String.valueOf(val));
                    }
      
                else{
                    if(listeParams.get(i).getType()==4){
                        //Reg
                        MotEtiq motEtiq;
                        motEtiq=(MotEtiq)listeParams.get(i);
                        res=res.concat(motEtiq.getEtiquette());

                    }
                }
                res=res.concat("");
                if(j==0)
                    res=res.concat(",");
                if(j==1 && listeParams.size()==4)
                    res=res.concat(",");
                j++;
            }            
            return res;
	}
        
    //added by LM BEGIN
        public boolean checkValParam() {
            int val;
            for(int i=1; i<listeParams.size();i++){
                if(listeParams.get(i).getType()==2){
                    //Imm
                    MotImm motImm;
                    motImm=(MotImm)listeParams.get(i);
                    val=motImm.getVal();
                    if(motImm.limits.checkLimit(val)==false){
                        System.out.println(
                                "Vous avez saisi la valeur immédiate:"
                                +motImm.getVal()+" ce qui est incorrect");
                        System.out.println(
                                "La valeur doit être comprise entre "
                                +motImm.limits.min+" et "+motImm.limits.max);
                        return false;
                    }
                }
                else{
                    if(listeParams.get(i).getType()==3){
                        //Reg
                        MotReg motReg;
                        motReg=(MotReg)listeParams.get(i);
                        val=motReg.getVal();
                        if(motReg.limits.checkLimit(val)==false){
                            System.out.println(
                                "Vous avez saisi R"
                                +motReg.getVal()+" ce qui est incorrect");

                            System.out.println(
                                "Le numéro du registre doit être compris entre "
                                +motReg.limits.min+" et "+motReg.limits.max);
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        //added by LM END

	/**
	 * Permet de réprésenter les différents mot binaire d'une instruction.
	 */
	public static abstract class Mot{
		protected int taille;
		protected int ordreBinaire;

		/**
		 * Constructeur qui définit la taille du Mot et indique un mauvais ordre binaire.
		 * @param t taille du Mot
		 */
		public Mot(int t)
		{
			taille=t;
			ordreBinaire=-1;
		}

		/**
		 *
		 * @param t taille du mot
		 * @param o position du mot dans l'instruction.
		 */
		public Mot(int t, int o)
		{
			taille=t;
			ordreBinaire=o;
		}

		/**
		 *
		 * @param o position du mot dans l'instruction.
		 */
		public void setOrdre(int o)
		{
			ordreBinaire=o;
		}

		/**
		 *
		 * @return position du mot dans l'instruction.
		 */
		public int getOrdre()
		{
			return ordreBinaire;
		}

		/**
		 *
		 * @return la taille du Mot.
		 */
		public int getTaille()
		{
			return taille;
		}
		
		public abstract int getType();
                public abstract String getValBin();
                public abstract int getValeur();
		@Override
		public abstract Mot clone();

		public abstract void charger(Mot m);

		public abstract String genererBin();
	}

	/**
	 * Permet de représenter les mot qui sont des constantes.
	 */
	public static class MotConst extends Mot{
		protected String valBin="";

		/**
		 * @param t taille du mot.
		 * @param vb valeur binaire de la constante.
		 * @throws compilateur.SystemeParsage.Instruction.NotBinaryException
		 */
		public MotConst(int t, String vb) throws NotBinaryException
		{
			super(t);
			if(t<vb.length())
				valBin=vb.substring(vb.length()-t, vb.length());
			else
			{
				valBin=vb;
				while(t>valBin.length())
					valBin="0"+vb;
			}
			if(!vb.matches("[01]*"))
				throw new NotBinaryException();
		}
		
		public MotConst(int t, String vb, int o) throws NotBinaryException
		{
			super(t,o);
			if(t<vb.length())
				valBin=vb.substring(vb.length()-t, vb.length());
			else
			{
				valBin=vb;
				while(t>valBin.length())
					valBin="0"+vb;
			}
			if(!vb.matches("[01]*"))
				throw new NotBinaryException();
		}
                
        @Override
                public String getValBin() {
                    return valBin;
                }

		@Override
		public int getType() {
			return 1;
		}

		@Override
		public Mot clone() {
			try {
				return new MotConst(this.taille, this.valBin, this.ordreBinaire);
			} catch (NotBinaryException ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire=m.ordreBinaire;
			this.taille=m.taille;
			this.valBin=((MotConst)m).valBin;
		}

		@Override
		public String genererBin() {
			return valBin;
		}
                        @Override
        public int getValeur() {
            return -1;
        }
	}

	/**
	 * Permet de réprésenter les mot qui sont des valeurs immédiates.
	 */
	public static class MotImm extends Mot{
		boolean signe;
		int valeur;
                //added by LM BEGIN
                Limits limits;
                //added by LM 
		/***
		 *
		 * @param t taille du mot.
		 * @param s signe de la valeur.
		 */
		public MotImm(int t, boolean s)
		{
			super(t);
			signe=s;
		}
		public MotImm(int t, boolean s, int o)
		{
			super(t, o);
			signe=s;
		}
                //added by LM BEGIN
                public MotImm(int t, boolean s,Limits l)
		{
			super(t);
			signe=s;
                        limits=l;
		}
		public MotImm(int t, boolean s, int o,Limits l)
		{
			super(t, o);
			signe=s;
                        limits=l;
		}

		@Override
		public int getType() {
			return 2;
		}

		public void setVal(int v)
		{
			valeur=v;
		}

		public int getVal()
		{
			return valeur;
		}

		@Override
		public Mot clone() {
			return new MotImm(this.taille, this.signe, this.ordreBinaire);
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire=m.ordreBinaire;
			this.taille=m.taille;
			this.signe=((MotImm)m).signe;
                        this.limits=((MotImm)m).limits;
		}

		@Override
		public String genererBin() {
			String ret=Integer.toBinaryString(valeur);
			if(valeur>0)
				ret="0"+ret;
			/* extension de signe */
			while(ret.length()<taille)
			{
				ret=ret.substring(0, 1)+ret;
			}

			/* Troncature */
			if(ret.length()>taille)
				ret=ret.substring(ret.length()-taille);
			return ret;
		}
                        @Override
        public String getValBin() {
            String b="Err";
            return b;
        }

        @Override
        public int getValeur() {
            return valeur;
        }
	}

	/**
	 * Permet de représenter un mot registre.
	 */
	public static class MotReg extends Mot{
		int valeur;
                //added by LM BEGIN
                public Limits limits;
                //added by LM 
		public MotReg(int t)
		{
			super(t);
                        limits=new Limits(0,7);
		}
		public MotReg(int t, int o)
		{
			super(t, o);
                        limits=new Limits(0,7);
		}

		@Override
		public int getType() {
			return 3;
		}

		public void setVal(int v)
		{
			valeur=v;
		}

		public int getVal()
		{
			return valeur;
		}

		@Override
		public Mot clone() {
			return new MotReg(this.taille,this.ordreBinaire);
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire=m.ordreBinaire;
			this.taille=m.taille;
                        this.limits=((MotReg)m).limits;
		}

		@Override
		public String genererBin() {
			String ret=Integer.toBinaryString(valeur);
			if(valeur>0)
				ret="0"+ret;
			/* extension de signe */
			while(ret.length()<taille)
			{
				ret=ret.substring(0, 1)+ret;
			}

			/* Troncature */
			if(ret.length()>taille)
				ret=ret.substring(ret.length()-taille);
			return ret;
		}
                        @Override
        public String getValBin() {
            String b="Err";
            return b;
        }

        @Override
        public int getValeur() {
            return valeur;
        }
	}

	/**
	 * Permet de représenter un mot étiquette.
	 */
	public static class MotEtiq extends Mot{
		String valeur;
		int relAdr;

		public MotEtiq(int t)
		{
			super(t);
		}
		public MotEtiq(int t, int o)
		{
			super(t, o);
		}

		@Override
		public int getType() {
			return 4;
		}
                
		public String getEtiquette() {
			return valeur;
		}
		public void setVal(String v)
		{
			valeur=v;
		}

        public int getRelAdr() {
            return relAdr;
        }
		public String getVal()
		{
			return valeur;
		}

		@Override
		public Mot clone() {
			return new MotReg(this.taille,this.ordreBinaire);
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire=m.ordreBinaire;
			this.taille=m.taille;
		}

		public void regler(ArrayList<Etiquette> liste, int l)
		{
			Etiquette trouvee=null;
			for(Etiquette e : liste)
			{
				if(e.nom.equals(valeur))
					trouvee=e;
			}
			relAdr=trouvee.adresse-l-1;
		}

		@Override
		public String genererBin() {
			int pos=0;
			if(relAdr>=0)
				pos=1;
			String ret=Integer.toBinaryString(relAdr);
			/* extension de signe */
			while(ret.length()<taille)
			{
				if(pos==1)
					ret="0"+ret;
				else
					ret="1"+ret;
			}

			/* Troncature */
			if(ret.length()>taille)
				ret=ret.substring(ret.length()-taille);
			return ret;
		}
                               @Override
                public String getValBin() {
                    String b="Err";
                    return b;
                }

                @Override
                public int getValeur() {
                    return -1;
                }
	}
	
	public static class NotBinaryException extends Exception
	{
		public NotBinaryException()
		{
			super("Le préfixe n'est pas binaire.");
		}
	}
	
        //added by LM BEGIN
	public static class Limits{
            int min,max;

            public Limits() {
                min=0;
                max=7;
            }
           public Limits(int min, int max) {
                this.min = min;
                this.max = max;
            }
           
            public int getMin() {
                return min;
            }

            public int getMax() {
                return max;
            }

            public void setMax(int max) {
                this.max = max;
            }

            public void setMin(int min) {
                this.min = min;
            }
            public boolean checkLimit(int val) {
                if(val>=min && val<=max)
                    return true;
                else
                    return false;
            }

        }
	//added by LM END
	
}
