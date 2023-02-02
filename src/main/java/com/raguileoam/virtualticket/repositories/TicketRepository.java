package com.raguileoam.virtualticket.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.raguileoam.virtualticket.model.Ticket;
import com.raguileoam.virtualticket.model.TicketState;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findTop1ByOfficeIdOrderByIdDesc(Long id);

    Optional<Ticket> findTop1ByOfficeIdAndStatusOrderByIdAsc(Long id, TicketState status);

    // List<Ticket> findByUserId(Long id);

    List<Ticket> findByOfficeId(Long id);
}
