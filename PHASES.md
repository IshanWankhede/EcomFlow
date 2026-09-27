# 🗺️ EcomFlow — Phased Build Plan

This breaks the build described in `README.md` / `ARCHITECTURE.md` into ordered phases. Each phase only depends on packages completed in earlier phases, so at every checkpoint the project still compiles and (from Phase 6 onward) actually runs — useful for incremental commits and viva demos along the way.

**Ordering principle:** build from the bottom of the dependency graph up — `enums → model → interfaces/strategies → repository → service → ui` — mirroring the layer rule in ARCHITECTURE.md §10 (a lower layer never depends on a higher one).

---

## Phase 0 — Project Skeleton

**Goal:** A compiling, empty shell before any real logic exists.

**Deliverables**
- `src/com/ecomflow/Main.java` — prints a placeholder banner and exits
- Empty package folders: `model`, `interfaces`, `payment`, `discount`, `service`, `repository`, `enums`, `ui`
- Confirm `javac -d out $(find src -name "*.java")` and `java -cp out com.ecomflow.Main` both work

**Done when:** project compiles and runs, doing nothing but printing the app name.

---

## Phase 1 — Enums & Custom Exceptions

No dependencies on anything else, so build these first.

**Deliverables**
- `enums/OrderStatus.java` (`PLACED, CONFIRMED, PACKED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED`)
- `enums/PaymentStatus.java`
- Custom exceptions (can live in their own `exceptions` package, referenced across `service`):
  - `InvalidLoginException`
  - `ProductNotFoundException`
  - `InsufficientStockException`
  - `EmptyCartException`
  - `InvalidPaymentException`

**OOP focus:** enums as fixed constant sets; checked/unchecked exception design.

**Done when:** each exception has a message-carrying constructor and every enum has all states listed in the ARCHITECTURE.md lifecycle diagram.

---

## Phase 2 — User Hierarchy (`model`)

**Deliverables**
- `model/User.java` — abstract, private fields (`userId`, `name`, `email`, `password`, `phone`), static `userCount`, `login()`, `logout()`, `displayProfile()`
- `model/Address.java`
- `model/Customer.java` — extends `User`; adds `address`, `cart`, `orderHistory`
- `model/Admin.java` — extends `User`

**OOP focus:** inheritance, encapsulation (private fields + getters/setters with validation), constructors + `this`/`super`, static counters, method overriding on shared `User` behavior.

**Done when:** `Customer` and `Admin` both construct correctly via `super(...)`, and a quick `Main.java` scratch test can create one of each and call `displayProfile()`.

---

## Phase 3 — Product Hierarchy (`model`)

**Deliverables**
- `model/Category.java`
- `model/Product.java` — abstract; fields per ARCHITECTURE.md §3.2; abstract `calculateDiscount()`; `displayDetails()`, `updateStock()`
- `model/Electronics.java`, `model/Clothing.java`, `model/Grocery.java`

**OOP focus:** abstraction (abstract class + abstract method), constructor overloading across subclasses, encapsulation with validation (price > 0, quantity ≥ 0 per the Validation Rules table).

**Done when:** each subclass implements `calculateDiscount()` differently and can be upcast to `Product` in a test list.

---

## Phase 4 — Cart & Order Models (`model`)

Builds on Phases 2 and 3 (references `Customer` and `Product`).

**Deliverables**
- `model/CartItem.java`, `model/Cart.java`
- `model/OrderItem.java`, `model/Order.java`
- `model/Invoice.java`

**Key design rule to enforce here (from ARCHITECTURE.md §3.3):** `OrderItem` snapshots `price`/`quantity`/product reference at creation time — it must **not** recompute from a live `Product` later, or a price change would retroactively alter past order totals.

**OOP focus:** composition (`Cart` *has-a* `List<CartItem>`), the deliberate mutable-vs-immutable contrast between `Cart` and `Order`.

**Done when:** a `Cart.calculateTotal()` and `Order.calculateTotal()` both work standalone against hand-built test objects (no services yet).

---

## Phase 5 — Strategy Interfaces & Implementations

Independent of `model` internals beyond `Product`/amount values, so this can be built in parallel with Phase 4 if you're working with a partner.

**Deliverables**
- `interfaces/Payment.java` (`pay()`, `refund()`)
- `payment/UPIPayment.java`, `payment/CardPayment.java`, `payment/CashOnDelivery.java`
- `interfaces/Discountable.java` (`calculateDiscount()`)
- `discount/PercentageDiscount.java`, `discount/FlatDiscount.java`, `discount/NoDiscount.java`

**OOP focus:** interfaces, polymorphism, upcasting/downcasting (e.g. `instanceof CardPayment cp` to reach `showCardSpecificDetails()`), Open/Closed Principle.

**Done when:** a test snippet can hold a `List<Payment>` of all three concrete types and call `.pay()` polymorphically.

---

## Phase 6 — Repository Layer

**Deliverables**
- `repository/DataStore.java` centralizing:
  - `HashMap<String, User>` — email → user
  - `HashMap<Integer, Product>` — id → product
  - `ArrayList<Order>` — order history
  - `HashSet<String>` — unique category names / unique emails
- Basic CRUD-style accessor methods only (no validation/business rules — that belongs to `service` per §10's matrix)

**Done when:** `DataStore` compiles standalone and a scratch test can insert/retrieve a `User` and a `Product` by key.

---

## Phase 7 — Service Layer, Part 1: Identity & Catalog

**Deliverables**
- `service/AuthenticationService.java` — register/login/logout, unique-email check, throws `InvalidLoginException`
- `service/ProductService.java` — add/update/delete/search/filter, throws `ProductNotFoundException`
- `service/InventoryService.java` — stock add/remove with the never-negative rule, throws `InsufficientStockException`

**Done when:** you can register a user, log in, add a product, and adjust its stock — all through services, all backed by `DataStore`, with invalid cases throwing the right custom exception instead of crashing.

---

## Phase 8 — Service Layer, Part 2: Cart, Discount, Payment

**Deliverables**
- `service/CartService.java` — add/remove/update cart items, throws `EmptyCartException` on empty checkout
- `service/DiscountService.java` — resolves a coupon code to a `Discountable` strategy
- `service/PaymentService.java` — resolves a method string to a `Payment` strategy, throws `InvalidPaymentException`

**Done when:** a cart can be built, a discount applied, and a payment "processed" (simulated) end to end, still without an `Order` being created yet.

---

## Phase 9 — Service Layer, Part 3: Order Orchestration

The most integration-heavy phase — wires together everything from Phases 2–8.

**Deliverables**
- `service/OrderService.java` — implements the full checkout sequence from ARCHITECTURE.md §8: `placeOrder()`, `cancelOrder()`, `updateStatus()`
- `service/CustomerService.java`, `service/AdminService.java` — thin orchestration wrappers used by the `ui` layer next

**Order lifecycle rules to enforce (ARCHITECTURE.md §7):**
- Deduct inventory the instant an order is placed
- Restore stock if cancelled before `DELIVERED`
- Reject cancellation of a `DELIVERED` order
- Never let stock go negative (delegate to `InventoryService`)

**Done when:** a full checkout — cart → discount → payment → order → inventory update → invoice — runs correctly via services alone (still no console UI).

---

## Phase 10 — UI Layer

**Deliverables**
- `ui/MainMenu.java` — login/register/exit
- `ui/CustomerMenu.java` — matches the Customer Dashboard menu in README.md
- `ui/AdminMenu.java` — matches the Admin Dashboard menu in README.md
- `Main.java` wired to launch `MainMenu`

**Rule to enforce (§10 matrix):** UI only calls services and prints results — no business logic, no direct `DataStore` access from `ui`.

**Done when:** the whole README.md "How to Use" walkthrough (as both Customer and Admin) works end-to-end from the console.

---

## Phase 11 — Exception Hardening & Manual Test Pass

**Deliverables**
- Walk every row in README.md's **Validation Rules** table and confirm each one is actually enforced somewhere in `service`
- Walk every row in the **Exception Handling** table and confirm each exception is both thrown and caught with a clean message (no raw stack traces reaching the console)
- Manually test the specific edge cases README.md calls out: cancelling a delivered order, applying an invalid discount code

**Done when:** every validation rule and every custom exception has been deliberately triggered once and behaves as documented.

---

## Phase 12 — Polish & Documentation Pass

**Deliverables**
- Re-check `README.md`'s OOP Concepts Summary table against the actual code and note any gaps
- Add/verify sample data (demo users, sample catalog from README.md) so the app is demo-ready on first run
- Optional: screenshots for the README's Screenshots section

**Done when:** the project is viva-ready — every claimed OOP concept has a real, pointable-to example in the code.

---

## Optional Phase 13 — Future Enhancements (stretch goals, out of Version 1 scope)

Only start these after Phase 12 is solid. Pick individually, they're independent of each other:

- JDBC-backed `repository` implementation behind the same interface as `DataStore`
- JUnit test suite replacing manual testing
- REST API layer (Spring Boot)
- JWT-based auth
- Persistent accounts across sessions

---

## Suggested Dependency Graph

```
Phase 0 (skeleton)
   │
Phase 1 (enums + exceptions)
   │
   ├── Phase 2 (User hierarchy) ──┐
   └── Phase 3 (Product hierarchy)├── Phase 4 (Cart/Order models)
                                   │
Phase 5 (Payment/Discount strategies) ── independent, parallelizable with 2–4
   │
Phase 6 (DataStore) ── needs Phases 2–4 model classes to store
   │
Phase 7 (Auth/Product/Inventory services)
   │
Phase 8 (Cart/Discount/Payment services)
   │
Phase 9 (Order orchestration) ── needs everything above
   │
Phase 10 (UI)
   │
Phase 11 (hardening) → Phase 12 (polish) → Phase 13 (stretch)
```
