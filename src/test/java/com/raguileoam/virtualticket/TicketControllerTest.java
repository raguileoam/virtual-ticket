package com.raguileoam.virtualticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import com.raguileoam.virtualticket.domain.controller.TicketController;
import com.raguileoam.virtualticket.domain.model.Office;
import com.raguileoam.virtualticket.domain.model.Ticket;
import com.raguileoam.virtualticket.domain.repository.OfficeRepository;
import com.raguileoam.virtualticket.domain.repository.TicketRepository;
import com.raguileoam.virtualticket.security.model.Account;
import com.raguileoam.virtualticket.security.repository.AccountRepository;

@SpringBootTest
public class TicketControllerTest {

    @Autowired
    TicketController ticketController;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OfficeRepository officeRepository;

    @BeforeEach
    void setup() {
        ticketRepository.deleteAll();
    }

    @Test
    @WithAnonymousUser
    void testFindAllByOfficeWithAnonymousUser() {
        assertThrows(AccessDeniedException.class, () -> {
            ticketController.findAllByOffice(1L);
        });
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testFindAllByOfficeWithRoleAdmin() {
        ticketController.findAllByOffice(1L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void testFindAllByOfficeWithRoleUser() {
        ticketController.findAllByOffice(1L);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ROLE_ADMIN")
    void testFindAllTicketsByUserWithRoleAdmin() {
        ticketController.findAllTicketsByUser("admin");
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testFindAllTicketsByUserWithRoleUserSameUser() {
        ticketController.findAllTicketsByUser("user");
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testFindAllTicketsByUserWithRoleUserDifferentUser() {
        assertThrows(AccessDeniedException.class, () -> {
            ticketController.findAllTicketsByUser("otherUsername");
        });
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetTicketByIdWithRoleAdminAndNoTicket() {
        ResponseEntity<Ticket> response = ticketController.getTicketById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "user", authorities = "ROLE_USER")
    void testGetTicketByIdWithRoleUserAndNoTicket() {
        ResponseEntity<Ticket> response = ticketController.getTicketById(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testGetTicketByIdWithRoleAdminAndTicket() {
        testSaveTicket();
        ResponseEntity<Ticket> response = ticketController.getTicketById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "usernameOiJzd@gmail.com", authorities = "ROLE_USER")
    void testGetTicketByIdWithRoleUserAndTicketSameUser() {
        testSaveTicket();
        ResponseEntity<Ticket> response = ticketController.getTicketById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "usernameOiJzd@gmail.com", authorities = "ROLE_USER")
    void testGetTicketByIdWithRoleUserAndTicketDifferntUser() {
        testSaveTicket();
        ResponseEntity<Ticket> response = ticketController.getTicketById(1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "usernameOiJzd@gmail.com", authorities = "ROLE_USER")
    void testMarkAsCancelled() {
        testSaveTicket();
        ResponseEntity<Ticket> response = ticketController.markAsCancelled(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "usernameOiJzd@gmail.com", authorities = "ROLE_USER")
    void testMarkAsDone() {
        testSaveTicket();
        ResponseEntity<Ticket> response = ticketController.markAsDone(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void testSaveTicket() {
        Account account = accountRepository.findById(1L).get();
        Office office = officeRepository.findById(1L).get();
        Ticket ticket = new Ticket(office, account);
        ticketController.saveTicket(ticket);
    }
}
