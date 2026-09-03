# Test Plan — Sunrise Dental Clinic (Phase 9)

## 1. Approach

- **Unit tests** (JUnit 5 + Mockito): every class in `service/**` — including the discount
  strategies, `BillBuilder`, `DiscountStrategyFactory`, and the notification observers —
  plus `AppointmentHistoryJdbcDao`, with all repository/collaborator dependencies mocked.
- **Integration tests** (Spring Boot Test + MockMvc) for the REST endpoints under `/api/**`,
  run against an in-memory H2 database (`src/test/resources/application.properties`,
  `spring.jpa.hibernate.ddl-auto=create-drop`) so the suite never touches the real MySQL data.
- **Scope note — the two Phase 6 DB-vendor-specific endpoints:** `fn_patient_loyalty_discount_rate`
  (a MySQL function) and `sp_appointment_history` (a MySQL stored procedure) exist only in the
  hand-applied MySQL schema, not in H2. `BillApiControllerTest` and `PatientHistoryApiControllerTest`
  boot the real application context (real `SecurityConfig`, real `BillServiceImpl`, real
  repositories/persistence) and mock only the one collaborator that calls the MySQL-specific
  object (`DiscountStrategyFactory`, `AppointmentHistoryJdbcDao` respectively) with `@MockitoBean`.
  Everything else in those flows runs for real against H2.
- **Scope note — FR1/FR5/FR6:** the brief scoped integration tests to `/api/**`, but three of the
  six functional requirements (login, Help, logout) are web-form flows, not REST endpoints. To
  keep the traceability matrix honest (every FR mapped to an actual passing test, not a gap),
  `AuthenticationFlowTest` and `HelpControllerTest` were added exercising the real Spring Security
  form-login/logout flow and the Help page. No production code changed for these — see the note to
  the user in-session.
- `AppointmentNumberGeneratorTest`, `AppointmentBookingNotifierTest`, `PatientConfirmationObserverTest`
  and `DentistScheduleObserverTest` support FR2 (they cover the appointment-number generation and
  booking-notification side effects of registering an appointment). `DashboardServiceImplTest`,
  `AppointmentHistoryJdbcDaoTest` and `PatientHistoryApiControllerTest` cover Phase 6/7 features
  (advanced DB features, the reporting dashboard) that sit outside the six core FRs; they're
  included in the test-case table below for completeness but not forced into the traceability matrix.
- **How to run:** `mvn test`. Latest run: **61/61 tests passing, BUILD SUCCESS.**

## 2. Traceability Matrix

| FR | Requirement | Class / Method | Verifying Test Case(s) |
|----|-------------|-----------------|-------------------------|
| FR1 | User Authentication (Login) | `SecurityConfig.securityFilterChain`, `StaffUserDetailsService.loadUserByUsername` | TC-058, TC-059, TC-061 |
| FR2 | Register New Appointment | `AppointmentServiceImpl.registerAppointment`, `AppointmentApiController.register`, `AppointmentNumberGenerator.next`, `AppointmentBookingNotifier.notifyObservers` | TC-004–TC-005, TC-007–TC-011, TC-040–TC-043, TC-045–TC-047, TC-049 |
| FR3 | Display Appointment Details | `AppointmentServiceImpl.findByAppointmentNumber`, `AppointmentApiController.get` | TC-012, TC-013, TC-048, TC-049 |
| FR4 | Calculate and Print Bill | `BillServiceImpl.generateBill`/`findByAppointmentNumber`, `BillBuilder.build`, `NoDiscountStrategy`/`ReturningPatientDiscountStrategy`/`PremiumTreatmentDiscountStrategy`, `DiscountStrategyFactory.resolve`, `BillPdfGenerator.generate`, `BillApiController` | TC-014–TC-039, TC-050–TC-054 |
| FR5 | Help Section | `HelpController.help` | TC-057 |
| FR6 | Exit System (logout) | `SecurityConfig` logout configuration | TC-060 |

*(Test IDs refer to the table in Section 3.)*

## 3. Test Case Table

All 61 cases pass as of the latest `mvn test` run.

| Test ID | Class.Method | Requirement | Input | Expected Result | Pass/Fail |
|---|---|---|---|---|---|
| TC-001 | SunriseDentalClinicApplicationTests.contextLoads | — (smoke) | Spring context startup | Application context loads without error | Pass |
| TC-002 | TreatmentServiceImplTest.listAll_mapsEntitiesToSummaries | FR2 (treatment lookup) | One `Treatment` from mocked repository | Mapped to matching `TreatmentSummary` | Pass |
| TC-003 | TreatmentServiceImplTest.listAll_noTreatments_returnsEmptyList | FR2 | Empty repository result | Empty list returned | Pass |
| TC-004 | AppointmentNumberGeneratorTest.next_seedsFromCurrentRowCountAndIncrements | FR2 | `count()` mocked to 5 | `next()` returns APT000006, then APT000007 | Pass |
| TC-005 | AppointmentNumberGeneratorTest.next_emptyTable_startsAtOne | FR2 | `count()` mocked to 0 | `next()` returns APT000001 | Pass |
| TC-006 | DashboardServiceImplTest.loadDashboard_boundsAppointmentQueryToTodayAndBundlesRevenue | Supplementary (Phase 7) | Mocked today's appointment + revenue row | View bundles both; query bounded to `[today 00:00, tomorrow 00:00)` | Pass |
| TC-007 | AppointmentServiceImplTest.registerAppointment_savesAndNotifiesObserversOnSuccess | FR2 | Valid registration request | Appointment saved, response mapped correctly, observers notified once | Pass |
| TC-008 | AppointmentServiceImplTest.registerAppointment_treatmentNotFound_throwsResourceNotFoundException | FR2 | Unknown treatment id | `ResourceNotFoundException`; no patient saved | Pass |
| TC-009 | AppointmentServiceImplTest.registerAppointment_dentistBusy_throwsDoubleBookingExceptionWithoutSaving | FR2 | Dentist already booked at that time | `DoubleBookingException`; no patient/notification | Pass |
| TC-010 | AppointmentServiceImplTest.registerAppointment_dbTriggerRejectsInsert_translatedToDoubleBookingException | FR2 | Repository save throws `DataIntegrityViolationException` with the trigger's message | Translated to `DoubleBookingException` | Pass |
| TC-011 | AppointmentServiceImplTest.registerAppointment_unrelatedDataAccessException_propagatesUnchanged | FR2 | Repository save throws an unrelated `DataIntegrityViolationException` | Original exception propagates unchanged | Pass |
| TC-012 | AppointmentServiceImplTest.findByAppointmentNumber_found_returnsResponse | FR3 | Existing appointment number | Response mapped with correct dentist name | Pass |
| TC-013 | AppointmentServiceImplTest.findByAppointmentNumber_notFound_throwsResourceNotFoundException | FR3 | Unknown appointment number | `ResourceNotFoundException` | Pass |
| TC-014 | BillBuilderTest.build_withNoDiscount_totalIsFullSubtotal | FR4 | fee 1000 + cost 500, `NoDiscountStrategy` | Total = 1500.00 | Pass |
| TC-015 | BillBuilderTest.build_withReturningPatientDiscount_appliesFivePercentOff | FR4 | fee 1000 + cost 500, `ReturningPatientDiscountStrategy` | Total = 1425.00 | Pass |
| TC-016 | BillBuilderTest.build_withoutExplicitDiscountStrategy_defaultsToNoDiscount | FR4 | No strategy set | Defaults to no discount; total = subtotal | Pass |
| TC-017 | BillBuilderTest.build_withoutExplicitIssuedAt_defaultsToNow | FR4 | No `issuedAt` set | `issuedAt` between call-time bounds | Pass |
| TC-018 | BillBuilderTest.build_withExplicitIssuedAt_usesGivenValue | FR4 | Explicit `issuedAt` | Bill uses the given value | Pass |
| TC-019 | BillBuilderTest.constructor_nullAppointment_throwsNullPointerException | FR4 | `null` appointment | `NullPointerException` | Pass |
| TC-020 | NoDiscountStrategyTest.apply_returnsSubtotalUnchanged | FR4 | 1234.56 | Returns 1234.56 unchanged | Pass |
| TC-021 | NoDiscountStrategyTest.getDescription_isHumanReadable | FR4 | — | Description = "No discount" | Pass |
| TC-022 | ReturningPatientDiscountStrategyTest.apply_deducts5PercentFromSubtotal | FR4 | 1000.00 | Returns 950.00 | Pass |
| TC-023 | ReturningPatientDiscountStrategyTest.apply_roundsHalfUpToTwoDecimalPlaces | FR4 | 1025.00 | Returns 973.75 | Pass |
| TC-024 | ReturningPatientDiscountStrategyTest.getDescription_mentionsFivePercent | FR4 | — | Description contains "5%" | Pass |
| TC-025 | PremiumTreatmentDiscountStrategyTest.apply_deducts7PercentFromSubtotal | FR4 | 10000.00 | Returns 9300.00 | Pass |
| TC-026 | PremiumTreatmentDiscountStrategyTest.getDescription_mentionsSevenPercent | FR4 | — | Description contains "7%" | Pass |
| TC-027 | DiscountStrategyFactoryTest.resolve_returningPatient_takesPrecedenceOverPremiumTreatment | FR4 | Loyalty rate 0.05 + premium-cost treatment | Returns `ReturningPatientDiscountStrategy` | Pass |
| TC-028 | DiscountStrategyFactoryTest.resolve_firstTimePatient_premiumTreatment_returnsPremiumDiscount | FR4 | Loyalty rate 0, treatment ≥ 10000 | Returns `PremiumTreatmentDiscountStrategy` | Pass |
| TC-029 | DiscountStrategyFactoryTest.resolve_firstTimePatient_belowPremiumThreshold_returnsNoDiscount | FR4 | Loyalty rate 0, treatment < 10000 | Returns `NoDiscountStrategy` | Pass |
| TC-030 | DiscountStrategyFactoryTest.resolve_treatmentCostExactlyAtThreshold_isTreatedAsPremium | FR4 | Treatment cost = 10000.00 exactly | Returns `PremiumTreatmentDiscountStrategy` | Pass |
| TC-031 | DiscountStrategyFactoryTest.resolve_nullLoyaltyRate_treatedAsNotReturning | FR4 | Loyalty rate = null | Falls through to premium/no-discount logic | Pass |
| TC-032 | BillPdfGeneratorTest.generate_producesNonEmptyPdfDocument | FR4 | A `BillResponse` | Non-empty byte array starting with `%PDF-` | Pass |
| TC-033 | BillServiceImplTest.generateBill_success_buildsSavesAndCompletesAppointment | FR4 | Valid appointment, no discount | Bill totals correct; appointment status → COMPLETED | Pass |
| TC-034 | BillServiceImplTest.generateBill_appointmentNotFound_throwsResourceNotFoundException | FR4 | Unknown appointment number | `ResourceNotFoundException` | Pass |
| TC-035 | BillServiceImplTest.generateBill_cancelledAppointment_throwsInvalidAppointmentStateException | FR4 | Appointment status = CANCELLED | `InvalidAppointmentStateException` | Pass |
| TC-036 | BillServiceImplTest.generateBill_alreadyBilled_throwsDuplicateBillException | FR4 | Bill already exists for the appointment | `DuplicateBillException` | Pass |
| TC-037 | BillServiceImplTest.findByAppointmentNumber_appointmentNotFound_throwsResourceNotFoundException | FR4 | Unknown appointment number | `ResourceNotFoundException` | Pass |
| TC-038 | BillServiceImplTest.findByAppointmentNumber_noBillYet_throwsResourceNotFoundException | FR4 | Appointment exists, no bill yet | `ResourceNotFoundException` | Pass |
| TC-039 | BillServiceImplTest.findByAppointmentNumber_billExists_returnsMappedResponse | FR4 | Existing bill, discounted total | discountAmount and totalCost mapped correctly | Pass |
| TC-040 | AppointmentBookingNotifierTest.notifyObservers_callsEveryRegisteredObserverExactlyOnce | FR2 | Two mock observers | Both called exactly once with the appointment | Pass |
| TC-041 | AppointmentBookingNotifierTest.notifyObservers_noObservers_doesNotThrow | FR2 | Empty observer list | No exception; unrelated mock untouched | Pass |
| TC-042 | PatientConfirmationObserverTest.onAppointmentBooked_logsConfirmationWithPatientNameAndAppointmentNumber | FR2 | An appointment | Log line contains patient name and appointment number | Pass |
| TC-043 | DentistScheduleObserverTest.onAppointmentBooked_logsDentistName | FR2 | An appointment | Log line contains dentist name | Pass |
| TC-044 | AppointmentHistoryJdbcDaoTest.findByContactNumber_callsStoredProcedureAndReturnsMappedRows | Supplementary (Phase 6) | Mocked `JdbcTemplate` | Calls `{call sp_appointment_history(?)}`; returns mapped rows | Pass |
| TC-045 | AppointmentApiControllerTest.register_validRequest_persistsAndReturns201 | FR2 | Valid JSON registration | 201 Created; appointment number present, status BOOKED | Pass |
| TC-046 | AppointmentApiControllerTest.register_blankPatientName_returns400WithFieldError | FR2 | Blank `patientName` | 400 Bad Request; `fieldErrors.patientName` present | Pass |
| TC-047 | AppointmentApiControllerTest.register_sameDentistAndTimeTwice_secondRequestReturns409 | FR2 | Two registrations, same dentist/time | First 201; second 409 Booking conflict | Pass |
| TC-048 | AppointmentApiControllerTest.get_unknownAppointmentNumber_returns404 | FR3 | Unknown appointment number | 404 Not Found | Pass |
| TC-049 | AppointmentApiControllerTest.registerThenGet_returnsSameAppointment | FR2/FR3 | Register then GET by returned number | GET returns same dentist name | Pass |
| TC-050 | BillApiControllerTest.generateBill_success_returns201AndMarksAppointmentCompleted | FR4 | Fresh appointment, mocked no-discount factory | 201; correct totals; appointment COMPLETED in DB | Pass |
| TC-051 | BillApiControllerTest.generateBill_calledTwice_secondCallReturns409 | FR4 | Bill generated twice | First 201; second 409 Bill already exists | Pass |
| TC-052 | BillApiControllerTest.generateBill_unknownAppointment_returns404 | FR4 | Unknown appointment number | 404 Not Found | Pass |
| TC-053 | BillApiControllerTest.getBill_beforeGeneration_returns404 | FR4 | No bill generated yet | 404 Not Found | Pass |
| TC-054 | BillApiControllerTest.getBill_afterGeneration_returnsSameBill | FR4 | Bill generated, then fetched | 200 OK; same total | Pass |
| TC-055 | PatientHistoryApiControllerTest.history_returnsRowsFromStoredProcedureCall | Supplementary (Phase 6) | Mocked DAO returns one row | 200 OK; JSON array with matching fields | Pass |
| TC-056 | PatientHistoryApiControllerTest.history_noAppointments_returnsEmptyArray | Supplementary (Phase 6) | Mocked DAO returns empty list | 200 OK; empty JSON array | Pass |
| TC-057 | HelpControllerTest.help_returnsGuidancePage | FR5 | GET /help, authenticated | 200 OK; view "help"; body contains "Signing in" | Pass |
| TC-058 | AuthenticationFlowTest.login_validCredentials_redirectsToDashboard | FR1 | admin / ChangeMe123! | 302 redirect to "/" | Pass |
| TC-059 | AuthenticationFlowTest.login_invalidCredentials_redirectsBackToLoginWithError | FR1 | admin / WrongPassword | 302 redirect to "/login?error" | Pass |
| TC-060 | AuthenticationFlowTest.logout_endsSessionAndRedirectsToLoginWithLogoutFlag | FR6 | Logout request | 302 redirect to "/login?logout" | Pass |
| TC-061 | AuthenticationFlowTest.unauthenticatedRequest_toProtectedPage_isRedirectedToLogin | FR1 | GET "/" with no session | 302 redirect (login required) | Pass |
