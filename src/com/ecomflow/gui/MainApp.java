package com.ecomflow.gui;

import java.io.File;

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
        // ── Demo Admin account (only system user seeded at startup) ───────────────
        authService.registerAdmin("System Administrator", "admin@ecomflow.com", "adminPass", "+91 9123456780");

        // ── Categories ────────────────────────────────────────────────────────────
        Category catElectronics = new Category(101, "Electronics");
        Category catClothing    = new Category(102, "Apparel");
        Category catGrocery     = new Category(103, "Groceries");
        Category catHome        = new Category(104, "Home & Living");
        Category catAccessories = new Category(105, "Accessories");

        dataStore.addCategory(catElectronics);
        dataStore.addCategory(catClothing);
        dataStore.addCategory(catGrocery);
        dataStore.addCategory(catHome);
        dataStore.addCategory(catAccessories);

        // ── Products Catalog (18 Sample Products) ──────────────────────────────────
        // 1. Electronics
        productService.addProduct(new Electronics(5001, "Noise-Cancelling Headphones", 199.99, 15, catElectronics, "SoundMax", 24,
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&q=80"));
        productService.addProduct(new Electronics(5002, "Pro Smartphone 5G", 799.99, 12, catElectronics, "ApexMobile", 12,
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&q=80"));
        productService.addProduct(new Electronics(5003, "Ultra Slim Laptop 16\"", 1299.99, 8, catElectronics, "ProTech", 36,
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&q=80"));
        productService.addProduct(new Electronics(5004, "Smart Fitness Watch Pro", 149.99, 20, catElectronics, "PulseTech", 18,
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&q=80"));
        productService.addProduct(new Electronics(5005, "Mirrorless 4K Camera", 899.99, 10, catElectronics, "Optix", 24,
                "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&q=80"));
        productService.addProduct(new Electronics(5006, "True Wireless Earbuds ANC", 79.99, 35, catElectronics, "AcousticAir", 12,
                "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&q=80"));

        // 2. Apparel & Clothing
        productService.addProduct(new Clothing(5007, "Classic Cotton T-Shirt", 24.99, 50, catClothing, "M", "100% Cotton",
                "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=400&q=80"));
        productService.addProduct(new Clothing(5008, "Winter Fleece Jacket", 89.99, 35, catClothing, "L", "Polyester",
                "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400&q=80"));
        productService.addProduct(new Clothing(5009, "Slim Fit Denim Jeans", 69.99, 40, catClothing, "32", "Denim",
                "https://images.unsplash.com/photo-1542272604-780c96856592?w=400&q=80"));
        productService.addProduct(new Clothing(5010, "Urban Streetwear Hoodie", 54.99, 28, catClothing, "XL", "Fleece Cotton",
                "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=400&q=80"));
        productService.addProduct(new Clothing(5011, "Performance Running Sneakers", 119.99, 22, catClothing, "10", "Breathable Mesh",
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&q=80"));

        // 3. Groceries & Pantry
        productService.addProduct(new Grocery(5012, "Organic Almond Milk 1L", 4.99, 30, catGrocery, java.time.LocalDate.now().plusDays(10),
                "https://images.unsplash.com/photo-1563636619-e9143da7973b?w=400&q=80"));
        productService.addProduct(new Grocery(5013, "Royal Basmati Rice 5 kg", 18.50, 45, catGrocery, java.time.LocalDate.now().plusMonths(6),
                "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&q=80"));
        productService.addProduct(new Grocery(5014, "Artisan Coffee Beans 500g", 14.99, 25, catGrocery, java.time.LocalDate.now().plusMonths(4),
                "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400&q=80"));
        productService.addProduct(new Grocery(5015, "Pure Raw Mountain Honey 500g", 12.50, 20, catGrocery, java.time.LocalDate.now().plusMonths(12),
                "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400&q=80"));
        productService.addProduct(new Grocery(5016, "Extra Virgin Olive Oil 750ml", 16.99, 18, catGrocery, java.time.LocalDate.now().plusMonths(8),
                "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=400&q=80"));

        // 4. Home & Living / Accessories
        productService.addProduct(new Electronics(5017, "Ceramic Aroma Diffuser", 39.99, 16, catHome, "AromaGlow", 12,
                "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=400&q=80"));
        productService.addProduct(new Clothing(5018, "Polarized Classic Sunglasses", 45.00, 30, catAccessories, "Standard", "Polycarbonate",
                "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=400&q=80"));
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("EcomFlow — Smart E-Commerce Management System");
        this.primaryStage.setMinWidth(960);
        this.primaryStage.setMinHeight(640);

        showLandingView();
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

    public void showLandingView() {
        LandingView landingView = new LandingView(this);
        setRoot(landingView.getView());
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
        AdminDashboardView adminView = new AdminDashboardView(this, admin);
        setRoot(adminView.getView());
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
