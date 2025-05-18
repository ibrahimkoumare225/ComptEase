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
    private Double solde;

    @Column(name = "note", nullable = true)
    private String note;

    @Column(name = "siret", length = 14, unique = true)
    private String siret;

    @Column(name = "rib", length = 23)
    private String rib;

    /*@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;*/

    @OneToMany(mappedBy = "client")
    private List<Devis> devis;

    @OneToMany(mappedBy = "client")
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "client")
    private List<Notification> notifications;

    public Client() {
    }

    public Client(Long idc, User user, String contact, String firstName, String lastName, String adresse, Double solde, String note, String siret, String rib) {
        this.idc = idc;
        this.user = user;
        this.contact = contact;
        this.lastName = lastName;
        this.firstName = firstName;
        this.adresse = adresse;
        this.solde = solde;
        this.note = note;
        this.siret = siret;
        this.rib = rib;
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
    public Double getSolde() {
        return solde;
    }
    public void setSolde(Double solde) {
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
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(1L);
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
    public String getSiret() {
        return siret;
    }
    public void setSiret(String siret) {
        this.siret = siret;
    }
    public String getRib() {
        return rib;
    }
    public void setRib(String rib) {
        this.rib = rib;
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
                ", siret='" + siret + '\'' +
                ", rib='" + rib + '\'' +
                '}';
    }
}