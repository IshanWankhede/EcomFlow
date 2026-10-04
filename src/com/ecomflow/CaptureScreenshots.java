/**
 * Screenshot capture utility.
 *
 * NOTE: This class requires the 'javafx.swing' module in addition to 'javafx.controls'.
 * Compile and run it SEPARATELY using:
 *
 *   javac --module-path lib\javafx-sdk-21.0.12\lib --add-modules javafx.controls,javafx.swing
 *         -cp out -d out src\com\ecomflow\CaptureScreenshots.java
 *
 *   java  --module-path lib\javafx-sdk-21.0.12\lib --add-modules javafx.controls,javafx.swing
 *         -cp "out;resources" com.ecomflow.CaptureScreenshots
 *
 * The main application (Main.java) does NOT depend on javafx.swing and should be
 * compiled/run with --add-modules javafx.controls only.
 */
package com.ecomflow;

import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

import com.ecomflow.gui.AdminDashboardView;
import com.ecomflow.gui.CustomerDashboardView;
import com.ecomflow.gui.LoginView;
import com.ecomflow.gui.MainApp;
import com.ecomflow.gui.RegisterView;
import com.ecomflow.model.Address;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Product;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.animation.PauseTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;

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
                Platform.exit();
            }
        });
    }

    private void captureAll() {
        Customer sampleCustomer = app.getAuthService().registerCustomer(
                "xyz", "xyz@gmail.com", "xyz", "0000000000",
                new Address("123 xyz street", "xyz city", "xyz state", "00000", "India"));
        Admin sampleAdmin = (Admin) app.getDataStore().getUserByEmail("admin@ecomflow.com");
        List<Product> products = app.getProductService().getAllProducts();
        if (products.size() < 2) {
            throw new IllegalStateException("At least two catalog products are required to capture screenshots.");
        }

        CustomerDashboardView custDash = new CustomerDashboardView(app, sampleCustomer);
        AdminDashboardView adminDash = new AdminDashboardView(app, sampleAdmin);

        List<CaptureStep> steps = List.of(
                () -> saveSnapshot(new LoginView(app, app.getAuthService()).getView(), "login_screen.png"),
                () -> saveSnapshot(new RegisterView(app, app.getAuthService()).getView(), "register_screen.png"),
                custDash::showBrowseView,
                () -> saveSnapshot(custDash.getView(), "customer_browse.png"),
                () -> {
                    app.getCartService().addItem(sampleCustomer.getCart(), products.get(0), 1);
                    app.getCartService().addItem(sampleCustomer.getCart(), products.get(1), 2);
                    custDash.showCartView();
                },
                () -> saveSnapshot(custDash.getView(), "customer_cart.png"),
                custDash::showCheckoutView,
                () -> saveSnapshot(custDash.getView(), "customer_checkout.png"),
                () -> app.getOrderService().placeOrder(
                        sampleCustomer.getCart(), "SAVE10", "UPI",
                        new Address("123 xyz street", "xyz city", "xyz state", "00000", "India")),
                custDash::showOrdersView,
                () -> saveSnapshot(custDash.getView(), "customer_orders.png"),
                custDash::showProfileView,
                () -> saveSnapshot(custDash.getView(), "customer_profile.png"),
                adminDash::showProductsView,
                () -> saveSnapshot(adminDash.getView(), "admin_products.png"),
                adminDash::showInventoryView,
                () -> saveSnapshot(adminDash.getView(), "admin_inventory.png"),
                adminDash::showOrdersView,
                () -> saveSnapshot(adminDash.getView(), "admin_orders.png"),
                adminDash::showCustomersView,
                () -> saveSnapshot(adminDash.getView(), "admin_customers.png")
        );
        runCaptureStep(steps, 0);
    }

    private void runCaptureStep(List<CaptureStep> steps, int index) {
        if (index >= steps.size()) {
            System.out.println("====================================================================");
            System.out.println("ALL SCREENSHOTS SUCCESSFULLY SAVED TO resources/images/screenshots/!");
            System.out.println("====================================================================");
            Platform.exit();
            return;
        }

        PauseTransition waitForLayout = new PauseTransition(Duration.millis(350));
        waitForLayout.setOnFinished(event -> {
            try {
                steps.get(index).run();
                runCaptureStep(steps, index + 1);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.exit();
            }
        });
        waitForLayout.play();
    }

    @FunctionalInterface
    private interface CaptureStep {
        void run() throws Exception;
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
