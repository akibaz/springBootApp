package com.akibazcode.journey;

import com.akibazcode.customer.Customer;
import com.akibazcode.customer.CustomerRegistrationRequest;
import com.akibazcode.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final String URI_PATH = "/api/v1/customers";
    private final static Faker FAKER = new Faker();
    private final static Random RANDOM = new Random();

    @Test
    void canRegisterCustomer() {

        // create registration request
        Name fakerName = FAKER.name();
        String name = fakerName.fullName();
        String email = fakerName.firstName().toLowerCase() + "-" + UUID.randomUUID() + "@akibazcode.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age
        );

        // send a post request
        webTestClient.post()
                .uri(URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        // get customer by id
        Integer expectedCustomerId = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        expectedCustomer.setId(expectedCustomerId);

        webTestClient.get()
                .uri(URI_PATH + "/{expectedCustomerId}", expectedCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        Name fakerName = FAKER.name();
        String name = fakerName.fullName();
        String email = fakerName.firstName().toLowerCase() + "-" + UUID.randomUUID() + "@akibazcode.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age
        );

        // send a post request
        webTestClient.post()
                .uri(URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Integer expectedCustomerId = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        // delete customer by id
        webTestClient.delete()
                .uri(URI_PATH + "/{expectedCustomerId}", expectedCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        webTestClient.get()
                .uri(URI_PATH + "/{expectedCustomerId}", expectedCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        Name fakerName = FAKER.name();
        String name = fakerName.fullName();
        String email = fakerName.firstName().toLowerCase() + "-" + UUID.randomUUID() + "@akibazcode.com";
        int age = RANDOM.nextInt(1, 100);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name,
                email,
                age
        );


        // send a post request
        webTestClient.post()
                .uri(URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(URI_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Integer expectedCustomerId = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        // update customer by id
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                "Test",
                "test" + UUID.randomUUID(),
                RANDOM.nextInt(13, 89)
        );
        webTestClient.put()
                .uri(URI_PATH + "/{expectedCustomerId}", expectedCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        // get customer by id
        Customer actualCustomer = webTestClient.get()
                .uri(URI_PATH + "/{expectedCustomerId}", expectedCustomerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                expectedCustomerId,
                customerUpdateRequest.name(),
                customerUpdateRequest.email(),
                customerUpdateRequest.age()
        );

        assertThat(actualCustomer).isEqualTo(expectedCustomer);
    }
}
