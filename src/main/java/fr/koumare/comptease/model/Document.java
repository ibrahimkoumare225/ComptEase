package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected double price;

    protected String description;

    protected Instant date;

    public Document() {
    }

    public Document(double price, String description, Instant date) {
        this.price = price;
        this.description = description;
        this.date = date;
    }
}