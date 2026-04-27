# Implementation Plan: Clarity Tattoo — Premium UI

## Overview

Incrementally enhance the existing Spring Boot + Thymeleaf tattoo shop application into a polished, premium commercial-grade web application branded as **Clarity Tattoo**. Backend entities, services, repositories, and controllers are extended first, followed by a complete frontend overhaul (CSS design system, JavaScript, and all Thymeleaf templates).

## Tasks

- [x] 1. Enhance JPA entities with new fields
  - [x] 1.1 Add `description` (TEXT) and `imageUrl` (VARCHAR) fields to `Tattoo.java` with JPA column mappings
    - Add `@Column(name = "description", columnDefinition = "TEXT")` and `@Column(name = "image_url")` fields with getters/setters
    - Hibernate `ddl-auto=update` will add the columns on next startup without data loss
    - _Requirements: 3.1, 3.2, 3.3_
  - [x] 1.2 Add `referenceImageUrl` field to `Booking.java` with JPA column mapping
    - Add `@Column(name = "reference_image_url")` field with getter/setter
    - _Requirements: 8.1, 8.4_

- [x] 2. Implement `ImageUploadService`
  - [x] 2.1 Create `ImageUploadService` interface in the service package
    - Define `String saveImage(MultipartFile file, String subDir) throws IOException`
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_
  - [x] 2.2 Create `ImageUploadServiceImpl` implementing the interface
    - Accept only `image/jpeg`, `image/png`, `image/webp`; throw `IllegalArgumentException` for others
    - Generate filename as `UUID.randomUUID().toString() + "-" + originalFilename`
    - Save to `src/main/resources/static/images/{subDir}/` and return `/images/{subDir}/{filename}`
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_
  - [ ]* 2.3 Write property test for `ImageUploadService` — Property 7: Unique filenames
    - **Property 7: Image upload generates unique filenames**
    - **Validates: Requirements 12.3**
    - Use jqwik; generate random original filenames and verify all returned paths are distinct
  - [ ]* 2.4 Write property test for `ImageUploadService` — Property 8: MIME type rejection
    - **Property 8: Image upload rejects unsupported MIME types**
    - **Validates: Requirements 12.4, 12.5**
    - Use jqwik; generate arbitrary MIME type strings not in the accepted set and assert `IllegalArgumentException` is thrown and no file is written

- [x] 3. Extend `BookingRepository` with search query
  - [x] 3.1 Add `findByEmailOrPhone` JPQL query method to `BookingRepository.java`
    - Annotate with `@Query("SELECT b FROM Booking b WHERE b.email = :query OR b.phone = :query")`
    - _Requirements: 9.3, 9.7_
  - [ ]* 3.2 Write property test for `BookingRepository` — Property 9: Search returns exact matches
    - **Property 9: Booking status search returns all and only matching records**
    - **Validates: Requirements 9.3, 9.7**
    - Use jqwik + `@DataJpaTest` with H2; generate random booking sets and query strings, assert returned set equals exact email/phone matches

- [x] 4. Extend `EmailService` with rejection email method
  - [x] 4.1 Add `sendBookingRejectedEmail(String to, String customerName, String tattooName)` to `EmailService` interface
    - _Requirements: 8.3_
  - [x] 4.2 Implement `sendBookingRejectedEmail` in `EmailServiceImpl`
    - Compose a rejection subject and body containing customer name and tattoo design name, then call `sendEmail`
    - _Requirements: 8.3_

- [x] 5. Extend `TattooService` with `updateTattoo`
  - [x] 5.1 Add `updateTattoo(Long id, Tattoo tattoo, MultipartFile image) throws IOException` to `TattooService` interface
    - _Requirements: 6.5_
  - [x] 5.2 Implement `updateTattoo` in `TattooServiceImpl`
    - Load existing record via `getTattooById`, apply designName/style/description/price changes, optionally replace image via `ImageUploadService` if file is non-empty, then save
    - _Requirements: 6.3, 6.4, 6.5_
  - [ ]* 5.3 Write property test for `TattooService` — Property 6: Round-trip field preservation
    - **Property 6: Tattoo add/update round-trip preserves all fields**
    - **Validates: Requirements 6.4, 6.5**
    - Use jqwik; generate random designName, style, description, price values; call `addTattoo` then `getTattooById` and assert all fields equal the submitted values

- [x] 6. Extend `BookingService` with `rejectBooking` and `findByEmailOrPhone`
  - [x] 6.1 Add `rejectBooking(Long id)` and `findByEmailOrPhone(String query)` to `BookingService` interface
    - _Requirements: 8.2, 9.3_
  - [x] 6.2 Implement `rejectBooking` in `BookingServiceImpl`
    - Load booking by ID (throw `RuntimeException("Booking not found")` if absent), set status to `"REJECTED"`, persist, then call `emailService.sendBookingRejectedEmail`
    - _Requirements: 8.2, 8.3_
  - [x] 6.3 Implement `findByEmailOrPhone` in `BookingServiceImpl`
    - Delegate to `bookingRepository.findByEmailOrPhone(query)`
    - _Requirements: 9.3_
  - [ ]* 6.4 Write property test for `BookingService` — Property 3: Booking starts as PENDING
    - **Property 3: Booking creation always starts as PENDING**
    - **Validates: Requirements 7.7**
    - Use jqwik; generate random valid booking inputs and assert persisted status equals `"PENDING"`
  - [ ]* 6.5 Write property test for `BookingService` — Property 4: Approve sets APPROVED
    - **Property 4: Approve booking sets status to APPROVED**
    - **Validates: Requirements 5.7**
    - Use jqwik; generate bookings with random initial statuses, call `approveBooking`, assert status equals `"APPROVED"`
  - [ ]* 6.6 Write property test for `BookingService` — Property 5: Reject sets REJECTED
    - **Property 5: Reject booking sets status to REJECTED**
    - **Validates: Requirements 5.8, 8.2**
    - Use jqwik; generate bookings with random initial statuses, call `rejectBooking`, assert status equals `"REJECTED"`

- [x] 7. Checkpoint — Ensure all backend service and repository tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Update `AdminPageController` with edit-tattoo and reject endpoints
  - [x] 8.1 Update `POST /admin/add-tattoo` to accept `MultipartFile image` and delegate to `ImageUploadService`
    - Inject `ImageUploadService`; if file is non-empty call `saveImage(file, "tattoos")` and set `tattoo.setImageUrl(...)`
    - _Requirements: 6.3_
  - [x] 8.2 Add `GET /admin/edit-tattoo/{id}` endpoint
    - Load tattoo via `tattooService.getTattooById(id)`, add to model as `"tattoo"`, return `"add-tattoo"` view
    - Include session guard: redirect to `/admin/login` if session attribute `"admin"` is null
    - _Requirements: 5.5, 6.5_
  - [x] 8.3 Add `POST /admin/edit-tattoo/{id}` endpoint
    - Accept multipart form params (designName, style, description, price, image), call `tattooService.updateTattoo(id, tattoo, image)`, redirect to `/admin/dashboard`
    - Include session guard
    - _Requirements: 5.5, 6.5_
  - [x] 8.4 Add `GET /admin/reject/{id}` endpoint
    - Call `bookingService.rejectBooking(id)`, redirect to `/admin/dashboard`
    - Include session guard
    - _Requirements: 5.8, 8.2_

- [x] 9. Upgrade `BookingSubmitController` to multipart form handling
  - [x] 9.1 Update `POST /book` to consume `multipart/form-data` and accept optional `referenceImage` `MultipartFile`
    - Change method signature to use `@RequestParam` for each field plus `@RequestParam(value = "referenceImage", required = false) MultipartFile referenceImage`
    - If `referenceImage` is non-null and non-empty, call `imageUploadService.saveImage(referenceImage, "references")` and set `booking.setReferenceImageUrl(...)`
    - _Requirements: 7.3, 7.4_

- [x] 10. Add booking-status endpoints to `ViewController`
  - [x] 10.1 Add `GET /booking-status` returning `"booking-status"` view
    - _Requirements: 9.1_
  - [x] 10.2 Add `POST /booking-status` accepting `@RequestParam("query") String query`
    - Call `bookingService.findByEmailOrPhone(query)`, add `"bookings"` and `"query"` to model, return `"booking-status"` view
    - _Requirements: 9.2, 9.3_

- [x] 11. Update `SecurityConfig` and `application.properties`
  - [x] 11.1 Permit `/booking-status` and `/booking-status/**` in `SecurityConfig` public route list
    - _Requirements: 9.1_
  - [x] 11.2 Add multipart configuration to `application.properties`
    - Set `spring.servlet.multipart.max-file-size=5MB` and `spring.servlet.multipart.max-request-size=10MB`
    - _Requirements: 12.6_

- [x] 12. Checkpoint — Ensure all controller and integration tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 13. Implement CSS design system (`style.css` complete rewrite)
  - [x] 13.1 Define CSS custom properties (`:root` variables) for the full dark color palette
    - `--bg-primary: #020617`, `--bg-card: #0f172a`, `--bg-elevated: #1e293b`, `--accent: #e11d48`, `--accent-hover: #be123c`, `--text-primary: #f1f5f9`, `--text-muted: #94a3b8`, `--border: #1e293b`, `--success: #22c55e`, `--warning: #eab308`, `--danger: #ef4444`, `--radius: 12px`, `--transition: 200ms ease`
    - _Requirements: 10.1, 10.2, 10.3_
  - [x] 13.2 Implement global reset, base typography, and body styles
    - Apply sans-serif font family globally; set `background-color: var(--bg-primary)` and `color: var(--text-primary)` on `body`
    - _Requirements: 10.5_
  - [x] 13.3 Implement `.navbar` component (sticky, flex, backdrop-blur)
    - _Requirements: 1.8, 10.6_
  - [x] 13.4 Implement `.hero` component (full-viewport, gradient overlay)
    - _Requirements: 1.1_
  - [x] 13.5 Implement `.card`, `.btn` / `.btn-primary`, `.form-box`, `.section`, `.grid-cards` components
    - `.btn-primary` uses `--accent` background with `--accent-hover` on hover within `--transition`
    - _Requirements: 10.2, 10.4, 10.6_
  - [x] 13.6 Implement `.table-premium`, `.badge` / `.badge-approved` / `.badge-pending` / `.badge-rejected`, `.stats-card`, `.sidebar` components
    - _Requirements: 5.1, 5.3, 9.4, 9.5, 9.6_
  - [x] 13.7 Add `@keyframes fadeIn` and `@keyframes slideUp` animations; implement `.reveal` / `.visible` scroll-reveal classes
    - _Requirements: 1.9, 10.4_
  - [x] 13.8 Add responsive media queries at 1024px and 768px breakpoints
    - At ≤768px: collapse navbar, single-column card grid, no horizontal scroll
    - _Requirements: 11.2, 11.3, 11.4, 11.5_

- [x] 14. Implement JavaScript (`app.js` complete rewrite)
  - [x] 14.1 Implement scroll-reveal via `IntersectionObserver` toggling `.visible` on `.reveal` elements
    - _Requirements: 1.9_
  - [x] 14.2 Implement hamburger menu toggle (`.nav-open` on `<nav>` at ≤768px)
    - _Requirements: 11.3_
  - [x] 14.3 Implement phone number client-side validation (exactly 10 digits, `/^\d{10}$/`)
    - Attach `input` event listener to phone fields; show inline error and prevent submit if invalid
    - _Requirements: 7.5_
  - [x] 14.4 Implement delete confirmation dialog for all delete form submissions
    - _Requirements: 5.6_
  - [ ]* 14.5 Write property test for phone validation — Property 10: Rejects non-10-digit strings
    - **Property 10: Phone validation rejects non-10-digit strings**
    - **Validates: Requirements 7.5**
    - Use jqwik; generate arbitrary strings not matching `/^\d{10}$/` and assert the validation function returns false/invalid

- [x] 15. Rewrite `home.html` — Premium Landing Page
  - [x] 15.1 Implement sticky navbar with logo "Clarity Tattoo" and links: Home, Explore Tattoos, Book Now, Admin Login
    - _Requirements: 1.8_
  - [x] 15.2 Implement full-viewport hero section with studio name, tagline "Where Art Meets Skin", and CTA button → `/tattoos`
    - _Requirements: 1.1_
  - [x] 15.3 Implement "Why Choose Us" section with three feature cards (Custom Designs, Hygienic Studio, Easy Booking)
    - Apply `.reveal` class for scroll animation
    - _Requirements: 1.2_
  - [x] 15.4 Implement "Services" section listing tattoo styles (Traditional, Neo-Traditional, Blackwork, Watercolor, Geometric, Realism)
    - _Requirements: 1.3_
  - [x] 15.5 Implement "Gallery Preview" section rendering up to 6 tattoos from `${tattoos}` model attribute using `th:each` with `th:block th:if="${tattooStat.index < 6}"`
    - _Requirements: 1.4_
  - [ ]* 15.6 Write property test for gallery preview cap — Property 1
    - **Property 1: Gallery preview respects the six-item cap**
    - **Validates: Requirements 1.4**
    - Use jqwik; generate lists of 0–100 Tattoo objects, render the template fragment, assert rendered card count ≤ 6
  - [x] 15.7 Implement "Testimonials" section with three static testimonial cards (name, quote, star rating)
    - _Requirements: 1.5_
  - [x] 15.8 Implement "Contact" section with studio address, phone, and email for Clarity Tattoo
    - _Requirements: 1.6_
  - [x] 15.9 Implement footer with copyright text and navigation links
    - _Requirements: 1.7_

- [x] 16. Rewrite `tattoos.html` — Explore Tattoos Page
  - [x] 16.1 Implement page header "Explore Our Designs" and CSS grid of tattoo cards using `.grid-cards`
    - Each card: image (or placeholder if `imageUrl` is null/empty via `th:if`/`th:unless`), design name, style badge, description, price, "Book Now" button → `/book/{tattoo.id}`
    - _Requirements: 2.1, 2.2, 2.3, 2.4_
  - [x] 16.2 Apply `.reveal` class to cards and CSS hover animation (`transform: scale(1.03)` + elevated box-shadow)
    - _Requirements: 2.5_
  - [ ]* 16.3 Write property test for explore page field rendering — Property 2
    - **Property 2: Explore page renders all tattoo fields**
    - **Validates: Requirements 2.1, 2.2**
    - Use jqwik; generate Tattoo records with random non-null field values, render the template, assert image URL (or placeholder), name, style, description, and price all appear in output

- [x] 17. Rewrite `booking-form.html` — Premium Booking Form
  - [x] 17.1 Implement two-column desktop layout: left = tattoo preview card (image, name, style, price from `${tattoo}` model), right = booking form
    - _Requirements: 7.1_
  - [x] 17.2 Implement booking form with fields: customerName, phone, email, referenceImage (file input, optional)
    - Set `enctype="multipart/form-data"`, `action="/book"`, `method="POST"`
    - _Requirements: 7.2, 7.3_
  - [x] 17.3 Wire phone validation from `app.js` to the phone input field
    - _Requirements: 7.5_

- [x] 18. Rewrite `admin-login.html` — Admin Login Page
  - [x] 18.1 Implement centered login card on dark background with logo/studio name, username field, password field, submit button
    - Display inline error via `th:if="${error}"` below the form
    - _Requirements: 4.1, 4.3, 4.4_

- [x] 19. Rewrite `admin-dashboard.html` — Admin Dashboard
  - [x] 19.1 Implement sidebar layout with nav links: Dashboard, Add Tattoo, Logout
    - _Requirements: 5.11_
  - [x] 19.2 Implement stats row with 4 `.stats-card` components: Total Tattoos, Total Bookings, Pending, Approved
    - Compute counts in template from `${tattoos}` and `${bookings}` model attributes
    - _Requirements: 5.1_
  - [x] 19.3 Implement "Manage Tattoos" `.table-premium` with columns: Image, Name, Style, Price, Actions (Edit → `/admin/edit-tattoo/{id}`, Delete form → `/admin/delete/{id}`)
    - Wire delete forms to confirmation dialog in `app.js`
    - _Requirements: 5.2, 5.4, 5.6_
  - [x] 19.4 Implement "Manage Bookings" `.table-premium` with columns: Customer, Phone, Email, Tattoo, Date, Status badge, Actions (Approve → `/admin/approve/{id}`, Reject → `/admin/reject/{id}`)
    - Use `.badge-approved`, `.badge-pending`, `.badge-rejected` based on `${booking.status}`
    - _Requirements: 5.3, 5.7, 5.8_

- [x] 20. Rewrite `add-tattoo.html` — Add / Edit Tattoo Form
  - [x] 20.1 Implement form with fields: designName, style, description (textarea), price, image (file input)
    - Set `enctype="multipart/form-data"`
    - Pre-populate fields via `th:value="${tattoo?.designName}"` etc. for edit mode
    - Form action: `/admin/add-tattoo` when `${tattoo}` is null, `/admin/edit-tattoo/{tattoo.id}` when editing
    - _Requirements: 6.1, 6.2, 6.5, 6.8_
  - [x] 20.2 Add validation error display next to each required field
    - _Requirements: 6.6_

- [x] 21. Create `booking-status.html` — Booking Status Tracking Page
  - [x] 21.1 Implement navbar (shared), search form (single text input for email or phone, submit button)
    - `action="/booking-status"`, `method="POST"`
    - _Requirements: 9.1, 9.2_
  - [x] 21.2 Implement results section shown when `${bookings}` is non-null
    - Table columns: Tattoo Design, Booking Date, Status (colored badge)
    - "No bookings found" message when `${bookings}` is empty
    - _Requirements: 9.3, 9.4, 9.5, 9.6, 9.7_

- [x] 22. Final checkpoint — Ensure all tests pass and application starts cleanly
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at backend, controller, and frontend milestones
- Property tests use [jqwik](https://jqwik.net/) — add as a test-scope dependency in `pom.xml` before running them
- All branding uses "Clarity Tattoo" throughout templates and static assets
- Hibernate `ddl-auto=update` handles schema migrations automatically on startup
