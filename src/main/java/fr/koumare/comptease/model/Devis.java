/*package fr.koumare.comptease.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class Devis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    private String description;

    private Instant date;

    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "devis_articles",
            joinColumns = @JoinColumn(name = "devis_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "devis")
    private Invoice invoice;

    public Devis() {
    }

    public Devis(double price, String description, Instant date, boolean valid, Client client, List<Article> articles, User user, Invoice invoice) {
        this.price = price;
        this.description = description;
        this.date = date;
        this.valid = valid;
        this.client = client;
        this.articles = articles;
        this.user = user;
        this.invoice = invoice;
    }
}*/
package fr.koumare.comptease.model;

import com.sun.istack.NotNull;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class Devis extends Document {

    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "devis_articles",
            joinColumns = @JoinColumn(name = "devis_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private List<Article> articles;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "devis")
    private Invoice invoice;

    public Devis() {
        super();
    }

    public Devis(double price, String description, Instant date, boolean valid, Client client, List<Article> articles, User user) {
        super(price, description, date);
        this.valid = valid;
        this.client = client;
        this.articles = articles;
        this.user = user;
    }
    
}