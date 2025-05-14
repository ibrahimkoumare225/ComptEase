package fr.koumare.comptease.model;

import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.enumarated.TypeInvoice;
import fr.koumare.comptease.model.Article;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Invoice extends Document {

    private static final Logger logger = LoggerFactory.getLogger(Invoice.class);

    @Enumerated(EnumType.STRING)
    private StatusInvoice status;

    @Enumerated(EnumType.STRING)
    private TypeInvoice type;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    
    @ManyToMany(cascade = CascadeType.ALL)
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
    public Invoice(Double price, String description, Instant date, StatusInvoice status, Client client, List<Article> articles, TypeInvoice type, int quantity) {
        super(price, description, date);
        this.status = status;
        this.client = client;
        this.articles = articles ;
        this.type = type;
        this.quantity = quantity;
        //calculatePrice();
    }

    //cretatio de facture en passant par un devis
    /*public Invoice(double price, String description, Instant date, StatusInvoice status,Client client, Devis devis, Article articles, ) {
        super(price, description, date);
        this.status = status;
        this.client = client;
        this.devis = devis;
        //this.articles = articles != null ? articles : new ArrayList<>();
        //calculatePrice();

    }*/

    public Invoice() {

    }

   /* public Double calculatePrice(Article articles) {
        if (articles == null || articles.isEmpty()) {
            this.price = 0.0;
            return;
        }
        this.price = articles.stream()
                .mapToDouble(article -> article.getPrice().doubleValue() * article.getQuantite())
                .sum();
        
        
    }*/
    
    public StatusInvoice getStatus() {
        return status;
    }
    public void setStatus(StatusInvoice status) {
        this.status = status;
    }
    public Client getClient() {
        return client;
    }
}