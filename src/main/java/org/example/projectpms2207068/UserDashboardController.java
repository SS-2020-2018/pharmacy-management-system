package org.example.projectpms2207068;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UserDashboardController {

    @FXML public Button logoutbtn;
    @FXML private Button homeBtn;
    @FXML private Button shopBtn;
    @FXML private Button aboutBtn;
    @FXML private Button accountBtn;
    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        homeBtn.setOnAction(e -> System.out.println("Home clicked"));
        shopBtn.setOnAction(e -> System.out.println("Shop clicked"));
        aboutBtn.setOnAction(e -> System.out.println("About clicked"));
        accountBtn.setOnAction(e -> System.out.println("Account clicked"));

        logoutbtn.setOnAction(e -> handleLogout());
    }

    private void handleLogout() {
        try {
            Stage currentStage = (Stage) logoutbtn.getScene().getWindow();
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
}
