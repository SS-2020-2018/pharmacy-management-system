package org.example.projectpms2207068;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:mydb.db";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDB() {

        String usersSql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL"
                + ");";

        String medicinesSql = "CREATE TABLE IF NOT EXISTS medicines ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "medicine_id TEXT NOT NULL UNIQUE, "
                + "brand_name TEXT NOT NULL, "
                + "product_name TEXT NOT NULL, "
                + "type TEXT NOT NULL, "
                + "price REAL NOT NULL, "
                + "quantity INTEGER NOT NULL DEFAULT 0, "
                + "status TEXT NOT NULL, "
                + "date TEXT NOT NULL"
                + ");";

        String ordersSql = "CREATE TABLE IF NOT EXISTS orders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL, "
                + "medicine_id TEXT NOT NULL, "
                + "product_name TEXT NOT NULL, "
                + "qty INTEGER NOT NULL, "
                + "unit_price REAL NOT NULL, "
                + "total_price REAL NOT NULL, "
                + "date TEXT NOT NULL, "
                + "status TEXT NOT NULL"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersSql);
            stmt.execute(medicinesSql);
            stmt.execute(ordersSql);

            // If your old DB doesn't have quantity column, try to add it
            try {
                stmt.execute("ALTER TABLE medicines ADD COLUMN quantity INTEGER NOT NULL DEFAULT 0");
            } catch (SQLException ignored) {
                // column already exists
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= USERS =================
    public static boolean createUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserCount() {
        String sql = "SELECT COUNT(*) AS c FROM users";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("c") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ================= MEDICINES =================
    public static boolean insertMedicine(String medicineId, String brandName, String productName,
                                         String type, double price, int quantity, String status, String date) {

        String finalStatus = (quantity > 0) ? "Available" : "Unavailable";

        // if admin chooses status manually, keep it only if consistent
        if ("Available".equalsIgnoreCase(status) && quantity <= 0) finalStatus = "Unavailable";
        if ("Unavailable".equalsIgnoreCase(status) && quantity > 0) finalStatus = "Available";

        String sql = "INSERT INTO medicines(medicine_id, brand_name, product_name, type, price, quantity, status, date) "
                + "VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineId);
            ps.setString(2, brandName);
            ps.setString(3, productName);
            ps.setString(4, type);
            ps.setDouble(5, price);
            ps.setInt(6, quantity);
            ps.setString(7, finalStatus);
            ps.setString(8, date);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteMedicineById(String medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<MedicineRow> getAllMedicinesList() {
        ObservableList<MedicineRow> list = FXCollections.observableArrayList();
        String sql = "SELECT medicine_id, brand_name, product_name, type, price, quantity, status, date FROM medicines";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new MedicineRow(
                        rs.getString("medicine_id"),
                        rs.getString("brand_name"),
                        rs.getString("product_name"),
                        rs.getString("type"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getString("status"),
                        rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static int getMedicineQuantity(String medicineId) {
        String sql = "SELECT quantity FROM medicines WHERE medicine_id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("quantity") : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean reduceMedicineQuantity(String medicineId, int qtyToReduce) {
        // reduce qty if enough stock, then auto-update status
        String getSql = "SELECT quantity FROM medicines WHERE medicine_id = ?";
        String updateSql = "UPDATE medicines SET quantity = ?, status = ? WHERE medicine_id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            int currentQty;
            try (PreparedStatement ps = conn.prepareStatement(getSql)) {
                ps.setString(1, medicineId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    currentQty = rs.getInt("quantity");
                }
            }

            if (qtyToReduce <= 0 || currentQty < qtyToReduce) {
                conn.rollback();
                return false;
            }

            int newQty = currentQty - qtyToReduce;
            String newStatus = (newQty > 0) ? "Available" : "Unavailable";

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, newQty);
                ps.setString(2, newStatus);
                ps.setString(3, medicineId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getAvailableMedicinesCount() {
        String sql = "SELECT COUNT(*) AS c FROM medicines WHERE quantity > 0";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("c") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ================= ORDERS =================
    public static boolean insertOrder(String username,
                                      String medicineId,
                                      String productName,
                                      int qty,
                                      double unitPrice,
                                      double totalPrice,
                                      String date,
                                      String status) {

        String sql = "INSERT INTO orders(username, medicine_id, product_name, qty, unit_price, total_price, date, status) "
                + "VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, medicineId);
            ps.setString(3, productName);
            ps.setInt(4, qty);
            ps.setDouble(5, unitPrice);
            ps.setDouble(6, totalPrice);
            ps.setString(7, date);
            ps.setString(8, status);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Admin should see only Pending; when Completed it disappears from admin list but still stays for user history
    public static ObservableList<OrderRow> getPendingOrders() {
        ObservableList<OrderRow> list = FXCollections.observableArrayList();
        String sql = "SELECT id, username, medicine_id, product_name, qty, unit_price, total_price, date, status "
                + "FROM orders WHERE status = 'Pending' ORDER BY id DESC";

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new OrderRow(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("medicine_id"),
                        rs.getString("product_name"),
                        rs.getInt("qty"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total_price"),
                        rs.getString("date"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static ObservableList<OrderRow> getOrdersByUser(String username) {
        ObservableList<OrderRow> list = FXCollections.observableArrayList();
        String sql = "SELECT id, username, medicine_id, product_name, qty, unit_price, total_price, date, status "
                + "FROM orders WHERE username = ? ORDER BY id DESC";

        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderRow(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("medicine_id"),
                            rs.getString("product_name"),
                            rs.getInt("qty"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("total_price"),
                            rs.getString("date"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getTotalIncomeCompleted() {
        String sql = "SELECT COALESCE(SUM(total_price), 0) AS s FROM orders WHERE status = 'Completed'";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getDouble("s") : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // ================= Models =================
    public static class MedicineRow {
        private final String medicineId, brandName, productName, type, status, date;
        private final Double price;
        private final Integer quantity;

        public MedicineRow(String medicineId, String brandName, String productName,
                           String type, Double price, Integer quantity, String status, String date) {
            this.medicineId = medicineId;
            this.brandName = brandName;
            this.productName = productName;
            this.type = type;
            this.price = price;
            this.quantity = quantity;
            this.status = status;
            this.date = date;
        }

        public String getMedicineId() { return medicineId; }
        public String getBrandName() { return brandName; }
        public String getProductName() { return productName; }
        public String getType() { return type; }
        public Double getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }

    public static class OrderRow {
        private final Integer id, qty;
        private final String username, medicineId, productName, date, status;
        private final Double unitPrice, totalPrice;

        public OrderRow(Integer id, String username, String medicineId, String productName,
                        Integer qty, Double unitPrice, Double totalPrice, String date, String status) {
            this.id = id;
            this.username = username;
            this.medicineId = medicineId;
            this.productName = productName;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.date = date;
            this.status = status;
        }

        public Integer getId() { return id; }
        public String getUsername() { return username; }
        public String getMedicineId() { return medicineId; }
        public String getProductName() { return productName; }
        public Integer getQty() { return qty; }
        public Double getUnitPrice() { return unitPrice; }
        public Double getTotalPrice() { return totalPrice; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
}
