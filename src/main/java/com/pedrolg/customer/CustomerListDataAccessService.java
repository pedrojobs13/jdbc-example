package com.pedrolg.customer;

import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    private static List<Customer> customerList;

    static {
        customerList = new ArrayList<>();
        Customer pedro = new Customer(1L, "Pedro", "pedro@gmail.com", 21);
        customerList.add(pedro);
        Customer lucas = new Customer(2L, "Lucas", "lucas@gmail.com", 22);
        customerList.add(lucas);

    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerList;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customerList.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerList.add(customer);
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customerList.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst()
                .ifPresent(o -> customerList.remove(o));

    }

    @Override
    public boolean existsPersonByEmail(String email) {
        return customerList.stream().anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customerList.stream().anyMatch(customer -> customer.getId().equals(id));
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerList.add(customer);

    }
}
