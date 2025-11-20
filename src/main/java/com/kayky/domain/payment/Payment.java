package com.kayky.domain.payment;

import com.kayky.domain.cashier.Cashier;
import com.kayky.domain.patient.Patient;
import com.kayky.domain.payment.enums.PaymentMethod;
import com.kayky.domain.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_payment",
        indexes = {
            @Index(name = "idx_payment_patient", columnList = "patient_id"),
            @Index(name = "idx_payment_cashier", columnList = "cashier_id"),
            @Index(name = "idx_payment_date", columnList = "payment_date")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "cashier_id")
    private Cashier cashier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @PrePersist
    public void prePersist() {
    }
}
