package dao;

import connection.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class SubcategoryDao {

    Connection con = MyConnection.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;

    //get subcategory table max row
    public int getMaxRow() {
        int row = 0;
        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(scid) from subcategory");
            while (rs.next()) {
                row = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return row + 1;
    }

    //product
    public int countCategories() {
        int total = 0;
        try {
            st = con.createStatement();
            rs = st.executeQuery("select count(*) as 'total' from category");
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    //product
    public String[] getCat() {
        String[] categories = new String[countCategories()];
        try {
            st = con.createStatement();
            rs = st.executeQuery("select * from category");
            int i = 0;
            while (rs.next()) {
                categories[i] = rs.getString(3);
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categories;
    }

    //check subcategory already exists
    public boolean isSubCategoryNameExist(String scname) {
        try {
            ps = con.prepareStatement("select * from subcategory where scname = ?");
            ps.setString(1, scname);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;

            }

        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //check subcategory ID already exists
    public boolean isIdExist(int id) {
        try {
            ps = con.prepareStatement("select * from subcategory where scid = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;

            }

        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //insert data into subcategory table
    public void insert(int id, String scname, int cid) {
        String sql = "insert into subcategory (scid, scname, cid) values(?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, scname);
            ps.setInt(3, cid);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Subcategory added successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //update subcategory data
    public void update(int id, String scname, int cid) {
        String sql = "UPDATE subcategory SET scname = ?, cid = ? WHERE scid = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, scname); // Set the subcategory name
            ps.setInt(2, cid);       // Set the category ID (foreign key)
            ps.setInt(3, id);        // Set the subcategory ID for updating

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Subcategory successfully updated!");
            } else {
                JOptionPane.showMessageDialog(null, "No subcategory found with the given ID", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error updating subcategory: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //delete subcategory
    public void delete(int id) {
        int x = JOptionPane.showConfirmDialog(null, "Are you sure to delete this subcategory?", "Delete Subcategory", JOptionPane.OK_CANCEL_OPTION, 0);
        if (x == JOptionPane.OK_OPTION) {
            try {
                ps = con.prepareStatement("delete from subcategory where scid = ?");
                ps.setInt(1, id);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "Subcategory Deleted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //get subcategory data
    public void getSubCategoryValue(JTable table, String search) {
        String sql = "select s.scid, s.scname, c.cname from subcategory s "
                + "join category c on s.cid = c.cid "
                + "where concat(s.scid, s.scname) like ? order by s.scid desc";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] row;
            while (rs.next()) {
                row = new Object[3];
                row[0] = rs.getInt(1); // scid
                row[1] = rs.getString(2); // scname
                row[2] = rs.getString(3); // category name (cname)
                model.addRow(row);

            }

        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Fetch subcategories for a specific category
    public List<String> getSubcategories(int cid) {
        List<String> subcategories = new ArrayList<>();
        try {
            String sql = "SELECT scname FROM subcategory WHERE cid = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, cid);
            rs = ps.executeQuery();
            while (rs.next()) {
                subcategories.add(rs.getString("scname")); 
            }
        } catch (SQLException ex) {
            Logger.getLogger(SubcategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subcategories;
    }

}
