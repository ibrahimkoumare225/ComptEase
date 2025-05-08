package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(unique = true, length = 20)
    private String contact;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "client")
    private List<Devis> devis;

    @OneToMany(mappedBy = "client")
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "client")
    private List<Notification> notifications;

    public Client() {
    }

    public Client(String lastName, String firstName, String contact, User user) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.contact = contact;
        this.user = user;
    }
}