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
    private int idc;

    @Column(name= "id_user", nullable= false)
    private int idUser;

    @Column(unique = true, length = 20)
    private String contact;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "adresse", nullable = false, length = 300)
    private String adresse;

    @Column(name = "solde", nullable = false)
    private Long solde;

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

    public Client(int idc, int idUser ,String contact, String firstName, String lastName, String adresse, Long solde ) {
        this.idc=idc;
        this.idUser= idUser;
        this.contact = contact;
        this.lastName = lastName;
        this.firstName = firstName;
        this.adresse = adresse;
        this.solde = solde;

    }
}