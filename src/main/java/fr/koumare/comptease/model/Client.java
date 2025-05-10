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
    private Long idc;

    @Column(name= "id_user", nullable= false)
    private Long idUser;

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

    public Client(Long idc, Long idUser ,String contact, String firstName, String lastName, String adresse, Long solde ) {
        this.idc=idc;
        this.idUser= idUser;
        this.contact = contact;
        this.lastName = lastName;
        this.firstName = firstName;
        this.adresse = adresse;
        this.solde = solde;

    }

    //getters et setters
    public Long getIdc() {
        return idc;
    }
    public void setIdc(Long idc) {
        this.idc = idc;
    }
    public Long getIdUser() {
        return idUser;
    }
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
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
    
    public List<Devis> getDevis() {
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
    }
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
                ", idUser=" + idUser +
                ", contact='" + contact + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", adresse='" + adresse + '\'' +
                ", solde=" + solde +
                '}';
    }
}