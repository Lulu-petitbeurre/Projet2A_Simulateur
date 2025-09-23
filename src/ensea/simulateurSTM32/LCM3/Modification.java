/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

/**
 *
 * @author Laurent
 */
public class Modification {
    int type;
    int loc;
    int val;
    public final static int REG = 1;
    public final static int PC = 2;
    public final static int RAM = 3;
    public final static int FLAG = 4;

    public Modification(int type, int loc, int val) {
        this.type = type;
        this.loc = loc;
        this.val = val;
    }

    public int getLoc() {
        return loc;
    }

    public int getType() {
        return type;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        String buf=new String("modif de ");
        if(type==REG)
            buf=buf.concat("R["+loc+"] <= "+val);
        if(type==PC)
            buf=buf.concat("PC <= "+Integer.toHexString(val));
        if(type==RAM)
            buf=buf.concat("Mem["+loc+"] <= "+val);
        if(type==FLAG){
            if((val & 8)==8)
                buf=buf.concat("N");
            else
                buf=buf.concat("n");
            if((val & 4)==4)
                buf=buf.concat("Z");
            else
                buf=buf.concat("z");
            if((val & 2)==2)
                buf=buf.concat("V");
            else
                buf=buf.concat("v");
            if((val & 1)==1)
                buf=buf.concat("C");
            else
                buf=buf.concat("c");
             
        }
        buf=buf.concat("\n");
        return buf;
    }
    
}
