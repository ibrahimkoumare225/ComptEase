package fr.koumare.comptease.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Client {
    //Modifier setId_user pour qu'il prenne l'utilisateur connecté
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idc;

    // debugagge :
    // l'erreur disait : 01:25:03.863 [JavaFX Application Thread] DEBUG org.hibernate.SQL --
    //    insert
    //    into
    //        Client
    //        (adresse, contact, first_name, last_name, note, solde, id_user)
    //    values
    //        (?, ?, ?, ?, ?, ?, ?)
    //Hibernate:
    //    insert
    //    into
    //        Client
    //        (adresse, contact, first_name, last_name, note, solde, id_user)
    //    values
    //        (?, ?, ?, ?, ?, ?, ?)
    //01:25:03.865 [JavaFX Application Thread] DEBUG org.hibernate.engine.jdbc.spi.SqlExceptionHelper -- could not execute statement [n/a]
    //java.sql.SQLException: Field 'idc' doesn't have a default value
    // idc n'etait pas le bon nom du champ de la colonne de pour l'id du client , car idc n'etait pas en AUTO INCREMENT , mais la colonne "id" oui
    // donc le bon nom de colonne ,n'etait pas utilisé . Il y'a une colonne id de plus inutile

    @ManyToOne
    @JoinColumn(name= "id_user", nullable= false)
    private User user;

    @Column(unique = true,name="contact", length = 20)
    private String contact;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "adresse", nullable = false, length = 300)
    private String adresse;

    @Column(name = "solde", nullable = false)
    private Long solde;

    @Column(name = "note", nullable = true)
    private String note;

    /*@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;*/

    @OneToMany(mappedBy = "client")
    private List<Devis> devis;

    @OneToMany(mappedBy = "client")
    private List<Facture> invoices;

    @OneToMany(mappedBy = "client")
    private List<Notification> notifications;

    public Client() {
    }

    public Client(Long idc, User user , String contact, String firstName, String lastName, String adresse, Long solde , String note) {
        this.idc = idc;
        this.user =user;
        this.contact = contact;
        this.lastName = lastName;
        this.firstName = firstName;
        this.adresse = adresse;
        this.solde = solde;
        this.note = note;

    }

    //getters et setters
    public Long getIdc() {
        return idc;
    }
    public void setIdc(Long id) {
        this.idc = id;
    }
    
    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public Long getSolde() {
        return solde;
    }
    public void setSolde(Long solde) {
        this.solde = solde;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Long getId_user() {
        return user.getId();
    }
    public void setId_user(Long id_user) {
        //this.user.setId(id_user);
        this.user = new User();
        this.user.setId(id_user);
    }
    
    /*public List<Devis> getDevis() {
        return devis;
    }
    public void setDevis(List<Devis> devis) {
        this.devis = devis;
    }
    public List<Invoice> getInvoices() {
        return invoices;
    }
    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }*/
    public List<Notification> getNotifications() {
        return notifications;
    }
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
    @Override
    public String toString() {
        return "Client{" +
                "idc=" + idc +
                ", id_user=" + user.getId() +
                ", contact='" + contact + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", adresse='" + adresse + '\'' +
                ", solde=" + solde +
                '}';
    }
}