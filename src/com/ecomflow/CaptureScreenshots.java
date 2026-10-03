package com.ecomflow;

import java.io.File;
import javax.imageio.ImageIO;

import com.ecomflow.gui.AdminDashboardView;
import com.ecomflow.gui.CustomerDashboardView;
import com.ecomflow.gui.LoginView;
import com.ecomflow.gui.MainApp;
import com.ecomflow.gui.RegisterView;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Customer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

public class CaptureScreenshots extends Application {
    private MainApp app;
    private Stage stage;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.app = new MainApp();
        this.app.init();

        new File("resources/images/screenshots").mkdirs();

        Platform.runLater(() -> {
            try {
                captureAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Platform.exit();
            }
        });
    }

    private void captureAll() throws Exception {
        Customer sampleCustomer = (Customer) app.getDataStore().getUserByEmail("alice@ecomflow.com");
        Admin sampleAdmin = (Admin) app.getDataStore().getUserByEmail("admin@ecomflow.com");

        // 1. Login Screen
        saveSnapshot(new LoginView(app, app.getAuthService()).getView(), "login_screen.png");

        // 2. Register Screen
        saveSnapshot(new RegisterView(app, app.getAuthService()).getView(), "register_screen.png");

        // 3. Customer Dashboard Views
        CustomerDashboardView custDash = new CustomerDashboardView(app, sampleCustomer);
        
        custDash.showBrowseView();
        saveSnapshot(custDash.getView(), "customer_browse.png");

        // Add 2 items to cart and snapshot Cart
        app.getCartService().addItem(sampleCustomer.getCart(), app.getProductService().getProductById(5001), 1);
        app.getCartService().addItem(sampleCustomer.getCart(), app.getProductService().getProductById(5004), 2);
        
        custDash.showCartView();
        saveSnapshot(custDash.getView(), "customer_cart.png");

        custDash.showCheckoutView();
        saveSnapshot(custDash.getView(), "customer_checkout.png");

        // Place an order to generate history
        app.getOrderService().placeOrder(sampleCustomer.getCart(), "SAVE10", "UPI");

        custDash.showOrdersView();
        saveSnapshot(custDash.getView(), "customer_orders.png");

        custDash.showProfileView();
        saveSnapshot(custDash.getView(), "customer_profile.png");

        // 4. Admin Dashboard Views
        AdminDashboardView adminDash = new AdminDashboardView(app, sampleAdmin);

        adminDash.showProductsView();
        saveSnapshot(adminDash.getView(), "admin_products.png");

        adminDash.showInventoryView();
        saveSnapshot(adminDash.getView(), "admin_inventory.png");

        adminDash.showOrdersView();
        saveSnapshot(adminDash.getView(), "admin_orders.png");

        adminDash.showCustomersView();
        saveSnapshot(adminDash.getView(), "admin_customers.png");

        System.out.println("====================================================================");
        System.out.println("ALL SCREENSHOTS SUCCESSFULLY SAVED TO resources/images/screenshots/!");
        System.out.println("====================================================================");
    }

    private void saveSnapshot(Parent root, String filename) throws Exception {
        if (scene == null) {
            scene = new Scene(root, 1060, 700);
            File css = new File("resources/css/styles.css");
            if (css.exists()) {
                scene.getStylesheets().add(css.toURI().toString());
            }
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }

        stage.show();
        root.applyCss();
        root.layout();

        WritableImage snapshot = scene.snapshot(null);
        File outFile = new File("resources/images/screenshots/" + filename);
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", outFile);
        System.out.println("Captured: " + filename);
    }

    public static void main(String[] args) {
        Application.launch(CaptureScreenshots.class, args);
    }
}
