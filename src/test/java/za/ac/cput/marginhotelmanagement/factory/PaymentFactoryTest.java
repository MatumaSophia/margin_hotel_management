package za.ac.cput.marginhotelmanagement.factory;
/*
   Author: DM Madondo (230949703)
   Date: 26 June 2026
   */

import org.junit.jupiter.api.*;
import za.ac.cput.marginhotelmanagement.domain.Invoice;
import za.ac.cput.marginhotelmanagement.domain.Payment;
import za.ac.cput.marginhotelmanagement.enums.PaymentStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentFactoryTest {

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        // Invoice object for association
        this.invoice = new Invoice();
    }

    @Test
    @Order(1)
    void createPayment() {
        Payment payment = PaymentFactory.createPayment(
                127L,
                1500.00,
                PaymentStatus.SUCCESS,
                LocalDateTime.now().minusDays(1),
                invoice);

        assertNotNull(payment);
        assertEquals(127L, payment.getPaymentId());
        assertEquals(1500.00, payment.getAmount());
        assertEquals(PaymentStatus.SUCCESS, payment.getPaymentStatus());
        assertEquals(invoice, payment.getInvoice());
        System.out.println("Payment created successfully");
    }
    @Test
    @Order(2)
    void createPaymentWithInvalidAmount() {
        Payment payment = PaymentFactory.createPayment(
                127L,
                -200.00,
                PaymentStatus.SUCCESS,
                LocalDateTime.now().minusDays(1),
                invoice
        );
        assertNull(payment);
    }
    @Test
    @Order(3)
    void createPaymentWithFutureDate() {
        Payment payment = PaymentFactory.createPayment(
                102L,
                300.00,
                PaymentStatus.SUCCESS,
                LocalDateTime.now().plusDays(1),
                invoice
        );
        assertNull(payment);
    }
    @Test
    @Order(4)
    void createPaymentWithNullInvoice() {
        Payment payment = PaymentFactory.createPayment(
                103L,
                400.00,
                PaymentStatus.SUCCESS,
                LocalDateTime.now().minusDays(1),
                null
        );
        assertNull(payment);
    }
}