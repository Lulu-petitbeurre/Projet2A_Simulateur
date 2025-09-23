/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

import java.awt.Color;

/**
 *  Définit les différentes variables liées au Pipeline, permet de retrouver le PC, ligne fetch , décode et execute
 * @author Laurent - Modifié Vincent/Guillaume
 */
public class EtatPipeline {
    int nStep;
    int fetchPC;
    int decodeCode;
    String executeInstruction;
    Color couleurF,couleurD,couleurE;

    public EtatPipeline() {
        this.nStep = -1;
        this.fetchPC = -1;
        this.decodeCode = -1;
        this.executeInstruction = "";
        couleurF=Color.LIGHT_GRAY;
   
    }

    public EtatPipeline(int nStep, int fetchPC, int decodeCode,
            String executeInstruction, Color cf, Color cd, Color ce) {
        this.nStep = nStep;
        this.fetchPC = fetchPC;
        this.decodeCode = decodeCode;
        this.executeInstruction = executeInstruction;
        couleurF=cf;
        couleurD=cd;
        couleurE=ce;
    }

    public int getDecodeCode() {
        return decodeCode;
    }

    public String getExecuteInstruction() {
        return executeInstruction;
    }

    public int getFetchPC() {
        return fetchPC;
    }

    public int getnStep() {
        return nStep;
    }

    public Color getCouleurF() {
        return couleurF;
    }

    public Color getCouleurD() {
        return couleurD;
    }

    public Color getCouleurE() {
        return couleurE;
    }

    public void setCouleur(Color couleur) {
        this.couleurF = couleur;
    }
    
}
