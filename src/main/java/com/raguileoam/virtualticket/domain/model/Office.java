package com.raguileoam.virtualticket.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "office")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private int ticketsTotal;

    public Office(String name) {
        this.name = name;
        this.ticketsTotal = 0;
    }

}