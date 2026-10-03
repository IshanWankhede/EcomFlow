package com.ecomflow.gui;

import com.ecomflow.model.Clothing;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Grocery;
import com.ecomflow.model.Product;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ProductCard extends VBox {
    private final Product product;
    private final Runnable onAddToCartCallback;

    public ProductCard(Product product, Runnable onAddToCartCallback) {
        this.product = product;
        this.onAddToCartCallback = onAddToCartCallback;
        buildCard();
    }

    private void buildCard() {
        getStyleClass().add("product-card");
        setPrefWidth(240);
        setMaxWidth(260);
        setSpacing(10);
        setAlignment(Pos.TOP_LEFT);

        // 1. Image Thumbnail
        String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "General";
        Node imageNode = GuiUtils.createProductImageView("product-" + product.getProductId() + ".png", categoryName, 208, 130);
        VBox imageContainer = new VBox(imageNode);
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPrefHeight(130);

        // 2. Category & Subtype Badge
        HBox badgeRow = new HBox(6);
        badgeRow.setAlignment(Pos.CENTER_LEFT);

        Label catBadge = new Label(categoryName.toUpperCase());
        catBadge.getStyleClass().add("badge-category");
        badgeRow.getChildren().add(catBadge);

        // Subtype-specific badge/info
        if (product instanceof Electronics el) {
            Label brandBadge = new Label(el.getBrand());
            brandBadge.getStyleClass().add("badge-category");
            badgeRow.getChildren().add(brandBadge);
        } else if (product instanceof Clothing cl) {
            Label sizeBadge = new Label("Size " + cl.getSize());
            sizeBadge.getStyleClass().add("badge-category");
            badgeRow.getChildren().add(sizeBadge);
        }

        // 3. Product Title
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("heading-md");
        nameLabel.setWrapText(true);
        nameLabel.setMinHeight(42);

        // 4. Subtype Details (Warranty, Material, Expiry)
        Label specLabel = new Label(getSpecText());
        specLabel.getStyleClass().add("text-muted");

        // 5. Price & Discount Row
        HBox priceRow = new HBox(8);
        priceRow.setAlignment(Pos.BASELINE_LEFT);

        double discount = product.calculateDiscount();
        if (discount > 0) {
            Label effectivePrice = new Label(GuiUtils.formatCurrency(product.getEffectivePrice()));
            effectivePrice.getStyleClass().add("price-tag");

            Label strikePrice = new Label(GuiUtils.formatCurrency(product.getPrice()));
            strikePrice.getStyleClass().add("price-strike");
            strikePrice.setStyle("-fx-strikethrough: true;");

            priceRow.getChildren().addAll(effectivePrice, strikePrice);
        } else {
            Label basePrice = new Label(GuiUtils.formatCurrency(product.getPrice()));
            basePrice.getStyleClass().add("price-tag");
            priceRow.getChildren().add(basePrice);
        }

        // 6. Stock Level Indicator
        Label stockLabel = new Label(product.getStock() > 0 ? product.getStock() + " in stock" : "Out of stock");
        if (product.getStock() <= 0) {
            stockLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else if (product.getStock() <= 5) {
            stockLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            stockLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12px;");
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 7. Add to Cart Button
        Button addBtn = new Button(product.getStock() > 0 ? "Add to Cart 🛒" : "Out of Stock");
        addBtn.getStyleClass().add("btn-accent");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setDisable(product.getStock() <= 0);

        addBtn.setOnAction(e -> {
            if (onAddToCartCallback != null) {
                onAddToCartCallback.run();
            }
        });

        getChildren().addAll(
                imageContainer,
                badgeRow,
                nameLabel,
                specLabel,
                priceRow,
                stockLabel,
                spacer,
                addBtn
        );
    }

    private String getSpecText() {
        if (product instanceof Electronics el) {
            return "Warranty: " + el.getWarrantyMonths() + " mos";
        } else if (product instanceof Clothing cl) {
            return "Material: " + cl.getMaterial();
        } else if (product instanceof Grocery gr) {
            return "Expires: " + gr.getExpiryDate();
        }
        return "";
    }

    public Product getProduct() {
        return product;
    }
}
