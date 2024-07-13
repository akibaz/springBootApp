package com.akibazcode.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController  // Create web controller with json response body
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    // REST API for getting all customers
    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }


    // REST API for getting customer by id
    @GetMapping("{customerId}")
    public Customer getCustomerById(
            @PathVariable(name = "customerId") Integer customerId
    ) {
        return customerService.getCustomerById(customerId);
    }

    // REST API for posting new customer
    @PostMapping
    public void registerCustomer(@RequestBody CustomerRegistrationRequest customerRegistrationRequest) {
        customerService.addCustomer(customerRegistrationRequest);
    }

    // REST API for deleting customer by id
    @DeleteMapping("{customerId}")
    public void deleteCustomerById(@PathVariable("customerId") Integer customerId) {
        customerService.deleteCustomerById(customerId);
    }

    // REST API for updating customer
    @PutMapping("{customerId}")
    public void updateCustomer(
            @PathVariable("customerId") Integer customerId,
            @RequestBody CustomerUpdateRequest customerUpdateRequest) {
        customerService.updateCustomer(customerId, customerUpdateRequest);
    }
}
