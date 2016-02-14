/**
 * Created by chris on 14-2-16.
 */
/**
 * Class om getoonde gegevens op te slaan
 */
public class State implements Cloneable{

    private Taal taal;
    private String categorie;
    private Titel titel;
    private String tekst;
    private boolean nieuw;

    public State(Taal taal) {
        this.taal = taal;
    }

    public State(State state) {
        this(state.getTaal(), state.getCategorie(), state.getTitel(), state.getTekst());
    }

    public State(Taal taal, String categorie, Titel titel, String tekst) {
        this.taal= taal;
        this.categorie = categorie;
        this.titel = titel;
        this.tekst = tekst;
        if (titel != null) {
            this.nieuw = false;
        } else {
            this.nieuw = true;
        }
    }

    public Taal getTaal() {
        return taal;
    }

    public void setTaal(Taal taal) {
        this.taal = taal;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Titel getTitel() {
        return titel;
    }

    public void setTitel(Titel titel) {
        this.titel = titel;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public void setNieuw(boolean nieuw) {
        this.nieuw = nieuw;
    }

    /**
     * Is het een nieuw record?
     * @return
     */
    public boolean isNieuw() {
        return (this.nieuw);
    }

    /**
     * Bepaal de titel van het scherm
     * @return
     */
    public String maakTitel() {
        return "Query " + (isNieuw() ? "nieuw" : this.titel.getId());
    }

    /**
     * Neem inhoud van andere State over
     * @param state
     */
    public void setState(State state) {
        this.taal = state.taal;
        this.categorie = state.categorie;
        this.titel = state.titel;
        this.tekst = state.tekst;
        this.nieuw = state.nieuw;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
