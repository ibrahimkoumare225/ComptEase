package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Invoice;

import java.util.List;

public interface DevisService extends DocumentService {
    Devis createDevis(Devis devis);
    Invoice createInvoiceFromDevis(Devis devis);
    void updateDevisStatus(Long invoiceId);
    List<Devis> getAllDevis();
    void deleteDevis(Long devisId);


}