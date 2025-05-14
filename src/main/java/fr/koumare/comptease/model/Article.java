package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @ElementCollection
    private List<String> category;

    private Long quantite;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToMany(mappedBy = "articles")
    private List<Devis> devis;

    @ManyToMany(mappedBy = "articles")
    private List<Invoice> invoices;

    public Article() {
    }

    public Article(String description, List<String> category, Long quantite, BigDecimal price) {
        this.description = description;
        this.category = category;
        this.quantite = quantite;
        this.price = price;
    }
    public Long getQuantite() {
        return quantite;
    }
}