package com.akibazcode.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
