package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ObligationFiscale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant dateEchance;

    private String typeImpot;

    private double amount;

    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "obligationFiscale", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "obligationFiscale", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Notification> notifications = new ArrayList<>();

    public ObligationFiscale() {
    }

    public ObligationFiscale(Instant dateEchance, double amount, String typeImpot, User user)
    {
        this.dateEchance = dateEchance;
        this.amount = amount;
        this.typeImpot = typeImpot;
        this.user = user;
        this.createdAt = Instant.now();
    }
}