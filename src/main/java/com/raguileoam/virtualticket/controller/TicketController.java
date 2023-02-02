package com.raguileoam.virtualticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raguileoam.virtualticket.model.Office;
import com.raguileoam.virtualticket.model.Ticket;
import com.raguileoam.virtualticket.model.TicketNotFoundException;
import com.raguileoam.virtualticket.model.TicketState;
import com.raguileoam.virtualticket.repositories.OfficeRepository;
import com.raguileoam.virtualticket.repositories.TicketRepository;
import com.raguileoam.virtualticket.socket.controller.WebSocketController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    OfficeRepository officeRepository;

    @Autowired
    WebSocketController webSocketController;

    @GetMapping("/")
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    // @GetMapping("/user/{id}")
    // public List<Ticket> findAllByUser(@PathVariable Long id) {
    // return ticketRepository.findByUserId(id);
    // }

    @GetMapping("/office/{id}")
    public List<Ticket> findAllByOffice(@PathVariable Long id) {
        return ticketRepository.findByOfficeId(id);
    }

    @PostMapping("/")
    public Ticket saveTicket(@RequestBody Ticket ticket) throws JsonProcessingException {
        Office office1 = officeRepository.findById(ticket.getOffice().getId()).map(office -> {
            office.setTicketsTotal(office.getTicketsTotal() + 1);
            return officeRepository.save(office);
        }).orElse(ticket.getOffice());
        ticket.setOffice(office1);
        ticket.setAttentionId(String.format("A%d", office1.getTicketsTotal()));
        Ticket ticket2 = ticketRepository.save(ticket);
        webSocketController.sendWebSocketUpdate();
        ;
        return ticket2;
    }

    @GetMapping("{id}")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    @DeleteMapping("{id}")
    public void deleteTicketById(@PathVariable Long id) {
        ticketRepository.deleteById(id);
    }

    @PutMapping("{id}/mark-as-late")
    public Ticket markAsLate(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(TicketState.LATE);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new TicketNotFoundException(id));
    }

    @PutMapping("{id}/mark-as-done")
    public Ticket markAsDone(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(TicketState.DONE);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new TicketNotFoundException(id));
    }

    @PutMapping("{id}/mark-as-cancelled")
    public Ticket markAsCancelled(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(TicketState.CANCELLED);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new TicketNotFoundException(id));
    }
}