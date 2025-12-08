package org.example.projectpms2207068;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class UserDashboardController {

    @FXML
    private Button homeBtn;
    @FXML
    private Button shopBtn;
    @FXML
    private Button wishlistBtn;
    @FXML
    private Button aboutBtn;
    @FXML
    private Button managementBtn;
    @FXML
    private Button contactBtn;
    @FXML
    private Button careerBtn;
    @FXML
    private Button accountBtn;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        setActiveButton(homeBtn);


        homeBtn.setOnAction(e -> {
            setActiveButton(homeBtn);
            System.out.println("Home Clicked");

        });

        shopBtn.setOnAction(e -> {
            setActiveButton(shopBtn);
            System.out.println("Shop Clicked");
        });

        wishlistBtn.setOnAction(e -> {
            setActiveButton(wishlistBtn);
            System.out.println("Wishlist Clicked");
        });

    }

    private void setActiveButton(Button activeButton) {
        resetButtonStyle(homeBtn);
        resetButtonStyle(shopBtn);
        resetButtonStyle(wishlistBtn);
        resetButtonStyle(aboutBtn);
        resetButtonStyle(managementBtn);
        resetButtonStyle(contactBtn);
        resetButtonStyle(careerBtn);
        resetButtonStyle(accountBtn);
        activeButton.getStyleClass().remove("nav-button");
        activeButton.getStyleClass().add("nav-button-active");
    }

    private void resetButtonStyle(Button btn) {
        btn.getStyleClass().remove("nav-button-active");
        if (!btn.getStyleClass().contains("nav-button")) {
            btn.getStyleClass().add("nav-button");
        }
    }
}
