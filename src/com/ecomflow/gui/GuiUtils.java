package com.ecomflow.gui;

import java.io.File;
import java.io.FileInputStream;

import com.ecomflow.enums.OrderStatus;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class GuiUtils {

    /**
     * Loads an image from the resources/images directory or returns a stylized placeholder node.
     */
    public static Node createProductImageView(String imageName, String categoryName, double fitWidth, double fitHeight) {
        String[] possiblePaths = {
            "resources/images/products/" + imageName,
            "resources/images/" + imageName,
            "resources/" + imageName
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try {
                    Image image = new Image(new FileInputStream(file));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(fitWidth);
                    imageView.setFitHeight(fitHeight);
                    imageView.setPreserveRatio(true);
                    return imageView;
                } catch (Exception ignored) {
                }
            }
        }

        // Fallback: Return a sleek graphical placeholder tile with category-based emoji
        StackPane placeholder = new StackPane();
        placeholder.setPrefSize(fitWidth, fitHeight);
        placeholder.setMaxSize(fitWidth, fitHeight);
        placeholder.setStyle(
            "-fx-background-color: #F3F4F6; " +
            "-fx-background-radius: 8px; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1px;"
        );

        String emoji = "🛍️";
        if (categoryName != null) {
            String lower = categoryName.toLowerCase();
            if (lower.contains("elect")) emoji = "🎧";
            else if (lower.contains("apparel") || lower.contains("cloth")) emoji = "🧥";
            else if (lower.contains("groc")) emoji = "🥛";
            else if (lower.contains("gadget")) emoji = "⌚";
            else if (lower.contains("appliance")) emoji = "🥤";
        }

        Label iconLabel = new Label(emoji);
        iconLabel.setStyle("-fx-font-size: 36px;");
        placeholder.getChildren().add(iconLabel);
        StackPane.setAlignment(iconLabel, Pos.CENTER);

        return placeholder;
    }

    public static Label createStatusBadge(OrderStatus status) {
        Label badge = new Label(status != null ? status.name() : "UNKNOWN");
        badge.getStyleClass().add("status-chip");

        if (status != null) {
            switch (status) {
                case PLACED:
                    badge.getStyleClass().add("status-placed");
                    break;
                case CONFIRMED:
                case PACKED:
                    badge.getStyleClass().add("status-confirmed");
                    break;
                case SHIPPED:
                case OUT_FOR_DELIVERY:
                    badge.getStyleClass().add("status-shipped");
                    break;
                case DELIVERED:
                    badge.getStyleClass().add("status-delivered");
                    break;
                case CANCELLED:
                    badge.getStyleClass().add("status-cancelled");
                    break;
            }
        }
        return badge;
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title != null ? title : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        showError("Operation Failed", message);
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title != null ? title : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInfo(String message) {
        showInfo("Notice", message);
    }

    public static String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }
}
