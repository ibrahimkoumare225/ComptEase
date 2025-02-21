package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private String description;
    private Instant date;

    public Transaction() {
    }

    public Transaction(Long id, double amount, String description, Instant date) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
}
