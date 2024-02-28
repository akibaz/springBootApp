package com.akibazcode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return null;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return Optional.empty();
    }

    @Override
    public void insertCustomer(Customer customer) {

    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return false;
    }

    @Override
    public boolean existsCustomerWithId(Integer customerId) {
        return false;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {

    }

    @Override
    public void updateCustomer(Customer update) {

    }
}
