package com.akibazcode.customer;

import com.akibazcode.exception.DuplicateResourceException;
import com.akibazcode.exception.RequestValidationException;
import com.akibazcode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();
        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomerById() {
        // Given
        int id = 1;
        Customer customer = new Customer(
                id,
                "baki",
                "test@test.com",
                35
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomerById(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void getCustomerByIdThrowsExceptionWhenSelectCustomerByIdReturnsEmptyOptional() {
        // Given
        int id = 1;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        //Then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id: [%s] not found.".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "test@test.com";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "baki",
                email,
                35
        );
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        // When
        underTest.addCustomer(customerRegistrationRequest);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer actual = customerArgumentCaptor.getValue();
        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo(customerRegistrationRequest.name());
        assertThat(actual.getEmail()).isEqualTo(email);
        assertThat(actual.getAge()).isEqualTo(customerRegistrationRequest.age());
    }

    @Test
    void addCustomerThrowsExceptionWhenPassedEmailWhichAlreadyTaken() {
        // Given
        String email = "test@test.com";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "baki",
                email,
                35
        );
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);


        // When
        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken.");
        //Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 1;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void deleteCustomerByIdThrowsExceptionWhenCustomerDoesNotExist() {
        //Given
        int id = 1;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id: [%s] not found.".formatted(id));

        //Then
        verify(customerDao, never()).deleteCustomerById(anyInt());
    }

    @Test
    void updateCustomerUpdatesAllProperties() {
        // Given
        int id = 1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Test",
                "test",
                1
        );
        Customer customer = new Customer(
                1,
                "Baki",
                "baki@test.com",
                35
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);
        // When
        underTest.updateCustomer(id, request);
        //Then
        verify(customerDao).selectCustomerById(id);
        verify(customerDao).existsCustomerWithEmail(request.email());

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void updateCustomerUpdatesOnlyNamePropertie() {
        // Given
        int id = 1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Test",
                null,
                null
        );
        Customer customer = new Customer(
                1,
                "Baki",
                "baki@test.com",
                35
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        // When
        underTest.updateCustomer(id, request);
        //Then
        verify(customerDao).selectCustomerById(id);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateCustomerUpdatesOnlyEmailPropertie() {
        // Given
        int id = 1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                "test@test.com",
                null
        );
        Customer customer = new Customer(
                1,
                "Baki",
                "baki@test.com",
                35
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(false);
        // When
        underTest.updateCustomer(id, request);
        //Then
        verify(customerDao).selectCustomerById(id);
        verify(customerDao).existsCustomerWithEmail(request.email());

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateCustomerUpdatesOnlyAgePropertie() {
        // Given
        int id = 1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                1
        );
        Customer customer = new Customer(
                1,
                "Baki",
                "baki@test.com",
                35
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        // When
        underTest.updateCustomer(id, request);
        //Then
        verify(customerDao).selectCustomerById(id);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void updateCustomerThrowsExceptionEmailAlreadyTaken() {
        // Given
        int id = 1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "test",
                "test@test.com",
                1
        );
        Customer customer = new Customer(
                1,
                "Baki",
                "baki@test.com",
                35
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        when(customerDao.existsCustomerWithEmail(request.email())).thenReturn(true);
        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken.");
        //Then
        verify(customerDao).selectCustomerById(id);

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomerThrowsExceptionWhenNoChangesSubmitted() {
        // Given
        int id = 1;
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "test",
                "test@test.com",
                1
        );
        Customer customer = new Customer(
                1,
                "test",
                "test@test.com",
                1
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found.");
        //Then
        verify(customerDao).selectCustomerById(id);

        verify(customerDao, never()).updateCustomer(any());
    }
}