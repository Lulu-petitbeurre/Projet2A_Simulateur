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
        this.offsetAdr = offsetAdr;
    }

    public void reset() {
        int i;
        for(i=0;i<sizeInWord;i++)
            mem[i]=0;
    }
    void writeHW(int data, int addr){
        int index;
        data=data&0xFFFF;
        if( (addr>=offsetAdr) && (addr<offsetAdr+sizeInWord*4)){
            if(addr%2==1){
                System.out.println("Erreur d'écriture: addresse impaire "+addr);
            }
            else{
                index= (addr-offsetAdr)/4;
                if(addr%4==0){
                    mem[index]=(mem[index]&0xFFFF0000)+data;
                }
                else{
                    mem[index]=(mem[index]&0x0000FFFF)+(data<<16);
                }

            }
                
        }
        else{
            System.out.println("Erreur d'écriture: pas de mémoire en "+addr);
        }
        
    }
        
    void writeW(int data, int addr){
        int index;
        if( (addr>=offsetAdr) && (addr<offsetAdr+sizeInWord*4)){
            index= (addr-offsetAdr)/4;
            mem[index]=data;
        }
        else{
            System.out.println("Erreur d'écriture: pas de mémoire en "+addr);
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
        int index;
        addr=(addr/4)*4;
        index=(addr-offsetAdr)/4;
        return(mem[index]);
    }
    @Override
    public String toString() {
        int i,k,l;
        String res= "";
        int nbr;
       
        if(memType==RAMTYPE){
            for(i=0;i<sizeInWord;i++){
                if((i)%8==0){
                    res=res.concat("m[");
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
        }
        else{//ROM
            for(i=0;i<sizeInWord;i++){
                if((i)%8==0){
                    res=res.concat("m[");
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
        String res= "";
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

}
