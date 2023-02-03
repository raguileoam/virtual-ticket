package com.raguileoam.virtualticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.raguileoam.virtualticket.model.Office;
import com.raguileoam.virtualticket.model.Ticket;
import com.raguileoam.virtualticket.model.TicketState;
import com.raguileoam.virtualticket.repositories.OfficeRepository;
import com.raguileoam.virtualticket.repositories.TicketRepository;
import com.raguileoam.virtualticket.security.model.Account;
import com.raguileoam.virtualticket.security.repository.AccountRepository;
import com.raguileoam.virtualticket.service.TicketService;

@SpringBootTest
public class TicketServiceTest {
    @Autowired
    TicketService ticketService;

    @Autowired
    OfficeRepository officeRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TicketRepository ticketRepository;

    Account account;

    Office office;

    @BeforeEach
    void setup() {
        ticketRepository.deleteAll();
        this.office = officeRepository.findById(1L).get();
        this.account = accountRepository.findById(1L).get();

    }

    @Test
    void testDeleteTicketById() {
        assertThrows(EmptyResultDataAccessException.class, () -> {
            ticketService.deleteTicketById(1L);
        });

    }

    @Test
    void testFindAll() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        List<Ticket> tickets = ticketService.findAll();
        assertEquals(1, tickets.size());
    }

    @Test
    void testFindAllByOffice() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        List<Ticket> tickets = ticketService.findAllByOffice(this.office.getId());
        assertEquals(1, tickets.size());
        List<Ticket> tickets2 = ticketService.findAllByOffice(10L);
        assertEquals(0, tickets2.size());

    }

    @Test
    void testFindAllByUser() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        List<Ticket> tickets = ticketService.findAllByUser(this.account.getId());
        assertEquals(1, tickets.size());
        List<Ticket> tickets2 = ticketService.findAllByUser(10L);
        assertEquals(0, tickets2.size());

    }

    @Test
    void testGetTicketById() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        Ticket ticket2 = ticketService.getTicketById(ticket.getId());
        assertEquals(ticket.getId(), ticket2.getId());
        assertEquals(ticket.getDate(), ticket2.getDate());
        assertEquals(ticket.getStatus(), ticket2.getStatus());
        assertEquals(ticket.getAccount().getEmail(), ticket2.getAccount().getEmail());
        assertEquals(ticket.getOffice().getId(), ticket2.getOffice().getId());
    }

    @Test
    void testMarkAsCancelled() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        Ticket ticket2 = ticketService.markAsCancelled(ticket.getId());
        assertNotEquals(ticket.getStatus().name(), ticket2.getStatus().name());
        assertEquals(TicketState.CANCELLED, ticket2.getStatus());
    }

    @Test
    void testMarkAsDone() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        Ticket ticket2 = ticketService.markAsDone(ticket.getId());
        assertNotEquals(ticket.getStatus().name(), ticket2.getStatus().name());
        assertEquals(TicketState.DONE, ticket2.getStatus());
    }

    @Test
    void testMarkAsLate() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        Ticket ticket2 = ticketService.markAsLate(ticket.getId());
        assertNotEquals(ticket.getStatus().name(), ticket2.getStatus().name());
        assertEquals(TicketState.LATE, ticket2.getStatus());
    }

    @Test
    void testSaveTicket() {
        Ticket ticket = new Ticket(this.office, this.account);
        ticket = ticketService.saveTicket(ticket);
        assertTrue(ticket.getId() != null);
        assertEquals(this.office.getId(), ticket.getOffice().getId());
        assertEquals(this.account.getId(), ticket.getAccount().getId());
    }
}
