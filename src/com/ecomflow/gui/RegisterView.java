package com.ecomflow.gui;

import com.ecomflow.model.Address;
import com.ecomflow.service.AuthenticationService;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RegisterView {
    private final MainApp app;
    private final AuthenticationService authService;
    private final HBox rootPane;

    public RegisterView(MainApp app, AuthenticationService authService) {
        this.app = app;
        this.authService = authService;
        this.rootPane = new HBox();
        buildUI();
    }

    private void buildUI() {
        rootPane.getStyleClass().add("split-auth-container");

        // ══════════════════════════════════════════════════════════════════════
        // LEFT HALF — Animated Gradient Onboarding Panel
        // ══════════════════════════════════════════════════════════════════════
        StackPane leftPanel = new StackPane();
        leftPanel.setPrefWidth(460);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        AnimatedGradientBackground gradientBg = new AnimatedGradientBackground();
        gradientBg.prefWidthProperty().bind(leftPanel.widthProperty());
        gradientBg.prefHeightProperty().bind(leftPanel.heightProperty());

        Region overlay = new Region();
        overlay.prefWidthProperty().bind(leftPanel.widthProperty());
        overlay.prefHeightProperty().bind(leftPanel.heightProperty());
        overlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 80%, rgba(11, 11, 15, 0.4) 0%, rgba(11, 11, 15, 0.85) 100%);");

        VBox leftContent = new VBox(32);
        leftContent.setAlignment(Pos.CENTER_LEFT);
        leftContent.setPadding(new Insets(48, 48, 48, 48));
        leftContent.setMaxWidth(480);

        // Brand & Headline
        VBox brandBox = new VBox(10);
        Label logoLabel = new Label("🛒 EcomFlow");
        logoLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 800; -fx-text-fill: #F5F5F7;");

        Label tagline = new Label("Join thousands of shoppers and manage your online commerce with unmatched ease.");
        tagline.setWrapText(true);
        tagline.setStyle("-fx-font-size: 15px; -fx-text-fill: #9CA3AF; -fx-line-spacing: 2px;");
        brandBox.getChildren().addAll(logoLabel, tagline);

        // 3-Step Onboarding Checklist
        VBox stepsBox = new VBox(18);
        stepsBox.getChildren().addAll(
            createStepItem("1", "Create your account", "Set up your customer credentials and default delivery address."),
            createStepItem("2", "Browse the catalog", "Explore curated electronics, apparel, and grocery essentials."),
            createStepItem("3", "Start shopping", "Enjoy fast checkout, coupons, and live package updates.")
        );

        // Back to Landing Page button
        Button backHomeBtn = new Button("← Back to Home");
        backHomeBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #9CA3AF; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 6px 0;"
        );
        backHomeBtn.setOnMouseEntered(e -> backHomeBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #FF6B35; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 6px 0;"
        ));
        backHomeBtn.setOnMouseExited(e -> backHomeBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #9CA3AF; " +
            "-fx-font-size: 13px; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 6px 0;"
        ));
        backHomeBtn.setOnAction(e -> app.showLandingView());

        leftContent.getChildren().addAll(brandBox, stepsBox, backHomeBtn);
        leftPanel.getChildren().addAll(gradientBg, overlay, leftContent);
        StackPane.setAlignment(leftContent, Pos.CENTER);

        // ══════════════════════════════════════════════════════════════════════
        // RIGHT HALF — Surface Dark Form Panel (Scrollable)
        // ══════════════════════════════════════════════════════════════════════
        StackPane rightPanel = new StackPane();
        rightPanel.getStyleClass().add("auth-right-panel");
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        VBox formCard = new VBox(16);
        formCard.setMaxWidth(420);
        formCard.setAlignment(Pos.CENTER_LEFT);
        formCard.setPadding(new Insets(32, 40, 32, 40));

        // Form Header
        VBox formHeader = new VBox(6);
        Label title = new Label("Create Account");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #F5F5F7;");

        Label subtitle = new Label("Fill in your details to start shopping on EcomFlow.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #9CA3AF;");
        formHeader.getChildren().addAll(title, subtitle);

        // 1. Name Field
        VBox nameBox = new VBox(4);
        Label nameLabel = new Label("Full Name *");
        nameLabel.getStyleClass().add("dark-label");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("dark-text-input");
        nameField.setPromptText("e.g. xyz");
        Label nameErr = new Label();
        nameErr.getStyleClass().add("error-text");
        nameErr.setVisible(false);
        nameBox.getChildren().addAll(nameLabel, nameField, nameErr);

        // 2. Email Field
        VBox emailBox = new VBox(4);
        Label emailLabel = new Label("Email Address *");
        emailLabel.getStyleClass().add("dark-label");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("dark-text-input");
        emailField.setPromptText("e.g. xyz@gmail.com");
        Label emailErr = new Label();
        emailErr.getStyleClass().add("error-text");
        emailErr.setVisible(false);
        emailBox.getChildren().addAll(emailLabel, emailField, emailErr);

        // 3. Password Field
        VBox passBox = new VBox(4);
        Label passLabel = new Label("Password *");
        passLabel.getStyleClass().add("dark-label");
        PasswordField passField = new PasswordField();
        passField.getStyleClass().add("dark-text-input");
        passField.setPromptText("Create a secure password (min 4 chars)");
        Label passErr = new Label();
        passErr.getStyleClass().add("error-text");
        passErr.setVisible(false);
        passBox.getChildren().addAll(passLabel, passField, passErr);

        // 4. Phone Field
        VBox phoneBox = new VBox(4);
        Label phoneLabel = new Label("Phone Number");
        phoneLabel.getStyleClass().add("dark-label");
        TextField phoneField = new TextField();
        phoneField.getStyleClass().add("dark-text-input");
        phoneField.setPromptText("e.g. +91 9876543210");
        phoneBox.getChildren().addAll(phoneLabel, phoneField);

        // Clear errors as user types
        nameField.textProperty().addListener((obs, oldV, newV) -> {
            nameErr.setVisible(false);
            nameField.getStyleClass().remove("dark-input-error");
        });
        emailField.textProperty().addListener((obs, oldV, newV) -> {
            emailErr.setVisible(false);
            emailField.getStyleClass().remove("dark-input-error");
        });
        passField.textProperty().addListener((obs, oldV, newV) -> {
            passErr.setVisible(false);
            passField.getStyleClass().remove("dark-input-error");
        });

        // 5. Shipping Address Section
        VBox addressSection = new VBox(8);
        Label addrHeader = new Label("Shipping Address");
        addrHeader.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #F5F5F7; -fx-padding: 6px 0 0 0;");

        TextField streetField = new TextField();
        streetField.getStyleClass().add("dark-text-input");
        streetField.setPromptText("Street Address (e.g. 101 Park Avenue)");

        GridPane addressGrid = new GridPane();
        addressGrid.setHgap(10);
        addressGrid.setVgap(10);

        TextField cityField = new TextField();
        cityField.getStyleClass().add("dark-text-input");
        cityField.setPromptText("City");

        TextField stateField = new TextField();
        stateField.getStyleClass().add("dark-text-input");
        stateField.setPromptText("State");

        TextField pinField = new TextField();
        pinField.getStyleClass().add("dark-text-input");
        pinField.setPromptText("Pincode (e.g. 560001)");

        TextField countryField = new TextField("India");
        countryField.getStyleClass().add("dark-text-input");
        countryField.setPromptText("Country");

        addressGrid.add(cityField, 0, 0);
        addressGrid.add(stateField, 1, 0);
        addressGrid.add(pinField, 0, 1);
        addressGrid.add(countryField, 1, 1);

        addressSection.getChildren().addAll(addrHeader, streetField, addressGrid);

        // Submit Button
        Button registerBtn = new Button("Complete Registration");
        registerBtn.setStyle(
            "-fx-background-color: #FF6B35; " +
            "-fx-text-fill: #FFFFFF; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 12px; " +
            "-fx-border-radius: 12px; " +
            "-fx-padding: 12px 20px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(255, 107, 53, 0.35), 12, 0.15, 0, 3);"
        );
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        attachScaleAnimation(registerBtn);

        // Switch to Login Link
        HBox switchBox = new HBox(4);
        switchBox.setAlignment(Pos.CENTER);
        switchBox.setPadding(new Insets(6, 0, 0, 0));

        Label switchPrompt = new Label("Already have an account?");
        switchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px;");

        Button switchBtn = new Button("Log In");
        switchBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #FF6B35; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 0 4px; " +
            "-fx-cursor: hand;"
        );
        switchBtn.setOnAction(e -> app.showLoginView());
        switchBox.getChildren().addAll(switchPrompt, switchBtn);

        // Registration Event Handler with Validation
        registerBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passField.getText();
            String phone = phoneField.getText();

            boolean hasError = false;

            if (name == null || name.trim().isEmpty()) {
                nameErr.setText("Full name is required.");
                nameErr.setVisible(true);
                if (!nameField.getStyleClass().contains("dark-input-error")) nameField.getStyleClass().add("dark-input-error");
                hasError = true;
            }

            if (email == null || email.trim().isEmpty()) {
                emailErr.setText("Email address is required.");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("dark-input-error")) emailField.getStyleClass().add("dark-input-error");
                hasError = true;
            } else if (!email.contains("@") || !email.contains(".")) {
                emailErr.setText("Please enter a valid email (e.g. xyz@gmail.com).");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("dark-input-error")) emailField.getStyleClass().add("dark-input-error");
                hasError = true;
            }

            if (password == null || password.trim().isEmpty()) {
                passErr.setText("Password is required.");
                passErr.setVisible(true);
                if (!passField.getStyleClass().contains("dark-input-error")) passField.getStyleClass().add("dark-input-error");
                hasError = true;
            } else if (password.length() < 4) {
                passErr.setText("Password must be at least 4 characters long.");
                passErr.setVisible(true);
                if (!passField.getStyleClass().contains("dark-input-error")) passField.getStyleClass().add("dark-input-error");
                hasError = true;
            }

            String enteredPin = pinField.getText().trim();
            if (!enteredPin.isEmpty() && !Address.isValidPincode(enteredPin)) {
                GuiUtils.showError("Invalid Pincode", "Pincode '" + enteredPin + "' is invalid. Please enter 3-10 alphanumeric characters.");
                if (!pinField.getStyleClass().contains("dark-input-error")) pinField.getStyleClass().add("dark-input-error");
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

        formCard.getChildren().addAll(
            formHeader,
            nameBox,
            emailBox,
            passBox,
            phoneBox,
            addressSection,
            registerBtn,
            switchBox
        );

        ScrollPane scrollPane = new ScrollPane(formCard);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;");
        scrollPane.setPadding(new Insets(10));

        rightPanel.getChildren().add(scrollPane);
        StackPane.setAlignment(formCard, Pos.CENTER);

        rootPane.getChildren().addAll(leftPanel, rightPanel);
    }

    private HBox createStepItem(String number, String stepTitle, String stepDesc) {
        HBox item = new HBox(14);
        item.setAlignment(Pos.TOP_LEFT);

        Label numBadge = new Label(number);
        numBadge.setStyle(
            "-fx-background-color: rgba(107, 33, 232, 0.45); " +
            "-fx-text-fill: #F5F5F7; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-min-width: 28px; " +
            "-fx-min-height: 28px; " +
            "-fx-max-width: 28px; " +
            "-fx-max-height: 28px; " +
            "-fx-alignment: center; " +
            "-fx-background-radius: 14px; " +
            "-fx-border-color: rgba(107, 33, 232, 0.7); " +
            "-fx-border-radius: 14px;"
        );

        VBox textGroup = new VBox(2);
        Label titleLabel = new Label(stepTitle);
        titleLabel.setStyle("-fx-text-fill: #F5F5F7; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label descLabel = new Label(stepDesc);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px;");
        textGroup.getChildren().addAll(titleLabel, descLabel);

        item.getChildren().addAll(numBadge, textGroup);
        return item;
    }

    private void attachScaleAnimation(Button button) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), button);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            scaleDown.stop();
            scaleUp.playFromStart();
            button.setStyle(
                "-fx-background-color: #E8551F; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-radius: 12px; " +
                "-fx-padding: 12px 20px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(232, 85, 31, 0.45), 14, 0.15, 0, 3);"
            );
        });

        button.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
            button.setStyle(
                "-fx-background-color: #FF6B35; " +
                "-fx-text-fill: #FFFFFF; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 12px; " +
                "-fx-border-radius: 12px; " +
                "-fx-padding: 12px 20px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 107, 53, 0.35), 12, 0.15, 0, 3);"
            );
        });
    }

    public Parent getView() {
        return rootPane;
    }
}
