package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.DevisService;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusInvoice status;

    @Enumerated(EnumType.STRING)
    private TypeInvoice type;

    private int quantity;

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
    @JoinColumn(unique = true,name = "devis_id")
    private Devis devis;

    @OneToMany(mappedBy = "invoice")
    private List<Transaction> transactions;

    public Invoice() {
        super();
    }
    @Column(name = "price")
    private double price;
    @Column(name = "description")
    private String description;
    @Column(name = "date")
    private Instant date;

    public Invoice(double price, String description, Instant date, StatusInvoice status, Client client, Devis devis) {
        //super(price, description, date);
        this.status = status;
        this.client = client;
        this.devis = devis;
        this.price=price;
        this.description=description;
        this.date=date;
    }

    
    public Devis getDevis() {
        return devis;
    }
    public void setDevis(Devis devis) {
        this.devis = devis;
    }
    public void setStatus(StatusInvoice status) {
        this.status = status;
    }
    public StatusInvoice getStatus() {
        return status;
    }
    public double getPrice() {
        return devis.getPrice();
    }
    public void setPrice(double price) {
        devis.setPrice(price);
    }
    
}