package com.example.travels.infraestructure.abstract_services;

import com.example.travels.api.models.request.ReservationRequest;
import com.example.travels.api.models.responses.ReservationResponse;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public interface IReservationService extends CrudService<ReservationRequest, ReservationResponse, UUID> {

     BigDecimal findPrice(Long hotelId, Currency currency);

}
