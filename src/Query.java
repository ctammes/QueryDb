/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 30-3-13
 * Time: 18:58
 * To change this template use File | Settings | File Templates.
 */
public class Query {

    private String categorie;
    private String titel;
    private String tekst;
    private Integer taal;

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public Integer getTaal() { return taal; }

    public void setTaal(Integer taal) { this.taal = taal; }

    public Query(String categorie) {
        this.categorie = categorie;
    }

    public Query(String categorie, String titel, String tekst, Integer taal) {
        this.categorie = categorie;
        this.titel = titel;
        this.tekst = tekst;
        this.taal = taal;
    }
}
