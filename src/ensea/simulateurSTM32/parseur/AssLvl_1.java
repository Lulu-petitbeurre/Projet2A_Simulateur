/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ensea.simulateurSTM32.parseur;

import ensea.simulateurSTM32.parseur.Instruction.*;

/**
 * Met en place les instructions du langage Assembleur level 1.
 * @author louiroll . Revisité par Vincent -Guillaume pour . Cette partie a ensuite été 
 * écarté afin de limité le nombre d'instruction
 */
public class AssLvl_1 extends Assembleur{

	/**
	 * On définit les instruction propre au langage assembleur level 1 dans la liste de commandes.
	 */
	@Override
	protected void initCommandes() {
		try {
                        //added by LM BEGIN
                    //ici ordre asm indiquer l'ordre dans le codage
                    // instruction  0: ADD  
			listeCommandes.add(new Instruction("add", new MotConst(7, "0001110", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2,new Limits(0,7))));
			listeCommandes.add(new Instruction("add", new MotConst(5, "00110"), new MotReg(3), new MotImm(8,false,new Limits(0,255))));
			listeCommandes.add(new Instruction("add", new MotConst(7, "0001100",1), new MotReg(3,4), new MotReg(3,3), new MotReg(3,2)));
			//listeCommandes.add(new Instruction("add", new MotConst(9, "010001000",1), new MotReg(3,3), new MotReg(4,2)));
                        //mis de côté
			//listeCommandes.add(new Instruction("adr", new MotConst(5, "10100"), new MotReg(3), new MotImm(8, false)));
                        listeCommandes.add(new Instruction("and", new MotConst(10, "0100000000", 1), new MotReg(3,3), new MotReg(3,2)));
			listeCommandes.add(new Instruction("b", new MotConst(5, "11100"),new MotEtiq(11)));
			listeCommandes.add(new Instruction("bne", new MotConst(8, "11010001"),new MotEtiq(8)));
			listeCommandes.add(new Instruction("beq", new MotConst(8, "11010000"),new MotEtiq(8)));
			//listeCommandes.add(new Instruction("bp", new MotConst(8, "11010011"),new MotEtiq(8)));
			//listeCommandes.add(new Instruction("bnz", new MotConst(8, "11011101"),new MotEtiq(8)));
			//listeCommandes.add(new Instruction("bnp", new MotConst(8, "11011011"),new MotEtiq(8)));
			//listeCommandes.add(new Instruction("bzp", new MotConst(8, "11010111"),new MotEtiq(8)));
			//listeCommandes.add(new Instruction("bnzp", new MotConst(8, "11011111"),new MotEtiq(8)));

			listeCommandes.add(new Instruction("cmp", new MotConst(5, "00101"), new MotReg(3), new MotImm(8, false,new Limits(0,255))));
                        //listeCommandes.add(new Instruction("ldr", new MotConst(10, "0110100000",1),new MotReg(3,2),new MotReg(3,3)));
                        listeCommandes.add(new Instruction("eor", new MotConst(10, "0100000001", 1), new MotReg(3,3), new MotReg(3,2)));
                        //LDRH Rt,Rn,Rm
			//listeCommandes.add(new Instruction("ldr", new MotConst(7, "0101100",1),new MotReg(3,4),new MotReg(3,3),new MotReg(3,2)));
                        //Modif DG
                        listeCommandes.add(new Instruction("ldr", new MotConst(10, "0110100000",1),new MotReg(3,3),new MotReg(3,2)));
                        // LM 03/01/2017 : annulation ligne
                        //listeCommandes.add(new Instruction("ldrh", new MotConst(5, "01001"),new MotReg(3),new MotEtiq(8)));
                        
                        listeCommandes.add(new Instruction("lsl", new MotConst(5, "00000", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(5, false,2,new Limits(0,31))));
                        
			listeCommandes.add(new Instruction("mov", new MotConst(5, "00100"), new MotReg(3), new MotImm(8, false,new Limits(0,255))));
			listeCommandes.add(new Instruction("mov", new MotConst(10, "0000000000",1), new MotReg(3,3), new MotReg(3,2)));
                        //Modif DG
			listeCommandes.add(new Instruction("str", new MotConst(10, "0110000000",1),new MotReg(3,3),new MotReg(3,2)));

			listeCommandes.add(new Instruction("sub", new MotConst(7, "0001111", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2,new Limits(0,7))));
			listeCommandes.add(new Instruction("sub", new MotConst(7, "0001101", 1), new MotReg(3,4), new MotReg(3,3), new MotReg(3,2)));
			listeCommandes.add(new Instruction("sub", new MotConst(5, "00111"), new MotReg(3), new MotImm(8, false,new Limits(0,255))));
                        
                        
                        listeCommandes.add(new Instruction("nop", new MotConst(16, "0000000000000000")));

		} catch (NotBinaryException ex) {
			ex.printStackTrace();
		}
	}
}
