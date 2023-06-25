package com.example.travels.infraestructure.services;

import com.example.travels.api.models.request.TicketRequest;
import com.example.travels.api.models.responses.FlyResponse;
import com.example.travels.api.models.responses.TicketResponse;
import com.example.travels.domain.entities.jpa.TicketEntity;
import com.example.travels.domain.repositories.jpa.CustomerRepository;
import com.example.travels.domain.repositories.jpa.FlyRepository;
import com.example.travels.domain.repositories.jpa.TicketRepository;
import com.example.travels.infraestructure.abstract_services.ITicketService;
import com.example.travels.infraestructure.helpers.ApiCurrencyConnectorHelper;
import com.example.travels.infraestructure.helpers.BlackListHelper;
import com.example.travels.infraestructure.helpers.CustomerHelper;
import com.example.travels.infraestructure.helpers.EmailHelper;
import com.example.travels.util.BestTravelUtil;
import com.example.travels.util.enums.Tables;
import com.example.travels.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class TicketService implements ITicketService {

    private final FlyRepository flyRepository;
    private final CustomerRepository customerRepository;
    private final TicketRepository ticketRepository;
    private final CustomerHelper customerHelper;
    private final BlackListHelper blackListHelper;
    private final ApiCurrencyConnectorHelper currencyConnectorHelper;
    private final EmailHelper emailHelper;

    @Override
    public TicketResponse create(TicketRequest request) {
        blackListHelper.isInBlackListCustomer(request.getIdClient());
        var fly  = flyRepository.findById(request.getIdFly()).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        var customer = customerRepository.findById(request.getIdClient()).orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        var ticketToPersist = TicketEntity.builder()
                .id(UUID.randomUUID())
                .fly(fly)
                .customer(customer)
                .price(fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)))
                .purchaseDate(LocalDate.now())
                .departureDate(BestTravelUtil.getRandomSoon())
                .arrivalDate(BestTravelUtil.getRandomLatter())
                .arrivalDate(BestTravelUtil.getRandomLatter())
                .build();

        var ticketPersisted = this.ticketRepository.save(ticketToPersist);

        customerHelper.incrase(customer.getDni(), TicketService.class);
        if(Objects.nonNull(request.getEmail())) this.emailHelper.sendMail(request.getEmail(), customer.getFullName(), Tables.ticket.name());
        log.info("Ticket saved with id: {}", ticketPersisted.getId());
        return this.entityToResponse(ticketPersisted);
    }

    @Override
    public TicketResponse read(UUID id) {
        var ticketFromDB = this.ticketRepository.findById(id).orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        return this.entityToResponse(ticketFromDB);
    }

    @Override
    public TicketResponse update(TicketRequest request, UUID id) {
        var ticketToUpdate = ticketRepository.findById(id).orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        var fly  = flyRepository.findById(request.getIdFly()).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));

        ticketToUpdate.setFly(fly);
        ticketToUpdate.setPrice(fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)));
        ticketToUpdate.setDepartureDate(BestTravelUtil.getRandomSoon());
        ticketToUpdate.setArrivalDate(BestTravelUtil.getRandomLatter());

        var ticketUpdated = this.ticketRepository.save(ticketToUpdate);

        log.info("Ticket updated with id {}", ticketUpdated.getId());

        return this.entityToResponse(ticketToUpdate);
    }

    @Override
    public void delete(UUID id) {
        var ticketToDelete = ticketRepository.findById(id).orElseThrow(() -> new IdNotFoundException(Tables.ticket.name()));
        this.ticketRepository.delete(ticketToDelete);
    }

    @Override
    public BigDecimal findPrice(Long flyId, Currency currency) {
        var fly = this.flyRepository.findById(flyId).orElseThrow(() -> new IdNotFoundException(Tables.fly.name()));
        if (currency.equals(Currency.getInstance("USD"))) return fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage));
        var currencyDTO = this.currencyConnectorHelper.getCurrency(currency);
        log.info("API currency in {}, response: {}", currencyDTO.getExchangeDate().toString(), currencyDTO.getRates());
        return fly.getPrice().add(fly.getPrice().multiply(charger_price_percentage)).multiply(currencyDTO.getRates().get(currency));
    }

    private TicketResponse entityToResponse(TicketEntity entity) {
        var response = new TicketResponse();
        BeanUtils.copyProperties(entity, response);
        var flyResponse = new FlyResponse();
        BeanUtils.copyProperties(entity.getFly(), flyResponse);
        response.setFly(flyResponse);
        return response;
    }

    public static final BigDecimal charger_price_percentage = BigDecimal.valueOf(0.25);
}
