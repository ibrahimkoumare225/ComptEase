/*package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusInvoice;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Facture extends Document {

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
    private List<Article> articles = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "devis_id")
    private Devis devis;

    public Facture() {
        super();
    }

    //creation d'une facture sans passer par un devis
    public Facture(Double price, String description, Instant date, StatusInvoice status, Client client, List<Article> articles) {
        super(price, description, date);
        this.status = status;
        this.client = client;
        this.articles = articles != null ? articles : new ArrayList<>();
        calculatePrice();
    }

    //cretatio de facture en passant par un devis
    public Facture(double price, String description, Instant date, StatusInvoice status,Client client, Devis devis, List<Article> articles) {
        super(price, description, date);
        this.status = status;
        this.client = client;
        this.devis = devis;
        this.articles = articles != null ? articles : new ArrayList<>();
        calculatePrice();

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


}*/