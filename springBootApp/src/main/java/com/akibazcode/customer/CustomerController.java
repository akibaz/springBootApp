package com.akibazcode.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController  // Create web controller with json response body
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // REST API for getting all customers
    @GetMapping("api/v1/customers")
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }


    // REST API for getting customer by id
    @GetMapping("api/v1/customers/{customerId}")
    public Customer getCustomerById(
            @PathVariable(name = "customerId") Integer customerId
    ) {
        return customerService.getCustomerById(customerId);
    }
}
