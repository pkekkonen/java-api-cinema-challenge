package com.booleanuk.api.cinema.controller;

import com.booleanuk.api.cinema.model.Customer;
import com.booleanuk.api.cinema.model.Ticket;
import com.booleanuk.api.cinema.repository.CustomerRepository;
import com.booleanuk.api.cinema.repository.TicketRepository;
import com.booleanuk.api.cinema.response.ErrorResponse;
import com.booleanuk.api.cinema.response.Response;
import com.booleanuk.api.cinema.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<Response<List<Customer>>> getAllCustomers() {
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse<>(this.customerRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<Response<?>> createCustomer(@RequestBody Customer customer) {
        if(containsNull(customer)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("bad request"));
        }
        Response<Customer> response = new SuccessResponse<>(customerRepository.save(customer));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteCustomer(@PathVariable int id) {
        Customer customerToDelete = findCustomer(id);
        if(customerToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("not found"));
        }

        for(Ticket ticket: customerToDelete.getTickets()) {
            ticketRepository.delete(ticket);
        }

        customerRepository.delete(customerToDelete);
        return ResponseEntity.status(HttpStatus.OK).body(new SuccessResponse<>(customerToDelete));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<?>> updateCustomer(@PathVariable int id, @RequestBody Customer customer) {
        Customer customerToUpdate = findCustomer(id);
        if(customerToUpdate == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("not found"));
        }

        if(customer.getName() != null) {
            customerToUpdate.setName(customer.getName());
        }
        if(customer.getPhone() != null) {
            customerToUpdate.setPhone(customer.getPhone());
        }
        if(customer.getEmail() != null) {
            customerToUpdate.setEmail(customer.getEmail());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse<>(customerToUpdate));
    }

    private Customer findCustomer(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    private boolean containsNull(Customer customer) {
        return customer.getName() == null || customer.getPhone() == null || customer.getEmail() == null;
    }
}


