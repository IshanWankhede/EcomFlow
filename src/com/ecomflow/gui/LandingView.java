package com.ecomflow.gui;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Rebuilt LandingView with an asymmetric layout:
 * - Top navigation bar with logo, decorative links, and sign-in action
 * - Left column: pill eyebrow badge, bold headline, muted copy, primary/ghost CTAs, trust items
 * - Right column: continuous staggered concentric ripple/radar animation with glowing central core
 * - Textured dark background with particle speckle starfield and ambient glow
 */
public class LandingView {
    private final MainApp app;
    private final StackPane rootPane;

    public LandingView(MainApp app) {
        this.app = app;
        this.rootPane = new StackPane();
        buildUI();
    }

    private void buildUI() {
        // Base dark container
        rootPane.setStyle("-fx-background-color: #0B0B0F;");

        // ── 1. Background Layers ────────────────────────────────────────────
        // Ambient purple/indigo glow in the upper-right hero area
        Region ambientGlow = new Region();
        ambientGlow.prefWidthProperty().bind(rootPane.widthProperty());
        ambientGlow.prefHeightProperty().bind(rootPane.heightProperty());
        ambientGlow.setStyle("-fx-background-color: radial-gradient(center 75% 45%, radius 65%, rgba(107, 33, 232, 0.22), rgba(11, 11, 15, 0.85) 70%, #0B0B0F 100%);");

        // Speckled particle/star field
        Pane particleField = createParticleField();

        // ── 2. Foreground Layout ────────────────────────────────────────────
        BorderPane contentLayout = new BorderPane();
        contentLayout.setMouseTransparent(false);

        // TOP: Navigation Bar
        HBox topNavBar = createTopNavBar();
        contentLayout.setTop(topNavBar);

        // CENTER: Asymmetric Hero Section (55/45 split)
        HBox heroColumns = new HBox(48);
        heroColumns.setAlignment(Pos.CENTER_LEFT);
        heroColumns.setPadding(new Insets(24, 56, 48, 56));

        // LEFT COLUMN: Copy & Call-to-actions
        VBox leftColumn = createLeftHeroColumn();
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        // RIGHT COLUMN: Animated Ripple & Visual
        StackPane rightColumn = createRightAnimatedVisual();
        rightColumn.setMinWidth(420);
        rightColumn.setMinHeight(420);

        heroColumns.getChildren().addAll(leftColumn, rightColumn);
        contentLayout.setCenter(heroColumns);

        // ── 3. Smooth Fade-in on Hero Content ───────────────────────────────
        contentLayout.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(550), contentLayout);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        // Assemble root
        rootPane.getChildren().addAll(ambientGlow, particleField, contentLayout);
    }

    private Pane createParticleField() {
        Pane pane = new Pane();
        pane.setMouseTransparent(true);
        pane.prefWidthProperty().bind(rootPane.widthProperty());
        pane.prefHeightProperty().bind(rootPane.heightProperty());

        // Generate fixed pseudo-random star dots for subtle background texture
        Random rand = new Random(42);
        for (int i = 0; i < 55; i++) {
            double x = rand.nextDouble() * 1400;
            double y = rand.nextDouble() * 900;
            double radius = 0.8 + rand.nextDouble() * 1.5;
            double opacity = 0.12 + rand.nextDouble() * 0.30;

            Circle dot = new Circle(radius);
            dot.setLayoutX(x);
            dot.setLayoutY(y);
            dot.setFill(Color.web("#E2E8F0", opacity));
            pane.getChildren().add(dot);
        }
        return pane;
    }

    private HBox createTopNavBar() {
        HBox nav = new HBox(32);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(24, 56, 12, 56));

        // Brand Logo + Wordmark
        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        StackPane logoIcon = new StackPane();
        logoIcon.setPrefSize(34, 34);
        logoIcon.setStyle(
            "-fx-background-color: linear-gradient(135deg, #6B21E8, #FF6B35);" +
            "-fx-background-radius: 9px;" +
            "-fx-effect: dropshadow(gaussian, rgba(107, 33, 232, 0.45), 10, 0.1, 0, 2);"
        );
        Label iconGlyph = new Label("⚡");
        iconGlyph.setStyle("-fx-font-size: 15px; -fx-text-fill: #FFFFFF;");
        logoIcon.getChildren().add(iconGlyph);

        Label brandName = new Label("EcomFlow");
        brandName.setStyle("-fx-font-family: 'Sora', 'Space Grotesk', sans-serif; -fx-font-size: 20px; -fx-font-weight: 800; -fx-text-fill: #F5F5F7;");

        logoBox.getChildren().addAll(logoIcon, brandName);

        // Decorative Nav Links
        HBox navLinks = new HBox(28);
        navLinks.setAlignment(Pos.CENTER_LEFT);
        Label navFeatures = createNavLink("Features");
        Label navCatalog  = createNavLink("Catalog");
        Label navAbout    = createNavLink("About Platform");
        navLinks.getChildren().addAll(navFeatures, navCatalog, navAbout);

        // Spacer to push Sign In to right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Top Nav Sign In button (Outlined / Pill)
        Button topSignInBtn = new Button("Sign In");
        topSignInBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #F5F5F7;" +
            "-fx-border-color: rgba(245, 245, 247, 0.28);" +
            "-fx-border-width: 1.2px;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 7px 20px;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        topSignInBtn.setOnAction(e -> app.showLoginView());
        attachScaleAnimation(topSignInBtn);

        nav.getChildren().addAll(logoBox, navLinks, spacer, topSignInBtn);
        return nav;
    }

    private Label createNavLink(String text) {
        Label link = new Label(text);
        link.setStyle("-fx-font-size: 13px; -fx-text-fill: #9CA3AF; -fx-cursor: hand;");
        link.setOnMouseEntered(e -> link.setStyle("-fx-font-size: 13px; -fx-text-fill: #F5F5F7; -fx-cursor: hand;"));
        link.setOnMouseExited(e -> link.setStyle("-fx-font-size: 13px; -fx-text-fill: #9CA3AF; -fx-cursor: hand;"));
        return link;
    }

    private VBox createLeftHeroColumn() {
        VBox col = new VBox(22);
        col.setAlignment(Pos.CENTER_LEFT);
        col.setMaxWidth(560);

        // 1. Eyebrow badge
        Label badge = new Label("✨  Next-Generation E-Commerce");
        badge.setStyle(
            "-fx-background-color: rgba(107, 33, 232, 0.20);" +
            "-fx-text-fill: #C084FC;" +
            "-fx-border-color: rgba(168, 85, 247, 0.45);" +
            "-fx-border-radius: 20px;" +
            "-fx-background-radius: 20px;" +
            "-fx-padding: 6px 16px;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );

        // 2. Main Headline
        Label headline = new Label("Smarter Shopping.\nSeamless Commerce.");
        headline.setStyle(
            "-fx-font-family: 'Sora', 'Space Grotesk', sans-serif;" +
            "-fx-font-size: 46px;" +
            "-fx-font-weight: 800;" +
            "-fx-text-fill: #F5F5F7;" +
            "-fx-line-spacing: -3px;"
        );

        // 3. Subheading paragraph
        Label subheading = new Label(
            "Experience high-performance e-commerce with real-time stock orchestration, frictionless multi-item checkout, and enterprise-grade order management."
        );
        subheading.setWrapText(true);
        subheading.setMaxWidth(520);
        subheading.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #9CA3AF;" +
            "-fx-line-spacing: 3px;"
        );

        // 4. CTA Button Group
        HBox buttonBox = new HBox(16);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 8, 0));

        // Sign Up (Filled Accent)
        Button signUpBtn = new Button("Get Started  →");
        signUpBtn.setStyle(
            "-fx-background-color: #FF6B35;" +
            "-fx-text-fill: #FFFFFF;" +
            "-fx-border-width: 0;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-padding: 13px 30px;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(255, 107, 53, 0.40), 16, 0.15, 0, 4);"
        );
        signUpBtn.setOnAction(e -> app.showRegisterView());
        attachScaleAnimation(signUpBtn);

        // Sign In (Outlined Ghost)
        Button signInBtn = new Button("Sign In");
        signInBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #F5F5F7;" +
            "-fx-border-color: rgba(245, 245, 247, 0.38);" +
            "-fx-border-width: 1.4px;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;" +
            "-fx-padding: 13px 28px;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-cursor: hand;"
        );
        signInBtn.setOnAction(e -> app.showLoginView());
        attachScaleAnimation(signInBtn);

        buttonBox.getChildren().addAll(signUpBtn, signInBtn);

        // 5. Trust / Feature highlights row
        HBox trustRow = new HBox(18);
        trustRow.setAlignment(Pos.CENTER_LEFT);
        trustRow.setPadding(new Insets(14, 0, 0, 0));

        Label t1 = createTrustLabel("🔒 Bank-grade Security");
        Label sep1 = createTrustSeparator();
        Label t2 = createTrustLabel("⚡ Instant Checkout");
        Label sep2 = createTrustSeparator();
        Label t3 = createTrustLabel("📦 Live Inventory");

        trustRow.getChildren().addAll(t1, sep1, t2, sep2, t3);

        col.getChildren().addAll(badge, headline, subheading, buttonBox, trustRow);
        return col;
    }

    private Label createTrustLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #6B7280; -fx-font-weight: bold;");
        return l;
    }

    private Label createTrustSeparator() {
        Label sep = new Label("•");
        sep.setStyle("-fx-font-size: 12px; -fx-text-fill: #4B5563;");
        return sep;
    }

    private StackPane createRightAnimatedVisual() {
        StackPane visualArea = new StackPane();
        visualArea.setAlignment(Pos.CENTER);

        // Staggered Concentric Circles Ripple Effect
        // 5 rings with increasing base radius, continuously scaling and fading out in staggered cycles
        double[] baseRadii = { 45, 80, 115, 150, 185 };
        int[] delaysMs = { 0, 600, 1200, 1800, 2400 };

        for (int i = 0; i < baseRadii.length; i++) {
            Circle ring = new Circle(baseRadii[i]);
            ring.setFill(Color.TRANSPARENT);
            // Purple & accent tinted stroke
            if (i % 2 == 0) {
                ring.setStroke(Color.web("#8B5CF6", 0.40));
            } else {
                ring.setStroke(Color.web("#A855F7", 0.30));
            }
            ring.setStrokeWidth(1.5);

            // Scale from 1.0 to 1.85
            ScaleTransition scale = new ScaleTransition(Duration.millis(3000), ring);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.85);
            scale.setToY(1.85);
            scale.setCycleCount(Animation.INDEFINITE);

            // Fade from 0.65 to 0.0
            FadeTransition fade = new FadeTransition(Duration.millis(3000), ring);
            fade.setFromValue(0.65);
            fade.setToValue(0.0);
            fade.setCycleCount(Animation.INDEFINITE);

            ParallelTransition ripple = new ParallelTransition(scale, fade);
            ripple.setCycleCount(Animation.INDEFINITE);
            ripple.setDelay(Duration.millis(delaysMs[i]));
            ripple.play();

            visualArea.getChildren().add(ring);
        }

        // ── Central Glowing Core ────────────────────────────────────────────
        // Outer soft glow aura
        Circle outerAura = new Circle(48);
        outerAura.setFill(Color.web("#6B21E8", 0.25));
        outerAura.setEffect(new DropShadow(24, Color.web("#8B5CF6", 0.5)));

        // Central Core Circle with vibrant gradient
        StackPane coreCenter = new StackPane();
        coreCenter.setPrefSize(72, 72);
        coreCenter.setMaxSize(72, 72);
        coreCenter.setStyle(
            "-fx-background-color: linear-gradient(135deg, #FF6B35, #6B21E8);" +
            "-fx-background-radius: 36px;" +
            "-fx-effect: dropshadow(gaussian, rgba(107, 33, 232, 0.6), 24, 0.25, 0, 4);"
        );

        Label coreGlyph = new Label("◈");
        coreGlyph.setStyle("-fx-font-size: 30px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
        coreCenter.getChildren().add(coreGlyph);

        // Core gentle breathing/pulse animation
        ScaleTransition corePulse = new ScaleTransition(Duration.millis(1400), coreCenter);
        corePulse.setFromX(1.0);
        corePulse.setFromY(1.0);
        corePulse.setToX(1.08);
        corePulse.setToY(1.08);
        corePulse.setAutoReverse(true);
        corePulse.setCycleCount(Animation.INDEFINITE);
        corePulse.play();

        visualArea.getChildren().addAll(outerAura, coreCenter);

        return visualArea;
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
