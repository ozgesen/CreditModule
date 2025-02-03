package org.credit.module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.credit.module.data.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerId(Long customerId);

    Optional<Loan> findById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Loan l SET l.isPaid = true WHERE l.id = :loanId AND l.customerId = :customerId")
    void markLoanAsPaid(@Param("customerId") Long customerId, @Param("loanId") Long loanId);
}
