package com.akibazcode.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {
    private CustomerRowMapper underTest;
    @Mock
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        // Given
        Customer customer = new Customer(
                1,
                "Test",
                "test@test.com",
                35
        );
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Test");
        when(rs.getString("email")).thenReturn("test@test.com");
        when(rs.getInt("age")).thenReturn(35);
        // When
        Customer actual = underTest.mapRow(rs, 1);
        //Then
        assertThat(actual).isEqualTo(customer);
    }
}