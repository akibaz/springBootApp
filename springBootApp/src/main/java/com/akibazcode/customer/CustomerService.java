package com.akibazcode.customer;

import com.akibazcode.exception.RequestValidationException;
import com.akibazcode.exception.DuplicateResourceException;
import com.akibazcode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void updateCustomer(Integer customerId, CustomerUpdateRequest customerUpdateRequest) {
        // check if customer with provided id exists
        Customer customerToBeUpdated = getCustomerById(customerId);

        // check whether there are any changes
        boolean hasChanges = false;
        if (customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customerToBeUpdated.getName())) {
            customerToBeUpdated.setName(customerUpdateRequest.name());
            hasChanges = true;
        }
        if (customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customerToBeUpdated.getEmail())) {
            // check whether provided email is already taken
            if (customerDao.existsCustomerWithEmail(customerUpdateRequest.email())) {
                throw new DuplicateResourceException(
                        "Email already taken."
                );
            }
            customerToBeUpdated.setEmail(customerUpdateRequest.email());
            hasChanges = true;
        }
        if (customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customerToBeUpdated.getAge())) {
            customerToBeUpdated.setAge(customerUpdateRequest.age());
            hasChanges = true;
        }

        // update customer if there are changes
        if (hasChanges) {
            customerDao.updateCustomer(customerToBeUpdated);
        } else {
            throw new RequestValidationException(
                    "No data changes found."
            );
        }

    }
}
