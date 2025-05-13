package fr.koumare.comptease.service.impl;

import fr.koumare.comptease.dao.DevisDao;
import fr.koumare.comptease.dao.FactureDao;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.Document;
import fr.koumare.comptease.model.Facture;
import fr.koumare.comptease.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final DevisDao devisDao = new DevisDao();
    private final FactureDao factureDao = new FactureDao();

    @Override
    public Document createDocument(Document document) {
        if (document == null) {
            logger.warn("Tentative de création d'un document null");
            throw new IllegalArgumentException("Le document ne peut pas être null");
        }
        logger.info("Création d'un document avec ID : {}", document.getId());
        if (document instanceof Facture) {
            logger.debug("Le document est une facture");
            Facture facture = (Facture) document;
            factureDao.saveFacture(facture);
            if (facture.getId() == null) {
                logger.error("Échec de la sauvegarde de la facture");
                return null;
            }
        } else if (document instanceof Devis) {
            logger.debug("Le document est un devis");
            Devis devis = (Devis) document;
            devisDao.saveDevis(devis);
            if (devis.getId() == null) {
                logger.error("Échec de la sauvegarde du devis");
                return null;
            }
        }
        return document;
    }

    @Override
    public Document findDocumentById(Long id) {
        if (id == null) {
            logger.warn("Tentative de recherche d'un document avec un ID null");
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        logger.info("Recherche du document avec ID : {}", id);
        Facture facture = factureDao.findFactureById(id);
        if (facture != null) {
            logger.debug("Facture trouvée avec ID : {}", id);
            return facture;
        }

        Devis devis = devisDao.findDevisById(id);
        if (devis != null) {
            logger.debug("Devis trouvé avec ID : {}", id);
            return devis;
        }

        logger.warn("Aucun document trouvé avec l'ID : {}", id);
        return null;
    }
    @Override
    public void deleteDocumentById(Long id){
        if (id == null) {
            logger.warn("Tentative de suppression d'un document avec un ID null");
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        logger.info("Suppression du document avec ID : {}", id);
        Document document = findDocumentById(id);
        if (document == null) {
            logger.warn("Document avec ID : {} non trouvé pour suppression", id);
            return;
        }
        if (document instanceof Facture) {
            factureDao.deleteFacture(id);
            logger.debug("Facture avec ID : {} supprimée", id);
        } else if (document instanceof Devis) {
            devisDao.deleteDevis(id);
            logger.debug("Devis avec ID : {} supprimé", id);
        }
    }
    public void updateDocument(Document document){
        if (document == null) {
            logger.warn("Tentative de mise à jour d'un document null");
            throw new IllegalArgumentException("Le document ne peut pas être null");
        }
        logger.info("Mise à jour du document avec ID : {}", document.getId());
        if (document instanceof Facture) {
            factureDao.updateFacture((Facture) document);
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