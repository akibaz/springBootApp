package com.akibazcode.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Creates service bean
public class CustomerService {
    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer customerId) {
        Optional<Customer> customerOptional = customerDao.selectCustomerById(customerId);
        return customerOptional.orElseThrow(
                () -> new IllegalArgumentException(
                        "Customer with id: [%s] not found.".formatted(customerId)
                )
        );
    }
}
