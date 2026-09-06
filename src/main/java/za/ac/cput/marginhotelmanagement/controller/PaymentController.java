package za.ac.cput.marginhotelmanagement.controller;
/*
   Author: DM Madondo (230949703)
   Date: 17 July 2026
   Updated: 24 August 2026
   */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.marginhotelmanagement.dtos.CreatePaymentRequest;
import za.ac.cput.marginhotelmanagement.dtos.PaymentDto;
import za.ac.cput.marginhotelmanagement.dtos.UpdatePaymentRequest;
import za.ac.cput.marginhotelmanagement.enums.PaymentStatus;
import za.ac.cput.marginhotelmanagement.service.PaymentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreatePaymentRequest request) {
        try {
            PaymentDto created = paymentService.createPayment(request);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<PaymentDto> read(@PathVariable Long id) {
        PaymentDto paymentDto = paymentService.readPayment(id);
        if (paymentDto == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(paymentDto);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdatePaymentRequest request) {
        try {
            PaymentDto updated = paymentService.updatePayment(id, request);
            if (updated == null) {
                return new ResponseEntity<>("No payment found with ID #" + id, HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = paymentService.deletePayment(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PaymentDto>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/findByAmount/{amount}")
    public ResponseEntity<List<PaymentDto>> findByAmount(@PathVariable double amount) {
        return ResponseEntity.ok(paymentService.getPaymentsByAmount(amount));
    }

    @GetMapping("/findPaymentByPaymentStatus/{paymentStatus}")
    public ResponseEntity<?> findPaymentByPaymentStatus(@PathVariable String paymentStatus) {
        PaymentStatus status;
        try {
            status = PaymentStatus.valueOf(paymentStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid payment status'" + paymentStatus + "' expected (SUCCESS or FAILED", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(paymentService.getPaymentsByPaymentStatus(status));
    }

    @GetMapping("/findPaymentByPaymentDateBetween/{startDate}/{endDate}")
    public ResponseEntity<List<PaymentDto>> findPaymentByPaymentDateBetween(@PathVariable LocalDateTime startDate, @PathVariable LocalDateTime endDate) {
        return ResponseEntity.ok(paymentService.getPaymentsByPaymentDateBetween(startDate, endDate));
    }
}
