package com.ecomflow.gui;

import com.ecomflow.model.Address;
import com.ecomflow.service.AuthenticationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RegisterView {
    private final MainApp app;
    private final AuthenticationService authService;
    private final StackPane rootPane;

    public RegisterView(MainApp app, AuthenticationService authService) {
        this.app = app;
        this.authService = authService;
        this.rootPane = new StackPane();
        buildUI();
    }

    private void buildUI() {
        rootPane.getStyleClass().add("app-container");
        rootPane.setPadding(new Insets(30));

        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setMaxWidth(520);
        card.setAlignment(Pos.CENTER_LEFT);

        // Header
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);

        Label title = new Label("Create Customer Account");
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label("Join EcomFlow to start shopping and tracking orders");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(title, subtitle);

        // 1. Name Field & Error
        VBox nameBox = new VBox(2);
        Label nameLabel = new Label("Full Name *");
        nameLabel.getStyleClass().add("label-field");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");
        Label nameErr = new Label();
        nameErr.getStyleClass().add("error-text");
        nameErr.setVisible(false);
        nameBox.getChildren().addAll(nameLabel, nameField, nameErr);

        // 2. Email Field & Error
        VBox emailBox = new VBox(2);
        Label emailLabel = new Label("Email Address *");
        emailLabel.getStyleClass().add("label-field");
        TextField emailField = new TextField();
        emailField.setPromptText("e.g. john@example.com");
        Label emailErr = new Label();
        emailErr.getStyleClass().add("error-text");
        emailErr.setVisible(false);
        emailBox.getChildren().addAll(emailLabel, emailField, emailErr);

        // 3. Password Field & Error
        VBox passBox = new VBox(2);
        Label passLabel = new Label("Password *");
        passLabel.getStyleClass().add("label-field");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Create a secure password (min 4 chars)");
        Label passErr = new Label();
        passErr.getStyleClass().add("error-text");
        passErr.setVisible(false);
        passBox.getChildren().addAll(passLabel, passField, passErr);

        // 4. Phone Field
        VBox phoneBox = new VBox(2);
        Label phoneLabel = new Label("Phone Number");
        phoneLabel.getStyleClass().add("label-field");
        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g. +91 9876543210");
        phoneBox.getChildren().addAll(phoneLabel, phoneField);

        // Clear errors as user types
        nameField.textProperty().addListener((obs, oldV, newV) -> {
            nameErr.setVisible(false);
            nameField.getStyleClass().remove("input-error");
        });
        emailField.textProperty().addListener((obs, oldV, newV) -> {
            emailErr.setVisible(false);
            emailField.getStyleClass().remove("input-error");
        });
        passField.textProperty().addListener((obs, oldV, newV) -> {
            passErr.setVisible(false);
            passField.getStyleClass().remove("input-error");
        });

        // 5. Shipping Address Section
        Label addrHeader = new Label("Shipping Address");
        addrHeader.getStyleClass().add("heading-md");
        addrHeader.setPadding(new Insets(8, 0, 0, 0));

        TextField streetField = new TextField();
        streetField.setPromptText("Street Address");

        GridPane addressGrid = new GridPane();
        addressGrid.setHgap(10);
        addressGrid.setVgap(10);

        TextField cityField = new TextField();
        cityField.setPromptText("City");

        TextField stateField = new TextField();
        stateField.setPromptText("State");

        TextField pinField = new TextField();
        pinField.setPromptText("Pincode");

        TextField countryField = new TextField("India");
        countryField.setPromptText("Country");

        addressGrid.add(cityField, 0, 0);
        addressGrid.add(stateField, 1, 0);
        addressGrid.add(pinField, 0, 1);
        addressGrid.add(countryField, 1, 1);

        // Action Buttons
        Button registerBtn = new Button("Complete Registration");
        registerBtn.getStyleClass().add("btn-primary");
        registerBtn.setMaxWidth(Double.MAX_VALUE);

        Button backBtn = new Button("Already have an account? Sign In");
        backBtn.getStyleClass().add("btn-outline");
        backBtn.setMaxWidth(Double.MAX_VALUE);

        // Registration Event Handler with Inline Validation
        registerBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passField.getText();
            String phone = phoneField.getText();

            boolean hasError = false;

            if (name == null || name.trim().isEmpty()) {
                nameErr.setText("Full name is required.");
                nameErr.setVisible(true);
                if (!nameField.getStyleClass().contains("input-error")) nameField.getStyleClass().add("input-error");
                hasError = true;
            }

            if (email == null || email.trim().isEmpty()) {
                emailErr.setText("Email address is required.");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("input-error")) emailField.getStyleClass().add("input-error");
                hasError = true;
            } else if (!email.contains("@") || !email.contains(".")) {
                emailErr.setText("Please enter a valid email (e.g. user@example.com).");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("input-error")) emailField.getStyleClass().add("input-error");
                hasError = true;
            }

            if (password == null || password.trim().isEmpty()) {
                passErr.setText("Password is required.");
                passErr.setVisible(true);
                if (!passField.getStyleClass().contains("input-error")) passField.getStyleClass().add("input-error");
                hasError = true;
            } else if (password.length() < 4) {
                passErr.setText("Password must be at least 4 characters long.");
                passErr.setVisible(true);
                if (!passField.getStyleClass().contains("input-error")) passField.getStyleClass().add("input-error");
                hasError = true;
            }

            if (hasError) return;

            Address address = new Address(
                    streetField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    pinField.getText(),
                    countryField.getText()
            );

            try {
                authService.registerCustomer(name, email, password, phone, address);
                GuiUtils.showInfo("Registration Successful",
                        "Account created successfully for " + name + "! You can now sign in.");
                app.showLoginView();
            } catch (IllegalArgumentException ex) {
                GuiUtils.showError("Registration Failed", ex.getMessage());
            } catch (Exception ex) {
                GuiUtils.showError("Unexpected Error", "Could not complete registration: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> app.showLoginView());

        card.getChildren().addAll(
                header,
                nameBox,
                emailBox,
                passBox,
                phoneBox,
                addrHeader, streetField, addressGrid,
                registerBtn, backBtn
        );

        ScrollPane scrollPane = new ScrollPane(card);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setPadding(new Insets(10));

        rootPane.getChildren().add(scrollPane);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    public Parent getView() {
        return rootPane;
    }
}
