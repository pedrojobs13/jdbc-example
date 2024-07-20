package com.pedrolg.customer;

import com.pedrolg.exception.DuplicateResourceException;
import com.pedrolg.exception.RequestValidationException;
import com.pedrolg.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {

        underTest = new CustomerService(customerDao);
    }


    @Test
    public void getAllCustomers() {
        underTest.getAllCustomers();

        verify(customerDao).selectAllCustomers();
    }

    @Test
    public void willThrowWhenGetCustomerReturnEmptyOptional() {
        Long id = 10L;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomer(id)).isInstanceOf(ResourceNotFoundException.class).hasMessageContaining("Custome with id [%d] not found".formatted(id));

    }

    @Test
    void getCustomer() {

        Long id = 10L;
        String email = "alex@gmail.com";
        Customer customer = new Customer(id, "alex", email, 19);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // WHEN
        Customer actual = underTest.getCustomer(id);
        //THEN

        assertThat(actual).isEqualTo(customer);

    }

    @Test
    void addCustomer() {
        String email = "alex@gmail.com";

        when(customerDao.existsPersonByEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 19);

        underTest.addCustomer(request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer customer = customerArgumentCaptor.getValue();

        assertThat(customer.getId()).isNull();
        assertThat(customer.getAge()).isEqualTo(request.age());
        assertThat(customer.getName()).isEqualTo(request.name());
        assertThat(customer.getEmail()).isEqualTo(request.email());

    }

    @Test
    void deleteCustomerById() {
        Long id = 10L;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        // WHEN
        underTest.deleteCustomerById(id);


        //THEN

        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void updateAllCustomer() {
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "jow@gmail.com";

        CustomerRegistrationRequest updateRegistration = new CustomerRegistrationRequest("jow", newEmail, 20);

        when(customerDao.existsPersonByEmail(newEmail)).thenReturn(false);

        //WHen

        underTest.updateCustomer(updateRegistration, id);


        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(updateRegistration.name());
        assertThat(captureCustomer.getAge()).isEqualTo(updateRegistration.age());
        assertThat(captureCustomer.getEmail()).isEqualTo(updateRegistration.email());

    }

    @Test
    void updateOnlyNameCustomer() {
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRegistrationRequest updateRegistration = new CustomerRegistrationRequest("jow", null, null);


        //WHen

        underTest.updateCustomer(updateRegistration, id);


        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(updateRegistration.name());
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void updateOnlyAgeCustomer() {
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRegistrationRequest updateRegistration =
                new CustomerRegistrationRequest(null, null, 22);
        //When
        underTest.updateCustomer(updateRegistration, id);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getAge()).isEqualTo(updateRegistration.age());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void updateOnlyEmailCustomer() {
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "jow@gmail.com";

        CustomerRegistrationRequest updateRegistration = new CustomerRegistrationRequest(null, newEmail, null);

        when(customerDao.existsPersonByEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(updateRegistration, id);

        // then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(captureCustomer.getEmail()).isEqualTo(updateRegistration.email());

    }


    @Test
    void willThrowAnExceptionWhenUpdateEmailCustomer() {
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "jow@gmail.com";

        CustomerRegistrationRequest updateRegistration = new CustomerRegistrationRequest(null, newEmail, null);

        when(customerDao.existsPersonByEmail(newEmail)).thenReturn(true);
        //When

        assertThatThrownBy(() -> underTest.updateCustomer(updateRegistration, id))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");
        // then
        verify(customerDao, never()).updateCustomer(any());


    }


    @Test
    void willThrowAnExceptionWhenUpdateNonePropertiesCustomer() {
        Long id = 10L;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", 19);

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerRegistrationRequest updateRegistration =
                new CustomerRegistrationRequest(customer.getName(), customer.getEmail(), customer.getAge());

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(updateRegistration, id))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");
        // then
        verify(customerDao, never()).updateCustomer(any());


    }
}