package com.akibazcode;

import com.akibazcode.customer.Customer;
import com.akibazcode.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            List<Customer> customers = List.of(
                    new Customer("Baki", "baki@gmail.com", 34),
                    new Customer("Maki", "maki@gmail.com", 33)
            );
//            customerRepository.saveAll(customers);
        };
    }
}
