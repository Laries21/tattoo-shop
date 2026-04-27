# Requirements Document

## Introduction

This feature transforms the existing Ink Tattoo Studio Spring Boot + Thymeleaf application into a polished, premium commercial-grade web application. The existing backend (Java 17, Spring Boot 3.4.5, Spring Security + JWT, MySQL/JPA, Thymeleaf, email notifications) is retained and extended. The scope covers a complete UI/UX overhaul of all pages — Landing Page, Explore Tattoos, Booking Form, Admin Dashboard, and Booking Approval — plus backend enhancements to support image uploads, tattoo editing, booking rejection, reference image uploads, and booking status tracking.

---

## Glossary

- **Application**: The Ink Tattoo Studio web application running on Spring Boot 3.4.5.
- **Landing_Page**: The public homepage served at `/` and `/home`.
- **Explore_Page**: The public tattoo gallery page served at `/tattoos`.
- **Booking_Form**: The public booking page served at `/book/{id}`.
- **Admin_Dashboard**: The protected admin management page served at `/admin/dashboard`.
- **Admin_Login_Page**: The admin authentication page served at `/admin/login`.
- **Add_Edit_Tattoo_Form**: The protected form page for creating or editing a tattoo record.
- **Booking_Status_Page**: The public page where users track their booking status by email or phone.
- **Tattoo**: A JPA entity with fields: id, designName, style, price, description, imageUrl.
- **Booking**: A JPA entity with fields: id, customerName, phone, email, bookingDate, status, referenceImageUrl, tattoo (ManyToOne).
- **Admin**: A JPA entity with fields: id, username, password.
- **JWT**: JSON Web Token used for stateless admin API authentication.
- **Session**: HTTP session used for Thymeleaf-based admin page authentication.
- **TattooService**: The Spring service layer managing Tattoo CRUD operations.
- **BookingService**: The Spring service layer managing Booking lifecycle operations.
- **AdminService**: The Spring service layer managing Admin authentication.
- **EmailService**: The Spring service that sends transactional emails via SMTP.
- **Static_Assets**: CSS, JavaScript, and image files served from `/static/`.
- **Thymeleaf_Template**: An HTML file in `src/main/resources/templates/` rendered server-side.
- **Multipart_File**: An HTTP multipart/form-data file upload handled by Spring's `MultipartFile`.
- **PENDING**: The initial booking status assigned when a booking is created.
- **APPROVED**: The booking status assigned by the admin when a booking is approved.
- **REJECTED**: The booking status assigned by the admin when a booking is rejected.

---

## Requirements

### Requirement 1: Premium Landing Page

**User Story:** As a visitor, I want to see a visually stunning and professional homepage, so that I am immediately impressed and motivated to explore the studio's services.

#### Acceptance Criteria

1. THE Landing_Page SHALL display a full-viewport hero section containing the studio name, a tagline, and a primary call-to-action button linking to `/tattoos`.
2. THE Landing_Page SHALL display a "Why Choose Us" section with at least three feature cards (e.g., Custom Designs, Hygienic Studio, Easy Booking).
3. THE Landing_Page SHALL display a "Services" section listing the tattoo styles offered by the studio.
4. THE Landing_Page SHALL display a "Gallery Preview" section showing up to six Tattoo records fetched from the database.
5. THE Landing_Page SHALL display a "Testimonials" section with at least three static customer testimonial cards.
6. THE Landing_Page SHALL display a "Contact" section containing the studio's address, phone number, and email address.
7. THE Landing_Page SHALL display a footer with copyright text and navigation links.
8. THE Landing_Page SHALL include a sticky navigation bar with links to Home, Explore Tattoos, Book Now, and Admin Login.
9. WHEN a visitor scrolls the page, THE Landing_Page SHALL apply smooth CSS scroll-reveal animations to each section as it enters the viewport.
10. THE Landing_Page SHALL render correctly on viewport widths from 320px to 2560px using a responsive CSS layout.
11. THE Landing_Page SHALL load all Static_Assets (CSS, JS, fonts) within the Thymeleaf_Template without external CDN dependencies that require internet access at runtime.

---

### Requirement 2: Explore Tattoos Page

**User Story:** As a visitor, I want to browse all available tattoo designs in a professional gallery layout, so that I can find a design I like and book it.

#### Acceptance Criteria

1. THE Explore_Page SHALL display all Tattoo records retrieved from the database via TattooService.
2. WHEN the Explore_Page renders a Tattoo record, THE Explore_Page SHALL display the tattoo image, design name, style, description, and price for each record.
3. IF a Tattoo record has no imageUrl, THEN THE Explore_Page SHALL display a styled placeholder image in place of the tattoo image.
4. THE Explore_Page SHALL render Tattoo records in a responsive CSS grid with a minimum card width of 280px.
5. WHEN a visitor hovers over a tattoo card, THE Explore_Page SHALL apply a CSS hover animation (e.g., scale transform and box-shadow elevation).
6. WHEN a visitor clicks the "Book Now" button on a tattoo card, THE Explore_Page SHALL navigate the visitor to the Booking_Form at `/book/{id}`.
7. THE Explore_Page SHALL include the same sticky navigation bar as the Landing_Page.
8. THE Explore_Page SHALL render correctly on viewport widths from 320px to 2560px.

---

### Requirement 3: Tattoo Entity Enhancement

**User Story:** As an admin, I want each tattoo to have a description and an image, so that visitors can see detailed information and a visual preview of each design.

#### Acceptance Criteria

1. THE Tattoo entity SHALL include a `description` field of type `String` mapped to a `description` column in the `tattoo` database table.
2. THE Tattoo entity SHALL include an `imageUrl` field of type `String` mapped to an `image_url` column in the `tattoo` database table.
3. WHEN the application starts, THE Application SHALL apply the schema changes to the `tattoo` table via JPA `ddl-auto=update` without data loss to existing records.

---

### Requirement 4: Admin Login Page

**User Story:** As an admin, I want a secure and visually premium login page, so that I can authenticate and access the admin dashboard confidently.

#### Acceptance Criteria

1. THE Admin_Login_Page SHALL display a centered login card containing a username field, a password field, and a submit button.
2. WHEN an admin submits valid credentials, THE Admin_Login_Page SHALL redirect the admin to `/admin/dashboard`.
3. IF an admin submits invalid credentials, THEN THE Admin_Login_Page SHALL display an inline error message below the form without a full page reload.
4. THE Admin_Login_Page SHALL apply the same dark premium visual theme as the rest of the Application.
5. THE Admin_Login_Page SHALL render correctly on viewport widths from 320px to 2560px.

---

### Requirement 5: Premium Admin Dashboard

**User Story:** As an admin, I want a comprehensive and visually premium dashboard, so that I can manage tattoos, bookings, and view analytics at a glance.

#### Acceptance Criteria

1. THE Admin_Dashboard SHALL display summary statistic cards showing: total tattoo count, total booking count, pending booking count, and approved booking count.
2. THE Admin_Dashboard SHALL display a "Manage Tattoos" section listing all Tattoo records in a styled table or card grid with Edit and Delete action controls for each record.
3. THE Admin_Dashboard SHALL display a "Manage Bookings" section listing all Booking records in a styled table showing: customer name, phone, email, tattoo design name, booking date, status, and action controls.
4. WHEN an admin clicks the "Add Tattoo" button, THE Admin_Dashboard SHALL navigate the admin to the Add_Edit_Tattoo_Form at `/admin/add-tattoo`.
5. WHEN an admin clicks the "Edit" control for a Tattoo record, THE Admin_Dashboard SHALL navigate the admin to the Add_Edit_Tattoo_Form at `/admin/edit-tattoo/{id}` pre-populated with the existing tattoo data.
6. WHEN an admin clicks the "Delete" control for a Tattoo record, THE Admin_Dashboard SHALL send a delete request and refresh the tattoo list without navigating away from the dashboard.
7. WHEN an admin clicks the "Approve" control for a PENDING Booking, THE Admin_Dashboard SHALL update the booking status to APPROVED and refresh the booking list.
8. WHEN an admin clicks the "Reject" control for a PENDING Booking, THE Admin_Dashboard SHALL update the booking status to REJECTED and refresh the booking list.
9. IF the admin session is not present, THEN THE Admin_Dashboard SHALL redirect the request to `/admin/login`.
10. THE Admin_Dashboard SHALL include a logout link that invalidates the admin session and redirects to `/admin/login`.
11. THE Admin_Dashboard SHALL apply a dark premium sidebar or top-navigation layout.
12. THE Admin_Dashboard SHALL render correctly on viewport widths from 768px to 2560px.

---

### Requirement 6: Add and Edit Tattoo Form

**User Story:** As an admin, I want to add new tattoo designs and edit existing ones including uploading images, so that the gallery stays current and accurate.

#### Acceptance Criteria

1. THE Add_Edit_Tattoo_Form SHALL contain input fields for: design name, style, description, and price.
2. THE Add_Edit_Tattoo_Form SHALL contain a file input that accepts image files (JPEG, PNG, WebP) for the tattoo image.
3. WHEN an admin submits the Add_Edit_Tattoo_Form with a valid Multipart_File, THE TattooService SHALL save the uploaded file to the `src/main/resources/static/images/tattoos/` directory and store the relative URL in the Tattoo `imageUrl` field.
4. WHEN an admin submits the Add_Edit_Tattoo_Form for a new tattoo, THE Application SHALL persist the new Tattoo record and redirect to `/admin/dashboard`.
5. WHEN an admin submits the Add_Edit_Tattoo_Form for an existing tattoo, THE Application SHALL update the existing Tattoo record and redirect to `/admin/dashboard`.
6. IF an admin submits the Add_Edit_Tattoo_Form with a missing required field, THEN THE Add_Edit_Tattoo_Form SHALL display a validation error message next to the offending field.
7. IF the admin session is not present, THEN THE Add_Edit_Tattoo_Form SHALL redirect the request to `/admin/login`.
8. THE Add_Edit_Tattoo_Form SHALL apply the same dark premium visual theme as the rest of the Application.

---

### Requirement 7: Premium Booking Form

**User Story:** As a visitor, I want a clean and professional booking form that shows me the tattoo I am booking, so that I can confidently submit my booking request.

#### Acceptance Criteria

1. THE Booking_Form SHALL display the selected tattoo's image, design name, style, and price at the top of the form.
2. THE Booking_Form SHALL contain input fields for: customer name, phone number (10 digits), and email address.
3. THE Booking_Form SHALL contain a file input that accepts image files (JPEG, PNG, WebP) for uploading a reference idea image.
4. WHEN a visitor submits the Booking_Form with a valid Multipart_File reference image, THE BookingService SHALL save the uploaded file to the `src/main/resources/static/images/references/` directory and store the relative URL in the Booking `referenceImageUrl` field.
5. WHEN a visitor submits the Booking_Form with a phone number that is not exactly 10 digits, THE Booking_Form SHALL display a validation error message and prevent form submission.
6. WHEN a visitor submits the Booking_Form with an invalid email format, THE Booking_Form SHALL display a validation error message and prevent form submission.
7. WHEN a visitor successfully submits the Booking_Form, THE Application SHALL create a Booking record with status PENDING, send a confirmation email via EmailService, and redirect the visitor to a booking confirmation page or the Landing_Page with a success message.
8. THE Booking_Form SHALL apply the same dark premium visual theme as the rest of the Application.
9. THE Booking_Form SHALL render correctly on viewport widths from 320px to 2560px.

---

### Requirement 8: Booking Entity Enhancement

**User Story:** As an admin, I want to be able to reject bookings and see reference images, so that I can manage the booking workflow completely.

#### Acceptance Criteria

1. THE Booking entity SHALL include a `referenceImageUrl` field of type `String` mapped to a `reference_image_url` column in the `booking` database table.
2. THE BookingService SHALL expose a `rejectBooking(Long id)` method that sets the Booking status to REJECTED and persists the change.
3. WHEN `rejectBooking` is called, THE EmailService SHALL send a rejection notification email to the Booking's email address containing the customer name and tattoo design name.
4. WHEN the application starts, THE Application SHALL apply the schema changes to the `booking` table via JPA `ddl-auto=update` without data loss to existing records.

---

### Requirement 9: Booking Status Tracking Page

**User Story:** As a visitor, I want to check the status of my booking using my email or phone number, so that I know whether my appointment has been approved or rejected.

#### Acceptance Criteria

1. THE Booking_Status_Page SHALL be accessible at `/booking-status` without authentication.
2. THE Booking_Status_Page SHALL display a search form with an input field for email address or phone number and a submit button.
3. WHEN a visitor submits the search form with a matching email or phone number, THE Booking_Status_Page SHALL display all matching Booking records showing: tattoo design name, booking date, and current status (PENDING, APPROVED, or REJECTED).
4. WHEN a Booking record has status APPROVED, THE Booking_Status_Page SHALL display the status with a green visual indicator.
5. WHEN a Booking record has status REJECTED, THE Booking_Status_Page SHALL display the status with a red visual indicator.
6. WHEN a Booking record has status PENDING, THE Booking_Status_Page SHALL display the status with a yellow visual indicator.
7. IF no Booking records match the submitted email or phone number, THEN THE Booking_Status_Page SHALL display a "No bookings found" message.
8. THE Booking_Status_Page SHALL apply the same dark premium visual theme as the rest of the Application.
9. THE Booking_Status_Page SHALL render correctly on viewport widths from 320px to 2560px.

---

### Requirement 10: Global Visual Design System

**User Story:** As a visitor or admin, I want a consistent, premium dark-themed visual design across all pages, so that the application feels like a professional commercial product.

#### Acceptance Criteria

1. THE Application SHALL apply a consistent dark color palette across all Thymeleaf_Templates using CSS custom properties (variables) defined in a single `style.css` file.
2. THE Application SHALL use a primary accent color of `#e11d48` (crimson red) for buttons, highlights, and interactive elements across all pages.
3. THE Application SHALL use a background color of `#020617` (near-black) for page backgrounds and `#0f172a` (dark navy) for card and panel backgrounds.
4. THE Application SHALL apply smooth CSS transitions (duration 200ms–400ms) to all interactive elements including buttons, cards, and navigation links.
5. THE Application SHALL use a consistent typographic scale with a sans-serif font family applied globally.
6. THE Application SHALL include a single shared CSS file at `/css/style.css` that defines all reusable component styles (navbar, card, btn, form-box, table).
7. THE Application SHALL include a single shared JavaScript file at `/js/app.js` that handles scroll-reveal animations and any shared interactive behavior.
8. WHEN a user interacts with any button, THE Application SHALL display a visual hover state change within 200ms.

---

### Requirement 11: Responsive Layout

**User Story:** As a visitor using any device, I want the application to display correctly on mobile, tablet, and desktop screens, so that I can use it comfortably regardless of my device.

#### Acceptance Criteria

1. THE Application SHALL use CSS Flexbox or CSS Grid for all multi-column layouts across all Thymeleaf_Templates.
2. THE Application SHALL include CSS media queries that adjust layout breakpoints at 768px and 1024px viewport widths.
3. WHEN the viewport width is below 768px, THE Application SHALL collapse the navigation bar into a hamburger menu or a vertically stacked layout.
4. WHEN the viewport width is below 768px, THE Application SHALL display tattoo cards in a single-column layout.
5. THE Application SHALL not require horizontal scrolling on any viewport width of 320px or greater.

---

### Requirement 12: Image Upload Infrastructure

**User Story:** As an admin, I want uploaded tattoo and reference images to be stored on the server and served as static files, so that images persist across application restarts.

#### Acceptance Criteria

1. THE Application SHALL store uploaded tattoo images in the directory `src/main/resources/static/images/tattoos/`.
2. THE Application SHALL store uploaded reference images in the directory `src/main/resources/static/images/references/`.
3. WHEN an image file is uploaded, THE Application SHALL generate a unique filename using a UUID prefix to prevent filename collisions.
4. WHEN an image file is uploaded, THE Application SHALL accept only files with MIME types `image/jpeg`, `image/png`, or `image/webp`.
5. IF an uploaded file has an unsupported MIME type, THEN THE Application SHALL return a validation error message and not persist the file.
6. THE Application SHALL configure Spring Boot's `spring.servlet.multipart.max-file-size` to `5MB` and `spring.servlet.multipart.max-request-size` to `10MB`.
