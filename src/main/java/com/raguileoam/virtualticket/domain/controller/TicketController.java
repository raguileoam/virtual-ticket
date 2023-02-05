package com.raguileoam.virtualticket.domain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.raguileoam.virtualticket.domain.model.Ticket;
import com.raguileoam.virtualticket.domain.service.TicketService;
import com.raguileoam.virtualticket.socket.controller.WebSocketController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @Autowired
    WebSocketController webSocketController;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Ticket> findAll() {
        return ticketService.findAll();
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("#id == authentication.principal.id")
    public List<Ticket> findAllByUser(@PathVariable Long id) {
        return ticketService.findAllByUser(id);
    }

    @GetMapping("/office/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public List<Ticket> findAllByOffice(@PathVariable Long id) {
        return ticketService.findAllByOffice(id);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('USER')")
    public Ticket saveTicket(@RequestBody Ticket ticket) throws JsonProcessingException {
        ticket = ticketService.saveTicket(ticket);
        webSocketController.sendWebSocketUpdate();
        return ticket;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Ticket getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicketById(@PathVariable Long id) {
        ticketService.deleteTicketById(id);
    }

    @PutMapping("{id}/mark-as-late")
    @PreAuthorize("hasRole('USER')")
    public Ticket markAsLate(@PathVariable Long id) {
        return ticketService.markAsLate(id);
    }

    @PutMapping("{id}/mark-as-done")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Ticket markAsDone(@PathVariable Long id) {
        return ticketService.markAsDone(id);
    }

    @PutMapping("{id}/mark-as-cancelled")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Ticket markAsCancelled(@PathVariable Long id) {
        return ticketService.markAsCancelled(id);
    }
}