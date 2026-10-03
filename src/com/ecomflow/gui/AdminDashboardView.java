package com.ecomflow.gui;

import java.time.LocalDate;
import java.util.List;

import com.ecomflow.enums.OrderStatus;
import com.ecomflow.exceptions.InsufficientStockException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.model.Admin;
import com.ecomflow.model.Category;
import com.ecomflow.model.Clothing;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Electronics;
import com.ecomflow.model.Grocery;
import com.ecomflow.model.Invoice;
import com.ecomflow.model.Order;
import com.ecomflow.model.Product;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AdminDashboardView {
    private final MainApp app;
    private final Admin admin;
    private final BorderPane rootPane;

    // Navigation Buttons
    private Button productsNavBtn;
    private Button inventoryNavBtn;
    private Button ordersNavBtn;
    private Button customersNavBtn;

    public AdminDashboardView(MainApp app, Admin admin) {
        this.app = app;
        this.admin = admin;
        this.rootPane = new BorderPane();
        buildDashboard();
    }

    private void buildDashboard() {
        rootPane.getStyleClass().add("app-container");

        // 1. Sidebar Navigation Rail
        VBox sidebar = buildSidebar();
        rootPane.setLeft(sidebar);

        // 2. Default initial view: Products Management
        showProductsView();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(220);

        // Brand
        Label brand = new Label("🛒 EcomFlow");
        brand.getStyleClass().add("brand-title");

        Label roleBadge = new Label("SYSTEM ADMINISTRATOR");
        roleBadge.getStyleClass().add("badge-admin");

        VBox userSnippet = new VBox(2);
        Label userName = new Label(admin.getName());
        userName.getStyleClass().add("heading-md");
        Label userEmail = new Label(admin.getEmail());
        userEmail.getStyleClass().add("text-muted");
        userSnippet.getChildren().addAll(userName, userEmail);
        userSnippet.setPadding(new Insets(10, 0, 10, 0));

        // Nav Buttons
        productsNavBtn = createNavButton("📦  Products Catalog", () -> showProductsView());
        inventoryNavBtn = createNavButton("📊  Inventory Stock", () -> showInventoryView());
        ordersNavBtn = createNavButton("📑  Customer Orders", () -> showOrdersView());
        customersNavBtn = createNavButton("👥  User Directory", () -> showCustomersView());

        Region navSpacer = new Region();
        VBox.setVgrow(navSpacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪  Sign Out");
        logoutBtn.getStyleClass().add("btn-outline");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            app.getAuthService().logout(admin);
            app.showLoginView();
        });

        sidebar.getChildren().addAll(
                brand, roleBadge, userSnippet,
                new Separator(),
                productsNavBtn, inventoryNavBtn, ordersNavBtn, customersNavBtn,
                navSpacer,
                logoutBtn
        );

        return sidebar;
    }

    private Button createNavButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("nav-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void setActiveNav(Button activeButton) {
        Button[] allButtons = {productsNavBtn, inventoryNavBtn, ordersNavBtn, customersNavBtn};
        for (Button btn : allButtons) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-btn-active")) {
            activeButton.getStyleClass().add("nav-btn-active");
        }
    }

    // =========================================================================
    // 1. PRODUCTS MANAGEMENT SUB-VIEW
    // =========================================================================

    public void showProductsView() {
        setActiveNav(productsNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        HBox topBar = new HBox();
        VBox titleBox = new VBox(2);
        Label title = new Label("Product Catalog Management");
        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Add, view, inspect, and remove products in the system");
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);
        topBar.getChildren().add(titleBox);

        HBox layout = new HBox(20);
        HBox.setHgrow(layout, Priority.ALWAYS);

        // --- Left: Products TableView ---
        TableView<Product> table = new TableView<>();
        table.getStyleClass().add("card-sm");
        HBox.setHgrow(table, Priority.ALWAYS);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Column: Thumbnail
        TableColumn<Product, Product> thumbCol = new TableColumn<>("Image");
        thumbCol.setPrefWidth(60);
        thumbCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        thumbCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                } else {
                    String cat = (p.getCategory() != null) ? p.getCategory().getName() : "";
                    Node img = GuiUtils.createProductImageView("product-" + p.getProductId() + ".png", cat, 40, 40);
                    setGraphic(img);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Column: ID
        TableColumn<Product, Number> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProductId()));

        // Column: Name
        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setPrefWidth(160);
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        // Column: Category
        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setPrefWidth(100);
        catCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategory() != null ? cellData.getValue().getCategory().getName() : "N/A"));

        // Column: Price
        TableColumn<Product, String> priceCol = new TableColumn<>("Price");
        priceCol.setPrefWidth(80);
        priceCol.setCellValueFactory(cellData -> new SimpleStringProperty(GuiUtils.formatCurrency(cellData.getValue().getPrice())));

        // Column: Stock
        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setPrefWidth(70);
        stockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()));

        // Column: Actions (Delete)
        TableColumn<Product, Product> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(80);
        actionCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button delBtn = new Button("Delete");
            {
                delBtn.getStyleClass().add("btn-danger");
                delBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4px 8px;");
                delBtn.setOnAction(e -> {
                    Product p = getItem();
                    if (p != null) {
                        try {
                            app.getProductService().deleteProduct(p.getProductId());
                            GuiUtils.showInfo("Product Deleted", "Removed '" + p.getName() + "' from catalog.");
                            table.setItems(FXCollections.observableArrayList(app.getProductService().getAllProducts()));
                        } catch (ProductNotFoundException ex) {
                            GuiUtils.showError(ex.getMessage());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setGraphic(empty || p == null ? null : delBtn);
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(thumbCol, idCol, nameCol, catCol, priceCol, stockCol, actionCol);
        table.setItems(FXCollections.observableArrayList(app.getProductService().getAllProducts()));

        // --- Right: Add Product Form Card ---
        VBox addFormCard = new VBox(12);
        addFormCard.getStyleClass().add("card");
        addFormCard.setPrefWidth(340);
        addFormCard.setMinWidth(320);

        Label formHeader = new Label("Add New Product");
        formHeader.getStyleClass().add("heading-md");

        Label nameLbl = new Label("Product Name *");
        nameLbl.getStyleClass().add("label-field");
        TextField nameIn = new TextField();
        nameIn.setPromptText("e.g. Wireless Ergonomic Mouse");

        Label catLbl = new Label("Category *");
        catLbl.getStyleClass().add("label-field");
        ComboBox<Category> catCombo = new ComboBox<>();
        catCombo.setItems(FXCollections.observableArrayList(app.getDataStore().getAllCategories()));
        if (!catCombo.getItems().isEmpty()) catCombo.setValue(catCombo.getItems().get(0));
        catCombo.setMaxWidth(Double.MAX_VALUE);

        Label typeLbl = new Label("Product Type *");
        typeLbl.getStyleClass().add("label-field");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Electronics", "Clothing", "Grocery");
        typeCombo.setValue("Electronics");
        typeCombo.setMaxWidth(Double.MAX_VALUE);

        // Price & Stock
        HBox numRow = new HBox(10);
        VBox priceBox = new VBox(4, new Label("Price ($) *"), new TextField("49.99"));
        VBox stockBox = new VBox(4, new Label("Stock *"), new TextField("20"));
        HBox.setHgrow(priceBox, Priority.ALWAYS);
        HBox.setHgrow(stockBox, Priority.ALWAYS);
        numRow.getChildren().addAll(priceBox, stockBox);

        // Subtype-specific dynamic field box
        VBox dynamicBox = new VBox(8);
        TextField field1 = new TextField("LogiTech");
        field1.setPromptText("Brand");
        TextField field2 = new TextField("24");
        field2.setPromptText("Warranty Months");
        DatePicker expiryPicker = new DatePicker(LocalDate.now().plusMonths(3));
        expiryPicker.setMaxWidth(Double.MAX_VALUE);

        dynamicBox.getChildren().addAll(new Label("Brand:"), field1, new Label("Warranty Months:"), field2);

        typeCombo.setOnAction(e -> {
            dynamicBox.getChildren().clear();
            String selectedType = typeCombo.getValue();
            if ("Electronics".equals(selectedType)) {
                field1.setPromptText("Brand Name");
                field2.setPromptText("Warranty Months");
                field1.setText("SoundMax");
                field2.setText("12");
                dynamicBox.getChildren().addAll(new Label("Brand:"), field1, new Label("Warranty Months:"), field2);
            } else if ("Clothing".equals(selectedType)) {
                field1.setPromptText("Size (e.g. S, M, L, XL)");
                field2.setPromptText("Material (e.g. Cotton)");
                field1.setText("L");
                field2.setText("100% Cotton");
                dynamicBox.getChildren().addAll(new Label("Size:"), field1, new Label("Material:"), field2);
            } else if ("Grocery".equals(selectedType)) {
                dynamicBox.getChildren().addAll(new Label("Expiry Date:"), expiryPicker);
            }
        });

        Button addProductBtn = new Button("Add Product ➕");
        addProductBtn.getStyleClass().add("btn-primary");
        addProductBtn.setMaxWidth(Double.MAX_VALUE);

        addProductBtn.setOnAction(e -> {
            try {
                String pName = nameIn.getText();
                Category pCat = catCombo.getValue();
                double pPrice = Double.parseDouble(((TextField) priceBox.getChildren().get(1)).getText());
                int pStock = Integer.parseInt(((TextField) stockBox.getChildren().get(1)).getText());
                String pType = typeCombo.getValue();

                if (pPrice <= 0) throw new IllegalArgumentException("Price must be greater than 0.");
                if (pStock < 0) throw new IllegalArgumentException("Stock cannot be negative.");

                Product newProduct;
                if ("Electronics".equals(pType)) {
                    String brand = field1.getText();
                    int warranty = Integer.parseInt(field2.getText());
                    newProduct = new Electronics(pName, pPrice, pStock, pCat, brand, warranty);
                } else if ("Clothing".equals(pType)) {
                    String size = field1.getText();
                    String material = field2.getText();
                    newProduct = new Clothing(pName, pPrice, pStock, pCat, size, material);
                } else {
                    LocalDate expiry = expiryPicker.getValue();
                    newProduct = new Grocery(pName, pPrice, pStock, pCat, expiry);
                }

                app.getProductService().addProduct(newProduct);
                GuiUtils.showInfo("Product Added", "Successfully added '" + pName + "' to the catalog.");
                table.setItems(FXCollections.observableArrayList(app.getProductService().getAllProducts()));

                nameIn.clear();
            } catch (NumberFormatException ex) {
                GuiUtils.showError("Invalid Input", "Please enter valid numeric values for price and stock.");
            } catch (Exception ex) {
                GuiUtils.showError("Validation Error", ex.getMessage());
            }
        });

        addFormCard.getChildren().addAll(
                formHeader, nameLbl, nameIn,
                catLbl, catCombo,
                typeLbl, typeCombo,
                numRow, dynamicBox,
                addProductBtn
        );

        layout.getChildren().addAll(table, addFormCard);
        content.getChildren().addAll(topBar, layout);
        rootPane.setCenter(content);
    }

    // =========================================================================
    // 2. INVENTORY MANAGEMENT SUB-VIEW
    // =========================================================================

    public void showInventoryView() {
        setActiveNav(inventoryNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        VBox titleBox = new VBox(2);
        Label title = new Label("Inventory & Stock Management");
        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Monitor stock levels and perform real-time restock adjustments");
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        VBox inventoryCard = new VBox(12);
        inventoryCard.getStyleClass().add("card");

        TableView<Product> invTable = new TableView<>();
        invTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Product, Number> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getProductId()));

        TableColumn<Product, String> nameCol = new TableColumn<>("Product");
        nameCol.setPrefWidth(220);
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Product, String> catCol = new TableColumn<>("Category");
        catCol.setPrefWidth(120);
        catCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategory() != null ? cellData.getValue().getCategory().getName() : "N/A"));

        TableColumn<Product, Number> stockCol = new TableColumn<>("Available Stock");
        stockCol.setPrefWidth(120);
        stockCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()));

        // Adjustment Controls Column
        TableColumn<Product, Product> adjustCol = new TableColumn<>("Stock Adjustments");
        adjustCol.setPrefWidth(220);
        adjustCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        adjustCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                } else {
                    HBox controlBox = new HBox(8);
                    controlBox.setAlignment(Pos.CENTER);

                    Button minus1 = new Button("−1");
                    minus1.getStyleClass().add("btn-qty");
                    minus1.setDisable(p.getStock() <= 0);
                    minus1.setOnAction(e -> {
                        try {
                            app.getInventoryService().removeStock(p.getProductId(), 1);
                            invTable.refresh();
                        } catch (Exception ex) {
                            GuiUtils.showError(ex.getMessage());
                        }
                    });

                    Button plus1 = new Button("+1");
                    plus1.getStyleClass().add("btn-qty");
                    plus1.setOnAction(e -> {
                        try {
                            app.getInventoryService().addStock(p.getProductId(), 1);
                            invTable.refresh();
                        } catch (Exception ex) {
                            GuiUtils.showError(ex.getMessage());
                        }
                    });

                    Button plus10 = new Button("+10 Restock");
                    plus10.getStyleClass().add("btn-chip");
                    plus10.setOnAction(e -> {
                        try {
                            app.getInventoryService().addStock(p.getProductId(), 10);
                            invTable.refresh();
                            GuiUtils.showInfo("Restocked", "Added +10 units to '" + p.getName() + "'.");
                        } catch (Exception ex) {
                            GuiUtils.showError(ex.getMessage());
                        }
                    });

                    controlBox.getChildren().addAll(minus1, plus1, plus10);
                    setGraphic(controlBox);
                }
            }
        });

        invTable.getColumns().addAll(idCol, nameCol, catCol, stockCol, adjustCol);
        invTable.setItems(FXCollections.observableArrayList(app.getProductService().getAllProducts()));

        inventoryCard.getChildren().add(invTable);
        content.getChildren().addAll(titleBox, inventoryCard);
        rootPane.setCenter(content);
    }

    // =========================================================================
    // 3. ORDERS MANAGEMENT SUB-VIEW
    // =========================================================================

    public void showOrdersView() {
        setActiveNav(ordersNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        VBox titleBox = new VBox(2);
        Label title = new Label("Customer Order Fulfillment");
        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Track all customer purchases and advance lifecycle statuses");
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        List<Order> orders = app.getAdminService().listAllOrders();

        if (orders.isEmpty()) {
            VBox emptyCard = new VBox(16);
            emptyCard.getStyleClass().add("card");
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(60));
            Label emptyMsg = new Label("No customer orders placed yet.");
            emptyMsg.getStyleClass().add("heading-md");
            emptyCard.getChildren().add(emptyMsg);
            content.getChildren().addAll(titleBox, emptyCard);
            rootPane.setCenter(content);
            return;
        }

        TableView<Order> ordersTable = new TableView<>();
        ordersTable.getStyleClass().add("card-sm");
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Order, Number> idCol = new TableColumn<>("Order ID");
        idCol.setPrefWidth(70);
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getOrderId()));

        TableColumn<Order, String> custCol = new TableColumn<>("Customer");
        custCol.setPrefWidth(160);
        custCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCustomer() != null ? cellData.getValue().getCustomer().getName() : "Guest"));

        TableColumn<Order, Number> itemsCol = new TableColumn<>("Items");
        itemsCol.setPrefWidth(60);
        itemsCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getItems().size()));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total Paid");
        totalCol.setPrefWidth(90);
        totalCol.setCellValueFactory(cellData -> new SimpleStringProperty(GuiUtils.formatCurrency(cellData.getValue().getTotal())));

        TableColumn<Order, Order> statusBadgeCol = new TableColumn<>("Current Status");
        statusBadgeCol.setPrefWidth(120);
        statusBadgeCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        statusBadgeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    setGraphic(GuiUtils.createStatusBadge(order.getStatus()));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Lifecycle Status Update Action Column
        TableColumn<Order, Order> updateStatusCol = new TableColumn<>("Update Status");
        updateStatusCol.setPrefWidth(220);
        updateStatusCol.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
        updateStatusCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<OrderStatus> statusCombo = new ComboBox<>();
            {
                statusCombo.setItems(FXCollections.observableArrayList(OrderStatus.values()));
                statusCombo.setOnAction(e -> {
                    Order order = getItem();
                    OrderStatus newStatus = statusCombo.getValue();
                    if (order != null && newStatus != null && newStatus != order.getStatus()) {
                        try {
                            app.getAdminService().updateOrderStatus(order.getOrderId(), newStatus);
                            ordersTable.refresh();
                            GuiUtils.showInfo("Status Updated", "Order #" + order.getOrderId() + " transitioned to " + newStatus);
                        } catch (Exception ex) {
                            GuiUtils.showError("Status Update Error", ex.getMessage());
                            statusCombo.setValue(order.getStatus());
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Order order, boolean empty) {
                super.updateItem(order, empty);
                if (empty || order == null) {
                    setGraphic(null);
                } else {
                    statusCombo.setValue(order.getStatus());
                    // Disable changing if already delivered or cancelled
                    statusCombo.setDisable(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED);
                    setGraphic(statusCombo);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        ordersTable.getColumns().addAll(idCol, custCol, itemsCol, totalCol, statusBadgeCol, updateStatusCol);
        ordersTable.setItems(FXCollections.observableArrayList(orders));

        content.getChildren().addAll(titleBox, ordersTable);
        rootPane.setCenter(content);
    }

    // =========================================================================
    // 4. CUSTOMERS DIRECTORY SUB-VIEW
    // =========================================================================

    public void showCustomersView() {
        setActiveNav(customersNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        VBox titleBox = new VBox(2);
        Label title = new Label("Registered Customers Directory");
        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Overview of registered customer accounts and activity history");
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        TableView<Customer> custTable = new TableView<>();
        custTable.getStyleClass().add("card-sm");
        custTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, Number> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(60);
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Customer Name");
        nameCol.setPrefWidth(160);
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Customer, String> emailCol = new TableColumn<>("Email Address");
        emailCol.setPrefWidth(200);
        emailCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setPrefWidth(120);
        phoneCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));

        TableColumn<Customer, String> addrCol = new TableColumn<>("Shipping Address");
        addrCol.setPrefWidth(220);
        addrCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getAddress() != null ? cellData.getValue().getAddress().toString() : "N/A"));

        TableColumn<Customer, Number> ordersCol = new TableColumn<>("Total Orders");
        ordersCol.setPrefWidth(90);
        ordersCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(
                cellData.getValue().getOrderHistory() != null ? cellData.getValue().getOrderHistory().size() : 0));

        custTable.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, addrCol, ordersCol);
        custTable.setItems(FXCollections.observableArrayList(app.getAdminService().listAllCustomers()));

        content.getChildren().addAll(titleBox, custTable);
        rootPane.setCenter(content);
    }

    public Parent getView() {
        return rootPane;
    }
}
