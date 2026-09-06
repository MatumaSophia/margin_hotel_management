package za.ac.cput.marginhotelmanagement.dtos;

import lombok.Data;
import za.ac.cput.marginhotelmanagement.enums.PaymentStatus;

/*
   Author: DM Madondo (230949703)
   Date: 24 August 2026
   */
@Data
public class UpdatePaymentRequest {
    private PaymentStatus paymentStatus;
}
