package fr.koumare.comptease.service;
import fr.koumare.comptease.model.Invoice;

import java.util.List;

public interface FactureService extends DocumentService {
    Invoice createInvoice(Invoice facture);
    void updateInvoiceStatus(Long invoiceId, String status);
    List<Invoice> getAllFactures();



}