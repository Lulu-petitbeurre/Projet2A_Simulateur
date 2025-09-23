    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

import ensea.simulateurSTM32.parseur.Etiquette;
        
import ensea.simulateurSTM32.parseur.Instruction;
import ensea.simulateurSTM32.parseur.Instruction.Mot;
import ensea.simulateurSTM32.parseur.Instruction.MotConst;
import ensea.simulateurSTM32.parseur.Instruction.MotEtiq;
import ensea.simulateurSTM32.parseur.Instruction.MotImm;
import ensea.simulateurSTM32.parseur.Instruction.MotReg;
import ensea.simulateurSTM32.parseur.Instruction.NotBinaryException;
import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Laurent
 */
public class SimLCM3 {
    private RAM theRAM;
    private ROM theROM;
    private Registers theRegisters;
    private Flags theFlags;
    public Pipeline thePipe;
    public int numException;
    // liste des instructions téléchargées
    ArrayList<Instruction> downloadedInstructions;
    // liste des codes téléchargés sous forme de String
    // code binaire! "000010001000
    ArrayList<String> downloadedCode;
    ArrayList<Etiquette> theLabels;
    public Modifications modifs;
    int numInstrF,numInstrD,numInstrE,nPhase,numStep;
    int fetchedCode,branchNumInstr;
    public static final int FETCH = 1;
    public static final int DECODE = 2;
    public static final int EXECUTE = 3;

    public SimLCM3() {
        theRAM=new RAM();
        theROM=new ROM();
        theRegisters=new Registers();
        thePipe=new Pipeline();
        theFlags=new Flags();
        theLabels=new ArrayList();
        downloadedInstructions=new ArrayList();
        //NEW LM Begin
        modifs=new Modifications();
        //NEW LM
        reset();
    }
    public void download(ArrayList<Instruction> downloadedInstructions,ArrayList<String> downloadedCode,ArrayList<Etiquette> listeEtiquette){
        this.downloadedInstructions=downloadedInstructions;
        this.downloadedCode=downloadedCode;
        int n=0,address;
        theROM.reset();
        address=theROM.offsetAdr;
        for(Instruction i : this.downloadedInstructions){
            System.out.println("instruction:"+i.getNom()+"=>"+downloadedCode.get(n)+"="+getCodeInInt(downloadedCode.get(n)));
            theROM.writeHW(getCodeInInt(downloadedCode.get(n)),address);
            address+=2;
            n++;
        }
        if((address%4)==0)
            address=address;
        else
            address=address+2;
        theROM.writeHW(0x2000,address);
        address+=2;
        theROM.writeHW(0x0000,address);
        address+=2;
        //theROM.writeHW(0x0800,address);
        //address+=2;
        //theROM.writeHW(0x0000,address);
        //address+=2;
    }
    public void reset(){
        theROM.reset();
        theRAM.reset();
        theRegisters.reset();
        theFlags.reset();
        thePipe.reset();
        downloadedInstructions.clear();
        theLabels.clear();
        modifs.clear();
        numInstrF=0;
        numInstrD=-1;
        numInstrE=-2;
        nPhase=FETCH;
        numStep=0;
        branchNumInstr=-1;
        numException=0;
    }
        public void reset2(){
        theROM.reset();
        theRAM.reset();
        theRegisters.reset();
        theFlags.reset();
        thePipe.reset();
        //downloadedInstructions.clear();
        theLabels.clear();
        modifs.clear();
        numInstrF=0;
        numInstrD=-1;
        numInstrE=-2;
        nPhase=FETCH;
        numStep=0;
        branchNumInstr=-1;
        numException=0;
    }
    public void resetVar(){
        
        theRAM.reset();
        theRegisters.reset();
        theFlags.reset();
        
        
        modifs.clear();
        numInstrF=0;
        numInstrD=-1;
        numInstrE=-2;
        thePipe.reset();
        nPhase=FETCH;
        numStep=0;
        branchNumInstr=-1;
        numException=0;
    }
    private int align(int val,int base){
        return (val/base)*base;
    }
    private int getCodeInInt(String binaryCode){
        int val=0,i,w=0x8000;
        char bit;
        for(i=0;i<16;i++){
            bit=binaryCode.charAt(i);
            if(bit=='1')
                val+=w;
            w=w>>1;
        }
        return val;
    }
    public void oneStep(){
        if(numException==0){
            int fPC=0,dCode=-1;
            String eInstr= new String("NO EXEC");
            
            System.out.println("STEP "+numStep+"Phase"+nPhase);
            fetchedCode=fetch();
            fPC=theROM.offsetAdr+numInstrF*2;
            System.out.println("Fetch instr "+numInstrF+"=>"+inHexa(fetchedCode,4));

            if(nPhase>=DECODE){
                System.out.println("Decode instr "+numInstrD+"=>"
                        +downloadedInstructions.get(numInstrD).getNom()+
                        "...");
                dCode=theROM.readHW(theROM.offsetAdr+numInstrD*2);
                decode();
            }

            if(nPhase>=EXECUTE){
                execute();
                eInstr=downloadedInstructions.get(numInstrE).getNomComplet();
            }
            else
                branchNumInstr=-1;
            //jump=>nPhase=0 and branchNumInstr=-1
            if(branchNumInstr==-1){
                // not a B or a jump
                numInstrE=numInstrD;
                numInstrD=numInstrF;
                numInstrF++;
                theRegisters.incPC();
                //NEW LM deb
                modifs.add(Modification.PC,0,theRegisters.PC);
                //NEW LM deb
                if(nPhase<EXECUTE)
                    nPhase++;
                //1 instruction =2 bytes!
            }
            else{
                numInstrF=branchNumInstr;
                numInstrE=-1;
                numInstrD=-1;
                nPhase=FETCH;
            }
            Color cf,cd,ce;
            cf=thePipe.couleur[numStep%Pipeline.NBCOLOR];
            if(numStep>0)
                cd=thePipe.couleur[(numStep-1)%Pipeline.NBCOLOR];
            else
                cd=Color.LIGHT_GRAY;
            if(numStep>1)
                ce=thePipe.couleur[(numStep-2)%Pipeline.NBCOLOR];
            else
                ce=Color.LIGHT_GRAY;            
            thePipe.insereEtat(new EtatPipeline(numStep,fPC,dCode,eInstr,
                    cf,cd,ce));
            numStep++;
        }
       
            
    }
    private int fetch(){
        
        return theROM.readHW(theROM.offsetAdr+numInstrF*2);
        
    }
    private void decode(){
        //"add", new MotConst(7, "0001110", 1),
        //new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2)
        if(downloadedInstructions.get(numInstrD).getNom().compareTo("add")==0){
            System.out.println("DECODE instr"+numInstrD+":"+"add ...");
        }
        
        /*
			listeCommandes.add(new Instruction("add", new MotConst(7, "0001110", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2)));
			listeCommandes.add(new Instruction("add", new MotConst(5, "00110"), new MotReg(3), new MotImm(8, false)));
			listeCommandes.add(new Instruction("add", new MotConst(7, "0001100",1), new MotReg(3,4), new MotReg(3,3), new MotReg(3,2)));
         * 
			listeCommandes.add(new Instruction("b", new MotConst(5, "11100"),new MotEtiq(11)));
			listeCommandes.add(new Instruction("bne", new MotConst(8, "11011001"),new MotEtiq(8)));
			listeCommandes.add(new Instruction("beq", new MotConst(8, "11010101"),new MotEtiq(8)));
         * 
			listeCommandes.add(new Instruction("cmp", new MotConst(5, "00101"), new MotReg(3), new MotImm(8, false)));
         * 
			listeCommandes.add(new Instruction("ldr", new MotConst(10, "0110100000",1),new MotReg(3,2),new MotReg(3,3)));
			listeCommandes.add(new Instruction("mov", new MotConst(5, "00100"), new MotReg(3), new MotImm(8, false)));
			listeCommandes.add(new Instruction("mov", new MotConst(10, "0000000000",1), new MotReg(3,3), new MotReg(3,2)));
			listeCommandes.add(new Instruction("str", new MotConst(10, "0101000000",1),new MotReg(3,3),new MotReg(3,2)));

			listeCommandes.add(new Instruction("sub", new MotConst(7, "0001111", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2)));
			listeCommandes.add(new Instruction("sub", new MotConst(7, "0001101", 1), new MotReg(3,4), new MotReg(3,3), new MotReg(3,2)));
			listeCommandes.add(new Instruction("sub", new MotConst(5, "00111"), new MotReg(3), new MotImm(8, false)));
			listeCommandes.add(new Instruction("nop", new MotConst(16, "0000000000000000")));
*/
  
    }  
    private int execute(){
        int n=0,m=0,val=0,d=0,res,t=0,adr=0;
        int msbR,msbV;
        long a,b,c;
        MotReg motReg;
        MotImm motImm;
        MotEtiq motEtiq;
        Mot mot;
        Instruction instr;
        System.out.println("EXECUTE instr "+numInstrE);
        branchNumInstr=-1;//pas de branchement par défaut
        if(downloadedInstructions.get(numInstrE).getNom().compareTo("add")==0){
             System.out.println("add type");
        }
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0001110")==0){
            //#1 Instruction("add", new MotConst(7, "0001110", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2)
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type add Rd Rn #imm3");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            n=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(3);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+n+" +"+val);
            theRegisters.R[d]=theRegisters.R[n]+val;
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM deb
            //theRegisters.incPC();
        }
        else
            //#2 Instruction("add", new MotConst(5, "00110"), new MotReg(3), new MotImm(8, false)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("00110")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type add Rd #imm8");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(2);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            //NEW LM deb
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+d+" +"+val);
            theRegisters.R[d]=theRegisters.R[d]+val;
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM deb
            //theRegisters.incPC();
        }
        else
            //#3 Instruction("add", new MotConst(7, "0001100",1), new MotReg(3,4), new MotReg(3,3), new MotReg(3,2)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0001100")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type add Rd Rn Rm");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            n=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(3);
            m=motReg.getVal();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+n+" +R"+m);
            theRegisters.R[d]=theRegisters.R[n]+theRegisters.R[m];
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM deb
            //theRegisters.incPC();
        }
        else
            //#4 Instruction("b", new MotConst(5, "11100"),new MotEtiq(11))
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("11100")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type B label");
            instr=downloadedInstructions.get(numInstrE);
            motEtiq=(MotEtiq) instr.getListeParams().get(1);
            int delta=motEtiq.getRelAdr();
            System.out.println("  EXECUTE instr:"+" PC"+d+"<= PC+2x"+delta);
            branch(delta);
        }
        else
            //#6 Instruction("bne", new MotConst(8, "11010001"),new MotEtiq(8))
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("11010001")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type BNE label");
            if(theFlags.getZ()==0){
                instr=downloadedInstructions.get(numInstrE);
                motEtiq=(MotEtiq) instr.getListeParams().get(1);
                int delta=motEtiq.getRelAdr();
                System.out.println("  EXECUTE instr BNE:"+" PC"+d+"<= PC+2x"+delta);
                branch(delta);
            }
        }
        else
            //#5 Instruction("beq", new MotConst(8, "11010000"),new MotEtiq(8))
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("11010000")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type BEQ label");
            if(theFlags.getZ()==1){
                instr=downloadedInstructions.get(numInstrE);
                motEtiq=(MotEtiq) instr.getListeParams().get(1);
                int delta=motEtiq.getRelAdr();
                System.out.println("  EXECUTE instr: BEQ"+" PC"+d+"<= PC+2x"+delta);
                branch(delta);
            }
        }
        else
            //#7 Instruction("cmp", new MotConst(5, "00101"), new MotReg(3), new MotImm(8, false)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("00101")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type CMP Rn,#imm8");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            n=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(2);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            System.out.println("  EXECUTE instr: NZVC<= R"+n+" -"+val);
            res=theRegisters.R[n]-val;
            int flag=0;
            if(res==0){
                theFlags.setZ();
                flag+=4;
            }
            else
                theFlags.clearZ();
            
            if((res&0x80000000)==0){
                theFlags.clearN();
                flag+=8;
            }
            else
                theFlags.setN();
            
            if(res<0){
                theFlags.setC();
                flag+=1;
            }
            else
                theFlags.clearC();

            if(theRegisters.R[n]<0 && res>=0){
                theFlags.setV();
                flag+=2;
            }
            else
                theFlags.clearV();
            //NEW LM deb
            modifs.add(Modification.FLAG,0,flag);
            //NEW LM deb
        }
        else
            //#8 Instruction("ldrh", new MotConst(7, "01001",1),new MotReg(3,2),new MotImm(8,false,new Limits(0,255))));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("01001")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type #8 LDRH Rn,label");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            t=motReg.getVal();
            motEtiq=(MotEtiq) instr.getListeParams().get(2);
            int delta=motEtiq.getRelAdr();
            val=delta;
            long address=0;
            address=theRegisters.PC+2*val;
            System.out.println("  EXECUTE instr:"+" R"+t+"<= M[0x"+inHexa((int)address,32)+"]");
            if((address>=theROM.offsetAdr)&&(address<theROM.offsetAdr+theROM.sizeInWord*4)){
                theRegisters.R[t]=theROM.readHW((int)address);
                //NEW LM deb
                modifs.add(Modification.REG,t,theRegisters.R[t]);
                //NEW LM fin
            }
            else
            if((address>=theRAM.offsetAdr)&&(address<theRAM.offsetAdr+theRAM.sizeInWord*4)){
                theRegisters.R[t]=theRAM.readHW((int)address);
                //NEW LM deb
                modifs.add(Modification.REG,t,theRegisters.R[t]);
                //NEW LM fin

            }
            else{
                System.out.println("  EXCEPTION: ERREUR DE LECTURE EN Memoire");
                numException=2;
            }

        }
        else
            //#9Instruction("ldr", new MotConst(7, "0101100",1),new MotReg(3,4),new MotReg(3,3),new MotReg(3,2)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0101100")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type #9 LDR Rt,[Rm,Rn]");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            t=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            n=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(3);
            m=motReg.getVal();
            System.out.println("  EXECUTE instr:"+" R"+t+"<= [R"+n+"+R"+m+"]");
            //theRegisters.R[d]=theRegisters.R[n]+theRegisters.R[m];
            long address=0;
            address=((theRegisters.R[n]+theRegisters.R[m])/4)*4;
            if((address>=theROM.offsetAdr)&&(address<theROM.offsetAdr+theROM.sizeInWord*4)){
                theRegisters.R[t]=theROM.readW((int)address);
               //NEW LM deb
                modifs.add(Modification.REG,t,theRegisters.R[t]);
                //NEW LM fin
            }
            else
            if((address>=theRAM.offsetAdr)&&(address<theRAM.offsetAdr+theRAM.sizeInWord*4)){
                theRegisters.R[t]=theRAM.readW((int)address);
                               //NEW LM deb
                modifs.add(Modification.REG,t,theRegisters.R[t]);
                //NEW LM fin
            }
            else{
                System.out.println("  EXCEPTION: ERREUR DE LECTURE EN Memoire");
                numException=2;
            }

        }
        else
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("00000")==0){
            //#10 add(new Instruction("lsl", new MotConst(5, "00000", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(5, false,2,new Limits(0,31))
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type lsl Rd<=Rn<<imm5");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            m=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(3);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+m+"<<"+val);
            theRegisters.R[d]=(theRegisters.R[m])<<val;
             //NEW LM deb
             modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
            //theRegisters.incPC();
        }
        else
            //#11 Instruction("mov", new MotConst(5, "00100"), new MotReg(3), new MotImm(8, false,new Limits(0,255)))
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("00100")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type MOV Rd #imm8");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(2);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= "+val);
            theRegisters.R[d]=val;
             //NEW LM deb
             modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
            //theRegisters.incPC();
        }
        else
            //#12 Instruction("mov", new MotConst(10, "0000000000",1), new MotReg(3,3), new MotReg(3,2)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0000000000")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type MOV Rd Rm");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(2);
            m=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();

            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+m);
            theRegisters.R[d]=theRegisters.R[m];
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
            //theRegisters.incPC();
        }
        else
        //#13Instruction("str", new MotConst(7, "0101000",1),new MotReg(3,4),new MotReg(3,3),new MotReg(3,2)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0101000")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type #9 STR Rt,[Rm,Rn]");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            t=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            n=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(3);
            m=motReg.getVal();
            System.out.println("  EXECUTE instr:mem[R"+n+"+R"+m+"]<="+" R"+t);
            long address=0;
            address=((theRegisters.R[n]+theRegisters.R[m])/4)*4;
            if((address>=theRAM.offsetAdr)&&(address<theRAM.offsetAdr+theRAM.sizeInWord*4)){
                theRAM.writeW(theRegisters.R[t], (int)address);
                //NEW LM deb
                modifs.add(Modification.RAM,(int)address,theRegisters.R[t]);
                //NEW LM fin
            }
            else{
                System.out.println("  EXCEPTION: ERREUR D'ECRITURE EN Memoire");
                numException=3;
            }

        }
        else
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0001111")==0){
            //#14 Instruction("sub", new MotConst(7, "0001111", 1), new MotReg(3,4), new MotReg(3,3), new MotImm(3, false,2,new Limits(0,7)))

            System.out.println("  EXECUTE instr"+numInstrE+":"+"type sub Rd Rn #imm3");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            n=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(3);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+n+" -"+val);
            theRegisters.R[d]=theRegisters.R[n]-val;
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
            //theRegisters.incPC();
        }
        else
            //#15 Instruction("sub", new MotConst(5, "00111"), new MotReg(3), new MotImm(8, false,new Limits(0,255)
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("00111")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type sub Rd #imm8");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motImm=(MotImm) instr.getListeParams().get(2);
            val=motImm.getVal();
            //motReg=(MotReg) instr.getListeParams().get(0);
            //n=downloadedInstructions.get(numInstrE).getListeParams().get(1).getValeur();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+d+" -"+val);
            theRegisters.R[d]=theRegisters.R[d]-val;
            //theRegisters.incPC();
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin            
        }
        else
            //#16 Instruction("sub", new MotConst(7, "0001101", 1), new MotReg(3,4), new MotReg(3,3), new MotReg(3,2)));
            
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0001101")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type sub Rd Rn Rm");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            n=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(3);
            m=motReg.getVal();
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+n+" -R"+m);
            theRegisters.R[d]=theRegisters.R[n]-theRegisters.R[m];
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
            //theRegisters.incPC();
        }
        else
            //#17 Instruction("and", new MotConst(10, "0100000000", 1), new MotReg(3,3), new MotReg(3,2)));
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0100000000")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type Rd <= Rd and Rm");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            m=motReg.getVal();

            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+d+"and R"+m);
            theRegisters.R[d]=theRegisters.R[d] & theRegisters.R[m] ;
            //theRegisters.incPC();
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
        }
        else
        //#18Instruction("eor", new MotConst(10, "0100000001", 1), new MotReg(3,3), new MotReg(3,2)
        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0100000001")==0){
            System.out.println("  EXECUTE instr"+numInstrE+":"+"type Rd <= Rd xor Rm");
            instr=downloadedInstructions.get(numInstrE);
            motReg=(MotReg) instr.getListeParams().get(1);
            d=motReg.getVal();
            motReg=(MotReg) instr.getListeParams().get(2);
            m=motReg.getVal();

            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+d+"xor R"+m);
            theRegisters.R[d]=theRegisters.R[d] ^ theRegisters.R[m] ;
            //NEW LM deb
            modifs.add(Modification.REG,d,theRegisters.R[d]);
            //NEW LM fin
            //theRegisters.incPC();
        }      
  //

         else{
            System.out.println(" EXCEPTION UNKNOWN CODE INSTRUCTION at 0x"+inHexa(theROM.offsetAdr+numInstrE*2,32));
            numException=1;
            return -1;
        }
 
        return 0;
    }
    @Override
    public String toString() {
        return "SimLCM3\n" + "--RAM\n" + theRAM 
                + "--ROM\n" + theROM 
                + "--Registers\n" + theRegisters
                + "--Flags\n" + theFlags
                + "--Modif\n" + modifs;
    }
    public String inHexa(int val, int nbDigit){
        String res=new String();
        String buf=new String();
        Integer nbr;
        char c;
        int tab[]=new int[8];
        int n=7,i;
        while(val!=0){
            tab[n]=val%16;
            n--;
            val=val/16;
        }
          
        if(nbDigit<=0){
            for(i=n+1;i<8;i++){
                nbr=new Integer(tab[i]);
                if(tab[i]<10)
                    res=res.concat(nbr.toString());
                else{
                    c=(char) ('A'+(char)(tab[i]-10));
                    buf=String.valueOf(c);
                    res=res.concat(buf);
                }
            }
        }
        else{
            nbDigit=min(nbDigit,8);
            for(i=8-nbDigit;i<8;i++){
                nbr=new Integer(tab[i]);
                if(tab[i]<10)
                    res=res.concat(nbr.toString());
                else{
                    c=(char) ('A'+(char)(tab[i]-10));
                    buf=String.valueOf(c);
                    res=res.concat(buf);
                }
            }
        }

        return res;
        
    }    
    private int max(int a,int b){
        if(a>b)
            return a;
        else
            return b;
        
    }
    private int min(int a,int b){
        if(a<b)
            return a;
        else
            return b;
        
    }

    public boolean checkValParam() {
        int n=0;
        
        for(Instruction i : this.downloadedInstructions){
            System.out.println("instruction:"+i.getNom()+"=>"+downloadedCode.get(n)+"="+getCodeInInt(downloadedCode.get(n)));
            if(i.checkValParam()==false){
                System.out.println("instruction:"+i.getNom()+" ERREUR pour val instruction n°"+i.getLine());
                return false;
            }
            System.out.println("instruction:"+i.getNom()+"OK pour val");
            n++;
            
        }
        return true;
    }

    private void branch(int delta) {
            theRegisters.setPC(theRegisters.PC+2*delta);
            //NEW LM deb
            modifs.add(Modification.PC,0,theRegisters.PC);
            //NEW LM deb
            nPhase=0;
            branchNumInstr=numInstrF+delta;
    }

    public RAM getTheRAM() {
        return theRAM;
    }

    public ROM getTheROM() {
        return theROM;
    }

    public Registers getTheRegisters() {
        return theRegisters;
    }

    public ArrayList<String> getDownloadedCode() {
        return downloadedCode;
    }

    public ArrayList<Instruction> getDownloadedInstructions() {
        return downloadedInstructions;
    }

    public void initValRAM(int adr, int val) {
        theRAM.writeW(val, adr);
    }

    public int getFlagN() {
        return theFlags.getN();
    }

    public int getFlagZ() {
        return theFlags.getZ();
    }
    public int getFlagV() {
        return theFlags.getV();
    }
    public int getFlagC() {
        return theFlags.getC();
    }
    
}
