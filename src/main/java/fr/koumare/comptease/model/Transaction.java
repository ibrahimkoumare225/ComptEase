package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    private String description;

    private Instant date;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "rapport_financier_id")
    private RapportFinancier rapportFinancier;

    @ManyToOne
    @JoinColumn(name = "obligation_fiscale_id")
    private ObligationFiscale obligationFiscale;

    public Transaction() {
    }

    public Transaction(double amount, String description, Instant date) {
        this.amount = amount;
        this.description = description;
        this.date = date;
    }
}