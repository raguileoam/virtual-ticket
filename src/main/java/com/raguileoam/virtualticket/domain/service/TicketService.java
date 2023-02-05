package com.raguileoam.virtualticket.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.raguileoam.virtualticket.domain.model.Office;
import com.raguileoam.virtualticket.domain.model.Ticket;
import com.raguileoam.virtualticket.domain.model.TicketNotFoundException;
import com.raguileoam.virtualticket.domain.model.TicketState;
import com.raguileoam.virtualticket.domain.repository.OfficeRepository;
import com.raguileoam.virtualticket.domain.repository.TicketRepository;

@Service
public class TicketService {
    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    OfficeRepository officeRepository;

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public List<Ticket> findAllByUser(Long id) {
        return ticketRepository.findByAccountId(id);
    }

    public List<Ticket> findAllByOffice(Long id) {
        return ticketRepository.findByOfficeId(id);
    }

    public Ticket saveTicket(Ticket ticket) {
        Office office1 = officeRepository.findById(ticket.getOffice().getId()).map(office -> {
            office.setTicketsTotal(office.getTicketsTotal() + 1);
            return officeRepository.save(office);
        }).orElse(ticket.getOffice());
        ticket.setOffice(office1);
        ticket.setAttentionId(String.format("A%d", office1.getTicketsTotal()));
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    public void deleteTicketById(Long id) {
        ticketRepository.deleteById(id);
    }

    public Ticket markAsLate(Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(TicketState.LATE);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new TicketNotFoundException(id));
    }

    public Ticket markAsDone(Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(TicketState.DONE);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new TicketNotFoundException(id));
    }

    public Ticket markAsCancelled(Long id) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatus(TicketState.CANCELLED);
                    return ticketRepository.save(ticket);
                }).orElseThrow(() -> new TicketNotFoundException(id));
    }
}
