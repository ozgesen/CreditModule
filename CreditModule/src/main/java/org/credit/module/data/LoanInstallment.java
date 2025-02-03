package org.credit.module.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "LoanInstallment")
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long loanId;
    private Float amount;
    private Float paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Boolean isPaid;
}
