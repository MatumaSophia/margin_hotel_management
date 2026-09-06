package za.ac.cput.marginhotelmanagement.service;
/*
   Author: DM Madondo (230949703)
   Date: 11 July 2026
   Updated: 24 August 2026
   */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.marginhotelmanagement.domain.Invoice;
import za.ac.cput.marginhotelmanagement.domain.Payment;
import za.ac.cput.marginhotelmanagement.dtos.CreatePaymentRequest;
import za.ac.cput.marginhotelmanagement.dtos.PaymentDto;
import za.ac.cput.marginhotelmanagement.dtos.UpdatePaymentRequest;
import za.ac.cput.marginhotelmanagement.enums.PaymentStatus;
import za.ac.cput.marginhotelmanagement.mappers.PaymentMapper;
import za.ac.cput.marginhotelmanagement.repository.PaymentRepository;
import za.ac.cput.marginhotelmanagement.util.Helper;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PaymentService implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper; //MapStruct generated bean

    @Autowired
    PaymentService(PaymentRepository paymentRepository,
                   InvoiceService invoiceService,
                   PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.invoiceService = invoiceService;
        this.paymentMapper = paymentMapper;
    }

    /* ==== IService persistence CRUD. Runs the same validatePayment() checks as the DTO-based methods */

    @Override
    public Payment create(Payment payment) {
        validatePayment(payment);
        return this.paymentRepository.save(payment);
    }

    @Override
    public Payment read(Long id) {
        return this.paymentRepository.findById(id).orElse(null);
    }

    @Override
    public Payment update(Payment payment) {
        validatePayment(payment);
        return this.paymentRepository.save(payment);
    }

    @Override
    public boolean delete(Payment payment) {
        this.paymentRepository.delete(payment);
        return true;
    }

    @Override
    public List<Payment> findAll() {
        return this.paymentRepository.findAll();
    }

    @Override
    public List<Payment> findPaymentByAmount(double amount) {
        return this.paymentRepository.findPaymentByAmount(amount);
    }

    @Override
    public List<Payment> findPaymentByPaymentStatus(PaymentStatus paymentStatus) {
        return this.paymentRepository.findPaymentByPaymentStatus(paymentStatus);
    }

    @Override
    public List<Payment> findPaymentByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return this.paymentRepository.findPaymentByPaymentDateBetween(startDate, endDate);
    }

    @Override
    public List<Payment> findPaymentByPaymentId(Long paymentId) {
        return this.paymentRepository.findPaymentByPaymentId(paymentId);
    }

    /* ==== DTO base methods, this is what PaymentController actually calls ==== */

    public PaymentDto createPayment(CreatePaymentRequest request) {
        Invoice invoice = resolveInvoice(request.getInvoiceId());
        Payment mapped = paymentMapper.toEntity(request);
        Payment payment = new Payment.Builder()
                .copy(mapped)
                .setInvoice(invoice)
                .setPaymentDate(LocalDateTime.now())
                .build();

        Payment savedPayment = create(payment);
        return paymentMapper.toDto(savedPayment);
    }

    public PaymentDto readPayment(Long id) {
        Payment payment = read(id);
        if (payment == null) {
            return null;
        }
        return paymentMapper.toDto(payment);
    }

    public PaymentDto updatePayment(Long id, UpdatePaymentRequest request) {
        Payment payment = read(id);
        if (payment == null) {
            return null; //Controller returns 404 Not Found
        }
        //Only the status changes
        Payment updatedPayment = new Payment.Builder()
                .copy(payment)
                .setPaymentStatus(request.getPaymentStatus())
                .build();
        Payment savedPayment = update(updatedPayment);
        return paymentMapper.toDto(savedPayment);
    }

    public boolean deletePayment(Long id) {
        Payment payment = read(id);
        if (payment == null) {
            return false;
        }
        return delete(payment);
    }

    public List<PaymentDto> getAllPayments() {
        return findAll()
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentDto> getPaymentsByAmount(double amount) {
        return findPaymentByAmount(amount)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentDto> getPaymentsByPaymentStatus(PaymentStatus paymentStatus) {
        return findPaymentByPaymentStatus(paymentStatus)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentDto> getPaymentsByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return findPaymentByPaymentDateBetween(startDate, endDate)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    //------ Private Payment helpers ----------//
    private void validatePayment(Payment payment) {
        if (Helper.isNullOrEmpty(payment)) {
            throw new IllegalArgumentException("Payment must not be null");
        }
        if (!Helper.isValidAmount(payment.getAmount())) {
            throw new IllegalArgumentException("Amount must be greater than R0");
        }
        if (Helper.isNullOrEmpty(payment.getPaymentStatus())) {
            throw new IllegalArgumentException("Payment status is required!");
        }
        if (Helper.isNullOrEmpty(payment.getInvoice())) {
            throw new IllegalArgumentException("Invoice is required");
        }
    }

    private Invoice resolveInvoice(Long invoiceId) {
        if (Helper.isNullOrEmpty(invoiceId)) {
            throw new IllegalArgumentException("Invoice ID is required");
        }
        Invoice invoice = this.invoiceService.read(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice not found with ID# " + invoiceId);
        }
        return invoice;
    }
}
