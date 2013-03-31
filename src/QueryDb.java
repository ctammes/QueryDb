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
        createQuery();
    }

    public void sluitDb() {
        super.sluitDb();
    }

    /**
     * Maak nieuwe database
     * @return
     */
    public boolean createQuery() {
        String sql = "CREATE TABLE IF NOT EXISTS query (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "    categorie TEXT NOT NULL," +
                "    titel TEXT NOT NULL," +
                "    tekst TEXT NOT NULL" +
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

    public ResultSet leesQueryId(Long id) {
        String sql = "select * from query" +
                " where id = " + Long.toString(id);
        return execute(sql);
    }

    public ArrayList<String> leesCategorien() {
        String sql = "select distinct categorie from query order by categorie";
        ResultSet rst = execute(sql);

        TreeSet<String> result = new TreeSet<String>();
        try {
            while (rst.next()) {
                result.add(rst.getString("categorie"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<String>(result);
    }

    public Titels[] leesTitels(String categorie) {
        String sql = "select id, titel from query where categorie='" + categorie.replaceAll("'", "''") + "'";
        ResultSet rst = execute(sql);

//        TreeMap<Integer, String> result = new TreeMap<Integer, String>();
        Titels[] result = new Titels[50];
        int i = 0;
        try {
            while (rst.next()) {
                result[i++] = new Titels(rst.getInt("id"), rst.getString("titel"));
//                result.put(rst.getInt("id"), rst.getString("titel"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return result;
    }


    public ArrayList<String> zoekTitels(String sleutel) {
        String sql = "select titel from query where titel like '%" + sleutel.replaceAll("'", "''") + "%'";
        ResultSet rst = execute(sql);
        System.out.println(sql);
        TreeSet<String> result = new TreeSet<String>();
        try {
            while (rst.next()) {
                result.add(rst.getString("titel"));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        return new ArrayList<String>(result);
    }


    public ArrayList<Object> zoekTitelsTest(String sleutel) {
        ArrayList<Object> items = new ArrayList<Object>();
        ResultSet rs = execute("select id,titel from query where titel like '%" +  sleutel + "%'");
        try {
            while (rs.next()) {
                items.add(new Titels(Integer.parseInt(rs.getString(1)), rs.getString(2)));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return items;
    }

    public ArrayList<Titels> zoekTitelsTest1(String sleutel) {
        ArrayList<Titels> items = new ArrayList<Titels>();
        ResultSet rs = execute("select id,titel from query where titel like '%" +  sleutel + "%'");
        try {
            while (rs.next()) {
                items.add(new Titels(Integer.parseInt(rs.getString(1)), rs.getString(2)));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return items;
    }

    public Object[] zoekTitelsTest2(String sleutel) {
        Object[] items = new Titels[50];
        ResultSet rs = execute("select id,titel from query where titel like '%" +  sleutel + "%'");
        try {
            int i = 0;
            while (rs.next()) {
                items[i++] = (new Titels(Integer.parseInt(rs.getString(1)), rs.getString(2)));
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return items;
    }

    public String leesTekst(String categorie, String titel) {
        String sql = "select tekst from query where categorie='" + categorie.replaceAll("'", "''") + "' and titel = '" + titel.replaceAll("'", "''") + "'";
        ResultSet rst = execute(sql);

        String result = "";
        try {
            while (rst.next()) {
                result = rst.getString("tekst");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;
    }

    public String leesTekstById(Integer id) {
        String sql = "select tekst from query where id=" + id ;
        ResultSet rst = execute(sql);

        String result = "";
        try {
            while (rst.next()) {
                result = rst.getString("tekst");
            }
        } catch(Exception e) {
            System.out.println(e.getMessage() + " - " + sql);
        }

        return result;

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
        executeNoResult(sql);
        return false;

    }



}
