package fr.koumare.comptease.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "legal_form")
    private String legalForm;

    @Column(name = "tax_regime")
    private String taxRegime;

    @Column(name = "profession")
    private String profession;

    @Column(name = "sales_nature")
    private String salesNature;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "siret", length = 14, unique = true)
    private String siret;

    @Column(name = "rib", length = 23)
    private String rib;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "capital_social")
    private Double capitalSocial;

    @Column(name = "tva_number", length = 20)
    private String tvaNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructeurs
    public Company() {
    }

    public Company(String companyName, String legalForm, String taxRegime, String profession,
                   String salesNature, LocalDate creationDate, LocalDate closingDate, User user,
                   String siret, String rib, String address, String phone, String email,
                   Double capitalSocial, String tvaNumber) {
        this.companyName = companyName;
        this.legalForm = legalForm;
        this.taxRegime = taxRegime;
        this.profession = profession;
        this.salesNature = salesNature;
        this.creationDate = creationDate;
        this.closingDate = closingDate;
        this.user = user;
        this.siret = siret;
        this.rib = rib;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.capitalSocial = capitalSocial;
        this.tvaNumber = tvaNumber;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getTaxRegime() {
        return taxRegime;
    }

    public void setTaxRegime(String taxRegime) {
        this.taxRegime = taxRegime;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getSalesNature() {
        return salesNature;
    }

    public void setSalesNature(String salesNature) {
        this.salesNature = salesNature;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Nouveaux getters et setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(Double capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    public String getTvaNumber() {
        return tvaNumber;
    }

    public void setTvaNumber(String tvaNumber) {
        this.tvaNumber = tvaNumber;
    }
}