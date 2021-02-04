package Entry;

import java.sql.*;
import Controllers.*;

public class Db {
    private static Db db;
    private Statement statement;
    private Connection connection;
    private Connection conn;

    Statement stmt = null;
    PreparedStatement pstmt = null;
    String sql;
    Market market;

    private static final String  CreateMarkettable =
      "CREATE TABLE IF NOT EXISTS Market (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "instrType VARCHAR(200) UNIQUE NOT NULL," +
              "quantity INT NOT NULL," +
            "price INT NOT NULL," +
            "seller VARCHAR(200) NOT NULL)";

    //create a database

    public static void dbCreate() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307", "root", "123456"
            );
            //System.out.println("you fucked");
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS Fixme");
        } catch (Exception e) {
            System.out.println("dbCreate:: " + e.getClass() + ":: " + e.getMessage());
            //System.exit(0);
        }

    }

    // connection and create table on database

    private Connection dbConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3307/fixme", "root", "123456"
            );
            //System.out.println("you fucked");
            statement = connection.createStatement();
            statement.executeUpdate(CreateMarkettable);
        } catch (Exception e) {
            System.out.println("dbConnect:: " + e.getClass() + ":: " + e.getMessage());
            //System.exit(0);
        }

        return (connection);
    }

    public Db(Market market) {
        this.market = market;
    }


    public String getInstruments() {
        String msg = "**These are the instruments currently listed on the market**\n";
        try {
            conn = this.dbConnect();
            stmt = conn.createStatement();
            sql = "SELECT * FROM Market";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                // Retrieve by column name
                String type = rs.getString("instrType");
                int quantity = rs.getInt("quantity");
                int price = rs.getInt("price");

                // Display values

                msg += ("TYPE " + type);
                msg += (" | QUANTITY " + quantity);
                msg += (" | PRICE " + price + "\n");
                // stmt.close();
                // conn.close();
            }
            rs.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                System.out.println(se2);
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                System.out.println(se);
            }
        }

        return msg;
    }


    public boolean checkBuyMultiPossible(String instrument, String qty, String priceReq, String brokerId) {
        try {
            conn = this.dbConnect();
            stmt = conn.createStatement();
            sql = "SELECT * FROM Market ORDER BY price ASC";
            ResultSet rs = stmt.executeQuery(sql);
            int amount = Integer.parseInt(qty);
            int totalspent = 0;
            int saved = 0;
            while (rs.next() && amount > 0) {

                // Retrieve by column name
                String type = rs.getString("instrType");
                int quantity = rs.getInt("quantity");
                int price = rs.getInt("price");
                int id = rs.getInt("id");

                if (type.equalsIgnoreCase(instrument) && price <= Integer.parseInt(priceReq)) {
                    if (quantity >= amount) {
                        totalspent = calcSpent(totalspent, amount, price);
                        // updateQty(id, quantity - amount);
                        amount = 0;
                    } else if (quantity < amount) {
                        totalspent = calcSpent(totalspent, quantity, price);
                        amount = amount - quantity;
                        // removeInstrument(id);
                    }
                }
            }
            rs.close();
            if (amount == 0) {
                System.out.println("Got it!!!");
                amount = Integer.parseInt(qty);
                totalspent = 0;
                saved = 0;
                rs = stmt.executeQuery(sql);
                while (rs.next() && amount > 0) {

                    // Retrieve by column name
                    String type = rs.getString("instrType");
                    int quantity = rs.getInt("quantity");
                    int price = rs.getInt("price");
                    int id = rs.getInt("id");
                    String seller = rs.getString("seller");
                    if (type.equalsIgnoreCase(instrument) && price <= Integer.parseInt(priceReq)) {
                        if (quantity > amount) {
                            totalspent = calcSpent(totalspent, amount, price);
                            updateQty(id, quantity - amount);
                            if (seller != null)
                                market.getFix().sendExecuteReport(seller, amount * price);
                            amount = 0;
                        } else if (quantity < amount) {
                            totalspent = calcSpent(totalspent, quantity, price);
                            amount = amount - quantity;
                            removeInstrument(id);
                            if (seller != null)
                                market.getFix().sendExecuteReport(seller, quantity * price);
                        }
                    }
                }
            }
            rs.close();
            if (amount == 0) {
                market.setErrorReason(null);
                saved = (Integer.parseInt(qty) * Integer.parseInt(priceReq)) - totalspent;
                market.getFix().sendExecuteReport(brokerId, saved);
                return true;
            }
            int refund =Integer.parseInt(qty) * Integer.parseInt(priceReq);
            market.setErrorReason("Not enough instruments within the price range to execute the order:" + refund);
            market.getFix().sendExecuteReport(brokerId, 0);
            return false;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                System.out.println(se2);
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                System.out.println(se);
            }
        }
        return false;

    }

    public int calcSpent(int total, int amount, int price) {
        return total + (amount * price);
    }

    public void removeInstrument(int id) {
        try {
            sql = "DELETE FROM Market WHERE id=?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (Exception e) {
            System.out.println("Removal error:" + e);
        }

    }

    public void updateQty(int id, int leftover) {
        try {
            sql = "UPDATE Market set quantity=? where id=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, leftover);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Leftover update error:" + e);
        }
    }

    public void buyInstrument(String instrument, String qty, String price, String brokerId) {
        // TODO
    }

    public void sellInstrument(String instrument, String qty, String price, String brokerId) {
        try {
            conn = this.dbConnect();
            sql = "INSERT INTO Market (instrType, quantity, price, seller) VALUES (?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(2, Integer.parseInt(qty));
            pstmt.setInt(3, Integer.parseInt(price));
            pstmt.setString(1, instrument);
            pstmt.setString(4, brokerId);
            int i = pstmt.executeUpdate();
            System.out.println(i + " records inserted");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            // finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                System.out.println(se2);
            } // nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                System.out.println(se);
            }
            try {
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException se) {
                System.out.println(se);
            }
        }

    }

}
