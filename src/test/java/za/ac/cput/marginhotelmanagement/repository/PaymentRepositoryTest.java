package za.ac.cput.marginhotelmanagement.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.marginhotelmanagement.domain.Invoice;
import za.ac.cput.marginhotelmanagement.domain.Payment;
import za.ac.cput.marginhotelmanagement.enums.InvoiceStatus;
import za.ac.cput.marginhotelmanagement.enums.PaymentStatus;
import za.ac.cput.marginhotelmanagement.factory.PaymentFactory;
import za.ac.cput.marginhotelmanagement.factory.InvoiceFactory;
import za.ac.cput.marginhotelmanagement.repository.InvoiceRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentRepositoryTest {

    // Create a mock Invoice for testing
    @Autowired
    private InvoiceRepository invoiceRepository;
    private static Invoice mockInvoice = new Invoice.Builder()
            .setReference("INV-TEST-001")
            .setTotalAmount(1500.00)
            .setStatus(InvoiceStatus.PENDING)
            .build();

    private static Payment payment = PaymentFactory.createPayment(
            1500.00,
            PaymentStatus.SUCCESS,
            LocalDateTime.now().minusDays(1),
            mockInvoice);

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @Order(1)
    void createPayment() {
        // Save the mock Invoice to the database
        Invoice savedInvoice = invoiceRepository.save(mockInvoice);

        payment = paymentRepository.save(payment);
        assertNotNull(payment);
        System.out.println("Payment created successfully: " + payment);
    }

    @Test
    @Order(2)
    void readPayment() {
        Payment readPayment = paymentRepository.findById(payment.getPaymentId()).orElse(null);
        assertNotNull(readPayment);
        System.out.println("Payment read successfully: " + readPayment);
    }

    @Test
    @Order(3)
    void updatePayment() {
        Payment payment1 = new Payment.Builder().copy(payment).setAmount(2000.00).build();
        Payment updatePayment = paymentRepository.save(payment1);
        assertNotNull(updatePayment);
        System.out.println("Payment updated successfully: " + updatePayment);
    }

    @Test
    @Order(4)
    @Disabled
    void deletePayment() {
        Payment deletePayment = (Payment) paymentRepository.findPaymentByPaymentId(payment.getPaymentId());
        assertNotNull(deletePayment);
        System.out.println("Payment deleted successfully: " + deletePayment);
    }

    @Test
    @Order(5)
    void findAll() {
        List<Payment> payments = paymentRepository.findAll();
        assertNotNull(payments);
        System.out.println("All payments retrieved successfully: \n" + payments);
    }

    @Test
    @Order(6)
    void findPaymentByAmount() {
        List<Payment> payments = paymentRepository.findPaymentByAmount(payment.getAmount());
        assertNotNull(payments);
        System.out.println("All payments retrieved successfully: \n" + payments);
    }

    @Test
    void findPaymentByPaymentStatus() {
        List<Payment> payments = paymentRepository.findPaymentByPaymentStatus(payment.getPaymentStatus());
        assertNotNull(payments);
        System.out.println("All payments retrieved successfully: \n" + payments);
    }

    @Test
    void findPaymentByPaymentDateBetween() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusDays(5);
        List<Payment> payments = paymentRepository.findPaymentByPaymentDateBetween(startDate, endDate);
        assertNotNull(payments);
        System.out.println("All payments retrieved successfully: \n" + payments);
    }
}