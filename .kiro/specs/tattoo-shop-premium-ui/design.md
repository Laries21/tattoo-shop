# Design Document: Clarity Tattoo — Premium UI

## Overview

This design transforms the existing Spring Boot + Thymeleaf tattoo shop application into a polished, premium commercial-grade web application branded as **Clarity Tattoo**. The existing backend stack (Java 17, Spring Boot 3.4.5, Spring Security + JWT, MySQL/JPA, Thymeleaf, JavaMail) is retained and extended with image upload infrastructure, tattoo editing, booking rejection, reference image uploads, and a booking status tracking page.

The approach is an incremental enhancement: all existing controllers, services, repositories, and security configuration are preserved and extended rather than replaced. New components are added alongside existing ones with minimal disruption.

---

## Architecture

The application follows the standard Spring Boot MVC layered architecture:

```
Browser
  │
  ▼
Thymeleaf Templates (HTML + CSS + JS)
  │  (HTTP GET/POST, multipart/form-data)
  ▼
Spring MVC Controllers  ◄──── HttpSession (admin auth)
  │                     ◄──── JwtFilter (API auth)
  ▼
Service Layer (Business Logic)
  │
  ├── TattooService
  ├── BookingService
  ├── AdminService
  ├── EmailService
  └── ImageUploadService  ← NEW
  │
  ▼
Repository Layer (Spring Data JPA)
  │
  ▼
MySQL Database
  │
  ▼
Static File System (src/main/resources/static/)
  └── images/tattoos/      ← uploaded tattoo images
  └── images/references/   ← uploaded reference images
```

### Authentication Dual-Track

- **Admin page routes** (`/admin/**`): Session-based authentication. `AdminPageController` checks `HttpSession` for the `"admin"` attribute and redirects to `/admin/login` if absent.
- **REST API routes** (`/api/admin/**`): JWT-based authentication via `JwtFilter`. The existing `SecurityConfig` is unchanged.
- **Public routes**: No authentication required. Includes `/`, `/home`, `/tattoos`, `/book/**`, `/booking-status`.

### Multipart Configuration

Spring Boot's embedded multipart resolver is configured in `application.properties`:

```properties
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=10MB
```

---

## Components and Interfaces

### New Component: ImageUploadService

Handles all file upload logic centrally. Both `AdminPageController` (tattoo images) and `BookingSubmitController` (reference images) delegate to this service.

```java
public interface ImageUploadService {
    /**
     * Validates MIME type, generates UUID-prefixed filename,
     * saves file to the target subdirectory under static/images/,
     * and returns the relative URL path (e.g. "/images/tattoos/uuid-name.jpg").
     *
     * @param file      the uploaded MultipartFile
     * @param subDir    "tattoos" or "references"
     * @return relative URL string for storage in the entity
     * @throws IllegalArgumentException if MIME type is unsupported
     * @throws IOException if file write fails
     */
    String saveImage(MultipartFile file, String subDir) throws IOException;
}
```

**Implementation details:**
- Accepted MIME types: `image/jpeg`, `image/png`, `image/webp`
- Filename generation: `UUID.randomUUID().toString() + "-" + originalFilename`
- Save path: `src/main/resources/static/images/{subDir}/`
- Returns: `/images/{subDir}/{uuid-filename}`

### Modified: TattooService

Add `updateTattoo` to the existing interface:

```java
public interface TattooService {
    Tattoo addTattoo(Tattoo tattoo);
    List<Tattoo> getAllTattoos();
    void deleteTattoo(Long id);
    Tattoo getTattooById(Long id);
    Tattoo updateTattoo(Long id, Tattoo tattoo, MultipartFile image) throws IOException; // NEW
}
```

`updateTattoo` loads the existing record, applies field changes, optionally replaces the image via `ImageUploadService`, and saves.

### Modified: BookingService

Add `rejectBooking` to the existing interface:

```java
public interface BookingService {
    Booking createBooking(Booking booking);
    Booking saveBooking(Booking booking);
    List<Booking> getAllBookings();
    Booking approveBooking(Long id);
    Booking rejectBooking(Long id);  // NEW
    List<Booking> findByEmailOrPhone(String query); // NEW
}
```

`rejectBooking` sets status to `"REJECTED"`, persists, and triggers `EmailService.sendBookingRejectedEmail(...)`.

`findByEmailOrPhone` delegates to the new `BookingRepository` query method.

### Modified: BookingRepository

Add a JPQL query method:

```java
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.email = :query OR b.phone = :query")
    List<Booking> findByEmailOrPhone(@Param("query") String query);
}
```

### Modified: EmailService

Add rejection email method:

```java
public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendBookingApprovedEmail(String to, String customerName, String tattooName, double price, String bookingDate);
    void sendBookingPendingEmail(String to, String customerName, String tattooName, String bookingDate);
    void sendBookingRejectedEmail(String to, String customerName, String tattooName); // NEW
}
```

### Modified: AdminPageController

Add edit-tattoo GET/POST endpoints and reject-booking endpoint:

| Method | Path | Description |
|--------|------|-------------|
| GET | `/admin/edit-tattoo/{id}` | Load tattoo by ID, add to model, return `add-tattoo` template |
| POST | `/admin/edit-tattoo/{id}` | Accept multipart form, call `updateTattoo`, redirect to dashboard |
| GET | `/admin/reject/{id}` | Call `rejectBooking`, redirect to dashboard |

The existing `POST /admin/add-tattoo` is updated to accept `MultipartFile image` and delegate to `ImageUploadService`.

### Modified: BookingSubmitController

Updated to handle multipart form with optional reference image:

```java
@PostMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public String submitBooking(
    @RequestParam("customerName") String customerName,
    @RequestParam("phone") String phone,
    @RequestParam("email") String email,
    @RequestParam("tattoo.id") Long tattooId,
    @RequestParam(value = "referenceImage", required = false) MultipartFile referenceImage,
    Model model)
```

If `referenceImage` is non-null and non-empty, it calls `imageUploadService.saveImage(referenceImage, "references")` and sets `booking.setReferenceImageUrl(...)`.

### Modified: ViewController

Add booking status endpoints:

```java
@GetMapping("/booking-status")
public String bookingStatusPage() {
    return "booking-status";
}

@PostMapping("/booking-status")
public String searchBookingStatus(
    @RequestParam("query") String query,
    Model model) {
    List<Booking> results = bookingService.findByEmailOrPhone(query);
    model.addAttribute("bookings", results);
    model.addAttribute("query", query);
    return "booking-status";
}
```

Also update `/booking-status` to be permitted in `SecurityConfig`.

---

## Data Models

### Tattoo Entity (Enhanced)

```java
@Entity
@Table(name = "tattoo")
public class Tattoo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "design_name")
    private String designName;

    @Column(name = "style")
    private String style;

    @Column(name = "price")
    private double price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;          // NEW

    @Column(name = "image_url")
    private String imageUrl;             // NEW
}
```

JPA `ddl-auto=update` adds the two new nullable columns to the existing `tattoo` table without data loss.

### Booking Entity (Enhanced)

```java
@Entity
@Table(name = "booking")
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name") private String customerName;
    @Column(name = "phone")         private String phone;
    @Column(name = "email")         private String email;
    @Column(name = "booking_date")  private String bookingDate;
    @Column(name = "status")        private String status;

    @Column(name = "reference_image_url")
    private String referenceImageUrl;    // NEW

    @ManyToOne
    @JoinColumn(name = "tattoo_id")
    private Tattoo tattoo;
}
```

### Database Schema Changes (auto-applied via ddl-auto=update)

```sql
-- Applied automatically by Hibernate on startup
ALTER TABLE tattoo ADD COLUMN description TEXT;
ALTER TABLE tattoo ADD COLUMN image_url VARCHAR(255);
ALTER TABLE booking ADD COLUMN reference_image_url VARCHAR(255);
```

### Booking Status Enum Values

The `status` field is a plain `String` with three valid values: `"PENDING"`, `"APPROVED"`, `"REJECTED"`.

---

## Frontend Architecture

### Template Inventory

| Template | Route | Auth Required |
|----------|-------|---------------|
| `home.html` | `/`, `/home` | No |
| `tattoos.html` | `/tattoos` | No |
| `booking-form.html` | `/book/{id}` | No |
| `booking-status.html` | `/booking-status` | No |
| `admin-login.html` | `/admin/login` | No |
| `admin-dashboard.html` | `/admin/dashboard` | Session |
| `add-tattoo.html` | `/admin/add-tattoo`, `/admin/edit-tattoo/{id}` | Session |

### CSS Design System (`style.css`)

All styles are defined using CSS custom properties for consistent theming:

```css
:root {
  --bg-primary:   #020617;   /* page background */
  --bg-card:      #0f172a;   /* card / panel background */
  --bg-elevated:  #1e293b;   /* elevated surfaces, table rows */
  --accent:       #e11d48;   /* primary accent — crimson red */
  --accent-hover: #be123c;   /* accent hover state */
  --text-primary: #f1f5f9;   /* primary text */
  --text-muted:   #94a3b8;   /* secondary / muted text */
  --border:       #1e293b;   /* border color */
  --success:      #22c55e;   /* APPROVED status */
  --warning:      #eab308;   /* PENDING status */
  --danger:       #ef4444;   /* REJECTED status */
  --radius:       12px;
  --transition:   200ms ease;
}
```

**Component classes:**

| Class | Description |
|-------|-------------|
| `.navbar` | Sticky top nav, flex layout, backdrop blur |
| `.hero` | Full-viewport hero with gradient overlay |
| `.card` | Dark card with border, border-radius, hover lift |
| `.btn` | Base button; `.btn-primary` uses `--accent` |
| `.form-box` | Centered form container with card styling |
| `.table-premium` | Dark-themed table with alternating row shading |
| `.badge` | Inline status badge; `.badge-approved`, `.badge-pending`, `.badge-rejected` |
| `.stats-card` | Dashboard stat card with icon and count |
| `.sidebar` | Admin sidebar navigation |
| `.section` | Page section with consistent vertical padding |
| `.grid-cards` | CSS Grid auto-fill layout for tattoo cards |

**Animations:**

```css
@keyframes fadeIn   { from { opacity: 0; } to { opacity: 1; } }
@keyframes slideUp  { from { opacity: 0; transform: translateY(24px); } to { opacity: 1; transform: translateY(0); } }
```

Scroll-reveal is applied via the `.reveal` class toggled by JavaScript `IntersectionObserver`.

**Responsive breakpoints:**

```css
@media (max-width: 1024px) { /* tablet adjustments */ }
@media (max-width: 768px)  { /* mobile: single column, hamburger menu */ }
```

### JavaScript (`app.js`)

```javascript
// 1. Scroll-reveal via IntersectionObserver
// 2. Hamburger menu toggle
// 3. Phone number validation (exactly 10 digits)
// 4. Delete confirmation dialog
```

**Scroll-reveal:** All elements with class `.reveal` are observed. When they enter the viewport, the class `.visible` is added, triggering the `slideUp` CSS animation.

**Hamburger menu:** Toggles `.nav-open` on the `<nav>` element at viewports below 768px.

**Phone validation:** Client-side `input` event listener on phone fields enforces `/^\d{10}$/`.

**Delete confirmation:** All delete form submissions prompt `confirm("Are you sure?")` before proceeding.

### Page Designs

#### `home.html` — Premium Landing Page

Sections in order:
1. **Navbar** — sticky, logo "Clarity Tattoo", links: Home / Explore Tattoos / Book Now / Admin Login
2. **Hero** — full-viewport, dark background with subtle gradient, studio name, tagline "Where Art Meets Skin", CTA button → `/tattoos`
3. **Why Choose Us** — 3 feature cards: Custom Designs, Hygienic Studio, Easy Booking
4. **Services** — style list: Traditional, Neo-Traditional, Blackwork, Watercolor, Geometric, Realism
5. **Gallery Preview** — up to 6 tattoos from `${tattoos}` model attribute, card grid
6. **Testimonials** — 3 static testimonial cards with name, quote, star rating
7. **Contact** — address, phone, email for Clarity Tattoo studio
8. **Footer** — copyright, nav links

#### `tattoos.html` — Explore Page

- Navbar (shared)
- Page header: "Explore Our Designs"
- CSS Grid of tattoo cards (`auto-fill, minmax(280px, 1fr)`)
- Each card: image (or placeholder if `imageUrl` is null/empty), design name, style badge, description, price, "Book Now" button → `/book/{tattoo.id}`
- Hover: `transform: scale(1.03)` + elevated box-shadow

#### `booking-form.html` — Booking Form

- Navbar (shared)
- Two-column layout (desktop): left = tattoo preview card, right = booking form
- Tattoo preview: image, design name, style, price
- Form fields: customerName, phone (10 digits), email, referenceImage (file input, optional)
- `enctype="multipart/form-data"`, `action="/book"`, `method="POST"`
- Client-side phone validation via `app.js`

#### `admin-login.html` — Admin Login

- Centered card on dark background
- Logo / studio name at top
- Username + password fields
- Submit button with accent color
- Error message display: `th:if="${error}"` inline below form

#### `admin-dashboard.html` — Admin Dashboard

- Sidebar layout: left sidebar with nav links (Dashboard, Add Tattoo, Logout)
- Main content area:
  - **Stats row**: 4 stats cards (Total Tattoos, Total Bookings, Pending, Approved) — counts computed in template from model attributes
  - **Manage Tattoos**: table with columns: Image, Name, Style, Price, Actions (Edit → `/admin/edit-tattoo/{id}`, Delete form → `/admin/delete/{id}`)
  - **Manage Bookings**: table with columns: Customer, Phone, Email, Tattoo, Date, Status badge, Actions (Approve → `/admin/approve/{id}`, Reject → `/admin/reject/{id}`)
- Session guard: `th:if` checks or controller redirect handles unauthenticated access

#### `add-tattoo.html` — Add / Edit Tattoo Form

- Shared for both add and edit (edit pre-populates via `th:value`)
- Fields: designName, style, description (textarea), price, image (file input)
- `enctype="multipart/form-data"`
- Validation error display
- Submit → `/admin/add-tattoo` (new) or `/admin/edit-tattoo/{id}` (edit)

#### `booking-status.html` — Booking Status Tracking

- Navbar (shared)
- Search form: single text input (email or phone), submit button
- Results table (shown when `${bookings}` is non-null):
  - Columns: Tattoo Design, Booking Date, Status
  - Status displayed as colored badge: green (APPROVED), yellow (PENDING), red (REJECTED)
- "No bookings found" message when `${bookings}` is empty

---

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system — essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Gallery preview respects the six-item cap

*For any* list of Tattoo records passed to the home page model, the gallery preview section SHALL render at most 6 tattoo cards, regardless of how many records exist in the database.

**Validates: Requirements 1.4**

---

### Property 2: Explore page renders all tattoo fields

*For any* Tattoo record with non-null fields, the rendered tattoo card on the explore page SHALL contain the tattoo's image URL (or placeholder), design name, style, description, and price.

**Validates: Requirements 2.1, 2.2**

---

### Property 3: Booking creation always starts as PENDING

*For any* valid booking submission (valid name, 10-digit phone, valid email, existing tattoo ID), the created Booking record SHALL have status `"PENDING"`.

**Validates: Requirements 7.7**

---

### Property 4: Approve booking sets status to APPROVED

*For any* Booking record in any initial status, calling `approveBooking(id)` SHALL result in the persisted booking having status `"APPROVED"`.

**Validates: Requirements 5.7**

---

### Property 5: Reject booking sets status to REJECTED

*For any* Booking record in any initial status, calling `rejectBooking(id)` SHALL result in the persisted booking having status `"REJECTED"`.

**Validates: Requirements 5.8, 8.2**

---

### Property 6: Tattoo add/update round-trip preserves all fields

*For any* Tattoo with valid designName, style, description, price, and imageUrl, after calling `addTattoo` or `updateTattoo` and then `getTattooById`, the retrieved record SHALL have field values equal to those that were submitted.

**Validates: Requirements 6.4, 6.5**

---

### Property 7: Image upload generates unique filenames

*For any* two image upload operations (even with identical original filenames), the filenames generated by `ImageUploadService.saveImage` SHALL be distinct.

**Validates: Requirements 12.3**

---

### Property 8: Image upload rejects unsupported MIME types

*For any* file whose MIME type is not one of `image/jpeg`, `image/png`, or `image/webp`, `ImageUploadService.saveImage` SHALL throw an `IllegalArgumentException` and SHALL NOT write any file to disk.

**Validates: Requirements 12.4, 12.5**

---

### Property 9: Booking status search returns all and only matching records

*For any* set of Booking records and any search query string, `BookingRepository.findByEmailOrPhone(query)` SHALL return exactly the subset of bookings whose `email` equals the query OR whose `phone` equals the query — no more, no fewer.

**Validates: Requirements 9.3, 9.7**

---

### Property 10: Phone validation rejects non-10-digit strings

*For any* string that is not composed of exactly 10 digit characters, the phone validation logic SHALL reject the input and prevent booking creation.

**Validates: Requirements 7.5**

---

## Error Handling

### Image Upload Errors

- **Unsupported MIME type**: `ImageUploadService` throws `IllegalArgumentException("Unsupported file type: " + mimeType)`. Controllers catch this and add a model error attribute, re-rendering the form with an inline error message.
- **File too large**: Spring's `MaxUploadSizeExceededException` is caught by `GlobalExceptionHandler` and returns a user-friendly error page or redirect with a flash message.
- **IO failure**: `IOException` from file write is propagated as a `RuntimeException` and handled by `GlobalExceptionHandler`.

### Booking Errors

- **Tattoo not found**: `BookingServiceImpl.createBooking` throws `RuntimeException("Tattoo not found")` if the tattoo ID does not exist. `GlobalExceptionHandler` returns a 404 error page.
- **Booking not found**: `approveBooking` and `rejectBooking` throw `RuntimeException("Booking not found")`. Handled by `GlobalExceptionHandler`.

### Admin Session Errors

- All admin page controller methods check `session.getAttribute("admin") == null` and redirect to `/admin/login`. No exception is thrown.

### Validation Errors

- Bean Validation (`@NotBlank`, `@Email`, `@Size`) on `Booking` fields is enforced at the service layer. Client-side validation in `app.js` provides immediate feedback before form submission.

---

## Testing Strategy

### Unit Tests

Unit tests cover specific examples, edge cases, and error conditions for the service and utility layers:

- `ImageUploadServiceTest`: verify accepted MIME types save correctly, verify unsupported MIME types throw `IllegalArgumentException`, verify UUID prefix uniqueness across multiple calls.
- `BookingServiceTest`: verify `createBooking` sets status to `"PENDING"`, verify `approveBooking` sets status to `"APPROVED"`, verify `rejectBooking` sets status to `"REJECTED"`, verify `rejectBooking` calls `EmailService.sendBookingRejectedEmail` (mock).
- `TattooServiceTest`: verify `addTattoo` persists and returns the entity, verify `updateTattoo` applies field changes.
- `BookingRepositoryTest` (Spring Data slice test): verify `findByEmailOrPhone` returns correct results for email match, phone match, both match, and no match.

### Property-Based Tests

Property-based testing is applicable to this feature because the core logic (image upload naming, booking status transitions, field persistence, search filtering) involves pure or near-pure functions with meaningful input variation.

**Library**: [jqwik](https://jqwik.net/) for Java property-based testing (add to `pom.xml` as test dependency).

Each property test runs a minimum of **100 iterations**.

Tag format: `// Feature: tattoo-shop-premium-ui, Property {N}: {property_text}`

| Property | Test Class | What Varies |
|----------|-----------|-------------|
| P1: Gallery preview cap | `HomePagePropertyTest` | List size (0–100 tattoos) |
| P2: Explore page renders all fields | `TattooCardPropertyTest` | Random Tattoo field values |
| P3: Booking starts as PENDING | `BookingServicePropertyTest` | Random valid booking inputs |
| P4: Approve → APPROVED | `BookingServicePropertyTest` | Random booking IDs and initial statuses |
| P5: Reject → REJECTED | `BookingServicePropertyTest` | Random booking IDs and initial statuses |
| P6: Tattoo round-trip | `TattooServicePropertyTest` | Random designName, style, description, price |
| P7: Unique filenames | `ImageUploadServicePropertyTest` | Random original filenames and MIME types |
| P8: MIME type rejection | `ImageUploadServicePropertyTest` | Random unsupported MIME type strings |
| P9: Search returns exact matches | `BookingRepositoryPropertyTest` | Random booking sets and query strings |
| P10: Phone validation | `PhoneValidationPropertyTest` | Random strings of varying length and content |

### Integration Tests

- `BookingRepositoryIntegrationTest`: uses `@DataJpaTest` with H2 in-memory database to verify `findByEmailOrPhone` query against real JPA layer.
- `AdminPageControllerIntegrationTest`: uses `@SpringBootTest` + `MockMvc` to verify session guard redirects and form submission flows.

### Smoke Tests

- Application context loads without errors (`TattooShopApplicationTests.contextLoads()`).
- `Tattoo` entity has `description` and `imageUrl` fields (verified via reflection in a single test).
- `Booking` entity has `referenceImageUrl` field.
