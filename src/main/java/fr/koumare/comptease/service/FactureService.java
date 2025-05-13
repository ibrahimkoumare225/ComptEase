package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Facture;

import java.util.List;

public interface FactureService extends DocumentService {
    Facture createInvoice(Facture facture);
    void updateInvoiceStatus(Long invoiceId, String status);
    List<Facture> getAllFactures();



}