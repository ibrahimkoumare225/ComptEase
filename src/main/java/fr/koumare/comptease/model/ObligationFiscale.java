package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
@Getter
@Setter
@Entity
public class ObligationFiscale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant dateEchance;
    private double amount;

    public ObligationFiscale() {
    }

    public ObligationFiscale(Long id, Instant dateEchance, double amount) {
        this.id = id;
        this.dateEchance = dateEchance;
        this.amount = amount;
    }
}
