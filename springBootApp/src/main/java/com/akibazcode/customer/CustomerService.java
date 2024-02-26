package com.akibazcode.customer;

import com.akibazcode.exception.DuplicateResourceException;
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
    public CustomerService(@Qualifier("list") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer customerId) {
        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Customer with id: [%s] not found.".formatted(customerId)
                        )
                );
        return customer;
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if email is already taken
        if (customerDao.existsCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException(
                    "Email already taken."
            );
        }

        // add customer
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {
        // check whether customer with provided id exist
        if (!customerDao.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer with id: [%s] not found.".formatted(customerId)
            );
        }

        // delete customer
        customerDao.deleteCustomerById(customerId);
    }
}
