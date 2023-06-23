package com.example.travels.infraestructure.abstract_services;

import com.example.travels.api.models.request.TicketRequest;
import com.example.travels.api.models.responses.TicketResponse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public interface ITicketService extends CrudService<TicketRequest, TicketResponse, UUID> {

    BigDecimal findPrice(Long flyId, Currency currency);

}
