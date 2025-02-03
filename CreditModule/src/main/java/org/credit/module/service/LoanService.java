package org.credit.module.service;

import org.credit.module.data.*;
import org.credit.module.repository.LoanInstallmentRepository;
import org.credit.module.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.credit.module.repository.CustomerRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class LoanService {

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    @Autowired
    private Status status;


    @Autowired
    public LoanService(CustomerRepository customerRepository, LoanRepository loanRepository,
                       LoanInstallmentRepository loanInstallmentRepository) {

        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }
    List<Integer> availableInstallmentsCounts = Arrays.asList(6, 9, 12, 24);
    public Integer createLoan(AppUser appUser, CreateLoanRequestBody createLoanRequestBody) {
        boolean accessCheck = appUser.getRole().equals("ADMIN") ||
                (appUser.getRole().equals("CUSTOMER") &&
                        (appUser.getCustomerId().equals(createLoanRequestBody.getCustomerId())) );
        if (accessCheck) {
            Optional<Customer> customer = customerRepository.findCustomerById(createLoanRequestBody.getCustomerId());
            if (customer.isPresent()) {
                //You should check if customer has enough limit to get this new loan
                //Number of installments can only be 6, 9, 12, 24
                //Interest rate can be between 0.1 – 0.5
                if (createLoanRequestBody.getAmount() <= (customer.get().getCreditLimit() - customer.get().getUsedCreditLimit())
                        && (availableInstallmentsCounts.contains(createLoanRequestBody.getInstallmentsNum()))
                        && (createLoanRequestBody.getInterestRate() >= 0.1 && createLoanRequestBody.getInterestRate() <= 0.5))
                {
                    LocalDate today = LocalDate.now();
                    Loan newLoan = new Loan();
                    newLoan.setCustomerId(customer.get().getId());
                    newLoan.setLoanAmount(createLoanRequestBody.getAmount()* (1 + createLoanRequestBody.getInterestRate()));
                    newLoan.setNumberOfInstallment(createLoanRequestBody.getInstallmentsNum());
                    newLoan.setCreateDate(today);
                    newLoan.setIsPaid(false);
                    Loan createdLoan = loanRepository.save(newLoan);

                    // - Due Date of Installments should be first day of months. So the first
                    //installment’s due date should be the first day of next month.
                    Float installmentAmount = newLoan.getLoanAmount() / createLoanRequestBody.getInstallmentsNum();
                    for (int i = 0; i < createLoanRequestBody.getInstallmentsNum(); i++) {
                        LoanInstallment loanInstallment = new LoanInstallment();
                        loanInstallment.setLoanId(createdLoan.getId());
                        loanInstallment.setAmount(installmentAmount);
                        loanInstallment.setPaidAmount(0.0f);
                        loanInstallment.setDueDate(today.plusMonths(i + 1).with(TemporalAdjusters.firstDayOfMonth()));
                        loanInstallment.setIsPaid(false);
                        loanInstallmentRepository.save(loanInstallment);
                    }

                    return 0;
                }
            }
        }
        return 5;
    }

    public static boolean isLoanIdPresent(List<Loan> loans, Long loanId) {
        return loans.stream()
                .anyMatch(customer -> customer.getId().equals(loanId));
    }

    public ResponseEntity<String> payLoan(AppUser appUser, Long customerId, Long loanId, Float customerPaidAmount) {
        Integer installmentsPaid = 0;
        Float totalAmountSpent = 0.0f;
        boolean isLoanPaidOff = false;
        if (appUser.getRole().equals("ADMIN") ||
                (appUser.getRole().equals("CUSTOMER") &&
                        (appUser.getCustomerId().equals(customerId)))) {

        Optional<Customer> customer = customerRepository.findCustomerById(customerId);
        List<Loan> loans = loanRepository.findByCustomerId(customerId);
        if (customer.isPresent() && isLoanIdPresent(loans, loanId)) {
            Loan customerLoan = loans.stream().filter(loan -> loan.getId() == loanId).findFirst().get();
            List<LoanInstallment> loanInstallments = loanInstallmentRepository.findByLoanId(customerLoan.getId());

            List<LoanInstallment> notPaidLoanInstallments = loanInstallments.stream()
                    .filter(installment -> installment.getIsPaid() == false).toList();
            //1. Installments should be paid wholly or not at all. So if installments amount is
            //10 and you send 20, 2 installments can be paid. If you send 15, only 1 can be
            //paid. If you send 5, no installments can be paid.
            if (!notPaidLoanInstallments.isEmpty() &&
                    customerPaidAmount >= notPaidLoanInstallments.get(0).getAmount()) {

                //2. Installments have due date that still more than 3 calendar months cannot be
                //paid. So if we were in January, you could pay only for January, February and
                //March installments.

                LocalDate today = LocalDate.now();
                int counter = 0;
                for (LoanInstallment installment: notPaidLoanInstallments) {
                    if (counter >= 3) {
                        break;
                    }
                    LocalDate installmentDueDate = installment.getDueDate();
                    long daysBetween = ChronoUnit.DAYS.between(today, installmentDueDate);
                    boolean isEarlyPay = installmentDueDate.isAfter(today);
                    Float installmentAmount = installment.getAmount();

                    if (isEarlyPay) {
                        installmentAmount = installmentAmount - (0.001f * daysBetween);
                    } else {
                        installmentAmount = installmentAmount + (0.001f * daysBetween);
                    }
                    if (customerPaidAmount < installmentAmount) {
                        break; // paid amount is not enough for later pay
                    }
                    customerPaidAmount = customerPaidAmount - installmentAmount;
                    loanInstallmentRepository.markAsPaid(loanId, installmentDueDate);
                    installmentsPaid = installmentsPaid +1;
                    loanInstallmentRepository.updatePaidAmountAndDate(loanId,  installmentDueDate,
                            installmentAmount, today);
                    totalAmountSpent = totalAmountSpent + installmentAmount;
                    counter ++;

                    List<LoanInstallment> notpaid= loanInstallments.stream()
                            .filter(installmnt -> installmnt.getIsPaid() == false).toList();

                    if (notpaid.isEmpty()) {
                        isLoanPaidOff = true;
                        loanRepository.markLoanAsPaid(customerId, loanId);
                    }
                    customerRepository.updateCreditLimits(customerId, installment.getAmount());
                }

            }// not has unpaid installment or paid amount is lower than first installment

        }
    }
        return ResponseEntity.ok(String.format("Installments Paid: %d, Total Amount Spent: %.2f, Loan Paid Off: %s",
                installmentsPaid, totalAmountSpent, isLoanPaidOff ? "Yes" : "No"));
    }

    public List<Loan> listLoans(AppUser appUser) {
        if (appUser.getRole().equals("ADMIN")){
            return loanRepository.findAll();

        } else if (appUser.getRole().equals("CUSTOMER")) {
            return loanRepository.findByCustomerId(appUser.getCustomerId());
        }
        return List.of();
    }

    public List<Loan> listLoans(AppUser appUser, Long customerId) {
        if (appUser.getRole().equals("ADMIN")){
            return loanRepository.findByCustomerId(customerId);
        } else if (appUser.getRole().equals("CUSTOMER")) {
            return loanRepository.findByCustomerId(appUser.getCustomerId());
        }
        return List.of();
    }

    public List<LoanInstallment> listLoanInstallment(AppUser appUser, Long loanId) {
        if (appUser.getRole().equals("ADMIN")){
            return loanInstallmentRepository.findByLoanId(loanId);
        } else if (appUser.getRole().equals("CUSTOMER")) {
            Optional<Loan> cusLoan = loanRepository.findById(loanId);
            if (cusLoan.isPresent() && cusLoan.get().getCustomerId() == appUser.getCustomerId()) {
                return loanInstallmentRepository.findByLoanId(loanId);
            }
        }
        return List.of();
    }

}
