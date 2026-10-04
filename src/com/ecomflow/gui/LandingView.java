package com.ecomflow.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LandingView {
    private final MainApp app;
    private final StackPane rootPane;
    private Timeline backgroundTimeline;

    public LandingView(MainApp app) {
        this.app = app;
        this.rootPane = new StackPane();
        buildUI();
    }

    private void buildUI() {
        // Base dark container
        rootPane.setStyle("-fx-background-color: #0B0B0F;");

        // ── 1. Animated Gradient Region ─────────────────────────────────────
        Region animatedBg = new Region();
        animatedBg.prefWidthProperty().bind(rootPane.widthProperty());
        animatedBg.prefHeightProperty().bind(rootPane.heightProperty());

        // Timeline to continuously interpolate the linear gradient
        DoubleProperty gradientProgress = new SimpleDoubleProperty(0.0);
        gradientProgress.addListener((obs, oldVal, newVal) -> {
            double t = newVal.doubleValue();
            
            // Interpolate colors derived from --gradient-hero (#1a0533 -> #6B21E8 -> #0B0B0F)
            int r1 = (int) (15 + (35 - 15) * t);
            int g1 = (int) (2 + (10 - 2) * t);
            int b1 = (int) (35 + (65 - 35) * t);

            int r2 = (int) (65 + (130 - 65) * t);
            int g2 = (int) (15 + (45 - 15) * t);
            int b2 = (int) (180 + (245 - 180) * t);

            int angle = (int) (125 + 35 * Math.sin(t * Math.PI));
            int stop1 = (int) (15 + 15 * t);
            int stop2 = (int) (50 + 20 * Math.cos(t * Math.PI));

            String gradStyle = String.format(
                "-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, #0B0B0F 0%%, rgb(%d,%d,%d) %d%%, rgb(%d,%d,%d) %d%%, #0B0B0F 100%%);",
                r1, g1, b1, stop1,
                r2, g2, b2, stop2
            );
            animatedBg.setStyle(gradStyle);
        });

        backgroundTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(gradientProgress, 0.0)),
            new KeyFrame(Duration.seconds(6), new KeyValue(gradientProgress, 1.0))
        );
        backgroundTimeline.setAutoReverse(true);
        backgroundTimeline.setCycleCount(Animation.INDEFINITE);
        backgroundTimeline.play();

        // ── 2. Subtle Dark Glow / Overlay ───────────────────────────────────
        Region overlay = new Region();
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());
        overlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 75%, transparent 35%, #0B0B0F 100%);");

        // ── 3. Hero Content Container ───────────────────────────────────────
        VBox heroBox = new VBox(22);
        heroBox.setAlignment(Pos.CENTER);
        heroBox.setMaxWidth(720);
        heroBox.setPadding(new Insets(40, 24, 40, 24));

        // Pill badge
        Label badge = new Label("✨ Next-Generation E-Commerce Experience");
        badge.setStyle(
            "-fx-background-color: rgba(107, 33, 232, 0.25);" +
            "-fx-text-fill: #F5F5F7;" +
            "-fx-border-color: rgba(107, 33, 232, 0.55);" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 6px 18px;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        // Hero Headline
        Label headline = new Label("Smarter Shopping.\nSeamless Commerce.");
        headline.setStyle(
            "-fx-font-size: 52px;" +
            "-fx-font-weight: 800;" +
            "-fx-text-fill: #F5F5F7;" +
            "-fx-text-alignment: center;" +
            "-fx-line-spacing: -4px;"
        );

        // Subheading
        Label subheading = new Label("Explore thousands of products, manage orders seamlessly, and experience real-time inventory power with EcomFlow.");
        subheading.setWrapText(true);
        subheading.setStyle(
            "-fx-font-size: 17px;" +
            "-fx-text-fill: #9CA3AF;" +
            "-fx-text-alignment: center;" +
            "-fx-max-width: 580px;"
        );

        // Action Buttons
        HBox buttonBox = new HBox(16);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(12, 0, 0, 0));

        // Sign In Button (Ghost / Outlined)
        Button signInBtn = new Button("Sign In");
        signInBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #F5F5F7;" +
            "-fx-border-color: rgba(245, 245, 247, 0.45);" +
            "-fx-border-width: 1.5px;" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 12px 30px;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );

        // Sign Up Button (Filled Accent #FF6B35)
        Button signUpBtn = new Button("Sign Up");
        signUpBtn.setStyle(
            "-fx-background-color: #FF6B35;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 12px 34px;" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(255, 107, 53, 0.4), 16, 0.15, 0, 4);"
        );

        // Attach ScaleTransition hover effects (~1.03x over 150ms)
        attachScaleAnimation(signInBtn);
        attachScaleAnimation(signUpBtn);

        // Navigation actions
        signInBtn.setOnAction(e -> app.showLoginView());
        signUpBtn.setOnAction(e -> app.showRegisterView());

        buttonBox.getChildren().addAll(signInBtn, signUpBtn);

        heroBox.getChildren().addAll(badge, headline, subheading, buttonBox);

        // ── 4. Fade-in Transition on Hero Content ───────────────────────────
        heroBox.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), heroBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Assemble root pane
        rootPane.getChildren().addAll(animatedBg, overlay, heroBox);
        StackPane.setAlignment(heroBox, Pos.CENTER);
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
        });

        button.setOnMouseExited(e -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });
    }

    public Parent getView() {
        return rootPane;
    }
}
