/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 31-3-13
 * Time: 12:26
 * To change this template use File | Settings | File Templates.
 */
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MijnComboBoxItem extends JFrame implements ActionListener
{
    public MijnComboBoxItem()
    {
        QueryDb db = new QueryDb("/home/chris/IdeaProjects/java/QueryDb", "QueryDb.db");
        String sleutel = "apotheek";
        ArrayList<Object> titels = db.zoekTitelsTest(sleutel);

//        Vector model = new Vector();
//        model.addElement( new Item(1, "car" ) );
//        model.addElement( new Item(2, "plane" ) );
//        model.addElement( new Item(3, "train" ) );
//        model.addElement( new Item(4, "boat" ) );
//        model.addElement( new Item(5, "boat aadf asfsdf a asd asd" ) );

        JComboBox comboBox;

        //  Easiest approach is to just override toString() method
        //  of the Item class

        comboBox = new JComboBox( titels.toArray() );
        comboBox.addActionListener( this );
        comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        getContentPane().add(comboBox, BorderLayout.NORTH );

        //  Most flexible approach is to create a custom render
        //  to diplay the Item data

        comboBox = new JComboBox( titels.toArray() );
        comboBox.setRenderer( new ItemRenderer() );
        comboBox.addActionListener( this );
        getContentPane().add(comboBox, BorderLayout.SOUTH );
    }

    public void actionPerformed(ActionEvent e)
    {
        JComboBox comboBox = (JComboBox)e.getSource();
        Titels titel = (Titels)comboBox.getSelectedItem();
        System.out.println( titel.getId() + " : " + titel.getTitel() );
    }

    class ItemRenderer extends BasicComboBoxRenderer
    {
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus)
        {
            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null)
            {
                Titels titel = (Titels)value;
                setText( titel.getTitel().toUpperCase() );
            }

            if (index == -1)
            {
                Titels titel = (Titels)value;
                setText( "" + titel.getId() );
            }


            return this;
        }
    }

    class Item
    {
        private int id;
        private String description;

        public Item(int id, String description)
        {
            this.id = id;
            this.description = description;
        }

        public int getId()
        {
            return id;
        }

        public String getDescription()
        {
            return description;
        }

        public String toString()
        {
            return description;
        }
    }

    public static void main(String[] args)
    {
        JFrame frame = new MijnComboBoxItem();
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }

}