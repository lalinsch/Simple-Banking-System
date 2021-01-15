package sqlconnection;

import banking.Card;

import java.sql.*;

public class Database {
    private static String url;

    public Database(String url) {
        Database.url = "jdbc:sqlite:" + url;
    }

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return conn;
    }

    public void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS accounts (\n "
                + "  id INTEGER PRIMARY KEY, \n"
                + "  number VARCHAR(20), \n"
                + "  pin VARCHAR(10), \n"
                + "  balance INTEGER DEFAULT 0\n"
                + ");";
        try (Connection conn = this.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void insert(Card card) {
        String number = card.getNumber();
        String pin = card.getPin();
        String sql = "INSERT INTO accounts (number, pin) VALUES (?, ?);";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getBalance(String number) {
        String sql = "SELECT * FROM accounts WHERE number = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt("balance");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public void addIncome(String number, int amount) {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE number = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, number);
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public boolean cardExists(String number) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE number = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean found = rs.getBoolean(1);
                    if (found) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean isCorrectPIN(String number, String pin) {
        String sql = "SELECT * FROM accounts WHERE number = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.getString("pin").equals(pin)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void doTransfer(String senderAccount, String recipientAccount, int amount) {
        String withdrawAmount = "UPDATE accounts SET balance = balance - ? WHERE number = ?; ";
        String depositAmount = "UPDATE accounts SET balance = balance + ? WHERE number = ?";
        try (Connection conn = this.connect(); PreparedStatement withdraw = conn.prepareStatement(withdrawAmount); PreparedStatement deposit = conn.prepareStatement(depositAmount)) {
            withdraw.setInt(1, amount);
            withdraw.setString(2, senderAccount);
            withdraw.executeUpdate();

            deposit.setInt(1, amount);
            deposit.setString(2, recipientAccount);
            deposit.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAccount(String currentUser) {
        String sql = "DELETE FROM accounts WHERE number = ?";
        try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUser);
            pstmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
