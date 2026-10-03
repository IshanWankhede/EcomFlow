package com.ecomflow.gui;

import com.ecomflow.exceptions.InvalidLoginException;
import com.ecomflow.model.User;
import com.ecomflow.service.AuthenticationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {
    private final MainApp app;
    private final AuthenticationService authService;
    private final StackPane rootPane;

    public LoginView(MainApp app, AuthenticationService authService) {
        this.app = app;
        this.authService = authService;
        this.rootPane = new StackPane();
        buildUI();
    }

    private void buildUI() {
        rootPane.getStyleClass().add("app-container");
        rootPane.setPadding(new Insets(40));

        // Center Login Card
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(440);
        card.setAlignment(Pos.CENTER_LEFT);

        // Header
        VBox header = new VBox(6);
        header.setAlignment(Pos.CENTER);
        
        Label brand = new Label("🛒 EcomFlow");
        brand.getStyleClass().add("brand-title");

        Label subtitle = new Label("Smart E-Commerce Management System");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(brand, subtitle);

        // Form Fields
        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("label-field");
        TextField emailField = new TextField();
        emailField.setPromptText("e.g. alice@ecomflow.com");

        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("label-field");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");

        // Action Buttons
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Button registerBtn = new Button("Create New Account");
        registerBtn.getStyleClass().add("btn-outline");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        // Quick Demo Credentials Section
        VBox demoBox = new VBox(8);
        demoBox.setAlignment(Pos.CENTER);
        demoBox.setPadding(new Insets(10, 0, 0, 0));

        Label demoLabel = new Label("Quick Demo Credentials:");
        demoLabel.getStyleClass().add("text-muted");

        HBox demoChips = new HBox(10);
        demoChips.setAlignment(Pos.CENTER);

        Button fillCustomer = new Button("Customer Demo");
        fillCustomer.getStyleClass().add("btn-chip");
        fillCustomer.setOnAction(e -> {
            emailField.setText("alice@ecomflow.com");
            passField.setText("pass123");
        });

        Button fillAdmin = new Button("Admin Demo");
        fillAdmin.getStyleClass().add("btn-chip");
        fillAdmin.setOnAction(e -> {
            emailField.setText("admin@ecomflow.com");
            passField.setText("adminPass");
        });

        demoChips.getChildren().addAll(fillCustomer, fillAdmin);
        demoBox.getChildren().addAll(demoLabel, demoChips);

        // Event Handlers
        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();

            try {
                User user = authService.login(email, password);
                app.handleUserNavigation(user);
            } catch (InvalidLoginException ex) {
                showAlert(Alert.AlertType.ERROR, "Authentication Failed", ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        passField.setOnAction(e -> loginBtn.fire());
        registerBtn.setOnAction(e -> app.showRegisterView());

        card.getChildren().addAll(
                header,
                emailLabel, emailField,
                passLabel, passField,
                loginBtn, registerBtn,
                demoBox
        );

        rootPane.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getView() {
        return rootPane;
    }
}
