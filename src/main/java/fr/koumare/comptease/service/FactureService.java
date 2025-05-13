package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Facture;

public interface FactureService extends DocumentService {
    Facture createInvoice(Facture facture);
    void updateInvoiceStatus(Long invoiceId, String status);



}