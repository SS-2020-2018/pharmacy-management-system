package org.example.projectpms2207068;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML private AnchorPane dashboardView;
    @FXML private AnchorPane addMedicinesView;
    @FXML private AnchorPane orderMedicinesView;

    @FXML private Button dashboardNavBtn;
    @FXML private Button addMedicinesNavBtn;
    @FXML private Button orderNavBtn;
    @FXML private Button signOutBtn;

    @FXML private TextField medicineIdField;
    @FXML private TextField brandNameField;
    @FXML private TextField productNameField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> typeCombo;

    @FXML private Button addBtn;
    @FXML private Button clearBtn;
    @FXML private Button deleteBtn;   // NEW

    @FXML private TableView<MedicineData> medicineTable;
    @FXML private TableColumn<MedicineData, String> colMedicineId;
    @FXML private TableColumn<MedicineData, String> colBrandName;
    @FXML private TableColumn<MedicineData, String> colProductName;
    @FXML private TableColumn<MedicineData, String> colType;
    @FXML private TableColumn<MedicineData, Double> colPrice;
    @FXML private TableColumn<MedicineData, String> colStatus;
    @FXML private TableColumn<MedicineData, String> colDate;

    @FXML private Label availableMedicinesLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalCustomersLabel;

    private final ObservableList<MedicineData> medicineList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        statusCombo.getItems().addAll("Available", "Unavailable");
        typeCombo.getItems().addAll("Antibiotic", "PainKiller", "Syrup", "Crim", "Drop");

        colMedicineId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        colBrandName.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        medicineTable.setItems(medicineList);
        medicineTable.getColumns().forEach(col -> col.setReorderable(false));

        dashboardNavBtn.setOnAction(e -> showDashboardView());
        addMedicinesNavBtn.setOnAction(e -> showAddMedicinesView());
        orderNavBtn.setOnAction(e -> showPurchaseView());
        signOutBtn.setOnAction(e -> handleSignOut());

        addBtn.setOnAction(e -> handleAddMedicine());
        clearBtn.setOnAction(e -> clearForm());
        deleteBtn.setOnAction(e -> handleDeleteMedicine());   // NEW

        showDashboardView();
        updateSummaryCards();
    }

    private void handleAddMedicine() {
        String id = medicineIdField.getText();
        String brand = brandNameField.getText();
        String product = productNameField.getText();
        String priceText = priceField.getText();
        String status = statusCombo.getValue();
        String type = typeCombo.getValue();

        if (id.isEmpty() || brand.isEmpty() || product.isEmpty()
                || priceText.isEmpty() || status == null || type == null) {
            System.out.println("Fill all fields.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            System.out.println("Price must be a number.");
            return;
        }

        String date = "2025-01-01";

        MedicineData data = new MedicineData(id, brand, product, type, price, status, date);
        medicineList.add(data);

        clearForm();
        updateSummaryCards();
    }

    // NEW: delete selected medicine
    private void handleDeleteMedicine() {
        MedicineData selected = medicineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Select a row to delete.");
            return;
        }
        medicineList.remove(selected);
        updateSummaryCards();
    }

    private void clearForm() {
        medicineIdField.clear();
        brandNameField.clear();
        productNameField.clear();
        priceField.clear();
        statusCombo.getSelectionModel().clearSelection();
        typeCombo.getSelectionModel().clearSelection();
    }

    private void updateSummaryCards() {
        int available = (int) medicineList.stream()
                .filter(m -> "Available".equalsIgnoreCase(m.getStatus()))
                .count();

        availableMedicinesLabel.setText(String.valueOf(available));
        totalIncomeLabel.setText("0");
        totalCustomersLabel.setText("0");
    }

    private void showDashboardView() {
        dashboardView.setVisible(true);
        addMedicinesView.setVisible(false);
        orderMedicinesView.setVisible(false);
    }

    private void showAddMedicinesView() {
        dashboardView.setVisible(false);
        addMedicinesView.setVisible(true);
        orderMedicinesView.setVisible(false);
    }

    private void showPurchaseView() {
        dashboardView.setVisible(false);
        addMedicinesView.setVisible(false);
        orderMedicinesView.setVisible(true);
    }

    private void handleSignOut() {
        try {
            Stage currentStage = (Stage) signOutBtn.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Welcome to Pharmacy Management System");
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MedicineData {
        private String medicineId;
        private String brandName;
        private String productName;
        private String type;
        private Double price;
        private String status;
        private String date;

        public MedicineData(String medicineId, String brandName, String productName,
                            String type, Double price, String status, String date) {
            this.medicineId = medicineId;
            this.brandName = brandName;
            this.productName = productName;
            this.type = type;
            this.price = price;
            this.status = status;
            this.date = date;
        }

        public String getMedicineId() { return medicineId; }
        public String getBrandName() { return brandName; }
        public String getProductName() { return productName; }
        public String getType() { return type; }
        public Double getPrice() { return price; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
}
