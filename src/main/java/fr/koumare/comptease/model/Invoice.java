package fr.koumare.comptease.model;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
@Getter
@Setter
@Entity
public class Invoice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double price;
    private String description;
    private Instant date;

    @Enumerated(EnumType.STRING)
    private StatusInvoice status;

    public Invoice() {
    }

    public Invoice(Long id, double price, String description, Instant date, StatusInvoice status) {
        this.id = id;
        this.price = price;
        this.description = description;
        this.date = date;
        this.status = status;
    }
}
