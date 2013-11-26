/**
 * Created by chris on 25-11-13.
 */
public class Variabele {
    private String naam;
    private String waarde;
    private boolean vast;

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getWaarde() {
        return waarde;
    }

    public void setWaarde(String waarde) {
        this.waarde = waarde;
    }

    public boolean isVast() {
        return vast;
    }

    public void setVast(boolean vast) {
        this.vast = vast;
    }

    public Variabele(String naam, boolean vast) {
        this.naam = naam;
        this.vast = vast;
    }

    public Variabele(String naam, String waarde) {
        this.naam = naam;
        this.waarde = waarde;
    }

    public String toString() {
        return this.naam;
    }

}
