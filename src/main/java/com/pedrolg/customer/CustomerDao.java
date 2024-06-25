package com.pedrolg.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Long id);

    void insertCustomer(Customer customer);

    void deleteCustomerById(Long id);

    boolean existsPersonByEmail(String email);

    boolean existsPersonWithId(Long id);

    void updateCustomer(Customer customer);
}
