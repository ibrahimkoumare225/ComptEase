
package fr.koumare.comptease.service.impl;
import fr.koumare.comptease.model.Devis;
import fr.koumare.comptease.model.enumarated.StatusDevis;
import fr.koumare.comptease.model.enumarated.StatusInvoice;
import fr.koumare.comptease.model.Facture;
import fr.koumare.comptease.service.FactureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class FactureServiceImpl extends DocumentServiceImpl implements FactureService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    public FactureServiceImpl() {
        super();
    }

    @Override
    public Facture createInvoice(Facture facture) {
        logger.info("Création d'une facture avec ID : {}", facture.getId());
        if (facture.getArticles() != null) {
            facture.calculatePrice();
        }
        Facture savedFacture = (Facture) createDocument(facture);
        if (savedFacture == null) {
            logger.error("Échec de la création de la facture");
            return null; // Retourner null si la sauvegarde a échoué
        }
        return savedFacture;
    }

    public Facture createInvoiceFromDevis(Devis devis) {
        logger.info("Création d'une facture à partir du devis ID : {}", devis.getId());
        if (devis.getStatus() != StatusDevis.ACCEPTED) {
            logger.warn("Le devis ID : {} n'est pas accepté, impossible de créer une facture", devis.getId());
            throw new IllegalStateException("Le devis doit être accepté pour créer une facture");
        }
        if (devis.getFacture() != null) {
            logger.warn("Le devis ID : {} a déjà une facture associée", devis.getId());
            throw new IllegalStateException("Une facture existe déjà pour ce devis");
        }

        Facture facture = new Facture(
                devis.getPrice(),
                devis.getDescription(),
                Instant.now(),
                StatusInvoice.UNPAID,
                devis.getClient(),
                devis,
                devis.getArticles()
        );

        Facture savedFacture = (Facture) createDocument(facture);
        if (savedFacture == null) {
            logger.error("Échec de la création de la facture à partir du devis ID : {}", devis.getId());
            return null; //
        }
        devis.setFacture(savedFacture);
        updateDocument(devis);
        logger.debug("Facture créée avec succès à partir du devis ID : {}", devis.getId());
        return savedFacture;

    }

    @Override
    public void updateInvoiceStatus(Long invoiceId, String status) {
        logger.info("Mise à jour du statut de la facture ID : {} avec statut : {}", invoiceId, status);
        Facture facture = (Facture) findDocumentById(invoiceId);
        if (facture != null) {
            facture.setStatus(StatusInvoice.valueOf(status.toUpperCase()));
            updateDocument(facture);
            logger.debug("Statut de la facture ID : {} mis à jour à : {}", invoiceId, status);
        } else {
            logger.warn("Facture non trouvée avec l'ID : {}", invoiceId);
            throw new IllegalArgumentException("Facture non trouvée avec l'ID : " + invoiceId);
        }
    }

}