package com.raguileoam.virtualticket.domain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.raguileoam.virtualticket.domain.model.TicketNotFoundException;
import com.raguileoam.virtualticket.domain.service.TicketService;
import com.raguileoam.virtualticket.security.model.ERole;
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
    public Ticket saveTicket(@RequestBody Ticket ticket) {
        ticket = ticketService.saveTicket(ticket);
        try {
            webSocketController.sendWebSocketUpdate();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ticket;
    }

    @GetMapping("{id}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            Ticket ticket = ticketService.getTicketById(id);
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(ERole.ROLE_USER.name()))
                    && !ticket.getAccount().getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("El usuario no puede tener acceso a información de otro usuario");
            }
            return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
        } catch (TicketNotFoundException e) {
            return new ResponseEntity<Ticket>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<Ticket>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("{id}/mark-as-done")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Ticket> markAsDone(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            Ticket ticket = ticketService.markAsDone(id);
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(ERole.ROLE_USER.name()))
                    && !ticket.getAccount().getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("El usuario no puede tener acceso a información de otro usuario");
            }
            return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
        } catch (TicketNotFoundException e) {
            return new ResponseEntity<Ticket>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<Ticket>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("{id}/mark-as-cancelled")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Ticket> markAsCancelled(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            Ticket ticket = ticketService.markAsCancelled(id);
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(ERole.ROLE_USER.name()))
                    && !ticket.getAccount().getUsername().equals(userDetails.getUsername())) {
                throw new AccessDeniedException("El usuario no puede tener acceso a información de otro usuario");
            }
            return new ResponseEntity<Ticket>(ticket, HttpStatus.OK);
        } catch (TicketNotFoundException e) {
            return new ResponseEntity<Ticket>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<Ticket>(HttpStatus.FORBIDDEN);
        }
    }
}