/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.parseur;

import java.util.ArrayList;
import java.util.Collections;

public class Instruction {

	protected String nom;
	protected ArrayList<Mot> listeParams;
	protected int line;

	public Instruction(String n) {
		listeParams = new ArrayList<>();
		line = 0;
		nom = n;
	}

	public ArrayList<Mot> getListeParams() {
		return listeParams;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public Instruction(String n, Mot[] tabM) {
		listeParams = new ArrayList<>();
		nom = n;
		ajouter(tabM);
	}

	public Instruction(String n, Mot m1) {
		this(n, new Mot[]{m1});
	}

	public Instruction(String n, Mot m1, Mot m2) {
		this(n, new Mot[]{m1, m2});
	}

	public Instruction(String n, Mot m1, Mot m2, Mot m3) {
		this(n, new Mot[]{m1, m2, m3});
	}

	public Instruction(String n, Mot m1, Mot m2, Mot m3, Mot m4) {
		this(n, new Mot[]{m1, m2, m3, m4});
	}

	public final void ajouter(Mot[] tabM) {
		int totalBits = 0;
		boolean tousOrdonnes = true;

		for (Mot m : tabM) {
			if (m.getOrdre() < 0)
				tousOrdonnes = false;
			totalBits += m.getTaille();
		}

		if (!tousOrdonnes)
			for (int i = 0; i < tabM.length; i++)
				tabM[i].setOrdre(i + 1);

		Collections.addAll(listeParams, tabM);

		if (totalBits != 16)
			System.out.println("Avertissement : mot binaire de " + nom + " de taille " + totalBits + " bits.");
	}

	public final void ajouterMot(Mot m) {
		listeParams.add(m);
	}

	public boolean compInstr(Instruction instr) {
		if (this.nom.equalsIgnoreCase(instr.nom) && this.listeParams.size() == instr.listeParams.size()) {
			for (int i = 0; i < instr.listeParams.size(); i++) {
				if (this.listeParams.get(i).getType() != instr.listeParams.get(i).getType())
					return false;
			}
			return true;
		}
		return false;
	}

	public void charger(Instruction instr) {
		for (int i = 0; i < listeParams.size(); i++) {
			Mot source = instr.listeParams.get(i);
			Mot target = listeParams.get(i);
			if (target instanceof MotConst && source instanceof MotConst && source.getTaille() == 0) {
				continue;
			}
			target.charger(source);
		}
	}

	public String genererBin(ArrayList<Etiquette> listeEtiquette, int ligne) {
		ArrayList<Mot> listeMot = new ArrayList<>();
		boolean ordonnee = false;
		int min = 0;
		int max;
		Mot prems;

		while (!ordonnee) {
			max = -1;
			prems = null;
			for (Mot m : listeParams) {
				if (m.getOrdre() > min && (m.getOrdre() < max || max == -1)) {
					max = m.getOrdre();
					prems = m;
				}
			}
			if (prems != null) {
				listeMot.add(prems);
				min = prems.getOrdre();
			} else {
				ordonnee = true;
			}
		}

		StringBuilder motBin = new StringBuilder();
		for (Mot m : listeMot) {
			if (m instanceof MotEtiq)
				((MotEtiq) m).regler(listeEtiquette, ligne);
			motBin.append(m.genererBin());
		}

		return motBin.toString();
	}

	public String getNom() {
		return nom;
	}

	public String getNomComplet() {
		StringBuilder res = new StringBuilder(nom + " ");
		int j = 0;

		for (int i = 1; i < listeParams.size(); i++) {
			Mot m = listeParams.get(i);

			if (m.getType() == 2) {
				MotImm motImm = (MotImm) m;
				res.append("#").append(motImm.getVal());
			} else if (m.getType() == 3) {
				MotReg motReg = (MotReg) m;
				if ((nom.equalsIgnoreCase("LDR") || nom.equalsIgnoreCase("STR")) && i > 1)
					res.append("[");
				res.append("R").append(motReg.getVal());
				if ((nom.equalsIgnoreCase("LDR") || nom.equalsIgnoreCase("STR")) && i > 1)
					res.append("]");
			} else if (m.getType() == 4) {
				MotEtiq motEtiq = (MotEtiq) m;
				res.append(motEtiq.getEtiquette());
			}

			if (j == 0 || (j == 1 && listeParams.size() == 4))
				res.append(",");
			j++;
		}
		return res.toString();
	}

	public boolean checkValParam() {
		for (int i = 1; i < listeParams.size(); i++) {
			Mot m = listeParams.get(i);
			if (m.getType() == 2) {
				MotImm motImm = (MotImm) m;
				if (motImm.limits.checkLimit(motImm.getVal())) {
					System.out.println("Valeur immédiate incorrecte : " + motImm.getVal());
					System.out.println("La valeur doit être comprise entre " + motImm.limits.min + " et " + motImm.limits.max);
					return false;
				}
			} else if (m.getType() == 3) {
				MotReg motReg = (MotReg) m;
				if (motReg.limits.checkLimit(motReg.getVal())) {
					System.out.println("Numéro de registre incorrect : R" + motReg.getVal());
					System.out.println("Le registre doit être compris entre " + motReg.limits.min + " et " + motReg.limits.max);
					return false;
				}
			}
		}
		return true;
	}

	// Classes internes

	public static abstract class Mot {
		protected int taille;
		protected int ordreBinaire;

		public Mot(int t, int o) {
			taille = t;
			ordreBinaire = o;
		}

		public void setOrdre(int o) {
			ordreBinaire = o;
		}

		public int getOrdre() {
			return ordreBinaire;
		}

		public int getTaille() {
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

	public static class MotConst extends Mot {
		protected String valBin;

		public MotConst(int t, String vb) throws NotBinaryException {
			this(t, vb, -1);
		}

		public MotConst(int t, String vb, int o) throws NotBinaryException {
			super(t, o);
			if (!vb.matches("[01]*"))
				throw new NotBinaryException();
			if (t < vb.length())
				valBin = vb.substring(vb.length() - t);
			else {
				valBin = vb;
				while (t > valBin.length())
					valBin = "0" + valBin;
			}
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
			} catch (NotBinaryException ignored) {
				return null;
			}
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire = m.ordreBinaire;
			this.taille = m.taille;
			this.valBin = ((MotConst) m).valBin;
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

	public static class MotImm extends Mot {
		boolean signe;
		int valeur;
		Limits limits;

		public MotImm(int t, boolean s) {
			this(t, s, -1, new Limits());
		}

		public MotImm(int t, boolean s, int o) {
			this(t, s, o, new Limits());
		}

		public MotImm(int t, boolean s, Limits l) {
			this(t, s, -1, l);
		}

		public MotImm(int t, boolean s, int o, Limits l) {
			super(t, o);
			signe = s;
			limits = l;
		}

		@Override
		public int getType() {
			return 2;
		}

		public void setVal(int v) {
			valeur = v;
		}

		public int getVal() {
			return valeur;
		}

		@Override
		public Mot clone() {
			return new MotImm(this.taille, this.signe, this.ordreBinaire, this.limits);
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire = m.ordreBinaire;
			this.taille = m.taille;
			this.signe = ((MotImm) m).signe;
			this.limits = ((MotImm) m).limits;
		}

		@Override
		public String genererBin() {
			String ret = Integer.toBinaryString(valeur);
			if (valeur > 0) ret = "0" + ret;
			while (ret.length() < taille)
				ret = ret.charAt(0) + ret;
			if (ret.length() > taille)
				ret = ret.substring(ret.length() - taille);
			return ret;
		}

		@Override
		public String getValBin() {
			return "Err";
		}

		@Override
		public int getValeur() {
			return valeur;
		}
	}

	public static class MotReg extends Mot {
		int valeur;
		public Limits limits;

		public MotReg(int t) {
			this(t, -1);
		}

		public MotReg(int t, int o) {
			super(t, o);
			limits = new Limits(0, 7);
		}

		@Override
		public int getType() {
			return 3;
		}

		public void setVal(int v) {
			valeur = v;
		}

		public int getVal() {
			return valeur;
		}

		@Override
		public Mot clone() {
			return new MotReg(this.taille, this.ordreBinaire);
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire = m.ordreBinaire;
			this.taille = m.taille;
			this.limits = ((MotReg) m).limits;
		}

		@Override
		public String genererBin() {
			String ret = Integer.toBinaryString(valeur);
			if (valeur > 0) ret = "0" + ret;
			while (ret.length() < taille)
				ret = ret.charAt(0) + ret;
			if (ret.length() > taille)
				ret = ret.substring(ret.length() - taille);
			return ret;
		}

		@Override
		public String getValBin() {
			return "Err";
		}

		@Override
		public int getValeur() {
			return valeur;
		}
	}

	public static class MotEtiq extends Mot {
		String valeur;
		int relAdr;

		public MotEtiq(int t) {
			this(t, -1);
		}

		public MotEtiq(int t, int o) {
			super(t, o);
		}

		@Override
		public int getType() {
			return 4;
		}

		public String getEtiquette() {
			return valeur;
		}

		public void setVal(String v) {
			valeur = v;
		}

		public int getRelAdr() {
			return relAdr;
		}

		@Override
		public Mot clone() {
			return new MotEtiq(this.taille, this.ordreBinaire);
		}

		@Override
		public void charger(Mot m) {
			this.ordreBinaire = m.ordreBinaire;
			this.taille = m.taille;
		}

		public void regler(ArrayList<Etiquette> liste, int l) {
			for (Etiquette e : liste) {
				if (e.nom.equals(valeur)) {
					relAdr = e.adresse - l - 1;
					break;
				}
			}
		}

		@Override
		public String genererBin() {
			int pos = (relAdr >= 0) ? 1 : 0;
			String ret = Integer.toBinaryString(relAdr);
			while (ret.length() < taille)
				ret = (pos == 1 ? "0" : "1") + ret;
			if (ret.length() > taille)
				ret = ret.substring(ret.length() - taille);
			return ret;
		}

		@Override
		public String getValBin() {
			return "Err";
		}

		@Override
		public int getValeur() {
			return -1;
		}
	}

	public static class NotBinaryException extends Exception {
		public NotBinaryException() {
			super("Le préfixe n'est pas binaire.");
		}
	}

	public static class Limits {
		int min, max;

		public Limits() {
			this(0, 7);
		}

		public Limits(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public boolean checkLimit(int val) {
			return val < min || val > max;
		}
	}
}