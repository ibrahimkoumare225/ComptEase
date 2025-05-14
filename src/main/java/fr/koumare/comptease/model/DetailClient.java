package fr.koumare.comptease.model;

import java.time.Instant;

public class DetailClient {
    //il faut modifier une table (devis ou invoice) pour avoir la quantité vendue
    //utiliser la table modifiée ppur avoir la quantité vendue
    private Client client;
    private Facture invoice;

    public DetailClient() {
    }

    public DetailClient(Client client,Facture invoice) {
        this.client = client;
        this.invoice = invoice;
    }

    public Long getClientId() {
        return client.getIdc();
    }
    public void setClienId(Long id) {
        client.setIdc(id);
    }
    public Instant getDate() {
        return invoice.getDate();
    }
    public void setDate(Instant date) {
        invoice.setDate(date);
    }
    public String getDescription() {
        return invoice.getDescription();
    }
    public void setDescription(String description) {
        invoice.setDescription(description);
    }
    public double getPrice() {
        return invoice.getPrice();
    }
    public void setPrice(double price) {
        invoice.setPrice(price);
    }

}
