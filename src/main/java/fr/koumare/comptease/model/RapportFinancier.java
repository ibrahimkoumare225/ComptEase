package fr.koumare.comptease.model;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
public class RapportFinancier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant date;
    private double incomeTotal;
    private double benefice;
    private double expenseTotal;

    public RapportFinancier() {
    }

    public RapportFinancier(Long id, Instant date, double incomeTotal, double benefice, double expenseTotal) {
        this.id = id;
        this.date = date;
        this.incomeTotal = incomeTotal;
        this.benefice = benefice;
        this.expenseTotal = expenseTotal;
    }
}
