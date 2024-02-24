package com.akibazcode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list") // Creates repository bean
public class CustomerListDataAccessService implements CustomerDao {
    private static final List<Customer> customers;

     static {
        customers = new ArrayList<>();
        Customer baki = new Customer(
                1,
                "Baki",
                "baki@gmail.com",
                34
        );
        Customer maki = new Customer(
                2,
                "Maki",
                "maki@gmail.com",
                33
        );
        customers.add(baki);
        customers.add(maki);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }
}
