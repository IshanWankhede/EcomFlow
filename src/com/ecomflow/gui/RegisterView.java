package com.ecomflow.gui;

import com.ecomflow.model.Address;
import com.ecomflow.service.AuthenticationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setMaxWidth(520);
        card.setAlignment(Pos.CENTER_LEFT);

        // Header
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);

        Label title = new Label("Create Account");
        title.getStyleClass().add("heading-lg");

        Label subtitle = new Label("Join EcomFlow to start shopping and tracking orders");
        subtitle.getStyleClass().add("subtitle");

        header.getChildren().addAll(title, subtitle);

        // Account Details
        Label nameLabel = new Label("Full Name *");
        nameLabel.getStyleClass().add("label-field");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. John Doe");

        Label emailLabel = new Label("Email Address *");
        emailLabel.getStyleClass().add("label-field");
        TextField emailField = new TextField();
        emailField.setPromptText("e.g. john@example.com");

        Label passLabel = new Label("Password *");
        passLabel.getStyleClass().add("label-field");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Create a secure password");

        Label phoneLabel = new Label("Phone Number");
        phoneLabel.getStyleClass().add("label-field");
        TextField phoneField = new TextField();
        phoneField.setPromptText("e.g. +91 9876543210");

        // Shipping Address Section
        Label addrHeader = new Label("Shipping Address");
        addrHeader.getStyleClass().add("heading-md");
        addrHeader.setPadding(new Insets(10, 0, 0, 0));

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

        TextField countryField = new TextField();
        countryField.setPromptText("Country");
        countryField.setText("India");

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

        // Registration Event Handler
        registerBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passField.getText();
            String phone = phoneField.getText();

            Address address = new Address(
                    streetField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    pinField.getText(),
                    countryField.getText()
            );

            try {
                authService.registerCustomer(name, email, password, phone, address);
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful",
                        "Account created successfully for " + name + "! You can now sign in.");
                app.showLoginView();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Registration Error", ex.getMessage());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Unexpected Error", "Could not complete registration: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> app.showLoginView());

        card.getChildren().addAll(
                header,
                nameLabel, nameField,
                emailLabel, emailField,
                passLabel, passField,
                phoneLabel, phoneField,
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
