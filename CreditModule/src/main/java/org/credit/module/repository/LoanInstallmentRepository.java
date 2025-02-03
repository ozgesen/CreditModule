package org.credit.module.repository;

import org.springframework.data.jpa.repository.Query;
import org.credit.module.data.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoanId(Long loanId);

    @Transactional
    @Modifying
    @Query("UPDATE LoanInstallment l SET l.isPaid = true WHERE l.loanId = :loanId AND l.dueDate = :dueDate")
    int markAsPaid(Long loanId, LocalDate dueDate);

    @Transactional
    @Modifying
    @Query("UPDATE LoanInstallment l SET l.paidAmount = :paidAmount, l.paymentDate = :paymentDate WHERE l.loanId = :loanId AND l.dueDate = :dueDate")
    int updatePaidAmountAndDate(Long loanId, LocalDate dueDate, Float paidAmount, LocalDate paymentDate);

}
