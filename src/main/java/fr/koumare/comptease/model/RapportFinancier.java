package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class RapportFinancier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant date;

    private double incomeTotal;

    private double benefice;

    private double expenseTotal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "rapportFinancier")
    private List<Transaction> transactions;

    public RapportFinancier() {
    }

    public RapportFinancier(Instant date, double incomeTotal, double benefice, double expenseTotal, User user) {
        this.date = date;
        this.incomeTotal = incomeTotal;
        this.benefice = benefice;
        this.expenseTotal = expenseTotal;
        this.user = user;
    }
}