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
        VBox card = new VBox(14);
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

        // Email Form Group
        VBox emailGroup = new VBox(4);
        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("label-field");
        TextField emailField = new TextField();
        emailField.setPromptText("e.g. alice@ecomflow.com");
        Label emailErr = new Label();
        emailErr.getStyleClass().add("error-text");
        emailErr.setVisible(false);
        emailGroup.getChildren().addAll(emailLabel, emailField, emailErr);

        // Password Form Group
        VBox passGroup = new VBox(4);
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("label-field");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");
        Label passErr = new Label();
        passErr.getStyleClass().add("error-text");
        passErr.setVisible(false);
        passGroup.getChildren().addAll(passLabel, passField, passErr);

        // Clear errors as user types
        emailField.textProperty().addListener((obs, oldV, newV) -> {
            emailErr.setVisible(false);
            emailField.getStyleClass().remove("input-error");
        });
        passField.textProperty().addListener((obs, oldV, newV) -> {
            passErr.setVisible(false);
            passField.getStyleClass().remove("input-error");
        });

        // Action Buttons
        Button loginBtn = new Button("Sign In");
        loginBtn.getStyleClass().add("btn-primary");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Button registerBtn = new Button("Create New Account");
        registerBtn.getStyleClass().add("btn-outline");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        // Quick Demo Credentials — Admin only (customers register via RegisterView)
        VBox demoBox = new VBox(8);
        demoBox.setAlignment(Pos.CENTER);
        demoBox.setPadding(new Insets(10, 0, 0, 0));

        Label demoLabel = new Label("Quick sign-in (Admin):");
        demoLabel.getStyleClass().add("text-muted");

        Button fillAdmin = new Button("Admin Demo");
        fillAdmin.getStyleClass().add("btn-chip");
        fillAdmin.setOnAction(e -> {
            emailField.setText("admin@ecomflow.com");
            passField.setText("adminPass");
            emailErr.setVisible(false);
            passErr.setVisible(false);
        });

        demoBox.getChildren().addAll(demoLabel, fillAdmin);

        // Event Handlers with Inline Validation Check
        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();

            boolean hasError = false;

            if (email == null || email.trim().isEmpty()) {
                emailErr.setText("Please enter your email address.");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("input-error")) emailField.getStyleClass().add("input-error");
                hasError = true;
            } else if (!email.contains("@")) {
                emailErr.setText("Please enter a valid email containing '@'.");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("input-error")) emailField.getStyleClass().add("input-error");
                hasError = true;
            }

            if (password == null || password.trim().isEmpty()) {
                passErr.setText("Please enter your password.");
                passErr.setVisible(true);
                if (!passField.getStyleClass().contains("input-error")) passField.getStyleClass().add("input-error");
                hasError = true;
            }

            if (hasError) return;

            try {
                User user = authService.login(email, password);
                app.handleUserNavigation(user);
            } catch (InvalidLoginException ex) {
                GuiUtils.showError("Authentication Failed", ex.getMessage());
            } catch (Exception ex) {
                GuiUtils.showError("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        passField.setOnAction(e -> loginBtn.fire());
        registerBtn.setOnAction(e -> app.showRegisterView());

        card.getChildren().addAll(
                header,
                emailGroup,
                passGroup,
                loginBtn, registerBtn,
                demoBox
        );

        rootPane.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    public Parent getView() {
        return rootPane;
    }
}
