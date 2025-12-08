package org.example.projectpms2207068;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    // --- FXML Injections ---

    // Panes
    @FXML private VBox adminPane;
    @FXML private VBox userPane;
    @FXML private VBox registerPane;

    // Side Buttons
    @FXML private Button adminBtn;
    @FXML private Button userBtn;

    // Admin Form
    @FXML private TextField adminUserField;
    @FXML private PasswordField adminPassField;
    @FXML private Button adminLoginBtn;

    // User Form
    @FXML private TextField userIdField;
    @FXML private PasswordField userPassField;
    @FXML private Button userLoginBtn;
    @FXML private Hyperlink registerLink;

    // Register Form
    @FXML private TextField regUserIdField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private Button registerBtn;
    @FXML private Hyperlink backToLoginLink;


    @FXML
    public void initialize() {
        showAdminForm();


        adminBtn.setOnAction(e -> showAdminForm());
        userBtn.setOnAction(e -> showUserForm());


        adminLoginBtn.setOnAction(e -> handleAdminLogin());
        userLoginBtn.setOnAction(e -> handleUserLogin());


        registerLink.setOnAction(e -> showRegisterPane());
        backToLoginLink.setOnAction(e -> showUserForm());
        registerBtn.setOnAction(e -> handleRegistration());
    }


    private void showAdminForm() {
        adminPane.setVisible(true);
        userPane.setVisible(false);
        registerPane.setVisible(false);
    }

    private void showUserForm() {
        adminPane.setVisible(false);
        userPane.setVisible(true);
        registerPane.setVisible(false);
    }

    private void showRegisterPane() {
        adminPane.setVisible(false);
        userPane.setVisible(false);
        registerPane.setVisible(true);
    }


    private void handleAdminLogin() {
        String username = adminUserField.getText();
        String password = adminPassField.getText();

        // Hardcoded Admin Check
        if ("admin".equals(username) && "admin123".equals(password)) {
            System.out.println("Admin Login Successful!");
            loadScene("AdminDashboard.fxml", "Pharmacy Management System - Admin Dashboard");
        } else {
            showAlert("Login Failed", "Invalid Admin Credentials.");
        }
    }

    private void handleUserLogin() {
        String userId = userIdField.getText();
        String password = userPassField.getText();

        if (!userId.isEmpty() && !password.isEmpty()) {
            System.out.println("User Login Successful!");
            loadScene("UserDashboard.fxml", "Pharmacy - Home");
        } else {
            showAlert("Login Failed", "Please enter both User ID and Password.");
        }
    }

    private void handleRegistration() {
        String newId = regUserIdField.getText();
        String newPass = regPasswordField.getText();
        String confirmPass = regConfirmPasswordField.getText();

        if (newId.isEmpty() || newPass.isEmpty()) {
            showAlert("Error", "Please fill all registration fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        showAlert("Success", "Registration Successful! You can now login.");
        showUserForm();
    }


    private void loadScene(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Stage stage = (Stage) adminLoginBtn.getScene().getWindow();
            Scene scene = new Scene(root);
            String css = getClass().getResource("pharmacy-style.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load " + fxmlFileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert("Error", "CSS or FXML file not found.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
