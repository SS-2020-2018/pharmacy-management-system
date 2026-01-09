package org.example.projectpms2207068;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML private VBox adminPane;
    @FXML private VBox userPane;
    @FXML private VBox registerPane;

    @FXML private Button adminBtn;
    @FXML private Button userBtn;

    @FXML private TextField adminUserField;
    @FXML private PasswordField adminPassField;
    @FXML private Button adminLoginBtn;

    @FXML private TextField userIdField;
    @FXML private PasswordField userPassField;
    @FXML private Button userLoginBtn;

    @FXML private Hyperlink registerLink;

    @FXML private TextField regUserIdField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private Button registerBtn;
    @FXML private Hyperlink backToLoginLink;

    @FXML
    public void initialize() {
        DatabaseHandler.initializeDB();

        showUserForm();

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
        String username = adminUserField.getText().trim();
        String password = adminPassField.getText().trim();

        if ("123".equals(username) && "123".equals(password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) adminLoginBtn.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Admin Dashboard");
                stage.centerOnScreen();
                stage.show();
            } catch (Exception e) {
                showAlert("Error", "Could not load AdminDashboard.fxml");
            }
        } else {
            showAlert("Login Failed", "Invalid Admin Username or Password.");
        }
    }

    private void handleUserLogin() {
        String userId = userIdField.getText().trim();
        String password = userPassField.getText().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "Enter Username and Password.");
            return;
        }

        if (!DatabaseHandler.validateLogin(userId, password)) {
            showAlert("Login Failed", "Invalid Username or Password.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            UserDashboardController controller = loader.getController();
            controller.setUsername(userId);

            Stage stage = (Stage) userLoginBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Dashboard");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            showAlert("Error", "Could not load UserDashboard.fxml");
        }
    }

    private void handleRegistration() {
        String newId = regUserIdField.getText().trim();
        String newPass = regPasswordField.getText().trim();
        String confirmPass = regConfirmPasswordField.getText().trim();

        if (newId.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showAlert("Error", "Please fill all registration fields.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        boolean created = DatabaseHandler.createUser(newId, newPass);
        if (created) {
            showAlert("Success", "Registration Successful! You can now login.");
            regUserIdField.clear();
            regPasswordField.clear();
            regConfirmPasswordField.clear();
            showUserForm();
        } else {
            showAlert("Error", "Registration Failed. Username might already exist.");
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
