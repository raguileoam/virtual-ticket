package com.raguileoam.virtualticket.model;

import lombok.*;

import java.time.Instant;
import java.util.Date;

import com.raguileoam.virtualticket.security.model.Account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@ToString
public class Ticket {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Date date;

    private TicketState status = TicketState.ACTIVE;

    @OneToOne
    @JoinColumn(name = "office_id")
    private Office office;

    private String attentionId;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account; 

    public Ticket() {
        this.date = Date.from(Instant.now());
    }
    public Ticket(Office office, Account account) {
        this.office = office;
        this.account = account;
        this.date = Date.from(Instant.now());
    }
}