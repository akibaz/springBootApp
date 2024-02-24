package com.akibazcode.customer;

import com.akibazcode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Creates service bean
public class CustomerService {
    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer customerId) {
        Optional<Customer> customerOptional = customerDao.selectCustomerById(customerId);
        return customerOptional.orElseThrow(
                () -> new ResourceNotFoundException(
                        "Customer with id: [%s] not found.".formatted(customerId)
                )
        );
    }
}
