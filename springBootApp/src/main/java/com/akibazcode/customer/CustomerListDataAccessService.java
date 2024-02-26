package com.akibazcode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list") // Creates repository bean
public class CustomerListDataAccessService implements CustomerDao {
    private final List<Customer> customers;
    private Integer index = 1;

    {
        customers = new ArrayList<>();
        Customer baki = new Customer(
                index++,
                "Baki",
                "baki@gmail.com",
                34
        );
        Customer maki = new Customer(
                index++,
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

    @Override
    public void insertCustomer(Customer customer) {
        customer.setId(index++);
        customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }
}
