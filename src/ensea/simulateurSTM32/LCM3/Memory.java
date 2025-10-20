/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

/**
 * Permet notamment de connaître l'état de la ROM , pour lire le code de la ROM
 * @author Laurent
 */
public class Memory {
    // the memory is here made of words (int)(4 bytes)
    // but each byte has an address
    int sizeInWord,offsetAdr;
    int []mem;
    public static final int RAMTYPE=1;
    public static final int ROMTYPE=2;
    int memType;

    public Memory(int sizeInByte, int offsetAdr) {
        sizeInWord=sizeInByte/4;
        mem=new int [sizeInWord];
        this.reset(); 
        this.offsetAdr = offsetAdr%4;
        this.offsetAdr = offsetAdr;
    }

    public void reset() {
        int i;
        for(i=0;i<sizeInWord;i++)
            mem[i]=0;
    }
    int writeHW(int data,int addr){
        int index;
        data=data&0xFFFF;
        if( (addr>=offsetAdr) && (addr<offsetAdr+sizeInWord*4)){
            if(addr%2==1){
                System.out.println("Erreur d'écriture: addresse impaire "+addr);
                return -1;
            }
            else{
                index= (addr-offsetAdr)/4;
                if(addr%4==0){
                    mem[index]=(mem[index]&0xFFFF0000)+data;
                }
                else{
                    mem[index]=(mem[index]&0x0000FFFF)+(data<<16);
                }
                
                return -1;
            }
                
        }
        else{
            System.out.println("Erreur d'écriture: pas de mémoire en "+addr);
            return -1;
        }
        
    }
        
    int writeW(int data,int addr){
        int index;
        if( (addr>=offsetAdr) && (addr<offsetAdr+sizeInWord*4)){
            index= (addr-offsetAdr)/4;
            mem[index]=data;
            return data;   
        }
        else{
            System.out.println("Erreur d'écriture: pas de mémoire en "+addr);
            return -1;
        }
        
    }
    // à utiliser pour lire code de la ROM
   public int readHW(int addr){
        int index,data;

        if( (addr>=offsetAdr) && (addr<offsetAdr+sizeInWord*4)){
            if(addr%2==1){
                System.out.println("Erreur de lecture: addresse impaire "+addr);
                return -1;
            }
            else{
                index= (addr-offsetAdr)/4;
                if(addr%4==0){
                    data=(mem[index]&0xFFFF);
                }
                else{
                    data=(mem[index]>>16)&0xFFFF;
                }
                
                return data;
            }
                
        }
        else{
            System.out.println("Erreur de lecture: pas de mémoire en "+addr);
            return -1;
        }    
    }
    public int readW(int addr){
        int index,data;
        addr=(addr/4)*4;
        index=(addr-offsetAdr)/4;
        return(mem[index]);
    }
    @Override
    public String toString() {
        int i,k,l;
        String res=new String();
        Integer nbr;
       
        if(memType==RAMTYPE){
            for(i=0;i<sizeInWord;i++){
                if((i)%8==0){
                    res=res.concat("m[");
                    //nbr=new Integer(i+offsetAdr);
                    //res=res.concat(nbr.toString());
                    res=res.concat("0x");
                    res=res.concat(inHexa(i*4+offsetAdr,8));
                    res=res.concat("]=");
                }
                nbr=new Integer(mem[i]);
                l=8-nbr.toString().length();
                for(k=0;k<l;k++)
                    res=res.concat(" ");
                res=res.concat(nbr.toString());
                if((i+1)%8==0)
                    res=res.concat("\n");
                else
                    res=res.concat(" ");
            }
        }
        else{//ROM
            for(i=0;i<sizeInWord;i++){
                if((i)%8==0){
                    res=res.concat("m[");
                    //nbr=new Integer(i+offsetAdr);
                    //res=res.concat(nbr.toString());
                    res=res.concat("0x");
                    res=res.concat(inHexa(i*4+offsetAdr,8));
                    res=res.concat("]= ");
                }
                res=res.concat(inHexa((mem[i]&0xFFFF0000)>>16,4));
                res=res.concat(":");
                res=res.concat(inHexa(mem[i]&0xFFFF,4));
                res=res.concat(" ");
                if((i+1)%8==0)
                    res=res.concat("\n");
                else
                    res=res.concat(" ");
            }
        }
        return res;
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

    public int[] getMem() {
        return mem;
    }
    
}
