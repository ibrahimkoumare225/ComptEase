package fr.koumare.comptease.model;

import javax.persistence.*;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Constructeurs
    public Stock() {
    }

    public Stock(Article article, int quantity) {
        this.article = article;
        this.quantity = quantity;
    }
}