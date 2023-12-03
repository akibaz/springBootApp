package com.akibazcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
@RestController // TODO: Create appropriate Controller class
public class Main {
    private static final List<Customer> customers;

    // Create primitive DB
    // TODO: Create appropriate DAO class
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

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping(
            path = "api/v1/customers",
            method = RequestMethod.GET
    ) // TODO: Replace with @GetMapping annotation
    public List<Customer> getCustomers() {
        return customers;
    }


    // TODO: Move this class to appropriate package
    public static class  Customer {
        private Integer id;
        private String name;
        private String email;
        private Integer age;

        public Customer() {
        }

        public Customer(Integer id, String name, String email, Integer age) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.age = age;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Customer customer = (Customer) o;
            return Objects.equals(id, customer.id) && Objects.equals(name, customer.name) && Objects.equals(email, customer.email) && Objects.equals(age, customer.age);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name, email, age);
        }

        @Override
        public String toString() {
            return "Customer{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
