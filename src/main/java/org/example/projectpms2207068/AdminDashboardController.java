package org.example.projectpms2207068;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AdminDashboardController {

    @FXML private AnchorPane dashboardView;
    @FXML private AnchorPane addMedicinesView;
    @FXML private AnchorPane orderMedicinesView;

    @FXML private Button dashboardNavBtn;
    @FXML private Button addMedicinesNavBtn;
    @FXML private Button orderNavBtn;
    @FXML private Button signOutBtn;

    // Form
    @FXML private TextField medicineIdField;
    @FXML private TextField brandNameField;
    @FXML private TextField productNameField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> typeCombo;

    @FXML private Button addBtn;
    @FXML private Button clearBtn;
    @FXML private Button deleteBtn;

    // Medicine table
    @FXML private TableView<DatabaseHandler.MedicineRow> medicineTable;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colMedicineId;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colBrandName;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colProductName;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colType;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, Double> colPrice;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, Integer> colQuantity;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colStatus;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colDate;

    // Dashboard
    @FXML private Label availableMedicinesLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalCustomersLabel;

    // Orders (Pending only)
    @FXML private TableView<DatabaseHandler.OrderRow> orderTable;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Integer> colOrderId;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colOrderUser;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colOrderMedicineId;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colOrderProduct;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Integer> colOrderQty;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Double> colOrderUnitPrice;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Double> colOrderTotal;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colOrderDate;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colOrderStatus;

    @FXML private Button refreshOrdersBtn;
    @FXML private Button completeOrderBtn;

    private final ObservableList<DatabaseHandler.MedicineRow> medicineList = FXCollections.observableArrayList();
    private final ObservableList<DatabaseHandler.OrderRow> ordersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        DatabaseHandler.initializeDB();

        statusCombo.getItems().addAll("Available", "Unavailable");
        typeCombo.getItems().addAll("Antibiotic", "PainKiller", "Syrup", "Crim", "Drop");

        // Medicines columns
        colMedicineId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        colBrandName.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        medicineTable.setItems(medicineList);

        // Orders columns
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colOrderMedicineId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        colOrderProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colOrderQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colOrderUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOrderTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOrderStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        orderTable.setItems(ordersList);

        // Nav
        dashboardNavBtn.setOnAction(e -> showDashboardView());
        addMedicinesNavBtn.setOnAction(e -> showAddMedicinesView());
        orderNavBtn.setOnAction(e -> showOrderView());
        signOutBtn.setOnAction(e -> handleSignOut());

        // Medicine actions
        addBtn.setOnAction(e -> handleAddMedicine());
        clearBtn.setOnAction(e -> clearForm());
        deleteBtn.setOnAction(e -> handleDeleteMedicine());

        // Order actions
        refreshOrdersBtn.setOnAction(e -> loadOrders());
        completeOrderBtn.setOnAction(e -> completeSelectedOrder());

        refreshAll();
        showDashboardView();
    }

    private void refreshAll() {
        loadMedicines();
        loadOrders();
        updateDashboardCards();
    }

    private void loadMedicines() {
        medicineList.setAll(DatabaseHandler.getAllMedicinesList());
    }

    private void loadOrders() {
        ordersList.setAll(DatabaseHandler.getPendingOrders());
    }

    private void updateDashboardCards() {
        availableMedicinesLabel.setText(String.valueOf(DatabaseHandler.getAvailableMedicinesCount()));
        totalIncomeLabel.setText(String.valueOf(DatabaseHandler.getTotalIncomeCompleted()));
        totalCustomersLabel.setText(String.valueOf(DatabaseHandler.getUserCount()));
    }

    private void handleAddMedicine() {
        String id = medicineIdField.getText().trim();
        String brand = brandNameField.getText().trim();
        String product = productNameField.getText().trim();
        String priceText = priceField.getText().trim();
        String qtyText = quantityField.getText().trim();
        String status = statusCombo.getValue();
        String type = typeCombo.getValue();

        if (id.isEmpty() || brand.isEmpty() || product.isEmpty() || priceText.isEmpty() || qtyText.isEmpty()
                || status == null || type == null) {
            showInfo("Error", "Please fill all fields.");
            return;
        }

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceText);
            qty = Integer.parseInt(qtyText);
        } catch (Exception ex) {
            showInfo("Error", "Price must be number and Quantity must be integer.");
            return;
        }

        if (price < 0 || qty < 0) {
            showInfo("Error", "Price and Quantity cannot be negative.");
            return;
        }

        boolean ok = DatabaseHandler.insertMedicine(
                id, brand, product, type, price, qty, status, LocalDate.now().toString()
        );

        if (!ok) {
            showInfo("Error", "Insert failed (Medicine ID may already exist).");
            return;
        }

        clearForm();
        refreshAll();
        showInfo("Success", "Medicine added.");
    }

    private void handleDeleteMedicine() {
        DatabaseHandler.MedicineRow selected = medicineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Error", "Select a medicine row to delete.");
            return;
        }

        boolean ok = DatabaseHandler.deleteMedicineById(selected.getMedicineId());
        if (!ok) {
            showInfo("Error", "Delete failed.");
            return;
        }

        refreshAll();
        showInfo("Success", "Medicine deleted.");
    }

    private void completeSelectedOrder() {
        DatabaseHandler.OrderRow selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Error", "Select an order first.");
            return;
        }

        // check stock again at completion time
        int availableQty = DatabaseHandler.getMedicineQuantity(selected.getMedicineId());
        if (availableQty < selected.getQty()) {
            showInfo("Stock Problem",
                    "Cannot complete. Available stock is " + availableQty + ", but order qty is " + selected.getQty());
            return;
        }

        // reduce stock
        boolean reduced = DatabaseHandler.reduceMedicineQuantity(selected.getMedicineId(), selected.getQty());
        if (!reduced) {
            showInfo("Error", "Failed to reduce medicine quantity.");
            return;
        }

        // mark order completed
        boolean ok = DatabaseHandler.updateOrderStatus(selected.getId(), "Completed");
        if (!ok) {
            showInfo("Error", "Failed to complete order.");
            return;
        }

        refreshAll();
        showInfo("Success", "Order completed. Medicine stock updated.");
    }

    private void clearForm() {
        medicineIdField.clear();
        brandNameField.clear();
        productNameField.clear();
        priceField.clear();
        quantityField.clear();
        statusCombo.getSelectionModel().clearSelection();
        typeCombo.getSelectionModel().clearSelection();
    }

    private void showDashboardView() {
        dashboardView.setVisible(true);
        addMedicinesView.setVisible(false);
        orderMedicinesView.setVisible(false);
        updateDashboardCards();
    }

    private void showAddMedicinesView() {
        dashboardView.setVisible(false);
        addMedicinesView.setVisible(true);
        orderMedicinesView.setVisible(false);
        loadMedicines();
    }

    private void showOrderView() {
        dashboardView.setVisible(false);
        addMedicinesView.setVisible(false);
        orderMedicinesView.setVisible(true);
        loadOrders();
    }

    private void handleSignOut() {
        try {
            Stage currentStage = (Stage) signOutBtn.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Welcome to Pharmacy Management System");
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
