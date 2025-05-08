package fr.koumare.comptease.service;

import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Invoice;

public interface FactureService extends DocumentService {
    Invoice createInvoice(Invoice invoice);
    void updateInvoiceStatus(Long invoiceId, String status);



}