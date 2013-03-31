/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 31-3-13
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class Titels {
    private Integer id;
    private String titel;

    public Integer getId() {
        return id;
    }

    public String getTitel() {
        return titel;
    }

    public Titels(Integer id, String titel) {
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
