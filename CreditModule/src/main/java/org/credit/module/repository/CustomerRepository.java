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
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.id = :id")
    Optional<Customer> findCustomerById(@Param("id") Long id);

    Optional<Customer> findByName(String name);

    Optional<Customer> findByNameAndSurname(String name, String surname);

    List<Customer> findByCreditLimitGreaterThan(Double limit);

    @Query("SELECT c.creditLimit - c.usedCreditLimit FROM Customer c WHERE c.id = :customerId")
    Double getAvailableCreditLimit(@Param("customerId") Long customerId);

    @Query("SELECT c.creditLimit FROM Customer c WHERE c.id = :id")
    Float findCreditLimitById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.usedCreditLimit = c.usedCreditLimit - :amount, c.creditLimit = c.creditLimit + :amount WHERE c.id = :customerId")
    void updateCreditLimits(@Param("customerId") Long customerId, @Param("amount") Float amount);
}
