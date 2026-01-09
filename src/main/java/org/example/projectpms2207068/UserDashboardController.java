package org.example.projectpms2207068;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Optional;

public class UserDashboardController {

    @FXML public Button logoutbtn;
    @FXML private Button homeBtn;
    @FXML private Button shopBtn;
    @FXML private Button aboutBtn;
    @FXML private Button accountBtn;

    @FXML private StackPane contentArea;

    @FXML private AnchorPane homeView;
    @FXML private AnchorPane shopView;
    @FXML private AnchorPane aboutView;
    @FXML private AnchorPane accountView;

    // HOME
    @FXML private TableView<DatabaseHandler.OrderRow> purchaseTable;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Integer> colPHOrderId;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colPHMedicineId;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colPHProductName;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Integer> colPHQty;
    @FXML private TableColumn<DatabaseHandler.OrderRow, Double> colPHTotal;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colPHDate;
    @FXML private TableColumn<DatabaseHandler.OrderRow, String> colPHStatus;

    // SHOP
    @FXML private TableView<DatabaseHandler.MedicineRow> shopMedicineTable;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colSMedicineId;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colSBrand;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colSProduct;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colSType;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, Double> colSPrice;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, Integer> colSQuantity;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colSStatus;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, String> colSDate;
    @FXML private TableColumn<DatabaseHandler.MedicineRow, Void> colSBuy;

    @FXML private Label accountUsernameLabel;

    private String currentUsername;

    @FXML
    public void initialize() {
        // home columns
        colPHOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPHMedicineId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        colPHProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colPHQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colPHTotal.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        colPHDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colPHStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // shop columns
        colSMedicineId.setCellValueFactory(new PropertyValueFactory<>("medicineId"));
        colSBrand.setCellValueFactory(new PropertyValueFactory<>("brandName"));
        colSProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colSDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // BUY button column
        colSBuy.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("BUY");
            {
                btn.setStyle("-fx-background-color: green; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    DatabaseHandler.MedicineRow m = getTableView().getItems().get(getIndex());
                    handleBuy(m);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        homeBtn.setOnAction(e -> showHome());
        shopBtn.setOnAction(e -> showShop());
        aboutBtn.setOnAction(e -> showAbout());
        accountBtn.setOnAction(e -> showAccount());
        logoutbtn.setOnAction(e -> handleLogout());

        showHome();
        loadShopMedicines();
    }

    public void setUsername(String username) {
        this.currentUsername = username;
        accountUsernameLabel.setText(username);
        loadOrderHistory();
    }

    private void handleBuy(DatabaseHandler.MedicineRow m) {
        if (currentUsername == null || currentUsername.isEmpty()) {
            showInfo("Error", "User not set. Please login again.");
            return;
        }

        int availableNow = DatabaseHandler.getMedicineQuantity(m.getMedicineId());
        if (availableNow <= 0) {
            showInfo("Not Available", "Stock is 0. You cannot buy this medicine now.");
            loadShopMedicines();
            return;
        }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Buy Medicine");
        dialog.setHeaderText("Enter Quantity for: " + m.getProductName());
        dialog.setContentText("Quantity:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        int qty;
        try {
            qty = Integer.parseInt(result.get().trim());
        } catch (Exception ex) {
            showInfo("Error", "Quantity must be a number.");
            return;
        }

        if (qty <= 0) {
            showInfo("Error", "Quantity must be greater than 0.");
            return;
        }

        // Stock limit check
        if (qty > availableNow) {
            showInfo("Low Stock", "You can buy max " + availableNow + " quantity.");
            return;
        }

        double unitPrice = m.getPrice();
        double total = unitPrice * qty;

        boolean ok = DatabaseHandler.insertOrder(
                currentUsername,
                m.getMedicineId(),
                m.getProductName(),
                qty,
                unitPrice,
                total,
                LocalDate.now().toString(),
                "Pending"
        );

        if (!ok) {
            showInfo("Error", "Order failed.");
            return;
        }

        showInfo("Order Placed", "Order sent to Admin.\nTotal price: " + total);
        loadOrderHistory();
    }

    private void loadOrderHistory() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            purchaseTable.getItems().clear();
            return;
        }
        purchaseTable.setItems(DatabaseHandler.getOrdersByUser(currentUsername));
    }

    private void loadShopMedicines() {
        shopMedicineTable.setItems(DatabaseHandler.getAllMedicinesList());
    }

    private void showHome() {
        homeView.setVisible(true);
        shopView.setVisible(false);
        aboutView.setVisible(false);
        accountView.setVisible(false);
        loadOrderHistory();
    }

    private void showShop() {
        homeView.setVisible(false);
        shopView.setVisible(true);
        aboutView.setVisible(false);
        accountView.setVisible(false);
        loadShopMedicines();
    }

    private void showAbout() {
        homeView.setVisible(false);
        shopView.setVisible(false);
        aboutView.setVisible(true);
        accountView.setVisible(false);
    }

    private void showAccount() {
        homeView.setVisible(false);
        shopView.setVisible(false);
        aboutView.setVisible(false);
        accountView.setVisible(true);
    }

    private void handleLogout() {
        try {
            Stage currentStage = (Stage) logoutbtn.getScene().getWindow();
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
