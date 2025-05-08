package fr.koumare.comptease.service.impl;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.service.FactureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FactureServiceImpl extends DocumentServiceImpl implements FactureService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    public FactureServiceImpl() {
        super();
    }

    @Override
    public Invoice createInvoice(Invoice invoice) {
        logger.info("Création d'une facture avec ID : {}", invoice.getId());
        return (Invoice) createDocument(invoice);
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
}