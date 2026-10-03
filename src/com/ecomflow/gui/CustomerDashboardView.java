package com.ecomflow.gui;

import java.util.ArrayList;
import java.util.List;

import com.ecomflow.enums.OrderStatus;
import com.ecomflow.exceptions.EmptyCartException;
import com.ecomflow.exceptions.InsufficientStockException;
import com.ecomflow.exceptions.InvalidPaymentException;
import com.ecomflow.exceptions.ProductNotFoundException;
import com.ecomflow.interfaces.Discountable;
import com.ecomflow.model.Address;
import com.ecomflow.model.Cart;
import com.ecomflow.model.CartItem;
import com.ecomflow.model.Category;
import com.ecomflow.model.Customer;
import com.ecomflow.model.Invoice;
import com.ecomflow.model.Order;
import com.ecomflow.model.OrderItem;
import com.ecomflow.model.Product;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CustomerDashboardView {
    private final MainApp app;
    private final Customer customer;
    private final BorderPane rootPane;

    // Navigation Buttons
    private Button browseNavBtn;
    private Button cartNavBtn;
    private Button checkoutNavBtn;
    private Button ordersNavBtn;
    private Button profileNavBtn;
    private Label cartBadgeLabel;

    // Active subview state
    private String activeCoupon = "";

    public CustomerDashboardView(MainApp app, Customer customer) {
        this.app = app;
        this.customer = customer;
        this.rootPane = new BorderPane();
        buildDashboard();
    }

    private void buildDashboard() {
        rootPane.getStyleClass().add("app-container");

        // 1. Sidebar Navigation Rail
        VBox sidebar = buildSidebar();
        rootPane.setLeft(sidebar);

        // 2. Default initial view: Browse Products
        showBrowseView();
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(12);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(220);

        // Brand
        Label brand = new Label("🛒 EcomFlow");
        brand.getStyleClass().add("brand-title");

        Label roleBadge = new Label("CUSTOMER PORTAL");
        roleBadge.getStyleClass().add("badge-role");

        VBox userSnippet = new VBox(2);
        Label userName = new Label(customer.getName());
        userName.getStyleClass().add("heading-md");
        Label userEmail = new Label(customer.getEmail());
        userEmail.getStyleClass().add("text-muted");
        userSnippet.getChildren().addAll(userName, userEmail);
        userSnippet.setPadding(new Insets(10, 0, 10, 0));

        // Nav Buttons
        browseNavBtn = createNavButton("🏷️  Browse Products", () -> showBrowseView());
        
        // Cart Nav Button with dynamic count badge
        cartBadgeLabel = new Label(String.valueOf(getCartItemCount()));
        cartBadgeLabel.getStyleClass().add("badge-cart-count");
        HBox cartBtnContent = new HBox(8);
        cartBtnContent.setAlignment(Pos.CENTER_LEFT);
        Label cartText = new Label("🛒  My Cart");
        cartText.setStyle("-fx-font-weight: bold;");
        Region cartSpacer = new Region();
        HBox.setHgrow(cartSpacer, Priority.ALWAYS);
        cartBtnContent.getChildren().addAll(cartText, cartSpacer, cartBadgeLabel);

        cartNavBtn = new Button();
        cartNavBtn.setGraphic(cartBtnContent);
        cartNavBtn.getStyleClass().add("nav-btn");
        cartNavBtn.setMaxWidth(Double.MAX_VALUE);
        cartNavBtn.setOnAction(e -> showCartView());

        checkoutNavBtn = createNavButton("💳  Checkout", () -> showCheckoutView());
        ordersNavBtn = createNavButton("📦  Order History", () -> showOrdersView());
        profileNavBtn = createNavButton("👤  My Profile", () -> showProfileView());

        Region navSpacer = new Region();
        VBox.setVgrow(navSpacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪  Sign Out");
        logoutBtn.getStyleClass().add("btn-outline");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            app.getAuthService().logout(customer);
            app.showLoginView();
        });

        sidebar.getChildren().addAll(
                brand, roleBadge, userSnippet,
                new Separator(),
                browseNavBtn, cartNavBtn, checkoutNavBtn, ordersNavBtn, profileNavBtn,
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
        Button[] allButtons = {browseNavBtn, cartNavBtn, checkoutNavBtn, ordersNavBtn, profileNavBtn};
        for (Button btn : allButtons) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-btn-active")) {
            activeButton.getStyleClass().add("nav-btn-active");
        }
        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = getCartItemCount();
        cartBadgeLabel.setText(String.valueOf(count));
        cartBadgeLabel.setVisible(count > 0);
    }

    private int getCartItemCount() {
        Cart cart = customer.getCart();
        if (cart == null) return 0;
        int sum = 0;
        for (CartItem item : cart.getItems()) {
            sum += item.getQuantity();
        }
        return sum;
    }

    // =========================================================================
    // 1. BROWSE PRODUCTS SUB-VIEW
    // =========================================================================

    public void showBrowseView() {
        setActiveNav(browseNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        // Header & Filters
        HBox topBar = new HBox(16);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Product Catalog");
        title.getStyleClass().add("heading-lg");
        Label subtitle = new Label("Explore all available products and categories");
        subtitle.getStyleClass().add("subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search Input
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search products...");
        searchField.setPrefWidth(240);

        // Category Filter
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All Categories");
        for (Category cat : app.getDataStore().getAllCategories()) {
            categoryFilter.getItems().add(cat.getName());
        }
        categoryFilter.setValue("All Categories");

        topBar.getChildren().addAll(titleBox, spacer, searchField, categoryFilter);

        // Products Grid
        FlowPane productGrid = new FlowPane(16, 16);
        productGrid.setAlignment(Pos.TOP_LEFT);

        // Helper to refresh filtered catalog
        Runnable refreshCatalog = () -> {
            productGrid.getChildren().clear();
            String search = searchField.getText();
            String cat = categoryFilter.getValue();

            List<Product> products = app.getProductService().getAllProducts();
            for (Product p : products) {
                boolean matchesName = (search == null || search.trim().isEmpty() ||
                        p.getName().toLowerCase().contains(search.toLowerCase().trim()));
                boolean matchesCat = (cat == null || cat.equals("All Categories") ||
                        (p.getCategory() != null && p.getCategory().getName().equalsIgnoreCase(cat)));

                if (matchesName && matchesCat) {
                    ProductCard card = new ProductCard(p, () -> {
                        app.getCartService().addItem(customer.getCart(), p, 1);
                        updateCartBadge();
                        GuiUtils.showInfo("Added to Cart", "Added 1x '" + p.getName() + "' to your cart.");
                    });
                    productGrid.getChildren().add(card);
                }
            }

            if (productGrid.getChildren().isEmpty()) {
                VBox emptyBox = new VBox(12);
                emptyBox.getStyleClass().add("empty-state-box");
                emptyBox.setPrefWidth(500);

                Label emptyIcon = new Label("🔍");
                emptyIcon.setStyle("-fx-font-size: 36px;");

                Label emptyLabel = new Label("No products match your search or filter.");
                emptyLabel.getStyleClass().add("heading-md");

                Label emptySub = new Label("Try adjusting your keyword or selecting 'All Categories'.");
                emptySub.getStyleClass().add("subtitle");

                Button resetBtn = new Button("Clear Search & Filters");
                resetBtn.getStyleClass().add("btn-outline");
                resetBtn.setOnAction(e -> {
                    searchField.clear();
                    categoryFilter.setValue("All Categories");
                });

                emptyBox.getChildren().addAll(emptyIcon, emptyLabel, emptySub, resetBtn);
                productGrid.getChildren().add(emptyBox);
            }
        };

        searchField.textProperty().addListener((obs, oldV, newV) -> refreshCatalog.run());
        categoryFilter.setOnAction(e -> refreshCatalog.run());

        refreshCatalog.run();

        ScrollPane scrollPane = new ScrollPane(productGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        content.getChildren().addAll(topBar, scrollPane);
        rootPane.setCenter(content);
    }

    // =========================================================================
    // 2. SHOPPING CART SUB-VIEW
    // =========================================================================

    public void showCartView() {
        setActiveNav(cartNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        Label title = new Label("My Shopping Cart");
        title.getStyleClass().add("heading-lg");

        Cart cart = customer.getCart();

        if (cart == null || cart.isEmpty()) {
            VBox emptyCard = new VBox(16);
            emptyCard.getStyleClass().add("card");
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(60));

            Label emptyMsg = new Label("🛒 Your shopping cart is empty");
            emptyMsg.getStyleClass().add("heading-md");

            Label emptySub = new Label("Discover great deals and add items to your cart!");
            emptySub.getStyleClass().add("subtitle");

            Button shopBtn = new Button("Browse Catalog");
            shopBtn.getStyleClass().add("btn-primary");
            shopBtn.setOnAction(e -> showBrowseView());

            emptyCard.getChildren().addAll(emptyMsg, emptySub, shopBtn);
            content.getChildren().addAll(title, emptyCard);
            rootPane.setCenter(content);
            return;
        }

        // Cart Split View (Items list on Left, Order Summary Card on Right)
        HBox cartLayout = new HBox(20);

        // Cart Items List
        VBox itemsBox = new VBox(12);
        HBox.setHgrow(itemsBox, Priority.ALWAYS);

        for (CartItem item : new ArrayList<>(cart.getItems())) {
            Product p = item.getProduct();

            HBox itemRow = new HBox(16);
            itemRow.getStyleClass().add("card-sm");
            itemRow.setAlignment(Pos.CENTER_LEFT);

            Node thumb = GuiUtils.createProductImageView("product-" + p.getProductId() + ".png",
                    p.getCategory() != null ? p.getCategory().getName() : "", 64, 64);

            VBox itemDetails = new VBox(4);
            HBox.setHgrow(itemDetails, Priority.ALWAYS);

            Label name = new Label(p.getName());
            name.getStyleClass().add("heading-md");

            Label unitPrice = new Label("Unit Price: " + GuiUtils.formatCurrency(p.getPrice()));
            unitPrice.getStyleClass().add("text-muted");
            itemDetails.getChildren().addAll(name, unitPrice);

            // Quantity adjusters
            HBox qtyBox = new HBox(6);
            qtyBox.setAlignment(Pos.CENTER);

            Button minusBtn = new Button("−");
            minusBtn.getStyleClass().add("btn-qty");
            minusBtn.setOnAction(e -> {
                if (item.getQuantity() > 1) {
                    app.getCartService().updateQuantity(cart, p.getProductId(), item.getQuantity() - 1);
                } else {
                    app.getCartService().removeItem(cart, p.getProductId());
                }
                showCartView();
            });

            Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
            qtyLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 8px;");

            Button plusBtn = new Button("+");
            plusBtn.getStyleClass().add("btn-qty");
            plusBtn.setOnAction(e -> {
                if (item.getQuantity() < p.getStock()) {
                    app.getCartService().updateQuantity(cart, p.getProductId(), item.getQuantity() + 1);
                    showCartView();
                } else {
                    GuiUtils.showError("Stock Limit Reached", "Only " + p.getStock() + " units available in stock.");
                }
            });

            qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

            // Subtotal
            Label subtotal = new Label(GuiUtils.formatCurrency(item.getSubtotal()));
            subtotal.getStyleClass().add("price-tag");
            subtotal.setPrefWidth(90);
            subtotal.setAlignment(Pos.CENTER_RIGHT);

            // Remove Button
            Button removeBtn = new Button("🗑️");
            removeBtn.getStyleClass().add("btn-danger-outline");
            removeBtn.setOnAction(e -> {
                app.getCartService().removeItem(cart, p.getProductId());
                showCartView();
            });

            itemRow.getChildren().addAll(thumb, itemDetails, qtyBox, subtotal, removeBtn);
            itemsBox.getChildren().add(itemRow);
        }

        // Summary Card
        VBox summaryCard = new VBox(14);
        summaryCard.getStyleClass().add("card");
        summaryCard.setPrefWidth(300);
        summaryCard.setMinWidth(280);

        Label summaryTitle = new Label("Order Summary");
        summaryTitle.getStyleClass().add("heading-md");

        HBox subtotalRow = new HBox();
        Label subLabel = new Label("Items Subtotal (" + getCartItemCount() + "):");
        Region subSpacer = new Region();
        HBox.setHgrow(subSpacer, Priority.ALWAYS);
        Label subVal = new Label(GuiUtils.formatCurrency(app.getCartService().getTotal(cart)));
        subVal.setStyle("-fx-font-weight: bold;");
        subtotalRow.getChildren().addAll(subLabel, subSpacer, subVal);

        HBox shippingRow = new HBox();
        Label shipLabel = new Label("Standard Shipping:");
        Region shipSpacer = new Region();
        HBox.setHgrow(shipSpacer, Priority.ALWAYS);
        Label shipVal = new Label("FREE");
        shipVal.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
        shippingRow.getChildren().addAll(shipLabel, shipSpacer, shipVal);

        Separator sep = new Separator();

        HBox totalRow = new HBox();
        Label totalLabel = new Label("Estimated Total:");
        totalLabel.getStyleClass().add("heading-md");
        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        Label totalVal = new Label(GuiUtils.formatCurrency(app.getCartService().getTotal(cart)));
        totalVal.getStyleClass().add("price-tag");
        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalVal);

        Button checkoutBtn = new Button("Proceed to Checkout ➡️");
        checkoutBtn.getStyleClass().add("btn-accent");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setOnAction(e -> showCheckoutView());

        Button clearBtn = new Button("Clear Cart");
        clearBtn.getStyleClass().add("btn-outline");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> {
            app.getCartService().clearCart(cart);
            showCartView();
        });

        summaryCard.getChildren().addAll(
                summaryTitle, subtotalRow, shippingRow, sep,
                totalRow, checkoutBtn, clearBtn
        );

        ScrollPane itemsScroll = new ScrollPane(itemsBox);
        itemsScroll.setFitToWidth(true);
        itemsScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(itemsScroll, Priority.ALWAYS);

        cartLayout.getChildren().addAll(itemsScroll, summaryCard);
        content.getChildren().addAll(title, cartLayout);
        rootPane.setCenter(content);
    }

    // =========================================================================
    // 3. CHECKOUT SUB-VIEW
    // =========================================================================

    public void showCheckoutView() {
        setActiveNav(checkoutNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        Label title = new Label("Checkout & Payment");
        title.getStyleClass().add("heading-lg");

        Cart cart = customer.getCart();
        if (cart == null || cart.isEmpty()) {
            VBox emptyCard = new VBox(16);
            emptyCard.getStyleClass().add("card");
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(60));

            Label emptyMsg = new Label("Your cart is empty. Please add items before checking out.");
            emptyMsg.getStyleClass().add("heading-md");

            Button shopBtn = new Button("Return to Catalog");
            shopBtn.getStyleClass().add("btn-primary");
            shopBtn.setOnAction(e -> showBrowseView());

            emptyCard.getChildren().addAll(emptyMsg, shopBtn);
            content.getChildren().addAll(title, emptyCard);
            rootPane.setCenter(content);
            return;
        }

        HBox checkoutGrid = new HBox(20);

        // Left Form: Shipping & Payment Information
        VBox formCard = new VBox(16);
        formCard.getStyleClass().add("card");
        HBox.setHgrow(formCard, Priority.ALWAYS);

        Label shipTitle = new Label("1. Shipping Address");
        shipTitle.getStyleClass().add("heading-md");

        Address currAddr = customer.getAddress();
        TextField streetField = new TextField(currAddr != null ? currAddr.getStreet() : "");
        streetField.setPromptText("Street Address");

        GridPane addrGrid = new GridPane();
        addrGrid.setHgap(10);
        addrGrid.setVgap(10);

        TextField cityField = new TextField(currAddr != null ? currAddr.getCity() : "");
        cityField.setPromptText("City");
        TextField stateField = new TextField(currAddr != null ? currAddr.getState() : "");
        stateField.setPromptText("State");
        TextField pinField = new TextField(currAddr != null ? currAddr.getPincode() : "");
        pinField.setPromptText("Pincode");
        TextField countryField = new TextField(currAddr != null ? currAddr.getCountry() : "India");
        countryField.setPromptText("Country");

        addrGrid.add(cityField, 0, 0);
        addrGrid.add(stateField, 1, 0);
        addrGrid.add(pinField, 0, 1);
        addrGrid.add(countryField, 1, 1);

        Label payTitle = new Label("2. Payment Method");
        payTitle.getStyleClass().add("heading-md");
        payTitle.setPadding(new Insets(10, 0, 0, 0));

        ToggleGroup payGroup = new ToggleGroup();
        RadioButton upiRadio = new RadioButton("UPI / QR Code");
        upiRadio.setToggleGroup(payGroup);
        upiRadio.setSelected(true);

        RadioButton cardRadio = new RadioButton("Credit / Debit Card");
        cardRadio.setToggleGroup(payGroup);

        RadioButton codRadio = new RadioButton("Cash on Delivery (COD)");
        codRadio.setToggleGroup(payGroup);

        HBox radioBox = new HBox(16, upiRadio, cardRadio, codRadio);

        // Payment Details Sub-form
        VBox paymentDetailBox = new VBox(8);
        TextField upiField = new TextField("alice@okaxis");
        upiField.setPromptText("Enter UPI ID (e.g. user@bank)");

        TextField cardNumField = new TextField("4532789012345678");
        cardNumField.setPromptText("Card Number");
        TextField cardHolderField = new TextField(customer.getName());
        cardHolderField.setPromptText("Cardholder Name");
        HBox cardExtra = new HBox(10, new TextField("12/28"), new TextField("123"));

        paymentDetailBox.getChildren().add(upiField);

        payGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            paymentDetailBox.getChildren().clear();
            if (newV == upiRadio) {
                paymentDetailBox.getChildren().add(upiField);
            } else if (newV == cardRadio) {
                paymentDetailBox.getChildren().addAll(cardNumField, cardHolderField, cardExtra);
            } else {
                Label codNote = new Label("💵 Pay in cash or UPI upon delivery.");
                codNote.getStyleClass().add("text-muted");
                paymentDetailBox.getChildren().add(codNote);
            }
        });

        formCard.getChildren().addAll(
                shipTitle, streetField, addrGrid,
                new Separator(),
                payTitle, radioBox, paymentDetailBox
        );

        // Right Order Review & Place Order Card
        VBox reviewCard = new VBox(14);
        reviewCard.getStyleClass().add("card");
        reviewCard.setPrefWidth(320);
        reviewCard.setMinWidth(300);

        Label reviewTitle = new Label("3. Order Summary");
        reviewTitle.getStyleClass().add("heading-md");

        VBox reviewItems = new VBox(6);
        for (CartItem ci : cart.getItems()) {
            HBox row = new HBox();
            Label itm = new Label(ci.getQuantity() + "x " + ci.getProduct().getName());
            itm.getStyleClass().add("text-muted");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label prc = new Label(GuiUtils.formatCurrency(ci.getSubtotal()));
            row.getChildren().addAll(itm, sp, prc);
            reviewItems.getChildren().add(row);
        }

        // Coupon Section
        Label couponTitle = new Label("Promo Code");
        couponTitle.getStyleClass().add("label-field");

        HBox couponRow = new HBox(8);
        TextField couponField = new TextField(activeCoupon);
        couponField.setPromptText("e.g. SAVE10");
        HBox.setHgrow(couponField, Priority.ALWAYS);

        Button applyCouponBtn = new Button("Apply");
        applyCouponBtn.getStyleClass().add("btn-outline");
        couponRow.getChildren().addAll(couponField, applyCouponBtn);

        Label couponStatus = new Label(activeCoupon.isEmpty() ? "Try 'SAVE10' or 'FLAT50'" : "Promo: " + activeCoupon);
        couponStatus.getStyleClass().add("text-muted");

        // Totals Calculation
        double subtotal = app.getCartService().getTotal(cart);
        Discountable discountStrategy = app.getDiscountService().resolveDiscount(activeCoupon);
        double discountAmt = discountStrategy.calculateDiscount(subtotal);
        double finalTotal = Math.max(0.0, subtotal - discountAmt);

        HBox subRow = new HBox();
        Label subL = new Label("Subtotal:");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        Label subV = new Label(GuiUtils.formatCurrency(subtotal));
        subRow.getChildren().addAll(subL, sp1, subV);

        HBox discRow = new HBox();
        Label discL = new Label("Discount (" + discountStrategy + "):");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        Label discV = new Label("-" + GuiUtils.formatCurrency(discountAmt));
        discV.setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
        discRow.getChildren().addAll(discL, sp2, discV);

        HBox totalR = new HBox();
        Label totL = new Label("Final Amount:");
        totL.getStyleClass().add("heading-md");
        Region sp3 = new Region();
        HBox.setHgrow(sp3, Priority.ALWAYS);
        Label totV = new Label(GuiUtils.formatCurrency(finalTotal));
        totV.getStyleClass().add("price-tag");
        totalR.getChildren().addAll(totL, sp3, totV);

        applyCouponBtn.setOnAction(e -> {
            String entered = couponField.getText().trim();
            if (!entered.isEmpty() && !app.getDiscountService().isValidCoupon(entered)) {
                GuiUtils.showError("Invalid Coupon", "Coupon code '" + entered + "' is unrecognized. Available promo codes: SAVE10 (10% off), SAVE20 (20% off), FLAT50 ($50 off), FLAT100 ($100 off).");
                activeCoupon = "";
            } else {
                activeCoupon = entered;
            }
            showCheckoutView();
        });

        // Place Order Button
        Button placeOrderBtn = new Button("Place Order 🛒");
        placeOrderBtn.getStyleClass().add("btn-primary");
        placeOrderBtn.setMaxWidth(Double.MAX_VALUE);

        placeOrderBtn.setOnAction(e -> {
            // Inline validation: pincode format
            String enteredPin = pinField.getText().trim();
            if (!enteredPin.isEmpty() && !Address.isValidPincode(enteredPin)) {
                GuiUtils.showError("Invalid Pincode",
                        "Pincode '" + enteredPin + "' is invalid. Please enter 3–10 alphanumeric characters.");
                return;
            }

            Address shippingAddr = new Address(
                    streetField.getText(), cityField.getText(),
                    stateField.getText(), pinField.getText(),
                    countryField.getText()
            );

            String selectedPayMethod = "UPI";
            if (cardRadio.isSelected()) selectedPayMethod = "CARD";
            else if (codRadio.isSelected()) selectedPayMethod = "COD";

            try {
                Invoice invoice = app.getOrderService().placeOrder(
                        cart, activeCoupon, selectedPayMethod, shippingAddr
                );
                activeCoupon = "";
                updateCartBadge();
                showOrderSuccessView(invoice);
            } catch (EmptyCartException | InsufficientStockException | InvalidPaymentException | ProductNotFoundException ex) {
                GuiUtils.showError("Checkout Failed", ex.getMessage());
            } catch (Exception ex) {
                GuiUtils.showError("Error", "An unexpected error occurred: " + ex.getMessage());
            }
        });

        reviewCard.getChildren().addAll(
                reviewTitle, reviewItems, new Separator(),
                couponTitle, couponRow, couponStatus, new Separator(),
                subRow, discRow, totalR, placeOrderBtn
        );

        checkoutGrid.getChildren().addAll(formCard, reviewCard);
        content.getChildren().addAll(title, checkoutGrid);
        rootPane.setCenter(content);
    }

    private void showOrderSuccessView(Invoice invoice) {
        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");
        content.setAlignment(Pos.CENTER);

        VBox successCard = new VBox(16);
        successCard.getStyleClass().add("card");
        successCard.setMaxWidth(620);
        successCard.setAlignment(Pos.CENTER);

        Label checkIcon = new Label("🎉");
        checkIcon.setStyle("-fx-font-size: 48px;");

        Label successTitle = new Label("Order Placed Successfully!");
        successTitle.getStyleClass().add("heading-lg");

        Label orderNum = new Label("Order ID: #" + invoice.getOrder().getOrderId());
        orderNum.getStyleClass().add("badge-role");

        TextArea invoiceArea = new TextArea(invoice.generateInvoiceText());
        invoiceArea.setEditable(false);
        invoiceArea.setPrefRowCount(14);
        invoiceArea.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER);

        Button viewOrdersBtn = new Button("View in Order History");
        viewOrdersBtn.getStyleClass().add("btn-primary");
        viewOrdersBtn.setOnAction(e -> showOrdersView());

        Button continueShopBtn = new Button("Continue Shopping");
        continueShopBtn.getStyleClass().add("btn-outline");
        continueShopBtn.setOnAction(e -> showBrowseView());

        actionRow.getChildren().addAll(viewOrdersBtn, continueShopBtn);

        successCard.getChildren().addAll(checkIcon, successTitle, orderNum, invoiceArea, actionRow);
        content.getChildren().add(successCard);
        rootPane.setCenter(content);
    }

    // =========================================================================
    // 4. ORDER HISTORY SUB-VIEW
    // =========================================================================

    public void showOrdersView() {
        setActiveNav(ordersNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        Label title = new Label("My Order History");
        title.getStyleClass().add("heading-lg");

        List<Order> orders = app.getCustomerService().getOrderHistory(customer);

        if (orders == null || orders.isEmpty()) {
            VBox emptyCard = new VBox(16);
            emptyCard.getStyleClass().add("card");
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(60));

            Label emptyMsg = new Label("No orders found");
            emptyMsg.getStyleClass().add("heading-md");
            Label emptySub = new Label("You haven't placed any orders yet.");
            emptySub.getStyleClass().add("subtitle");

            Button shopBtn = new Button("Start Shopping");
            shopBtn.getStyleClass().add("btn-primary");
            shopBtn.setOnAction(e -> showBrowseView());

            emptyCard.getChildren().addAll(emptyMsg, emptySub, shopBtn);
            content.getChildren().addAll(title, emptyCard);
            rootPane.setCenter(content);
            return;
        }

        VBox orderList = new VBox(14);

        for (Order order : orders) {
            VBox orderCard = new VBox(12);
            orderCard.getStyleClass().add("card-sm");

            // Header row: Order ID, Date, Status Chip
            HBox topRow = new HBox(12);
            topRow.setAlignment(Pos.CENTER_LEFT);

            Label orderIdLabel = new Label("Order #" + order.getOrderId());
            orderIdLabel.getStyleClass().add("heading-md");

            Label dateLabel = new Label("Placed: " + (order.getPlacedAt() != null ? order.getPlacedAt().toLocalDate() : "N/A"));
            dateLabel.getStyleClass().add("text-muted");

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label statusBadge = GuiUtils.createStatusBadge(order.getStatus());

            topRow.getChildren().addAll(orderIdLabel, dateLabel, sp, statusBadge);

            // Item summary row
            VBox itemsSummary = new VBox(4);
            for (OrderItem oi : order.getItems()) {
                HBox iRow = new HBox();
                Label iName = new Label("• " + oi.getProductName() + " (x" + oi.getQuantity() + ")");
                Region spItem = new Region();
                HBox.setHgrow(spItem, Priority.ALWAYS);
                Label iSub = new Label(GuiUtils.formatCurrency(oi.getSubtotal()));
                iSub.getStyleClass().add("text-muted");
                iRow.getChildren().addAll(iName, spItem, iSub);
                itemsSummary.getChildren().add(iRow);
            }

            // Bottom row: Total, View Receipt button, Cancel button
            HBox bottomRow = new HBox(12);
            bottomRow.setAlignment(Pos.CENTER_LEFT);

            Label totalLabel = new Label("Total Paid: " + GuiUtils.formatCurrency(order.getTotal()));
            totalLabel.getStyleClass().add("price-tag");

            Region spBottom = new Region();
            HBox.setHgrow(spBottom, Priority.ALWAYS);

            Button invoiceBtn = new Button("📄 Invoice");
            invoiceBtn.getStyleClass().add("btn-chip");
            invoiceBtn.setOnAction(e -> {
                Invoice inv = new Invoice(order);
                showInvoiceDialog(inv);
            });

            Button cancelBtn = new Button("Cancel Order");
            cancelBtn.getStyleClass().add("btn-danger-outline");
            
            // Disable if delivered or already cancelled
            boolean canCancel = (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CANCELLED);
            cancelBtn.setDisable(!canCancel);

            cancelBtn.setOnAction(e -> {
                try {
                    app.getOrderService().cancelOrder(order.getOrderId());
                    GuiUtils.showInfo("Order Cancelled", "Order #" + order.getOrderId() + " has been cancelled and items were restored to inventory.");
                    showOrdersView();
                } catch (Exception ex) {
                    GuiUtils.showError("Cancellation Error", ex.getMessage());
                }
            });

            bottomRow.getChildren().addAll(totalLabel, spBottom, invoiceBtn, cancelBtn);

            orderCard.getChildren().addAll(topRow, new Separator(), itemsSummary, new Separator(), bottomRow);
            orderList.getChildren().add(orderCard);
        }

        ScrollPane scrollPane = new ScrollPane(orderList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        content.getChildren().addAll(title, scrollPane);
        rootPane.setCenter(content);
    }

    private void showInvoiceDialog(Invoice invoice) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("EcomFlow Invoice — Order #" + invoice.getOrder().getOrderId());
        dialog.setHeaderText(null);

        TextArea txt = new TextArea(invoice.generateInvoiceText());
        txt.setEditable(false);
        txt.setPrefRowCount(16);
        txt.setPrefColumnCount(50);
        txt.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");

        dialog.getDialogPane().setContent(txt);
        dialog.showAndWait();
    }

    // =========================================================================
    // 5. PROFILE SUB-VIEW
    // =========================================================================

    public void showProfileView() {
        setActiveNav(profileNavBtn);

        VBox content = new VBox(20);
        content.getStyleClass().add("content-area");

        Label title = new Label("My Account Profile");
        title.getStyleClass().add("heading-lg");

        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setMaxWidth(560);

        Label nameLabel = new Label("Full Name");
        nameLabel.getStyleClass().add("label-field");
        TextField nameField = new TextField(customer.getName());
        nameField.setEditable(false);

        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("label-field");
        TextField emailField = new TextField(customer.getEmail());
        emailField.setEditable(false);

        Label phoneLabel = new Label("Phone Number");
        phoneLabel.getStyleClass().add("label-field");
        TextField phoneField = new TextField(customer.getPhone());

        Label addrTitle = new Label("Shipping Address");
        addrTitle.getStyleClass().add("heading-md");
        addrTitle.setPadding(new Insets(10, 0, 0, 0));

        Address addr = customer.getAddress();
        TextField streetField = new TextField(addr != null ? addr.getStreet() : "");
        streetField.setPromptText("Street");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField cityField = new TextField(addr != null ? addr.getCity() : "");
        cityField.setPromptText("City");
        TextField stateField = new TextField(addr != null ? addr.getState() : "");
        stateField.setPromptText("State");
        TextField pinField = new TextField(addr != null ? addr.getPincode() : "");
        pinField.setPromptText("Pincode");
        TextField countryField = new TextField(addr != null ? addr.getCountry() : "");
        countryField.setPromptText("Country");

        grid.add(cityField, 0, 0);
        grid.add(stateField, 1, 0);
        grid.add(pinField, 0, 1);
        grid.add(countryField, 1, 1);

        Button saveBtn = new Button("Save Profile Changes");
        saveBtn.getStyleClass().add("btn-primary");
        saveBtn.setOnAction(e -> {
            customer.setPhone(phoneField.getText());
            Address newAddr = new Address(
                    streetField.getText(),
                    cityField.getText(),
                    stateField.getText(),
                    pinField.getText(),
                    countryField.getText()
            );
            app.getCustomerService().updateAddress(customer, newAddr);
            GuiUtils.showInfo("Profile Updated", "Your profile details have been successfully updated.");
        });

        card.getChildren().addAll(
                nameLabel, nameField,
                emailLabel, emailField,
                phoneLabel, phoneField,
                addrTitle, streetField, grid,
                saveBtn
        );

        content.getChildren().addAll(title, card);
        rootPane.setCenter(content);
    }

    public Parent getView() {
        return rootPane;
    }
}
