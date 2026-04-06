/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

/**
 *
 * @author Laurent
 */
public class ROM extends Memory {
    public static final int NBWROM = 32;
    public static final int BASEROM=0x08000000;

    public ROM() {
        super(NBWROM*4, BASEROM);
        memType=Memory.ROMTYPE;
    }
    @Override
    public String toString() {
        int i;
        String res= "";
        //ROM
        for(i=0;i<sizeInWord;i++){
            if((i)%8==0){
                res=res.concat("m[");
                res=res.concat("0x");
                res=res.concat(inHexa(i*4+offsetAdr,8));
                res=res.concat("]= ");
            }
            res=res.concat(inHexa(mem[i]&0xFFFF,4));
            res=res.concat(":");
            res=res.concat(inHexa((mem[i]>>16)&0xFFFF,4));
            res=res.concat(" ");
            if((i+1)%8==0)
                res=res.concat("\n");
            else
                res=res.concat(" ");
        }
        return res;
    }
    
}
