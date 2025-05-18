package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class Invoice {

    private static final Logger logger = LoggerFactory.getLogger(Invoice.class);

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
    private StatusInvoice status;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "description_article", nullable = false)
    private String descriptionArticle;

    @Enumerated(EnumType.STRING)
    private TypeInvoice type;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;

    @OneToOne
    @JoinColumn(name = "devis_id", nullable = true)
    private Devis devis; // Simplifié en une seule relation

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "invoice_articles",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy = "invoice")
    private List<Transaction> transactions = new ArrayList<>();

    // constructeur avec tous les paramètres
    public Invoice(Double price, String description, Instant date, StatusInvoice status, Client client, List<Article> articles, TypeInvoice type, int quantity, String descriptionArticle) {
        this.descriptionArticle = descriptionArticle;
        this.price = price;
        this.description = description;
        this.date = date != null ? date : Instant.now();
        this.status = status;
        this.client = client;
        this.articles = articles != null ? articles : new ArrayList<>();
        this.type = type;
        this.quantity = quantity;
        calculatePrice(); // calculer le prix initial
    }

    // constructeur par défaut
    public Invoice() {
    }

    // calculer le prix total et la quantite total des articles

    public void calculatePrice() {
        if (articles == null || articles.isEmpty()) {
            this.price = 0.0;
            this.quantity = 0;
            logger.warn("Aucun article pour calculer le prix, prix défini à 0.0 et quantité à 0");
            return;
        }
        this.quantity = articles.stream()
                .mapToInt(Article::getQuantite)
                .sum();
        this.price = articles.stream()
                .mapToDouble(article -> {
                    if (article.getPrice() == null) {
                        logger.error("Prix de l'article null : {}", article);
                        return 0.0;
                    }
                    return article.getPrice().doubleValue() * article.getQuantite();
                })
                .sum();
        logger.info("Prix calculé de la facture : {}, Quantité totale : {}", this.price, this.quantity);
    }
    // Getters et setters spécifiques si nécessaires (déjà gérés par Lombok)
}