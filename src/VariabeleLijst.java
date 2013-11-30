import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 26-11-13.
 * Lijst van Variabele objecten
 */
public class VariabeleLijst extends ArrayList {

    private List<Variabele> list = new ArrayList();

    /**
     * Element toevoegen
     * @param var
     */
    public void add(Variabele var) {
        list.add(var);
    }

    /**
     * Element verwijderen op naam
     * @param naam
     */
    public void remove(String naam) {
        Variabele var = zoekElement(naam);
        list.remove(var);
    }


    /**
     * Geef waarde van element
     * @param naam
     * @return
     */
    public String get(String naam) {
        String result = "";
        Variabele var = zoekElement(naam);
        if (var != null) {
            result = var.getWaarde();
        }
        return result;
    }

    /**
     * Vul nieuw element of wijzig waarde van bestaand element
     * @param naam
     * @param waarde
     */
    public void put(String naam, String waarde) {
        Variabele var = zoekElement(naam);
        if (var != null) {
            var.setWaarde(waarde);
        } else {
            add(new Variabele(naam, waarde));
        }

    }

    /**
     * Bestaat element?
     * @param naam
     * @return
     */
    public boolean contains(String naam) {
        return (zoekElement(naam) != null);
    }

    /**
     * Geef aantal elementen
     * @return
     */
    public int size() {
        return list.size();
    }

    /**
     * Geef lijst met alle elementen
     * @return
     */
    public List<Variabele> list() {
        return list;
    }

    /**
     * Zoek element op naam
     * @param naam
     * @return
     */
    private Variabele zoekElement(String naam) {
        for (Variabele var : list) {
            if (var.getNaam().equals(naam)) {
                return var;
            }
        }
        return null;
    }

}
