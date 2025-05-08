package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.DevisDao;
import fr.koumare.comptease.dao.InvoiceDao;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Invoice;
import fr.koumare.comptease.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final DevisDao devisDao = new DevisDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();

    @Override
    public Document createDocument(Document document) {
        logger.info("Création d'un document avec ID : {}", document.getId());
        if (document instanceof Invoice) {
            logger.debug("Le document est une facture");
            invoiceDao.saveFacture((Invoice) document);
        } else if (document instanceof Devis) {
            logger.debug("Le document est un devis");
            devisDao.saveDevis((Devis) document);
        }
        return document;
    }

    @Override
    public Document findDocumentById(Long id) {

        logger.info("Recherche du document avec ID : {}", id);
        Invoice invoice = invoiceDao.getAllFactures().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (invoice != null) {
            logger.debug("Facture trouvée avec ID : {}", id);
            return invoice;
        }

        Devis devis = devisDao.getAllDevis().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (devis != null) {
            logger.debug("Devis trouvé avec ID : {}", id);
        } else {
            logger.warn("Aucun document trouvé avec l'ID : {}", id);
        }
        return devis;
    }
    @Override
    public void deleteDocumentById(Long id){
        logger.info("Suppression du document avec ID : {}", id);
        Document document = findDocumentById(id);
        if (document == null) {
            logger.warn("Document avec ID : {} non trouvé pour suppression", id);
            return;
        }
        if (document instanceof Invoice) {
            invoiceDao.deleteFacture(id);
            logger.debug("Facture avec ID : {} supprimée", id);
        } else if (document instanceof Devis) {
            devisDao.deleteDevis(id);
            logger.debug("Devis avec ID : {} supprimé", id);
        }
    }
    public void updateDocument(Document document){
        logger.info("Mise à jour du document avec ID : {}", document.getId());
        if (document instanceof Invoice) {
            invoiceDao.updateFacture((Invoice) document);
            logger.debug("Facture avec ID : {} mise à jour", document.getId());
        } else if (document instanceof Devis) {
            devisDao.updateDevis((Devis) document);
            logger.debug("Devis avec ID : {} mis à jour", document.getId());
        }
    }

    @Override
    public void generatePDF(Document document) {

    }
}