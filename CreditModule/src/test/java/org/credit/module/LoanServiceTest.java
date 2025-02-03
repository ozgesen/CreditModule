package org.credit.module;

import org.credit.module.data.*;
import org.credit.module.repository.CustomerRepository;
import org.credit.module.repository.LoanInstallmentRepository;
import org.credit.module.repository.LoanRepository;
import org.credit.module.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @InjectMocks
    private LoanService loanService;

    private AppUser adminUser;
    private AppUser customerUser;
    private Customer customer;
    private Loan loan;

    @BeforeEach
    void setUp() {
        adminUser = new AppUser();
        adminUser.setRole("ADMIN");

        customerUser = new AppUser();
        customerUser.setRole("CUSTOMER");
        customerUser.setCustomerId(1L);

        customer = new Customer();
        customer.setId(1L);
        customer.setCreditLimit(10000.0F);
        customer.setUsedCreditLimit(2000.0F);

        loan = new Loan();
        loan.setId(1L);
        loan.setCustomerId(1L);
        loan.setLoanAmount(5000.0F);
        loan.setNumberOfInstallment(12);
    }

    @Test
    void testCreateLoan_Success() {
        CreateLoanRequestBody request = new CreateLoanRequestBody();
        request.setCustomerId(1L);
        request.setAmount(3000.0F);
        request.setInstallmentsNum(12);
        request.setInterestRate(0.2F);

        when(customerRepository.findCustomerById(1L)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Integer result = loanService.createLoan(customerUser, request);

        assertEquals(4, result);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testListLoans_AdminUser() {
        when(loanRepository.findAll()).thenReturn(List.of(loan));
        List<Loan> loans = loanService.listLoans(adminUser);
        assertEquals(1, loans.size());
    }

    @Test
    void testListLoans_CustomerUser() {
        when(loanRepository.findByCustomerId(1L)).thenReturn(List.of(loan));
        List<Loan> loans = loanService.listLoans(customerUser);
        assertEquals(1, loans.size());
    }
}

