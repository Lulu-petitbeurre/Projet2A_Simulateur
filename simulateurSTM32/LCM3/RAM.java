/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

/**
 *
 * @author Laurent
 */
public class RAM extends Memory{
    public static final int NBWRAM = 64;
    public static final int BASERAM=0x20000000;

    public RAM() {
        super(NBWRAM*4, BASERAM);
        memType=Memory.RAMTYPE;
    }
    @Override
    public String toString() {
        int i,k,l;
        String res= "";
        int nbr;
         for(i=0;i<sizeInWord;i++){
            if((i)%8==0){
                res=res.concat("m[");
                //nbr=new Integer(i+offsetAdr);
                //res=res.concat(nbr.toString());
                res=res.concat("0x");
                res=res.concat(inHexa(i*4+offsetAdr,8));
                res=res.concat("]=");
            }
            nbr=mem[i];
            l=8- Integer.toString(nbr).length();
            for(k=0;k<l;k++)
                res=res.concat(" ");
            res=res.concat(Integer.toString(nbr));
            if((i+1)%8==0)
                res=res.concat("\n");
            else
                res=res.concat(" ");
        }
   return res;
    }
    
    
}
