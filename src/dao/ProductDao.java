package dao;

import connection.MyConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ProductDao {

    Connection con = MyConnection.getConnection();
    PreparedStatement ps;
    Statement st;
    ResultSet rs;

    //get product table max row
    public int getMaxRow() {
        int row = 0;
        try {
            st = con.createStatement();
            rs = st.executeQuery("select max(pid) from product");
            while (rs.next()) {
                row = rs.getInt(1);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return row + 1;
    }

    public int countCategories() {
        int total = 0;
        try {
            st = con.createStatement();
            rs = st.executeQuery("select count(*) as 'total' from category");
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return total;
    }

    public String[] getCat() {
        String[] categories = new String[countCategories()];
        try {
            st = con.createStatement();
            rs = st.executeQuery("select * from category");
            int i = 0;
            while (rs.next()) {
                categories[i] = rs.getString(2);
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return categories;
    }

//check product ID already exists
    public boolean isIdExist(int id) {
        try {
            ps = con.prepareStatement("select * from product where pid = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;

            }

        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //check product, category, subcategory is existed
    public boolean isProSubCatExist(String pname, int cid, int scid) {
        String sql = "SELECT COUNT(*) FROM product WHERE pname = ? AND cid = ? AND scid = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, pname);
            ps.setInt(2, cid);
            ps.setInt(3, scid);
            rs = ps.executeQuery();

            // Debugging: Log the query and the result
            System.out.println("Checking product with pname: " + pname + ", cid: " + cid + ", scid: " + scid);

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Product already exists with the same category and subcategory.");
                return true; // Product already exists with the same category and subcategory
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    //insert data into product table
    public void insert(int id, String pname, int cid, int scid, int qty, double price, String imagePath) {
        String sql = "INSERT INTO product (pid, pname, cid, scid, pqty, pprice, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, pname);
            ps.setInt(3, cid);
            ps.setInt(4, scid);
            ps.setInt(5, qty);
            ps.setDouble(6, price);
            ps.setString(7, imagePath);

            System.out.println("Executing SQL Query: " + sql); // Debug log
            System.out.println("Image Path Passed: " + imagePath); // Debug log

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Product added successfully");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQL Error: " + ex.getMessage()); // Debug log
        }

    }

    //update product data
    public void update(int id, String pname, String scname, int qty, double price, String imagePath) {
        String sql = "update product set pname = ?, scname = ?, pqty = ?, pprice = ?, image_path = ? where pid = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, pname);       // pname
            ps.setString(2, scname);       // scname
            ps.setInt(3, qty);            // quantity
            ps.setDouble(4, price);       // price
            ps.setString(5, imagePath);   // image path 
            ps.setInt(6, id);             // product id 

            int rowsUpdated = ps.executeUpdate(); // Get the number of rows updated
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Product successfully updated!");
            } else {
                JOptionPane.showMessageDialog(null, "No product found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //delete product
    public void delete(int id) {
        int x = JOptionPane.showConfirmDialog(null, "Are you sure to delete this Product?", "Delete Product", JOptionPane.OK_CANCEL_OPTION, 0);
        if (x == JOptionPane.OK_OPTION) {
            try {
                ps = con.prepareStatement("delete from product where pid = ?");
                ps.setInt(1, id);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "Product Deleted");
                }

            } catch (SQLException ex) {
                Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //get product data
    public void getProductValue(JTable table, String search) {
        String sql = "select * from product where concat(pid, pname, scname) like ? order by pid desc";
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            rs = ps.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            Object[] row;
            while (rs.next()) {
                row = new Object[6];
                row[0] = rs.getInt(1);
                row[1] = rs.getString(2);
                row[2] = rs.getString(3);
                row[3] = rs.getInt(4);
                row[4] = rs.getDouble(5);
                row[5] = rs.getString(6);
                model.addRow(row);

            }

        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Get Subcategories for a Category
    public String[] getSubcategoriesByCategory(String category) {
        String[] subcategories = new String[0];
        try {
            String sql = "select scname from subcategory where cid = (select cid from category where cname = ?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, category);
            rs = ps.executeQuery();

            // Get the number of subcategories first
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            subcategories = new String[size];
            int i = 0;
            while (rs.next()) {
                subcategories[i] = rs.getString("scname");
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subcategories;
    }

}
