package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusInvoice;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    private String description;

    private Instant date;

    @Enumerated(EnumType.STRING)
    private StatusInvoice status;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "invoice_articles",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

    @OneToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    @OneToMany(mappedBy = "invoice")
    private List<Transaction> transactions;

    public Invoice() {
    }

    public Invoice(double price, String description, Instant date, StatusInvoice status, Client client) {
        this.price = price;
        this.description = description;
        this.date = date;
        this.status = status;
        this.client = client;
    }
}