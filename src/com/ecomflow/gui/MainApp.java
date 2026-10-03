package com.ecomflow.gui;

import java.io.File;
import java.time.LocalDate;

import com.ecomflow.model.Address;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Category;
import com.ecomflow.model.Clothing;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Grocery;
import com.ecomflow.model.User;
import com.ecomflow.repository.DataStore;
import com.ecomflow.service.AdminService;
import com.ecomflow.service.AuthenticationService;
import com.ecomflow.service.CartService;
import com.ecomflow.service.CustomerService;
import com.ecomflow.service.DiscountService;
import com.ecomflow.service.InventoryService;
import com.ecomflow.service.OrderService;
import com.ecomflow.service.PaymentService;
import com.ecomflow.service.ProductService;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private Scene mainScene;

    // Core System Singletons
    private DataStore dataStore;
    private AuthenticationService authService;
    private ProductService productService;
    private InventoryService inventoryService;
    private CartService cartService;
    private DiscountService discountService;
    private PaymentService paymentService;
    private OrderService orderService;
    private CustomerService customerService;
    private AdminService adminService;

    @Override
    public void init() {
        this.dataStore = new DataStore();
        this.authService = new AuthenticationService(dataStore);
        this.productService = new ProductService(dataStore);
        this.inventoryService = new InventoryService(dataStore);
        this.cartService = new CartService();
        this.discountService = new DiscountService();
        this.paymentService = new PaymentService();
        this.orderService = new OrderService(dataStore, cartService, discountService, paymentService, inventoryService);
        this.customerService = new CustomerService(dataStore);
        this.adminService = new AdminService(dataStore, orderService, inventoryService);

        seedInitialData();
    }

    private void seedInitialData() {
        // Seed Users
        Address defaultAddr = new Address("42 Tech Boulevard", "Bengaluru", "Karnataka", "560001", "India");
        authService.registerCustomer("Alice Johnson", "alice@ecomflow.com", "pass123", "+91 9876543210", defaultAddr);
        authService.registerAdmin("System Administrator", "admin@ecomflow.com", "adminPass", "+91 9123456780");

        // Seed Categories
        Category catElectronics = new Category(101, "Electronics");
        Category catClothing = new Category(102, "Apparel");
        Category catGrocery = new Category(103, "Groceries");
        dataStore.addCategory(catElectronics);
        dataStore.addCategory(catClothing);
        dataStore.addCategory(catGrocery);

        // Seed Products
        productService.addProduct(new Electronics("Noise-Cancelling Headphones", 199.99, 15, catElectronics, "SoundMax", 24));
        productService.addProduct(new Electronics("Smart Fitness Band 8", 49.99, 25, catElectronics, "FitTrack", 12));
        productService.addProduct(new Clothing("Winter Fleece Jacket", 59.99, 40, catClothing, "L", "Polyester"));
        productService.addProduct(new Clothing("Classic Cotton T-Shirt", 19.99, 50, catClothing, "M", "100% Cotton"));
        productService.addProduct(new Grocery("Organic Almond Milk", 4.99, 30, catGrocery, LocalDate.now().plusDays(15)));
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("EcomFlow — Smart E-Commerce Management System");
        this.primaryStage.setMinWidth(960);
        this.primaryStage.setMinHeight(640);

        showLoginView();
        this.primaryStage.show();
    }

    public void setRoot(Parent rootNode) {
        if (mainScene == null) {
            mainScene = new Scene(rootNode, 1000, 680);
            applyStyles(mainScene);
            primaryStage.setScene(mainScene);
        } else {
            mainScene.setRoot(rootNode);
        }
    }

    private void applyStyles(Scene scene) {
        // Look up stylesheet from resources folder or classpath
        File cssFile = new File("resources/css/styles.css");
        if (cssFile.exists()) {
            scene.getStylesheets().add(cssFile.toURI().toString());
        } else {
            var resource = getClass().getResource("/css/styles.css");
            if (resource != null) {
                scene.getStylesheets().add(resource.toExternalForm());
            }
        }
    }

    public void showLoginView() {
        LoginView loginView = new LoginView(this, authService);
        setRoot(loginView.getView());
    }

    public void showRegisterView() {
        RegisterView registerView = new RegisterView(this, authService);
        setRoot(registerView.getView());
    }

    public void showCustomerDashboard(Customer customer) {
        CustomerDashboardView customerView = new CustomerDashboardView(this, customer);
        setRoot(customerView.getView());
    }

    public void showAdminDashboard(Admin admin) {
        // Interim screen for Phase 10; expanded in Phase 12
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().addAll("app-container", "card");
        root.setStyle("-fx-max-width: 600px; -fx-max-height: 400px;");

        Label title = new Label("Administrator Dashboard");
        title.getStyleClass().add("heading-lg");

        Label badge = new Label("ROLE: SYSTEM ADMINISTRATOR");
        badge.getStyleClass().add("badge-admin");

        Label welcome = new Label("Logged in as: " + admin.getName() + " (" + admin.getEmail() + ")");
        welcome.getStyleClass().add("subtitle");

        Label note = new Label("Phase 10: Routing verified. Full catalog & order management UI builds in Phase 12.");
        note.getStyleClass().add("text-muted");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("btn-outline");
        logoutBtn.setOnAction(e -> {
            authService.logout(admin);
            showLoginView();
        });

        root.getChildren().addAll(title, badge, welcome, note, logoutBtn);

        VBox container = new VBox(root);
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("app-container");
        setRoot(container);
    }

    public void handleUserNavigation(User user) {
        if (user instanceof Customer customer) {
            showCustomerDashboard(customer);
        } else if (user instanceof Admin admin) {
            showAdminDashboard(admin);
        } else {
            showLoginView();
        }
    }

    // Getters for services
    public DataStore getDataStore() { return dataStore; }
    public AuthenticationService getAuthService() { return authService; }
    public ProductService getProductService() { return productService; }
    public InventoryService getInventoryService() { return inventoryService; }
    public CartService getCartService() { return cartService; }
    public DiscountService getDiscountService() { return discountService; }
    public PaymentService getPaymentService() { return paymentService; }
    public OrderService getOrderService() { return orderService; }
    public CustomerService getCustomerService() { return customerService; }
    public AdminService getAdminService() { return adminService; }
}
