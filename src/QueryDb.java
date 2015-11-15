import nl.ctammes.common.Sqlite;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 30-3-13
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public class QueryDb extends Sqlite {

    private Integer id;
    private String categorie;
    private String titel;
    private String tekst;
    private Integer taal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getTaal() { return taal; }

    public void setTaal(Integer taal) { this.taal = taal; }

    public QueryDb(String dir, String db) {
        super(dir, db);
        openDb();
        createQuery();
        createTaal();
    }

    public void sluitDb() {
        super.sluitDb();
    }

    /**
     * Maak nieuwe Query database
     * @return
     */
    public boolean createQuery() {
        String sql = "CREATE TABLE IF NOT EXISTS query (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    categorie TEXT NOT NULL," +
                "    titel TEXT NOT NULL," +
                "    tekst TEXT NOT NULL," +
                "    taal INTEGER NULL" +
                ");";
        return executeNoResult(sql);
    }

    /**
     * Maak nieuwe Taal database
     * @return
     */
    public boolean createTaal() {
        String sql = "CREATE TABLE IF NOT EXISTS taal (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "  taal TEXT NOT NULL"+
                ");";
        return executeNoResult(sql);
    }

    /**
     * Truncate vervanger
     * @return
     */
    public boolean truncateQuery() {
        String sql = "DROP TABLE IF EXISTS query;";
        boolean result = executeNoResult(sql);
        if (result) {
            result = createQuery();
        }
        return result;

    }

    /**
     * Lees een query ahv. de id
     * @param id
     * @return
     */
    public ResultSet leesQueryById(Integer id) {
        String sql = "select * from query" +
                " where id = " + Integer.toString(id);
        try {
            return execute(sql);
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
            return null;
        }
    }

    /**
     * Lees alle categorieen
     * @param taal
     * @return
     */
    public ArrayList<String> leesCategorieen(Taal taal) {
        String sql = "select distinct categorie from query" +
                " where taal = " + taal.getId() +
                " order by categorie";

        TreeSet<String> result = new TreeSet<String>();
        try {
            ResultSet rst = execute(sql);
            while (rst.next()) {
                result.add(rst.getString("categorie"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return new ArrayList<String>(result);
    }

    /**
     * Lees alle talen
     * @return
     */
    public ArrayList<Object> leesTalen() {
        ArrayList<Object> items = new ArrayList<Object>();
        String sql = "select id, taal from taal";

        try {
            ResultSet rst = execute(sql);
            int i = 0;
            while (rst.next()) {
                items.add(new Taal(Integer.parseInt(rst.getString("id")), rst.getString("taal")));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }
        return items;
    }

    /**
     * Lees alle titels bij een bepaalde categorie
     * @param categorie
     * @param taal
     * @return
     */
    public ArrayList<Object> leesTitels(String categorie, Taal taal) {
        ArrayList<Object> items = new ArrayList<Object>();
        String sql = "select id, titel from query" +
                " where categorie='" + categorie.replaceAll("'", "''") + "'" +
                " and taal = " + taal.getId();

        try {
            ResultSet rst = execute(sql);
            int i = 0;
            while (rst.next()) {
                items.add(new Titel(Integer.parseInt(rst.getString("id")), rst.getString("titel")));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }
        return items;
    }

    /**
     * Geef een titel object ahv. de id
     * @param id
     * @return
     */
    public Titel leesTitelById(Integer id) {
        String sql = "select titel from query where id=" + id ;

        Titel result = null;
        try {
            ResultSet rst = execute(sql);
            while (rst.next()) {
                result = new Titel(id, rst.getString("titel"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;

    }


    /**
     * Zoek titels met een bepaalde tekst in de titel
     * @param taal
     * @param sleutel
     * @return array met resultaten
     */
    public Object[] zoekTitels(String sleutel, Taal taal) {
        Object[] items = new Titel[50];
        Object[] result = null;
        String sql = "select id, titel from query where titel like '%" +  sleutel + "%'" +
                " and taal = " + taal.getId();

        try {
            ResultSet rst = execute(sql);
            int i = 0;
            while (rst.next()) {
                items[i++] = (new Titel(Integer.parseInt(rst.getString("id")), rst.getString("titel")));
            }
            result = new Titel[i];
            java.lang.System.arraycopy(items, 0, result, 0, i);
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }
        return result;
    }

    /**
     * Lees de tekst bij een categorie en titel
     * (een titel kan bij neer dan een categorie voorkomen!)
     * @param categorie
     * @param titel
     * @return
     */
    public String leesTekst(String categorie, String titel, Taal taal) {
        String sql = "select tekst from query " +
                " where categorie='" + categorie.replaceAll("'", "''") +
                "' and titel = '" + titel.replaceAll("'", "''") + "'" +
                " and taal = " + taal.getId();

        String result = "";
        try {
            ResultSet rst = execute(sql);
            while (rst.next()) {
                result = rst.getString("tekst");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }

    /**
     * Lees de tekst bij een bepaalde id
     * @param id
     * @return
     */
    public String leesTekstById(Integer id) {
        String sql = "select tekst from query where id=" + id ;

        String result = "";
        try {
            ResultSet rst = execute(sql);
            while (rst.next()) {
                result = rst.getString("tekst");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;

    }

    /**
     * Voeg een nieuw Query record toe
     * @param query
     * @return id van laatst toegevoegde record
     */
    public Integer insertQuery(Query query) {

        String categorie = query.getCategorie().replaceAll("'", "''");
        String titel = query.getTitel().replaceAll("'", "''");
        String tekst = query.getTekst().replaceAll("'", "''");
        Integer taal = query.getTaal();
        String values = String.format("'%s', '%s', '%s', %d",
                categorie, titel, tekst, taal);
        String sql = "insert into query" +
                " (categorie, titel, tekst, taal)" +
                " values (" + values + ")";
        try {
            executeNoResult(sql);
            sql = "select last_insert_rowid() last_id from query limit 1";
            ResultSet rs = execute(sql);
            return rs.getInt("last_id");
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
            return -1;
        }

    }

    /**
     * Voeg een nieuw Taal record toe
     * @param taal
     * @return id van laatst toegevoegde record
     */
    public Integer insertTaal(String taal) {

        String values = String.format("'%s'",
                taal);
        String sql = "insert into taal" +
                " (taal)" +
                " values (" + values + ")";
        try {
            executeNoResult(sql);
            sql = "select last_insert_rowid() last_id from taal limit 1";
            ResultSet rs = execute(sql);
            return rs.getInt("last_id");
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
            return -1;
        }

    }

    /**
     * Wijzig de tekst van een query
     * @param titel
     * @return
     */
    public boolean wijzigQueryTekst(Titel titel, String tekst) {

        tekst = tekst.replaceAll("'", "''");
        String sql = "update query" +
                " set tekst = '" + tekst + "'" +
                " where id = " + titel.getId();
        try {
            executeNoResult(sql);
            return true;
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
            return false;
        }

    }

    /**
     * Verwijder een query (id is onderdeel van Titel object)
     * @param titel
     * @return
     */
    public boolean verwijderQueryTekst(Titel titel) {

        String sql = "delete from query" +
                " where id = " + titel.getId();
        try {
            executeNoResult(sql);
            return true;
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
            return false;
        }

    }

    /**
     * Geeft de hoogste id terug (na insert)
     * @return
     */
    public int getMaxId() {
        try {
            return getMax("query", "id");
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }

    }

}
