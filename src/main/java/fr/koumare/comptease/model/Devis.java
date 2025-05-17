package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusDevis;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Devis  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    private StatusDevis status;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "devis_articles",
            joinColumns = @JoinColumn(name = "devis_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles = new ArrayList<>();

    @OneToOne(mappedBy = "devis")
    private Invoice invoice;

    public Devis() {
        super();
    }

    public Devis(Double price, String description, Instant date, StatusDevis status, Client client, List<Article> articles) {
        this.price = price;
        this.description = description;
        this.date = date != null ? date : Instant.now();
        this.status = status;
        this.client = client;
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
}