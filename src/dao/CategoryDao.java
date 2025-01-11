package dao;

import connection.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class CategoryDao {

    Connection con = MyConnection.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;

    //get category table max row
    public int getMaxRow() {
        int row = 0;
        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(cid) from category");
            while (rs.next()) {
                row = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return row + 1;
    }

    //check category already exists
    public boolean isCategoryNameExist(String cname) {
        try {
            ps = con.prepareStatement("select * from category where cname = ?");
            ps.setString(1, cname);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;

            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //check category ID already exists
    public boolean isIdExist(int id) {
        try {
            ps = con.prepareStatement("select * from category where cid = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;

            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //insert data into category table
    public void insert(String cname) {
        String sql = "insert into category (cname) values(?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, cname);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Category added successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //update category data
    public void update(int id, String cname) {
        String sql = "update category set cname = ? where cid = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, cname);
            ps.setInt(2, id);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Category successfully updated!");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //delete category
    public void delete(int id) {
        int x = JOptionPane.showConfirmDialog(null, "Are you sure to delete this category?", "Delete Category", JOptionPane.OK_CANCEL_OPTION, 0);
        if (x == JOptionPane.OK_OPTION) {
            try {
                ps = con.prepareStatement("delete from category where cid = ?");
                ps.setInt(1, id);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "Category Deleted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(SupplierDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //get category data
    public void getCategoryValue(JTable table, String search) {
        String sql = "select * from category where concat(cid, cname) like ? order by cid desc";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] row;
            while (rs.next()) {
                row = new Object[3];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                model.addRow(row);

            }

        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Fetch all categories
    public Map<Integer, String> getCategories() {
        Map<Integer, String> categories = new HashMap<>();
        try {
            String sql = "SELECT cid, cname FROM category";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                categories.put(rs.getInt("cid"), rs.getString("cname")); // Store cid and cname
            }
        } catch (SQLException ex) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categories;
    }

}
