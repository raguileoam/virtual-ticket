package com.raguileoam.virtualticket.model;

public class TicketNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TicketNotFoundException(Long id) {
        super("Could not find ticket with an id of: " + id);
    }
}