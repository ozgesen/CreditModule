package org.credit.module.controller;

import org.credit.module.data.*;
import org.credit.module.service.AppUserService;
import org.credit.module.service.CustomerService;
import org.credit.module.service.LoanService;
import org.credit.module.service.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CreditModuleController is a REST API controller that manages credit and payment operations.
 */
@RestController
@RequestMapping("/api")
public class CreditModuleController {

    @Autowired
    LoanService loanService;

    @Autowired
    CustomerService customerService;

    @Autowired
    AppUserService appUserService;

    @Autowired
    Status statusService;

    /**
     * Creates a new application user. Only users with the ADMIN role can access this endpoint.
     *
     * @param appUser  The authenticated user
     * @param newUser  The new user details
     * @return The created user's ID or an unauthorized access message
     */
    @PostMapping("/createAppUser")
    public ResponseEntity<String> createAppUser(@AuthenticationPrincipal AppUser appUser,
                                                @RequestBody AppUser newUser) {
        if ("ADMIN".equals(appUser.getRole())) {
            Long appUserId = appUserService.createAppUser(newUser);
            return ResponseEntity.ok("App user created with app user id " + appUserId);
        }
        return ResponseEntity.ok("No Access");
    }

    /**
     * Creates a new customer. Only users with the ADMIN role can access this endpoint.
     *
     * @param appUser         The authenticated user
     * @param createCustomer  The customer details
     * @return The created customer's ID or an unauthorized access message
     */
    @PostMapping("/createCustomer")
    public ResponseEntity<String> createCustomer(@AuthenticationPrincipal AppUser appUser,
                                                 @RequestBody Customer createCustomer) {
        if ("ADMIN".equals(appUser.getRole())) {
            Long customerId = customerService.createCustomer(createCustomer);
            return ResponseEntity.ok("User created with customer id " + customerId);
        }
        return ResponseEntity.ok("No Access");
    }

    /**
     * Lists all customers. Only users with the ADMIN role can access this endpoint.
     *
     * @param appUser The authenticated user
     * @return A list of customers or an empty list in case of unauthorized access
     */
    @GetMapping("/listCustomers")
    public ResponseEntity<List<Customer>> listCustomers(@AuthenticationPrincipal AppUser appUser) {
        if ("ADMIN".equals(appUser.getRole())) {
            List<Customer> customers = customerService.listCustomers();
            return ResponseEntity.ok(customers);
        }
        return ResponseEntity.ok(List.of());
    }

    /**
     * Creates a new loan. Both ADMIN and CUSTOMER users can access this endpoint.
     *
     * @param appUser               The authenticated user
     * @param createLoanRequestBody The loan creation request body
     * @return Loan status message
     */
    @PostMapping("/createLoan")
    public ResponseEntity<String> createLoan(@AuthenticationPrincipal AppUser appUser,
                                             @RequestBody CreateLoanRequestBody createLoanRequestBody) {
        return ResponseEntity.ok(statusService.getStatusMessage(loanService.createLoan(appUser, createLoanRequestBody)));
    }

    /**
     * Lists all loans. Both ADMIN and CUSTOMER users can access this endpoint.
     *
     * @param appUser The authenticated user
     * @return A list of loans
     */
    @GetMapping("/listAllLoans")
    public ResponseEntity<List<Loan>> listAllLoans(@AuthenticationPrincipal AppUser appUser) {
        List<Loan> loans = loanService.listLoans(appUser);
        return ResponseEntity.ok(loans);
    }

    /**
     * Lists loans for a specific customer. Both ADMIN and CUSTOMER users can access this endpoint.
     *
     * @param appUser    The authenticated user
     * @param customerId The customer ID
     * @return A list of loans
     */
    @GetMapping("/listLoans")
    public ResponseEntity<List<Loan>> listLoans(@AuthenticationPrincipal AppUser appUser, @RequestParam Long customerId) {
        List<Loan> loans = loanService.listLoans(appUser, customerId);
        return ResponseEntity.ok(loans);
    }

    /**
     * Lists loan installments for a given loan ID. Both ADMIN and CUSTOMER users can access this endpoint.
     *
     * @param appUser The authenticated user
     * @param loanId  The loan ID
     * @return A list of loan installments
     */
    @GetMapping("/listInstallments")
    public ResponseEntity<List<LoanInstallment>> listLoanInstallments(@AuthenticationPrincipal AppUser appUser,
                                                                      @RequestParam Long loanId) {
        List<LoanInstallment> loansInstallments = loanService.listLoanInstallment(appUser, loanId);
        return ResponseEntity.ok(loansInstallments);
    }

    /**
     * Pays a loan installment. Both ADMIN and CUSTOMER users can access this endpoint.
     *
     * @param appUser             The authenticated user
     * @param payLoanRequestBody  The loan payment request body
     * @return Payment status message
     */
    @PostMapping("/payLoan")
    public ResponseEntity<String> payLoan(@AuthenticationPrincipal AppUser appUser,
                                          @RequestBody PayLoanRequestBody payLoanRequestBody) {
        return loanService.payLoan(appUser, payLoanRequestBody.getCustomerId(), payLoanRequestBody.getLoanId(),
                payLoanRequestBody.getAmount());
    }
}
