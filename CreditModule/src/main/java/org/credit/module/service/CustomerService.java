package org.credit.module.service;

import org.credit.module.data.CreateLoanRequestBody;
import org.credit.module.data.Customer;
import org.credit.module.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    private Status status;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Long createCustomer(Customer createCustomer) {
        Customer customer = new Customer();
        customer.setName(createCustomer.getName());
        customer.setSurname(createCustomer.getSurname());
        customer.setCreditLimit(createCustomer.getCreditLimit());
        customer.setUsedCreditLimit(0.0f);

        Customer savedCustomer = customerRepository.save(customer);
        return savedCustomer.getId();

    }

    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomer(Long customerId) {
        return customerRepository.findCustomerById(customerId);
    }

}
