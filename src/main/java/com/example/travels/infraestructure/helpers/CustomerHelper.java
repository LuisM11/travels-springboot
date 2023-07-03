package com.example.travels.infraestructure.helpers;

import com.example.travels.domain.repositories.jpa.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
@AllArgsConstructor
public class CustomerHelper {

    private final CustomerRepository customerRepository;

    public void incrase(String customerId, Class<?> type) {
        var customerToUpdate = this.customerRepository.findById(customerId).orElseThrow();
        switch (type.getSimpleName()) {
            case "TourService" -> customerToUpdate.setTotalTours(customerToUpdate.getTotalTours() + 1);
            case "TicketService" -> customerToUpdate.setTotalFlights(customerToUpdate.getTotalFlights() + 1);
            case "ReservationService" -> customerToUpdate.setTotalLodgings(customerToUpdate.getTotalLodgings() + 1);
        }
        this.customerRepository.save(customerToUpdate);
    }
}
