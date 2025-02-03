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

    // Only admin user can use
    @PostMapping("/createAppUser")
    public ResponseEntity<String> createAppUser(@AuthenticationPrincipal AppUser appUser,
                                                @RequestBody AppUser newUser) {
        if (appUser.getRole() == "ADMIN") {
            Long appUserId = appUserService.createAppUser(newUser);
            return ResponseEntity.ok("App user created with app user id " + appUserId) ;
        }
        return ResponseEntity.ok("No Access ") ;
    }

    // Only Admin user can use
    @PostMapping("/createCustomer")
    public ResponseEntity<String> createCustomer(@AuthenticationPrincipal AppUser appUser,
            @RequestBody Customer createCustomer) {
        if (appUser.getRole() == "ADMIN") {
            Long customerId = customerService.createCustomer(createCustomer);
            return ResponseEntity.ok("User created with customer id " + customerId) ;
        }
        return ResponseEntity.ok("No Access ") ;
    }

    // Only Admin user can use
    @GetMapping("/listCustomers")
    public ResponseEntity<List<Customer>> listCustomers(@AuthenticationPrincipal AppUser appUser) {

        if (appUser.getRole() == "ADMIN") {
            List<Customer> customers = customerService.listCustomers();
            return ResponseEntity.ok(customers);
        }
        return ResponseEntity.ok(List.of()) ;


    }

    // Admin and customer can do this
    @PostMapping("/createLoan")
    public ResponseEntity<String> createLoan(@AuthenticationPrincipal AppUser appUser,
                                             @RequestBody CreateLoanRequestBody createLoanRequestBody) {
        return ResponseEntity.ok(statusService.getStatusMessage(loanService.createLoan(appUser, createLoanRequestBody)));
    }

    // Admin and customer can do this
    @GetMapping("/listAllLoans")
    public ResponseEntity<List<Loan>> listAllLoans(@AuthenticationPrincipal AppUser appUser) {
        List<Loan> loans = loanService.listLoans(appUser);
        return ResponseEntity.ok(loans);
    }

    // Admin and customer can do this
    @GetMapping("/listLoans")
    public ResponseEntity<List<Loan>> listLoans(@AuthenticationPrincipal AppUser appUser, @RequestParam Long customerId) {
        List<Loan> loans = loanService.listLoans(appUser, customerId);
        return ResponseEntity.ok(loans);
    }

    // Admin and customer can do this +
    @GetMapping("/listInstallments")
    public ResponseEntity<List<LoanInstallment>> listLoanInstallments(@AuthenticationPrincipal AppUser appUser,
                                                                      @RequestParam Long loanId) {
        List<LoanInstallment> loansInstallments = loanService.listLoanInstallment(appUser, loanId);
        return ResponseEntity.ok(loansInstallments);
    }

    // Admin and customer can do this +
    // Pay Loan: Pay installment for a given loan and amount
    @PostMapping("/payLoan")
    public ResponseEntity<String> payLoan(@AuthenticationPrincipal AppUser appUser,
                                          @RequestParam Long customerId, @RequestParam Long loanId,
                                          @RequestParam Float amount) {
        loanService.payLoan(appUser, customerId, loanId, amount);
        return ResponseEntity.ok("OK");
    }
}