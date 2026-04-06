import ensea.simulateurSTM32.parseur.*;
import ensea.simulateurSTM32.ihm.Panel.PanelDialog;

public class TestMatch {
    public static void main(String[] args) {
        AssLvl_1 asm = new AssLvl_1();
        PanelDialog pd = new PanelDialog();
        Parseur p = new Parseur("mov R0, #1\n", pd);
        Instruction reconnue = p.TypeParam();
        System.out.println("Parsed: name=" + reconnue.getNom() + " params=" + reconnue.getListeParams().size());
        for (int i=0;i<reconnue.getListeParams().size();i++) {
            Instruction.Mot m=reconnue.getListeParams().get(i);
            System.out.println(" parsed["+i+"]="+m.getClass().getSimpleName()+" type="+m.getType()+" taille="+m.getTaille()+" ord="+m.getOrdre());
        }
        for (Instruction i : asm.getListeCommandes()) {
            if (i.getNom().equalsIgnoreCase(reconnue.getNom())) {
                System.out.print(" model: "+i.getNom()+" params="+i.getListeParams().size());
                for (int j=0;j<i.getListeParams().size();j++) {
                    Instruction.Mot m=i.getListeParams().get(j);
                    System.out.print(" ["+j+":"+m.getClass().getSimpleName()+" t="+m.getType()+" sz="+m.getTaille()+" ord="+m.getOrdre()+"]");
                }
                System.out.println(" comp="+i.compInstr(reconnue));
            }
        }
    }
}
