package za.ac.cput.marginhotelmanagement.controller;
/*
   Author: DM Madondo (230949703)
   Date: 17 July 2026
   Updated: 3 September 2026 — rewritten against the DTO-based contract
   (CreatePaymentRequest / PaymentDto / UpdatePaymentRequest) that
   PaymentController actually exposes.
   */
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.marginhotelmanagement.domain.*;
import za.ac.cput.marginhotelmanagement.dtos.CreatePaymentRequest;
import za.ac.cput.marginhotelmanagement.dtos.PaymentDto;
import za.ac.cput.marginhotelmanagement.dtos.UpdatePaymentRequest;
import za.ac.cput.marginhotelmanagement.enums.*;
import za.ac.cput.marginhotelmanagement.factory.*;
import za.ac.cput.marginhotelmanagement.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private Invoice mockInvoice;
    private Long paymentId;

    private String BASE_URL() {
        return "http://localhost:" + port + "/marginhotel/payment";
    }

    @BeforeAll
    void setUp() {
        paymentRepository.deleteAll();
        invoiceRepository.deleteAll();
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        guestRepository.deleteAll();

        Guest mockGuest = GuestFactory.createGuest(
                new Name.Builder()
                        .setFirstName("John")
                        .setMiddleName("M")
                        .setLastName("Doe")
                        .build(),
                new ContactDetails.Builder()
                        .setEmail("john.doe@example.com")
                        .setMobile("0123456789")
                        .build());
        assertNotNull(mockGuest, "Mock guest creation failed");
        mockGuest = guestRepository.save(mockGuest);

        Room mockRoom = RoomFactory.createRoom(101, RoomType.SINGLE, 750.00, RoomStatus.AVAILABLE);
        assertNotNull(mockRoom, "Mock room creation failed");
        mockRoom = roomRepository.save(mockRoom);

        StayPeriod mockStayPeriod = StayPeriodFactory.createStayPeriod(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3));
        assertNotNull(mockStayPeriod, "Mock stay period creation failed");

        Booking mockBooking = BookingFactory.createBooking(
                LocalDate.now(),
                mockStayPeriod,
                BookingChannel.ONLINE,
                mockGuest,
                mockRoom);
        assertNotNull(mockBooking, "Mock booking creation failed");
        mockBooking = bookingRepository.save(mockBooking);

        mockInvoice = InvoiceFactory.createInvoice(
                "INV-TEST-001",
                1500.00,
                InvoiceStatus.PENDING,
                LocalDate.now(),
                mockBooking);
        assertNotNull(mockInvoice, "Mock invoice creation failed");
        mockInvoice = invoiceRepository.save(mockInvoice);
    }

    @Test
    @Order(1)
    void create() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setAmount(1500.00);
        request.setPaymentStatus(PaymentStatus.SUCCESS);
        request.setInvoiceId(mockInvoice.getInvoiceId());

        String url = BASE_URL() + "/create";
        ResponseEntity<PaymentDto> response = this.restTemplate.postForEntity(url, request, PaymentDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PaymentDto created = response.getBody();
        assertNotNull(created, "Payment should be created");
        assertNotNull(created.getPaymentId());
        assertEquals(1500.00, created.getAmount());
        assertEquals(PaymentStatus.SUCCESS, created.getPaymentStatus());
        assertEquals(mockInvoice.getInvoiceId(), created.getInvoiceId());

        paymentId = created.getPaymentId();
        System.out.println("Created Payment: " + created);
    }

    @Test
    @Order(2)
    void read() {
        String url = BASE_URL() + "/read/" + paymentId;
        ResponseEntity<PaymentDto> response = this.restTemplate.getForEntity(url, PaymentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentDto read = response.getBody();
        assertNotNull(read);
        assertEquals(paymentId, read.getPaymentId());
        System.out.println("Read Payment: " + read);
    }

    @Test
    @Order(3)
    void readMissingReturnsNotFound() {
        String url = BASE_URL() + "/read/999999999";
        ResponseEntity<PaymentDto> response = this.restTemplate.getForEntity(url, PaymentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    void getAll() {
        String url = BASE_URL() + "/getall";
        ResponseEntity<PaymentDto[]> response = this.restTemplate.getForEntity(url, PaymentDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        System.out.println("Get All Payments: " + response.getBody().length);
    }

    @Test
    @Order(5)
    void findByAmount() {
        String url = BASE_URL() + "/findByAmount/1500.0";
        ResponseEntity<PaymentDto[]> response = this.restTemplate.getForEntity(url, PaymentDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        System.out.println("Find Payments by Amount: " + response.getBody().length);
    }

    @Test
    @Order(6)
    void findPaymentByPaymentStatus() {
        String url = BASE_URL() + "/findPaymentByPaymentStatus/success";
        ResponseEntity<PaymentDto[]> response = this.restTemplate.getForEntity(url, PaymentDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        System.out.println("Find Payments by Status: " + response.getBody().length);
    }

    @Test
    @Order(7)
    void findPaymentByPaymentStatusInvalidReturnsBadRequest() {
        String url = BASE_URL() + "/findPaymentByPaymentStatus/bogus";
        ResponseEntity<String> response = this.restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(8)
    void findPaymentByPaymentDateBetween() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String start = LocalDateTime.now().minusDays(1).format(formatter);
        String end = LocalDateTime.now().plusDays(1).format(formatter);
        String url = BASE_URL() + "/findPaymentByPaymentDateBetween/" + start + "/" + end;

        ResponseEntity<PaymentDto[]> response = this.restTemplate.getForEntity(url, PaymentDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        System.out.println("Find Payments by Date Range: " + response.getBody().length);
    }

    @Test
    @Order(9)
    void update() {
        UpdatePaymentRequest request = new UpdatePaymentRequest();
        request.setPaymentStatus(PaymentStatus.FAILED);

        String url = BASE_URL() + "/update/" + paymentId;
        HttpEntity<UpdatePaymentRequest> entity = new HttpEntity<>(request);
        ResponseEntity<PaymentDto> response = this.restTemplate.exchange(
                url, HttpMethod.PUT, entity, PaymentDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentDto updated = response.getBody();
        assertNotNull(updated);
        assertEquals(PaymentStatus.FAILED, updated.getPaymentStatus());
        System.out.println("Updated Payment: " + updated);
    }

    @Test
    @Order(10)
    void updateMissingReturnsNotFound() {
        UpdatePaymentRequest request = new UpdatePaymentRequest();
        request.setPaymentStatus(PaymentStatus.SUCCESS);

        String url = BASE_URL() + "/update/999999999";
        HttpEntity<UpdatePaymentRequest> entity = new HttpEntity<>(request);
        ResponseEntity<String> response = this.restTemplate.exchange(
                url, HttpMethod.PUT, entity, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(11)
    void delete() {
        String url = BASE_URL() + "/delete/" + paymentId;
        this.restTemplate.delete(url);

        ResponseEntity<PaymentDto> response = this.restTemplate.getForEntity(
                BASE_URL() + "/read/" + paymentId, PaymentDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        System.out.println("Deleted Payment #" + paymentId);
    }
}
