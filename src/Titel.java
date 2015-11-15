/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 31-3-13
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class Titel {
    private Integer id;     // Id van het query record
    private String titel;

    public Integer getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public Titel() {
    }

    public Titel(Integer id, String titel) {
        this.id = id;
        this.titel = titel;
    }

    @Override
    // getSelectedItem() geeft de toString waarde
    public String toString(){
        // return Integer.toString(id);
        return titel;
    }
}
