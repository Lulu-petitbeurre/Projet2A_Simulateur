/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

/**
 *
 * @author Laurent -Modifié Vincent -Guillaume
 */
public class Registers {
    public static final int NBREG = 8;
    int []R;
    int PC;

    public Registers() {
        R=new int [NBREG];
        PC=0;
        this.reset();
    }

    public void setPC(int PC) {
        this.PC = PC;
    }
    public void incPC() {
        PC = PC+2;
    }    
    public void reset() {
        int i;
        for(i=0;i<NBREG;i++)
            R[i]=0;
        PC=0x08000000;
    }

    @Override
    public String toString() {
        int i,k,l;
        String res=new String();
        Integer nbr;
        
        res=res.concat("PC=0x");
        
        res=res.concat(inHexa(PC,32));
        res=res.concat("\n");
       
        for(i=0;i<NBREG;i++){
            res=res.concat("R[");
            nbr=new Integer(i);
            res=res.concat(nbr.toString());
            res=res.concat("]=");
            nbr=new Integer(R[i]);
            l=10-nbr.toString().length();
            for(k=0;k<l;k++)
                res=res.concat(" ");
            res=res.concat(nbr.toString());
            res=res.concat(" (0x");
            res=res.concat(inHexa(R[i],32));
            res=res.concat(") | ");
            if((i+1)%2==0)
                res=res.concat("\n");
            else
                res=res.concat(" ");
        }
        return res;
    }
    public String inHexa(int val, int nbDigit){
        if(val<0)
            return(Integer.toHexString(val));
        else{
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
   
}
