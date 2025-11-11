package com.kayky.domain.receipt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    boolean existsByPaymentId(Long paymentId);
}
