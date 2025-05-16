package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Invoice extends Document {

    @Enumerated(EnumType.STRING)
    private StatusInvoice status;

    @Enumerated(EnumType.STRING)
    private TypeInvoice type;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "date", nullable = true)
    private Instant date;

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

    //invoice sans devis
    public Invoice(Double price, String description, Instant date, StatusInvoice status, Client client, List<Article> articles) {
        super(price, description, date);
        this.status = status;
        this.client = client;
        this.articles = articles != null ? articles : new ArrayList<>();
        calculatePrice();
    }

    //cretatio de facture en passant par un devis
    public Invoice(double price, String description, Instant date, StatusInvoice status,Client client, Devis devis, List<Article> articles) {
        super(price, description, date);
        this.status = status;
        this.client = client;
        this.devis = devis;
        this.articles = articles != null ? articles : new ArrayList<>();
        calculatePrice();

    }

    public Invoice() {

    }

    public void calculatePrice() {
        if (articles == null || articles.isEmpty()) {
            this.price = 0.0;
            return;
        }
        this.price = articles.stream()
                .mapToDouble(article -> article.getPrice().doubleValue() * article.getQuantite())
                .sum();
    }
    
    
}