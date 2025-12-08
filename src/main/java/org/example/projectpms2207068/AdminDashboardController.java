package org.example.projectpms2207068;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class AdminDashboardController {

    @FXML
    private AnchorPane dashboardView;

    @FXML
    private AnchorPane addMedicinesView;

    @FXML
    private AnchorPane purchaseMedicinesView;

    @FXML
    private Button dashboardNavBtn;

    @FXML
    private Button addMedicinesNavBtn;

    @FXML
    private Button purchaseNavBtn;

    @FXML
    private Button signOutBtn;

    @FXML
    private TextField medicineIdField;
    @FXML
    private TextField brandNameField;
    @FXML
    private TextField productNameField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private Button importBtn;
    @FXML
    private Button addBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private TextField searchField;
    @FXML
    private TableView<MedicineData> medicineTable;
    @FXML
    private TableColumn<MedicineData, String> colMedicineId;
    @FXML
    private TableColumn<MedicineData, String> colBrandName;
    @FXML
    private TableColumn<MedicineData, String> colProductName;
    @FXML
    private TableColumn<MedicineData, String> colType;
    @FXML
    private TableColumn<MedicineData, Double> colPrice;
    @FXML
    private TableColumn<MedicineData, String> colStatus;
    @FXML
    private TableColumn<MedicineData, String> colDate;


    @FXML
    public void initialize() {
        dashboardNavBtn.setOnAction(e -> showDashboardView());
        addMedicinesNavBtn.setOnAction(e -> showAddMedicinesView());
        purchaseNavBtn.setOnAction(e -> showPurchaseView());
        signOutBtn.setOnAction(e -> handleSignOut());
        setupAddMedicinesView();
    }
    private void setupAddMedicinesView() {
        ObservableList<String> statusList = FXCollections.observableArrayList("Available", "Unavailable");
        statusCombo.setItems(statusList);

        ObservableList<String> typeList = FXCollections.observableArrayList("Antibiotics", "Painkillers", "Cream", "Syrup");
        typeCombo.setItems(typeList);

        colMedicineId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        colBrandName.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        addBtn.setOnAction(e -> System.out.println("Add Button Clicked"));
        clearBtn.setOnAction(e -> clearForm());
    }

    private void clearForm() {
        medicineIdField.clear();
        brandNameField.clear();
        productNameField.clear();
        priceField.clear();
        statusCombo.getSelectionModel().clearSelection();
        typeCombo.getSelectionModel().clearSelection();
    }


    private void showDashboardView() {
        dashboardView.setVisible(true);
        addMedicinesView.setVisible(false);
        purchaseMedicinesView.setVisible(false);
    }

    private void showAddMedicinesView() {
        dashboardView.setVisible(false);
        addMedicinesView.setVisible(true);
        purchaseMedicinesView.setVisible(false);
    }

    private void showPurchaseView() {
        dashboardView.setVisible(false);
        addMedicinesView.setVisible(false);
        purchaseMedicinesView.setVisible(true);
    }

    private void handleSignOut() {
        try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
             Parent root = loader.load();
             Stage loginStage = new Stage();
             loginStage.setScene(new Scene(root));
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

        public MedicineData(String medicineId, String brandName, String productName, String type, Double price, String status, String date) {
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
