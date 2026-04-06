/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

import ensea.simulateurSTM32.parseur.Etiquette;

import ensea.simulateurSTM32.parseur.Instruction;
import java.util.Arrays;
import ensea.simulateurSTM32.parseur.Instruction.MotEtiq;
import ensea.simulateurSTM32.parseur.Instruction.MotImm;
import ensea.simulateurSTM32.parseur.Instruction.MotReg;
import ensea.simulateurSTM32.ihm.Panel.PanelBouttons;
import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Integer.MIN_VALUE;

/**
 *
 * @author Laurent
 */
public class SimLCM3 {
    // Constantes pour les adresses mémoire
    private static final int ROM_BASE = 0x08000000;  // Adresse de base de la ROM

    private final RAM theRAM;
    private final ROM theROM;
    private static PanelBouttons theButtons;
    private final Registers theRegisters;
    private final Flags theFlags;
    public Pipeline thePipe;
    // savedRAM stock les valuers de RAM entrées par l'utilisateur (address -> value)
    private final int[] savedRAM;
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

    // Constantes de formatage pour rendre les affichages dans la console plus clairs
    private static final String STEP_FORMAT = "STEP %d Phase%d";
    private static final String FETCH_FORMAT = "Fetch instruction %d => 0x%s";

    public SimLCM3() {
        theRAM=new RAM();
        theROM=new ROM();
        theRegisters=new Registers();
        thePipe=new Pipeline();
        theFlags=new Flags();
        theLabels=new ArrayList<>();
        downloadedInstructions=new ArrayList<>();
        modifs=new Modifications();
        savedRAM = new int[theRAM.sizeInWord];
        Arrays.fill(savedRAM, MIN_VALUE);

        reset();
    }
    public void download(ArrayList<Instruction> downloadedInstructions,ArrayList<String> downloadedCode){
        // copy the lists to avoid external modifications clearing the simulator's lists
        if (downloadedInstructions != null) {
            this.downloadedInstructions = new ArrayList<>(downloadedInstructions);
        } else {
            this.downloadedInstructions = new ArrayList<>();
        }
        if (downloadedCode != null) {
            this.downloadedCode = new ArrayList<>(downloadedCode);
        } else {
            this.downloadedCode = new ArrayList<>();
        }
        int n=0,address;
        theROM.reset();
        address=theROM.offsetAdr;
        for(Instruction i : this.downloadedInstructions){
            System.out.println("instruction:"+i.getNom()+"=>"+downloadedCode.get(n)+"="+getCodeInInt(downloadedCode.get(n)));
            theROM.writeHW(getCodeInInt(downloadedCode.get(n)),address);
            address+=2;
            n++;
        }
        if((address%4)!=0) {
            address=address+2;
        }
        theROM.writeHW(0x2000,address);
        address+=2;
        theROM.writeHW(0x0000,address);
    }
    public void reset(){
        // Ne pas réinitialiser la ROM ni la RAM ici : on veut garder le programme et la RAM modifiée par l'utilisateur.
        theRegisters.reset();
        theFlags.reset();
        thePipe.reset();
        theLabels.clear();
        modifs.clear();
        numInstrF=0;
        numInstrD=-1;
        numInstrE=-2;
        nPhase=FETCH;
        numStep=0;
        branchNumInstr=-1;
        numException=0;

        // Restaurer les valeurs sauvegardées de la RAM
        if (savedRAM != null) {
            for (int i = 0; i < savedRAM.length; i++) {
                if (savedRAM[i] != Integer.MIN_VALUE) { // Si une valeur a été sauvegardée
                    int address = theRAM.offsetAdr + (i * 4); // Calculer l'adresse réelle
                    theRAM.writeW(savedRAM[i], address);
                    modifs.add(Modification.RAM, address, savedRAM[i]);
                }
            }
        }
    }
    public void resetVar(){
        // Ne pas réinitialiser la RAM ici pour préserver les valeurs modifiées par l'utilisateur
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

    //ERREUR FATALE
    private void triggerHardFault(Instruction instr, String detail) {
        this.numException = 2; // Bloque le pipeline du simulateur

        // Construction du message pour les élèves
        String nomInstr = instr.getNom().toUpperCase();
        int ligne = instr.getLine();
        String message = "\nSimulation interrompue !\n" +
                "Exécution de l'instruction '" + nomInstr + "' (Ligne " + ligne + ") impossible : HardFault.\n" +
                "-> Raison : " + detail + "\n";

        System.out.println(message); // Affichage console

        // Affichage dans la zone de dialogue (PanelDialog) de l'IHM
        try {
            ensea.simulateurSTM32.ihm.Panel.PanelDialog dialog = ensea.simulateurSTM32.simulateurstm32.MainFrame.ihm.panelDialog;
            if (dialog != null) {
                dialog.setText(dialog.getText() + message);
            }
        } catch (Exception e) {
            System.out.println("Impossible d'afficher l'erreur dans l'IHM.");
        }
    }

    public void oneStep(){
        if(numException==0){
            String eInstr= "NO EXEC";

            System.out.printf(STEP_FORMAT + "%n", numStep, nPhase);
            fetchedCode=fetch();
            int fPC;
            fPC = theROM.offsetAdr + numInstrF * 2;

            AtomicInteger dCode = new AtomicInteger();
            if(nPhase>=DECODE){
                if (numInstrD >= 0 && numInstrD < downloadedInstructions.size()) {
                    dCode.set(theROM.readHW(theROM.offsetAdr + numInstrD * 2));
                    decode();
                } else {
                    dCode.set(-1);
                }
            }

            if(nPhase>=EXECUTE){
                if (numInstrE >= 0 && numInstrE < downloadedInstructions.size()) {
                    execute();
                    eInstr=downloadedInstructions.get(numInstrE).getNomComplet();
                } else {
                    branchNumInstr=-1;
                }
            }
            else
                branchNumInstr=-1;

            // Gestion de l'avancement ou du saut
            if(branchNumInstr==-1){
                numInstrE=numInstrD;
                numInstrD=numInstrF;
                numInstrF++;
                theRegisters.incPC();
                modifs.add(Modification.PC,0,theRegisters.PC);
                if(nPhase<EXECUTE)
                    nPhase++;
            }
            else{
                // --- UN SAUT EST PRIS : VIDAGE DU PIPELINE ---
                numInstrF=branchNumInstr;
                numInstrE=-1;
                numInstrD=-1;
                nPhase=FETCH;

                eInstr += " (FLUSH)"; // Marqueur pour l'IHM graphique

                // Message pédagogique pour les élèves
                String msg = "\nVidage (Flush) à l'étape t" + numStep + " !\n" +
                        "-> Le branchement a été éxécuté\n" +
                        "-> Conséquence : Les instructions en cours de Fetch et Decode sont annulées.\n";
                try {
                    ensea.simulateurSTM32.ihm.Panel.PanelDialog dialog = ensea.simulateurSTM32.simulateurstm32.MainFrame.ihm.panelDialog;
                    // On évite de spammer si la boucle tourne en rond
                    if (dialog != null && !dialog.getText().contains("Vidage (Flush) à l'étape t" + numStep)) {
                        dialog.setText(dialog.getText() + msg);
                    }
                } catch (Exception e) {}
            }

            Color cf,cd,ce;
            cf=thePipe.couleur[numStep%Pipeline.NBCOLOR];
            if(numStep>0) cd=thePipe.couleur[(numStep-1)%Pipeline.NBCOLOR];
            else cd=Color.LIGHT_GRAY;
            if(numStep>1) ce=thePipe.couleur[(numStep-2)%Pipeline.NBCOLOR];
            else ce=Color.LIGHT_GRAY;

            thePipe.insereEtat(new EtatPipeline(numStep,fPC, dCode.get(),eInstr, cf,cd,ce));
            numStep++;
        }
    }
    private int fetch(){

        return theROM.readHW(theROM.offsetAdr+numInstrF*2);

    }
    private void decode(){
        if(downloadedInstructions.get(numInstrD).getNom().compareTo("add")==0){
            System.out.println("DECODE instr"+numInstrD+":"+"add ...");
        }
        // Placeholder decode operation, actual decoding done in execute


    }
    private void execute(){
        int n,m,val,d=0,res,t;
        MotReg motReg;
        MotImm motImm;
        MotEtiq motEtiq;
        Instruction instr;
        System.out.println("EXECUTE instr "+numInstrE);
        branchNumInstr=-1;//pas de branchement par défaut
        // defensive: ensure index valid
        if (numInstrE < 0 || numInstrE >= downloadedInstructions.size()) {
            System.out.println("EXECUTE: aucun instruction valide à exécuter (numInstrE="+numInstrE+")");
            return;
        }
        instr = downloadedInstructions.get(numInstrE);
        System.out.println("EXECUTE START: instr nom='"+instr.getNom()+"' params="+instr.getListeParams().size());
        for(int i=0;i<instr.getListeParams().size();i++){
            Instruction.Mot p=instr.getListeParams().get(i);
            System.out.println("  param["+i+"] type="+p.getClass().getSimpleName()+" ordre="+p.getOrdre()+" valBin='"+p.getValBin()+"'");
        }
        String _opcode = instr.getListeParams().isEmpty() ? "VIDE" : instr.getListeParams().get(0).getValBin();
        System.out.println("DEBUG EXECUTE: opcode param[0]='" + _opcode + "'");
        System.out.println("DEBUG EXECUTE: comparaison STR -> '" + _opcode + "'.equals('0110000000') = " + _opcode.equals("0110000000"));
        System.out.println("DEBUG EXECUTE: comparaison LDR -> '" + _opcode + "'.equals('0110100000') = " + _opcode.equals("0110100000"));
        System.out.println("DEBUG EXECUTE: numException avant exécution = " + numException);
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
            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+n+" +"+val);
            theRegisters.R[d]=theRegisters.R[n]+val;
            modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+d+" +"+val);
                theRegisters.R[d]=theRegisters.R[d]+val;
                modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                    modifs.add(Modification.REG,d,theRegisters.R[d]);
                }
                else
                    //#4 Instruction("b", new MotConst(5, "11100"),new MotEtiq(11))
                    if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("11100")==0){
                        System.out.println("  EXECUTE instr"+numInstrE+":"+"type B label");
                        instr=downloadedInstructions.get(numInstrE);
                        motEtiq=(MotEtiq) instr.getListeParams().get(1);
                        int delta=motEtiq.getRelAdr();
                        System.out.println("  EXECUTE instr:"+" PC"+d+"<= PC+2x"+delta);
                        theButtons.bcle(delta);
                        if(delta == -2){theButtons.stop();}
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
                                theButtons.bcle(delta);
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
                                    theButtons.bcle(delta);
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
                                    modifs.add(Modification.FLAG,0,flag);
                                }
                                else
                                    //#8 Instruction("ldrh", new MotConst(7, "01001",1),new MotReg(3,2),new MotImm(8,false,new Limits(0,255))));
                                    if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("01001")==0){
                                        System.out.println("  EXECUTE instr"+numInstrE+":"+"type #8 LDRH Rn,label");
                                        instr=downloadedInstructions.get(numInstrE);
                                        motReg=(MotReg) instr.getListeParams().get(1);
                                        t=motReg.getVal();
                                        motEtiq=(MotEtiq) instr.getListeParams().get(2);
                                        val= motEtiq.getRelAdr();
                                        long address;
                                        address=theRegisters.PC+ 2L *val;
                                        System.out.println("  EXECUTE instr:"+" R"+t+"<= M[0x"+inHexa((int)address,32)+"]");
                                        if((address>=theROM.offsetAdr)&&(address<theROM.offsetAdr+theROM.sizeInWord* 4L)){
                                            theRegisters.R[t]=theROM.readHW((int)address);
                                            modifs.add(Modification.REG,t,theRegisters.R[t]);
                                        }
                                        else
                                        if((address>=theRAM.offsetAdr)&&(address<theRAM.offsetAdr+theRAM.sizeInWord* 4L)){
                                            theRegisters.R[t]=theRAM.readHW((int)address);
                                            modifs.add(Modification.REG,t,theRegisters.R[t]);

                                        }
                                        else{
                                            System.out.println("  EXCEPTION: ERREUR DE LECTURE EN Memoire");
                                            numException=2;
                                        }

                                    }
                                    else
                                        //#9 Instruction("ldr", ...) avec appel au HardFault pédagogique
                                        if(downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0110100000")==0){
                                            System.out.println("  EXECUTE instr" + numInstrE + ":" + "type #9 LDR Rt,[Rn]");
                                            instr = downloadedInstructions.get(numInstrE);
                                            motReg = (MotReg) instr.getListeParams().get(1);
                                            t = motReg.getVal();
                                            motReg = (MotReg) instr.getListeParams().get(2);
                                            n = motReg.getVal();

                                            // Récupérer l'adresse brute
                                            long address = theRegisters.R[n];

                                            // 1. SIMULATION HARDFAULT : Défaut d'alignement
                                            if (address % 4 != 0) {
                                                triggerHardFault(instr, "Défaut d'alignement en lecture (0x" + Long.toHexString(address) + " n'est pas un multiple de 4)");
                                            }
                                            // 2. SIMULATION HARDFAULT : Zones mémoire autorisées (ROM et RAM)
                                            else if ((address >= theROM.offsetAdr) && (address < theROM.offsetAdr + theROM.sizeInWord * 4L)) {
                                                theRegisters.R[t] = theROM.readW((int)address);
                                                modifs.add(Modification.REG, t, theRegisters.R[t]);
                                            }
                                            else if ((address >= theRAM.offsetAdr) && (address < theRAM.offsetAdr + theRAM.sizeInWord * 4L)) {
                                                theRegisters.R[t] = theRAM.readW((int)address);
                                                modifs.add(Modification.REG, t, theRegisters.R[t]);
                                            }
                                            else {
                                                triggerHardFault(instr, "Violation d'accès mémoire en lecture (0x" + Long.toHexString(address) + " est en dehors de la RAM et de la ROM)");
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
                                            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+m+"<<"+val);
                                            theRegisters.R[d]=(theRegisters.R[m])<<val;
                                            modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                                                System.out.println("  EXECUTE instr:"+" R"+d+"<= "+val);
                                                theRegisters.R[d]=val;
                                                modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                                                    modifs.add(Modification.REG,d,theRegisters.R[d]);
                                                }
                                                else
                                                    //#13 Instruction("str", ...) avec appel au HardFault pédagogique
                                                    if(downloadedInstructions.get(numInstrE).getListeParams().size() > 0 &&
                                                            downloadedInstructions.get(numInstrE).getListeParams().get(0).getValBin().compareTo("0110000000")==0){

                                                        System.out.println("  EXECUTE instr" + numInstrE + ":" + "type #13 STR Rt,[Rn]");
                                                        instr = downloadedInstructions.get(numInstrE);

                                                        try {
                                                            motReg = (MotReg) instr.getListeParams().get(1);
                                                            t = motReg.getVal();
                                                            motReg = (MotReg) instr.getListeParams().get(2);
                                                            n = motReg.getVal();

                                                            // Récupérer l'adresse brute
                                                            long address = theRegisters.R[n];

                                                            // 1. SIMULATION HARDFAULT : Défaut d'alignement
                                                            if (address % 4 != 0) {
                                                                triggerHardFault(instr, "Défaut d'alignement en écriture (0x" + Long.toHexString(address) + " n'est pas un multiple de 4)");
                                                                return;
                                                            }

                                                            // 2. SIMULATION HARDFAULT : Violation de zone mémoire (STR uniquement en RAM)
                                                            if ((address >= theRAM.offsetAdr) && (address < theRAM.offsetAdr + theRAM.sizeInWord * 4L)) {
                                                                theRAM.writeW(theRegisters.R[t], (int)address);
                                                                modifs.add(Modification.RAM, (int)address, theRegisters.R[t]);
                                                            } else {
                                                                triggerHardFault(instr, "Violation d'accès mémoire en écriture (0x" + Long.toHexString(address) + " n'est pas une adresse RAM valide)");
                                                            }
                                                        } catch (Exception e) {
                                                            System.out.println("  ERREUR STR: " + e.getMessage());
                                                        }
                                                        return;
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
                                                        System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+n+" -"+val);
                                                        theRegisters.R[d]=theRegisters.R[n]-val;
                                                        modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                                                            System.out.println("  EXECUTE instr:"+" R"+d+"<= R"+d+" -"+val);
                                                            theRegisters.R[d]=theRegisters.R[d]-val;
                                                            modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                                                                modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                                                                    modifs.add(Modification.REG,d,theRegisters.R[d]);
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
                                                                        modifs.add(Modification.REG,d,theRegisters.R[d]);
                                                                    }
                                                                    //

                                                                    else{
                                                                        System.out.println(" EXCEPTION UNKNOWN CODE INSTRUCTION at 0x"+inHexa(theROM.offsetAdr+numInstrE*2,32));
                                                                        System.out.println("DEBUG EXECUTE: opcode non reconnu='" + _opcode + "' -> AUCUN handler ne correspond !");
                                                                        numException=1;
                                                                    }
        System.out.println("DEBUG EXECUTE: numException après exécution = " + numException);
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
        String res = "";
        String buf;
        int nbr;
        char c;
        int[] tab =new int[8];
        int n=7,i;
        while(val!=0){
            tab[n]=val%16;
            n--;
            val=val/16;
        }

        if(nbDigit<=0){
            for(i=n+1;i<8;i++){
                nbr = tab[i];
                if(tab[i]<10)
                    res=res.concat(Integer.toString(nbr));
                else{
                    c=(char) ('A'+(char)(tab[i]-10));
                    buf=String.valueOf(c);
                    res=res.concat(buf);
                }
            }
        }
        else{
            nbDigit=min(nbDigit);
            for(i=8-nbDigit;i<8;i++){
                nbr=tab[i];
                if(tab[i]<10)
                    res=res.concat(Integer.toString(nbr));
                else{
                    c=(char) ('A'+(char)(tab[i]-10));
                    buf=String.valueOf(c);
                    res=res.concat(buf);
                }
            }
        }

        return res;

    }

    private int min(int a){
        return Math.min(a, 8);

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

    public void resetRAM() {
        theRAM.reset();
        modifs.clear();
        // Réinitialiser aussi le tableau savedRAM
        if (savedRAM != null) {
            for (int i = 0; i < savedRAM.length; i++) {
                savedRAM[i] = Integer.MIN_VALUE;
            }
        }
    }

    public ArrayList<Instruction> getDownloadedInstructions() {
        return downloadedInstructions;
    }

    public void initValRAM(int adr, int val) {
        theRAM.writeW(val, adr);
        // Calculer l'index dans le tableau savedRAM
        if (savedRAM != null && adr >= theRAM.offsetAdr) {
            int index = (adr - theRAM.offsetAdr) / 4; // Index du mot de 32 bits
            if (index >= 0 && index < savedRAM.length) {
                savedRAM[index] = val;
            }
        }
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
    //DGLOL
    public static void setBouttons(PanelBouttons nBouttons){
        theButtons = nBouttons;
    }

}