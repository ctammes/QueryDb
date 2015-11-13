/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-11-15
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
public class Taal {
    private Integer id;
    private String taal;

    public Integer getId() {
        return id;
    }

    public String getTaal() {
        return taal;
    }

    public Taal() {
    }

    public Taal(Integer id, String taal) {
        this.id = id;
        this.taal = taal;
    }

    @Override
    // getSelectedItem() geeft de toString waarde
    public String toString(){
        // return Integer.toString(id);
        return taal;
    }
}
