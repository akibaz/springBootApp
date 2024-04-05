package com.akibazcode.customer;

import com.akibazcode.AbstractTestcontainers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJDBCTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomers();
        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer id = getCustomerId(email);

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        int id = 0;

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        // When
        underTest.insertCustomer(customer);
        Customer actual = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow();
        //Then
        assertThat(actual).isEqualTo(new Customer(
                actual.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        ));
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existsCustomerWithEmail(email);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithEmailReturnsFalseWhenDoesNotExist() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsCustomerWithEmail(email);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer expectedId = getCustomerId(email);

        // When
        boolean actual = underTest.existsCustomerWithId(expectedId);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        int id = 0;

        // When
        boolean actual = underTest.existsCustomerWithId(id);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        // When
        underTest.deleteCustomerById(customerId);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(customerId);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomer() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        // When
        Customer updatedCustomer = new Customer(
                customerId,
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.updateCustomer(updatedCustomer);
        Customer actualCustomer = underTest.selectCustomerById(customerId).orElseThrow();

        //Then
        assertThat(actualCustomer).isEqualTo(updatedCustomer);
        assertThat(actualCustomer).isNotEqualTo(customer);
    }

    @Test
    void updateCustomerName() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        String newName = "foo";

        // When
        Customer updatedCustomer = new Customer(
                customerId,
                newName,
                customer.getEmail(),
                customer.getAge()
        );

        underTest.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        String newEmail = "foo@bar.com";

        // When
        Customer updatedCustomer = new Customer(
                customerId,
                customer.getName(),
                newEmail,
                customer.getAge()
        );

        underTest.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        int newAge = 11;

        // When
        Customer updatedCustomer = new Customer(
                customerId,
                customer.getName(),
                customer.getEmail(),
                newAge
        );

        underTest.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void updateCustomerAllProperties() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        int newAge = 11;

        // When
        Customer updatedCustomer = new Customer(
                customerId,
                "foo",
                UUID.randomUUID().toString(),
                11
        );

        underTest.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);
        assertThat(actualCustomer).isPresent().hasValue(updatedCustomer);
    }

    @Test
    void updateCustomerwillNotUpdateWhenThereAreNoChanges() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        int age = 20;
        Customer customer = new Customer(
                name,
                email,
                age
        );
        underTest.insertCustomer(customer);
        Integer customerId = getCustomerId(email);
        int newAge = 11;

        // When
        Customer updatedCustomer = new Customer(
                customerId,
                name,
                email,
                age
        );

        underTest.updateCustomer(updatedCustomer);

        //Then
        Optional<Customer> actualCustomer = underTest.selectCustomerById(customerId);
        customer.setId(customerId);
        assertThat(actualCustomer).isPresent().hasValue(customer);
    }

    @NotNull
    private Integer getCustomerId(String email) {
        Integer id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        return id;
    }
}