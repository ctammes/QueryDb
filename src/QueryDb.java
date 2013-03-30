import nl.ctammes.common.Sqlite;

import java.sql.ResultSet;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 30-3-13
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public class QueryDb extends Sqlite {

    private Long id;
    private String categorie;
    private String titel;
    private String tekst;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public QueryDb(String dir, String db) {
        super(dir, db);
        openDb();
    }

    public void sluitDb() {
        super.sluitDb();
    }

    public ResultSet leesQuery(Long id) {
        String sql = "select * from query" +
                " where id = " + Long.toString(id);
        return execute(sql);
    }

    public boolean schrijfQuery(Query query) {

        String categorie = query.getCategorie().replaceAll("'", "''");
        String titel = query.getTitel().replaceAll("'", "''");
        String tekst = query.getTekst().replaceAll("'", "''");
        String values = String.format("'%s', '%s', '%s'",
                categorie, titel, tekst);
        String sql = "insert into query" +
                " (categorie, titel, tekst)" +
                " values (" + values + ")";
        System.out.println(sql);
//        Tomboy2Everpad.log.fine("sql: " + sql);
        executeNoResult(sql);
        return false;

    }



}
