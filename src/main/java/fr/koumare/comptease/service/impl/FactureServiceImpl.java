package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.InvoiceDao;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.enumarated.StatusDevis;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.FactureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FactureServiceImpl extends DocumentServiceImpl implements FactureService {

    private static final Logger logger = LoggerFactory.getLogger(FactureServiceImpl.class);

    @Override
    public Invoice createInvoice(Invoice invoice) {
        logger.info("Création d'une facture avec ID : {}", invoice.getId());
        if (invoice.getArticles() != null) {
            invoice.calculatePrice();
        }
        Invoice savedInvoice = (Invoice) super.createDocument(invoice);
        if (savedInvoice == null) {
            logger.error("Échec de la création de la facture");
            return null;
        }
        logger.info("Facture créée avec succès : ID {}", savedInvoice.getId());
        return savedInvoice;
    }

    public Invoice createInvoiceFromDevis(Devis devis) {
        logger.info("Création d'une facture à partir du devis ID : {}", devis.getId());
        if (devis.getStatus() != StatusDevis.ACCEPTED) {
            logger.warn("Le devis ID : {} n'est pas accepté, impossible de créer une facture", devis.getId());
            throw new IllegalStateException("Le devis doit être accepté pour créer une facture");
        }
        if (devis.getInvoice() != null) {
            logger.warn("Le devis ID : {} a déjà une facture associée", devis.getId());
            throw new IllegalStateException("Une facture existe déjà pour ce devis");
        }

        Invoice invoice = new Invoice(
                devis.getPrice(),
                devis.getDescription(),
                Instant.now(),
                StatusInvoice.UNPAID,
                devis.getClient(),
                devis,
                devis.getArticles()
        );

        Invoice savedInvoice = (Invoice) super.createDocument(invoice);
        if (savedInvoice == null) {
            logger.error("Échec de la création de la facture à partir du devis ID : {}", devis.getId());
            return null;
        }
        devis.setInvoice(savedInvoice);
        super.updateDocument(devis);
        logger.debug("Facture créée avec succès à partir du devis ID : {}", devis.getId());
        return savedInvoice;
    }

    @Override
    public void updateInvoiceStatus(Long invoiceId, String status) {
        logger.info("Mise à jour du statut de la facture ID : {} avec statut : {}", invoiceId, status);
        Invoice invoice = (Invoice) findDocumentById(invoiceId);
        if (invoice != null) {
            invoice.setStatus(StatusInvoice.valueOf(status.toUpperCase()));
            updateDocument(invoice);
            logger.debug("Statut de la facture ID : {} mis à jour à : {}", invoiceId, status);
        } else {
            logger.warn("Facture non trouvée avec l'ID : {}", invoiceId);
            throw new IllegalArgumentException("Facture non trouvée avec l'ID : " + invoiceId);
        }
    }

    public List<Invoice> getAllFactures() {
        try {
            List<Invoice> factures = findAllDocumentsByType(Invoice.class);
            logger.info("Récupération de {} factures", factures.size());
            return factures;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des factures : {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void generatePDF(Document document) {
        super.generatePDF(document);
    }
}