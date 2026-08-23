# 🛒 EcomFlow

### Smart E-Commerce Management System

**A Java OOP Course Project — demonstrating core Object-Oriented Programming principles through a modular, console-based e-commerce simulation.**

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)
![Type](https://img.shields.io/badge/Type-Academic%20Project-blue)
![Interface](https://img.shields.io/badge/Interface-Console%20%2F%20CLI-lightgrey)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)
![License](https://img.shields.io/badge/License-MIT-green)

> 📌 **Core Purpose:** *To demonstrate how Object-Oriented Programming concepts in Java can be applied to design and implement a modular, real-world e-commerce management system.*
>
> EcomFlow is **not primarily an e-commerce product** — it is an academic vehicle for learning and showcasing Java OOP design. Every class, relationship, and workflow in this project exists to illustrate a specific OOP concept in a realistic context.

---

## 📖 About the Project

EcomFlow is a **console-based Java application** that simulates the core operations of an online shopping platform. It allows **customers** to browse products, manage a shopping cart, and place orders, while **admins** manage products, inventory, categories, and order fulfillment.

The system is intentionally built using **layered architecture** and **classic OOP design patterns** (abstraction, inheritance hierarchies, interface-based polymorphism) so that each design decision maps back to a specific Java OOP concept — making it suitable for coursework, viva presentations, and portfolio demonstration.

---

## 🎯 Objectives

- Apply core Java OOP concepts to a realistic, multi-module domain problem.
- Design a clean, layered architecture separating **models**, **services**, **repositories**, and **UI**.
- Demonstrate **abstraction** and **polymorphism** through product and payment hierarchies.
- Practice **encapsulation** and **defensive validation** across all entity classes.
- Build a fully working **console application** without relying on external frameworks or databases.

---

## ✨ Features

> All features below are **implemented in Version 1 (console-based, in-memory)** unless otherwise noted in [Future Enhancements](#-future-enhancements).

### Customer Features
- Register and log in
- Browse all products
- Search products by name/keyword
- Filter products by category
- View detailed product information
- Add / remove items from cart, update quantities
- View cart with live subtotal and total
- Apply a discount/coupon at checkout
- Select a payment method (UPI, Card, Cash on Delivery)
- Place an order and receive a generated invoice
- View order history and track order status
- Log out

### Admin Features
- Log in
- Add, update, delete products
- View and search all products
- Manage product categories
- Update inventory stock levels
- View all customer orders
- Update order status (e.g., PLACED → SHIPPED → DELIVERED)
- View registered customers
- Manage discount rules
- Log out

---

## 👥 User Roles

| Role | Description |
|------|-------------|
| **Customer** | End user who browses products, manages a cart, and places orders. |
| **Admin** | Manages the product catalog, inventory, categories, and order lifecycle. |

Both roles inherit from a common **`User`** base class, sharing authentication and profile behavior while overriding role-specific operations — a direct demonstration of **inheritance** and **method overriding**.

---

## 🏗️ System Architecture

EcomFlow follows a **layered, package-based architecture** that separates concerns between data models, business logic, and the console UI.

```mermaid
flowchart TD
    UI["ui package<br/>MainMenu / CustomerMenu / AdminMenu"] --> SVC["service package<br/>Business Logic Layer"]
    SVC --> MODEL["model package<br/>Entity Classes"]
    SVC --> REPO["repository package<br/>DataStore (In-Memory)"]
    SVC --> IFACE["interfaces package<br/>Payment / Discountable"]
    IFACE --> PAY["payment package<br/>UPIPayment / CardPayment / CashOnDelivery"]
    IFACE --> DISC["discount package<br/>PercentageDiscount / FlatDiscount / NoDiscount"]
    MODEL --> ENUMS["enums package<br/>OrderStatus / PaymentStatus"]
```

**Layer responsibilities:**

| Layer | Responsibility |
|-------|-----------------|
| `ui` | Console menus and user interaction (input/output only) |
| `service` | Business logic — validation, orchestration, workflows |
| `model` | Core entity classes (data + entity-level behavior) |
| `repository` | In-memory data storage using Java Collections |
| `interfaces` | Contracts implemented by payment and discount strategies |
| `payment` / `discount` | Concrete strategy implementations |
| `enums` | Fixed sets of constants (order status, payment status) |

### High-Level Class Relationships

```mermaid
classDiagram
    class User {
        <<abstract>>
        -int userId
        -String name
        -String email
        -String password
        -String phone
        +login()
        +logout()
        +displayProfile()
    }

    class Customer {
        -Address address
        -Cart cart
        -List~Order~ orderHistory
        +browseProducts()
        +addToCart()
        +removeFromCart()
        +checkout()
        +viewOrders()
    }

    class Admin {
        +addProduct()
        +updateProduct()
        +deleteProduct()
        +manageInventory()
        +updateOrderStatus()
        +viewCustomers()
    }

    class Product {
        <<abstract>>
        -int productId
        -String name
        -double price
        -int quantity
        -String description
        -Category category
        +displayDetails()
        +calculateDiscount()* double
        +updateStock()
    }

    class Electronics {
        -String brand
        -int warranty
    }
    class Clothing {
        -String size
        -String material
    }
    class Grocery {
        -Date expiryDate
    }

    class Cart {
        -int cartId
        -Customer customer
        -List~CartItem~ cartItems
        +addItem()
        +removeItem()
        +updateQuantity()
        +calculateTotal()
        +clearCart()
        +displayCart()
    }

    class CartItem {
        -Product product
        -int quantity
        -double subtotal
        +calculateSubtotal()
        +displayItem()
    }

    class Order {
        -int orderId
        -Customer customer
        -List~OrderItem~ orderItems
        -double totalAmount
        -Date orderDate
        -OrderStatus status
        -String paymentMethod
        -Address shippingAddress
        +placeOrder()
        +cancelOrder()
        +calculateTotal()
        +updateStatus()
        +displayOrder()
    }

    class OrderItem {
        -Product product
        -int quantity
        -double price
        +calculateSubtotal()
        +displayItem()
    }

    class Payment {
        <<interface>>
        +pay()
        +refund()
    }

    class UPIPayment
    class CardPayment
    class CashOnDelivery

    class Discountable {
        <<interface>>
        +calculateDiscount()
    }

    class PercentageDiscount
    class FlatDiscount
    class NoDiscount

    User <|-- Customer
    User <|-- Admin
    Product <|-- Electronics
    Product <|-- Clothing
    Product <|-- Grocery
    Customer "1" --> "1" Cart
    Cart "1" --> "*" CartItem
    CartItem --> Product
    Customer "1" --> "*" Order
    Order "1" --> "*" OrderItem
    OrderItem --> Product
    Payment <|.. UPIPayment
    Payment <|.. CardPayment
    Payment <|.. CashOnDelivery
    Discountable <|.. PercentageDiscount
    Discountable <|.. FlatDiscount
    Discountable <|.. NoDiscount
```

---

## 📂 Project Structure

```
src/
└── com/
    └── ecomflow/
        │
        ├── Main.java                     # Application entry point
        │
        ├── model/                        # Core entity classes
        │   ├── User.java
        │   ├── Customer.java
        │   ├── Admin.java
        │   ├── Product.java
        │   ├── Electronics.java
        │   ├── Clothing.java
        │   ├── Grocery.java
        │   ├── Category.java
        │   ├── Cart.java
        │   ├── CartItem.java
        │   ├── Order.java
        │   ├── OrderItem.java
        │   ├── Address.java
        │   └── Invoice.java
        │
        ├── interfaces/                   # Contracts / abstractions
        │   ├── Payment.java
        │   └── Discountable.java
        │
        ├── payment/                      # Payment strategy implementations
        │   ├── UPIPayment.java
        │   ├── CardPayment.java
        │   └── CashOnDelivery.java
        │
        ├── discount/                     # Discount strategy implementations
        │   ├── PercentageDiscount.java
        │   ├── FlatDiscount.java
        │   └── NoDiscount.java
        │
        ├── service/                      # Business logic layer
        │   ├── AuthenticationService.java
        │   ├── ProductService.java
        │   ├── CustomerService.java
        │   ├── AdminService.java
        │   ├── CartService.java
        │   ├── OrderService.java
        │   ├── PaymentService.java
        │   ├── InventoryService.java
        │   └── DiscountService.java
        │
        ├── repository/                   # In-memory data storage
        │   └── DataStore.java
        │
        ├── enums/                        # Fixed constant sets
        │   ├── OrderStatus.java
        │   └── PaymentStatus.java
        │
        └── ui/                           # Console interaction layer
            ├── MainMenu.java
            ├── CustomerMenu.java
            └── AdminMenu.java
```

📄 See [`ARCHITECTURE.md`](./ARCHITECTURE.md) for detailed module descriptions, package responsibilities, and additional sequence diagrams.

---

## 🧠 OOP Concepts Demonstrated

EcomFlow is built specifically to give every major Java OOP concept a concrete, working home in the codebase.

### 🔒 Encapsulation
All entity fields are declared `private`, exposed only through public getters/setters — protecting internal state from uncontrolled external access.

```java
public class Product {
    private double price;

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price > 0) this.price = price;
    }
}
```

### 🧬 Inheritance
Two clear inheritance hierarchies drive the domain model:

```
User → Customer, Admin
Product → Electronics, Clothing, Grocery
```

`Customer` and `Admin` inherit shared identity/authentication behavior from `User`; `Electronics`, `Clothing`, and `Grocery` inherit shared pricing/stock behavior from `Product`.

### 🔁 Polymorphism

**Compile-time (Method Overloading):** multiple constructors/methods with different parameter lists (e.g., overloaded `Product` constructors for different subclass needs).

**Runtime (Method Overriding):** subclasses override abstract/parent behavior, and callers interact through the parent type.

```java
Payment payment = new UPIPayment();
payment.pay();   // resolved at runtime based on actual object type
```

### 🎭 Abstraction
`Product` is declared `abstract` with at least one abstract method, forcing every subclass to define its own discount logic:

```java
abstract class Product {
    abstract double calculateDiscount();
}
```

### 🔌 Interfaces
- **`Payment`** — implemented by `UPIPayment`, `CardPayment`, `CashOnDelivery`
- **`Discountable`** — implemented by `PercentageDiscount`, `FlatDiscount`, `NoDiscount`

Interfaces decouple *what* an operation does from *how* each strategy performs it.

### 🏗️ Constructors & Constructor Overloading
Entity classes provide both default and parameterized constructors, with overloading used where a class needs to be built from different sets of initial data.

### 🔑 `this` Keyword
Used consistently to disambiguate instance fields from constructor/method parameters:

```java
public Customer(String name, String email) {
    this.name = name;
    this.email = email;
}
```

### ⬆️ `super` Keyword
Used in subclasses to invoke parent constructors and reuse shared parent logic:

```java
public Customer(String name, String email, String password, String phone, Address address) {
    super(name, email, password, phone);
    this.address = address;
}
```

### 📌 Static Members
Shared, class-level data is modeled with static fields/methods:

```java
public class User {
    private static int userCount = 0;
}
```

Used for the application name constant, and running counters for users, products, and orders.

### 🔓 Access Modifiers
| Modifier | Usage in EcomFlow |
|----------|--------------------|
| `private` | Entity fields (encapsulation) |
| `public` | Getters/setters, service methods, class declarations |
| `protected` | Fields/methods intended for subclass access (e.g., shared `User` internals) |
| *default (package-private)* | Internal helper classes/methods used only within the same package |

### ⬆️⬇️ Upcasting & Downcasting
Payment and discount strategies are referenced via their interface type (**upcasting**) and, where strategy-specific behavior is needed, cast back to the concrete type (**downcasting**):

```java
Payment payment = new CardPayment();     // Upcasting
if (payment instanceof CardPayment cp) { // Downcasting (pattern variable)
    cp.showCardSpecificDetails();
}
```

---

## 🔄 Application Workflow

### Main Flow

```mermaid
flowchart TD
    A[Start] --> B[Main Menu]
    B --> C{Choice}
    C -->|Login| D[AuthenticationService]
    C -->|Register| E[Create New User]
    C -->|Exit| F[Terminate Application]
    D --> G{Identify Role}
    G -->|Customer| H[Customer Menu]
    G -->|Admin| I[Admin Menu]
```

### Customer Flow

```mermaid
flowchart TD
    A[Customer Menu] --> B[View Products]
    B --> C[Search / Filter]
    C --> D[View Product Details]
    D --> E[Add to Cart]
    E --> F[View Cart]
    F --> G[Checkout]
    G --> H[Apply Discount]
    H --> I[Select Address]
    I --> J[Select Payment Method]
    J --> K[Process Payment]
    K --> L[Create Order]
    L --> M[Update Inventory]
    M --> N[Generate Invoice]
    N --> O[Display Order Confirmation]
```

### Admin Flow

```mermaid
flowchart TD
    A[Admin Menu] --> B[Manage Products]
    B --> C[Add / Update / Delete Product]
    A --> D[Manage Inventory]
    A --> E[View Orders]
    E --> F[Update Order Status]
    A --> G[Manage Discounts]
```

---

## 🛠️ Technology Stack

| Category | Technology |
|-----------|-------------|
| Language | Java |
| Version | Java 17+ (recommended) |
| Paradigm | Object-Oriented Programming |
| Data Storage | In-memory (Java Collections Framework) |
| Interface | Console / CLI |
| Build | Manual compilation via `javac` (no build tool required) |
| External Frameworks | None — pure core Java |

---

## 📦 Java Concepts Used

- Classes & Objects
- Encapsulation
- Inheritance (single-level, multi-class hierarchies)
- Polymorphism (compile-time & runtime)
- Abstraction (abstract classes & methods)
- Interfaces (multiple interface implementation)
- Constructors & Constructor Overloading
- Method Overloading & Method Overriding
- Static Variables, Methods, and Blocks
- Access Modifiers (`public`, `private`, `protected`, default)
- `this` and `super` keywords
- Upcasting & Downcasting
- Exception Handling (`try` / `catch` / `finally` / `throw` / `throws`, custom exceptions)
- Java Collections Framework (`ArrayList`, `HashMap`, `HashSet`)
- Enums (`OrderStatus`, `PaymentStatus`)

---

## 🖥️ Console Interface

### Main Menu
```
=========================================
              ECOMFLOW
     Smart E-Commerce Management System
=========================================

1. Login
2. Register
3. Exit

Enter your choice:
```

### Customer Dashboard
```
=========================================
           CUSTOMER DASHBOARD
=========================================

1. Browse Products
2. Search Products
3. View Cart
4. Place Order
5. Order History
6. Profile
7. Logout
```

### Admin Dashboard
```
=========================================
             ADMIN DASHBOARD
=========================================

1. Add Product
2. Update Product
3. Delete Product
4. View Products
5. Manage Inventory
6. View Orders
7. Update Order Status
8. Logout
```

---

## 🚀 Installation & Setup

### Prerequisites
- Java Development Kit (JDK) **17 or higher** installed and available on your `PATH`
- Git (to clone the repository)

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/<your-username>/ecomflow.git
cd ecomflow
```

**2. Open the project**
Open the folder in your preferred IDE (IntelliJ IDEA, Eclipse, VS Code) or continue directly from the terminal.

**3. Verify Java is installed**
```bash
java -version
javac -version
```

**4. Compile the project**

Compile a single entry file:
```bash
javac -d out src/com/ecomflow/Main.java
```

Or compile the entire source tree (recommended, since `Main.java` depends on all packages):
```bash
javac -d out $(find src -name "*.java")
```

**5. Run the application**
```bash
java -cp out com.ecomflow.Main
```

---

## ▶️ How to Use

### As a Customer
1. Choose **Register** to create an account, or **Login** with existing/demo credentials.
2. From the Customer Dashboard, browse or search products.
3. Add desired products to your cart and adjust quantities.
4. Proceed to **Checkout**, apply a discount code if available, choose a shipping address, and select a payment method.
5. Confirm the order to receive a generated invoice and order confirmation.
6. Visit **Order History** at any time to view past orders and track status.

### As an Admin
1. Log in with admin credentials.
2. Use **Add / Update / Delete Product** to manage the catalog.
3. Use **Manage Inventory** to adjust stock levels.
4. Use **View Orders** and **Update Order Status** to progress orders through their lifecycle (`PLACED → CONFIRMED → PACKED → SHIPPED → OUT_FOR_DELIVERY → DELIVERED`).
5. Use **Manage Discounts** to configure available discount strategies.

---

## 🧪 Sample Credentials

> ⚠️ These are **demo credentials for local testing only** — no real passwords or sensitive data are stored or transmitted.

| Role | Email | Password |
|------|-------|----------|
| Admin | `admin@ecomflow.com` | *(demo password — set locally)* |
| Customer | `customer@ecomflow.com` | *(demo password — set locally)* |

### Sample Product Catalog

| Category | Sample Products |
|-----------|------------------|
| Electronics | Laptop, Smartphone, Headphones |
| Clothing | T-Shirt, Jeans, Jacket |
| Grocery | Rice, Milk, Coffee |

---

## 📸 Screenshots

<!-- Add screenshot here -->
<!-- Example: ![Main Menu](docs/screenshots/main-menu.png) -->

<!-- Add screenshot here -->
<!-- Example: ![Customer Dashboard](docs/screenshots/customer-dashboard.png) -->

<!-- Add screenshot here -->
<!-- Example: ![Admin Dashboard](docs/screenshots/admin-dashboard.png) -->

---

## 🧪 Testing

Version 1 of EcomFlow relies on **manual console-driven testing**:

- Each service (`AuthenticationService`, `CartService`, `OrderService`, etc.) is exercised through the console menus covering both valid and invalid inputs.
- Validation rules (e.g., empty cart checkout, negative stock, insufficient stock, invalid login) are manually verified against the [Validation Rules](#validation-rules) below.
- Edge cases such as cancelling a delivered order or applying an invalid discount are tested to confirm the correct custom exception is thrown and handled gracefully.

> Automated unit testing (e.g., JUnit) is noted as a future enhancement.

### Validation Rules

- Product price must be greater than 0.
- Product quantity cannot be negative.
- Email and password fields cannot be empty.
- Product name cannot be empty.
- Cannot add an unavailable product to the cart.
- Cannot order a quantity greater than available stock.
- Cannot checkout an empty cart.
- Cannot cancel a delivered order.
- Cannot apply an invalid discount code.
- Pincode must pass basic format validation.

### Exception Handling

Custom exceptions are used to represent domain-specific failure cases, handled using `try` / `catch` / `finally` / `throw` / `throws`:

- `InvalidLoginException`
- `ProductNotFoundException`
- `InsufficientStockException`
- `EmptyCartException`
- `InvalidPaymentException`

---

## 🔮 Future Enhancements

The following are **not implemented in Version 1** and are listed purely as potential future directions:

- Persistent database storage (PostgreSQL / MySQL) via JDBC
- REST API layer using Spring Boot
- Web frontend (e.g., React)
- JWT-based authentication
- Real payment gateway integration
- Email notifications
- Product recommendation system
- AI-powered shopping assistant / chatbot
- Cloud deployment
- Docker containerization
- Admin analytics dashboard
- Persistent user accounts across sessions

---

## 📚 Learning Outcomes

By completing EcomFlow, this project demonstrates practical understanding of:

- Core Java syntax and program structure
- Designing classes, objects, and their relationships
- Encapsulation for safe, controlled data access
- Inheritance for modeling shared behavior across related entities
- Polymorphism (compile-time and runtime) for flexible, extensible behavior
- Abstraction for defining contracts without dictating implementation
- Interfaces for decoupled, strategy-based design (payments, discounts)
- Constructors, `this`, and `super` for proper object initialization
- Access modifiers for controlling visibility and encapsulation boundaries
- Static members for shared, class-level state
- Exception handling for robust, predictable failure modes
- Java Collections (`ArrayList`, `HashMap`, `HashSet`) for in-memory data management
- Modular, layered software architecture

---

## 👨‍💻 OOP Concepts Summary

| Concept | Where It's Used |
|----------|------------------|
| Encapsulation | Private fields + getters/setters in every model class |
| Inheritance | `User → Customer, Admin` · `Product → Electronics, Clothing, Grocery` |
| Polymorphism | `Payment`/`Discountable` implementations, overridden `calculateDiscount()` |
| Abstraction | Abstract `Product` class with abstract `calculateDiscount()` |
| Interfaces | `Payment`, `Discountable` |
| Constructors | Default & parameterized constructors across all models |
| Constructor Overloading | `Product` and subclass constructors |
| Method Overloading | Service-layer utility methods with varying parameters |
| Method Overriding | `displayDetails()`, `calculateDiscount()`, `pay()`, role-specific `User` methods |
| Static Members | Application name constant, `userCount`, `productCount`, `orderCount` |
| Access Modifiers | `private`, `protected`, `public`, default — across models and services |
| `this` Keyword | Constructor parameter disambiguation |
| `super` Keyword | Subclass constructors calling parent (`User`, `Product`) constructors |
| Upcasting / Downcasting | `Payment payment = new UPIPayment();` and safe casts back to concrete types |

---

## 🤝 Contribution

This is an academic/portfolio project. Suggestions and improvements are welcome:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes
4. Open a pull request describing the change

---

## 📄 License

This project is released under the [MIT License](./LICENSE) — free to use for learning and portfolio purposes.

---

<p align="center">Built with ☕ Java and a focus on solid Object-Oriented Design.</p>
