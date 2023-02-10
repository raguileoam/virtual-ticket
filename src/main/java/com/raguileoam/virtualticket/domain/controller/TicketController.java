package com.raguileoam.virtualticket.domain.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.raguileoam.virtualticket.security.repository.AccountRepository;
import com.raguileoam.virtualticket.socket.controller.WebSocketController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @Autowired
    WebSocketController webSocketController;

    @Autowired
    AccountRepository accountRepository;

    @GetMapping("/user/{username}")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ROLE_ADMIN')")
    public List<Ticket> findAllTicketsByUser(@PathVariable String username) {
        return ticketService.findAllByUsername(username);
    }

    @GetMapping("/office/{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public List<Ticket> findAllByOffice(@PathVariable Long id) {
        return ticketService.findAllByOffice(id);
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public Ticket saveTicket(@RequestBody Ticket ticket) throws JsonProcessingException {
        ticket = ticketService.saveTicket(ticket);
        webSocketController.sendWebSocketUpdate();
        return ticket;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails)
            throws AccessDeniedException {
        try {
            Ticket ticket = ticketService.getTicketById(id);
            if (ticket.getAccount().getEmail() != userDetails.getUsername()) {
                throw new AccessDeniedException("null");
            }
            return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return new ResponseEntity<Ticket>(HttpStatus.NOT_FOUND);

    }

    @PutMapping("{id}/mark-as-done")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public Ticket markAsDone(@PathVariable Long id) {
        return ticketService.markAsDone(id);
    }

    @PutMapping("{id}/mark-as-cancelled")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public Ticket markAsCancelled(@PathVariable Long id) {
        return ticketService.markAsCancelled(id);
    }
}