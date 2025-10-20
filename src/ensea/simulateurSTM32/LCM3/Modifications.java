/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ensea.simulateurSTM32.LCM3;

import java.util.ArrayList;

/**
 *
 * @author Laurent
 */
public class Modifications {
    ArrayList<Modification> curModifs;
    ArrayList<Modification> oldModifs;

    public Modifications() {
        curModifs=new ArrayList<Modification>();
        oldModifs=new ArrayList<Modification>();
    }

    @Override
    public String toString() {
        String buf=new String();
        for(Modification m : curModifs){
            buf=buf.concat(m.toString());
        }
        return buf;
    }

    public void clear() {
        curModifs.clear();
        oldModifs.clear();
    }

    void add(int type, int loc, int val) {
        curModifs.add(new Modification(type,loc,val));
    }

    public ArrayList<Modification> getCurModifs() {
        return curModifs;
    }

    public ArrayList<Modification> getOldModifs() {
        return oldModifs;
    }
    public void next() {
        oldModifs=(ArrayList<Modification>)curModifs.clone();
        curModifs.clear();
    }   
    
}
