/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

/**
 *   Permet d'identifier la valeur des drapeaux
 * @author Laurent
 */
public class Flags {
    private int C,N,V,Z;
    public void reset() {
        C=0;
        N=0;
        V=0;
        Z=0;
    }

    @Override
    public String toString() {
        String buf;
        buf = new String();
        if(N==0)
            buf=buf.concat("n");
        else
            buf=buf.concat("N");
        if(Z==0)
            buf=buf.concat("z");
        else
            buf=buf.concat("Z");
        
        if(V==0)
            buf=buf.concat("v");
        else
            buf=buf.concat("V");
        
        if(C==0)
            buf=buf.concat("c\n");
        else
            buf=buf.concat("C\n");
        return buf;
    }

    public int getC() {
        return C;
    }

    public int getN() {
        return N;
    }

    public int getV() {
        return V;
    }

    public int getZ() {
        return Z;
    }

    public void setC() {
        C = 1;
    }

    public void setN() {
        N = 1;
    }

    public void setV() {
        V = 1;
    }

    public void setZ() {
        Z = 1;
    }
    public void clearC() {
        C = 0;
    }

    public void clearN() {
        N = 0;
    }

    public void clearV() {
        V = 0;
    }

    public void clearZ() {
        Z = 0;
    }    
}
