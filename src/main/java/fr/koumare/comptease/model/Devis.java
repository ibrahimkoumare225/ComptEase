package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
@Getter
@Setter
@Entity
public class Devis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double price;
    private String description;
    private Instant date;
    private boolean valid;

    public Devis() {
    }

    public Devis(Long id, double price, String description, Instant date, boolean valid) {
        this.id = id;
        this.price = price;
        this.description = description;
        this.date = date;
        this.valid = valid;
    }
}
