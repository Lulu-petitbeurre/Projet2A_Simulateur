/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

import java.awt.Color;

/**
 *
 * @author Laurent
 */
public class Pipeline {
    int nStep=-1;
    public static final int NBETATS = 4;
    public static final int NBCOLOR = 8;
    EtatPipeline []etat;
    Color[]couleur;

    public Pipeline() {
        nStep=0;
        etat=new EtatPipeline [NBETATS];
        couleur=new Color[NBCOLOR];
        couleur[0]=new Color(64,128+64,128+64+63);
        couleur[1]=new Color(64,128+64+63,64);
        couleur[2]=new Color(128+64,128,128+64);
        couleur[3]=new Color(128,128+64,128+64);
        couleur[4]=new Color(128+64+63,128+64,128+64);
        couleur[5]=new Color(128+64+63,128+64+63,64);
        couleur[6]=new Color(128+64+63,128,128);
        couleur[7]=new Color(128+64,128+64,128+64+63);
        reset();
    }

    public void reset() {
        nStep=-1;
        int i;
        for(i=0;i<NBETATS;i++){
            etat[i]=new EtatPipeline();
            
        }

    }
    public void insereEtat(EtatPipeline nvlEtat){
        int i;
        
        for(i=NBETATS-1;i>0;i--){
            etat[i]=etat[i-1];
        }
        etat[0]=nvlEtat;
        nStep++;
    }

    public static int getNBETATS() {
        return NBETATS;
    }

    public EtatPipeline[] getEtat() {
        return etat;
    }

    public int getnStep() {
        return nStep;
    }
    
}
