package com.ecomflow.gui;

import com.ecomflow.exceptions.InvalidLoginException;
import com.ecomflow.model.User;
import com.ecomflow.service.AuthenticationService;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginView {
    private final MainApp app;
    private final AuthenticationService authService;
    private final HBox rootPane;

    public LoginView(MainApp app, AuthenticationService authService) {
        this.app = app;
        this.authService = authService;
        this.rootPane = new HBox();
        buildUI();
    }

    private void buildUI() {
        rootPane.getStyleClass().add("split-auth-container");

        // ══════════════════════════════════════════════════════════════════════
        // LEFT HALF — Animated Gradient Brand & Step List Panel
        // ══════════════════════════════════════════════════════════════════════
        StackPane leftPanel = new StackPane();
        leftPanel.setPrefWidth(460);
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        // Animated gradient background
        AnimatedGradientBackground gradientBg = new AnimatedGradientBackground();
        gradientBg.prefWidthProperty().bind(leftPanel.widthProperty());
        gradientBg.prefHeightProperty().bind(leftPanel.heightProperty());

        // Dark glow / overlay for crisp readability
        Region overlay = new Region();
        overlay.prefWidthProperty().bind(leftPanel.widthProperty());
        overlay.prefHeightProperty().bind(leftPanel.heightProperty());
        overlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 80%, rgba(11, 11, 15, 0.4) 0%, rgba(11, 11, 15, 0.85) 100%);");

        // Content on top of gradient
        VBox leftContent = new VBox(32);
        leftContent.setAlignment(Pos.CENTER_LEFT);
        leftContent.setPadding(new Insets(48, 48, 48, 48));
        leftContent.setMaxWidth(480);

        // Brand & Headline
        VBox brandBox = new VBox(10);
        Label logoLabel = new Label("🛒 EcomFlow");
        logoLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 800; -fx-text-fill: #F5F5F7;");

        Label tagline = new Label("Experience smart shopping & automated store management with enterprise speed.");
        tagline.setWrapText(true);
        tagline.setStyle("-fx-font-size: 15px; -fx-text-fill: #9CA3AF; -fx-line-spacing: 2px;");
        brandBox.getChildren().addAll(logoLabel, tagline);

        // 3-Step Onboarding / Feature List
        VBox stepsBox = new VBox(18);
        stepsBox.getChildren().addAll(
            createStepItem("1", "Instant Access", "Sign in securely to your customer or admin workspace."),
            createStepItem("2", "Real-Time Catalog", "Browse live inventories and dynamic pricing updates."),
            createStepItem("3", "Seamless Checkout", "Multi-method payments with automated order tracking.")
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
        // RIGHT HALF — Surface Dark Form Panel
        // ══════════════════════════════════════════════════════════════════════
        StackPane rightPanel = new StackPane();
        rightPanel.getStyleClass().add("auth-right-panel");
        rightPanel.setPadding(new Insets(40, 48, 40, 48));
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        VBox formContainer = new VBox(18);
        formContainer.setMaxWidth(400);
        formContainer.setAlignment(Pos.CENTER_LEFT);

        // Form Header
        VBox formHeader = new VBox(6);
        Label title = new Label("Welcome Back");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #F5F5F7;");

        Label subtitle = new Label("Sign in to your account to continue.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #9CA3AF;");
        formHeader.getChildren().addAll(title, subtitle);

        // Email Form Group
        VBox emailGroup = new VBox(6);
        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("dark-label");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("dark-text-input");
        emailField.setPromptText("e.g. user@example.com");
        Label emailErr = new Label();
        emailErr.getStyleClass().add("error-text");
        emailErr.setVisible(false);
        emailGroup.getChildren().addAll(emailLabel, emailField, emailErr);

        // Password Form Group
        VBox passGroup = new VBox(6);
        Label passLabel = new Label("Password");
        passLabel.getStyleClass().add("dark-label");
        PasswordField passField = new PasswordField();
        passField.getStyleClass().add("dark-text-input");
        passField.setPromptText("Enter your password");
        Label passErr = new Label();
        passErr.getStyleClass().add("error-text");
        passErr.setVisible(false);
        passGroup.getChildren().addAll(passLabel, passField, passErr);

        // Clear errors as user types
        emailField.textProperty().addListener((obs, oldV, newV) -> {
            emailErr.setVisible(false);
            emailField.getStyleClass().remove("dark-input-error");
        });
        passField.textProperty().addListener((obs, oldV, newV) -> {
            passErr.setVisible(false);
            passField.getStyleClass().remove("dark-input-error");
        });

        // Submit Button
        Button loginBtn = new Button("Sign In");
        loginBtn.setStyle(
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
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        attachScaleAnimation(loginBtn);

        // Quick Demo Credentials (Admin only)
        VBox demoBox = new VBox(8);
        demoBox.setAlignment(Pos.CENTER);
        demoBox.setPadding(new Insets(6, 0, 0, 0));

        Label demoLabel = new Label("Quick sign-in for testing:");
        demoLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        Button fillAdmin = new Button("Admin Demo Account");
        fillAdmin.getStyleClass().add("auth-demo-btn");
        fillAdmin.setOnAction(e -> {
            emailField.setText("admin@ecomflow.com");
            passField.setText("adminPass");
            emailErr.setVisible(false);
            passErr.setVisible(false);
        });
        demoBox.getChildren().addAll(demoLabel, fillAdmin);

        // Switch to Register View Link
        HBox switchBox = new HBox(4);
        switchBox.setAlignment(Pos.CENTER);
        switchBox.setPadding(new Insets(10, 0, 0, 0));

        Label switchPrompt = new Label("Don't have an account?");
        switchPrompt.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 13px;");

        Button switchBtn = new Button("Sign Up");
        switchBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #FF6B35; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 0 4px; " +
            "-fx-cursor: hand;"
        );
        switchBtn.setOnAction(e -> app.showRegisterView());
        switchBox.getChildren().addAll(switchPrompt, switchBtn);

        // Event Handlers
        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();

            boolean hasError = false;

            if (email == null || email.trim().isEmpty()) {
                emailErr.setText("Please enter your email address.");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("dark-input-error")) emailField.getStyleClass().add("dark-input-error");
                hasError = true;
            } else if (!email.contains("@")) {
                emailErr.setText("Please enter a valid email containing '@'.");
                emailErr.setVisible(true);
                if (!emailField.getStyleClass().contains("dark-input-error")) emailField.getStyleClass().add("dark-input-error");
                hasError = true;
            }

            if (password == null || password.trim().isEmpty()) {
                passErr.setText("Please enter your password.");
                passErr.setVisible(true);
                if (!passField.getStyleClass().contains("dark-input-error")) passField.getStyleClass().add("dark-input-error");
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

        formContainer.getChildren().addAll(
            formHeader,
            emailGroup,
            passGroup,
            loginBtn,
            demoBox,
            switchBox
        );

        rightPanel.getChildren().add(formContainer);
        StackPane.setAlignment(formContainer, Pos.CENTER);

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
