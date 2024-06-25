package com.pedrolg.customer;

import com.pedrolg.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

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
                new JdbcTemplate(getDataSource()),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //GIVEN
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );

        underTest.insertCustomer(customer);
        List<Customer> customerList = underTest.selectAllCustomers();

        assertThat(customerList).isNotEmpty();

    }

    @Test
    void selectCustomerById() {
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                23
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(c -> c.getId())
                .findFirst().orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void willReturnEmptyWithWrongId() {
        Long id = -1L;

        Optional<Customer> customer = underTest.selectCustomerById(id);

        assertThat(customer).isEmpty();

    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                23
        );

        underTest.insertCustomer(customer);
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

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(id);

        // Theb
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();

    }

    @Test
    void existsPersonByEmail() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );

        underTest.insertCustomer(customer);

        // When
        boolean actual = underTest.existsPersonByEmail(email);


        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithId() {
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean actual = underTest.existsPersonWithId(id);

        // Then
        assertThat(actual).isTrue();

    }


    @Test
    void existsPersonEmailReturnsFalseWhenDoesNotExists() {

        // Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsPersonByEmail(email);


        // Then

        assertThat(actual).isFalse();

    }

    @Test
    void existsPersonIdReturnsFalseWhenDoesNotExists() {

        // Given
        Long id = -1L;

        // When
        var actual = underTest.existsPersonWithId(id);


        // Then
        assertThat(actual).isFalse();

    }

    @Test
    void updateCustomerName() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName = "foo";
        // When
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }

    @Test
    void updateCustomerEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        String name = FAKER.name().fullName();
        Customer customer = new Customer(
                name,
                email,
                20
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newEmail =  FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        // When
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.selectCustomerById(id);


        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }
}