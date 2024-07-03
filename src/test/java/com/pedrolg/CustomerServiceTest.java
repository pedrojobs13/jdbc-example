package com.pedrolg;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.pedrolg.customer.CustomerDao;
import com.pedrolg.customer.CustomerService;
import com.pedrolg.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;
    
    @BeforeEach
    void setUp(){
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers(){
        underTest.getAllCustomers();

        verify(customerDao).selectAllCustomers();
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional(){
        Long id = 10L;
        
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomer(id))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Custome with id [%d] not found".formatted(id));
    
    }

}
